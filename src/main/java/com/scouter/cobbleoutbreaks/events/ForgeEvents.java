package com.scouter.cobbleoutbreaks.events;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.logging.LogUtils;
import com.scouter.cobbleoutbreaks.config.CobblemonOutbreaksConfig;
import com.scouter.cobbleoutbreaks.data.OutbreakPlayerManager;
import com.scouter.cobbleoutbreaks.data.OutbreaksJsonDataManager;
import com.scouter.cobbleoutbreaks.data.PokemonOutbreakManager;
import com.scouter.cobbleoutbreaks.entity.OutbreakPortalEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.UUID;


public class ForgeEvents {
    private static final Logger LOGGER = LogUtils.getLogger();


    /**
     * The outbreak timer, initially set to the value defined in the config (OUTBREAK_SPAWN_TIMER).
     * Represents the time until the next outbreak of Pokémon portals.
     */
    public static int outbreakTimer = CobblemonOutbreaksConfig.OUTBREAK_SPAWN_TIMER.get();

    /**
     * The number of outbreak portals to spawn, defined in the config (OUTBREAK_SPAWN_COUNT).
     */
    public static int outbreakCount = CobblemonOutbreaksConfig.OUTBREAK_SPAWN_COUNT.get();

    /**
     * Subscribes to the PlayerTickEvent and creates Pokémon outbreaks based on a timer.
     * Checks if the player is a server player, if the event is on the server side, and if it's in the END phase.
     * If any of these conditions are not met, the method returns.
     * Manages the outbreak timer for each player and spawns outbreak portals when the timer reaches zero.
     */
    @SubscribeEvent
    public static void createOutbreaks(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer serverPlayer) || serverPlayer.level.isClientSide || event.phase == TickEvent.Phase.END) return;
        OutbreakPlayerManager outbreakPlayerManager = OutbreakPlayerManager.get((ServerLevel) serverPlayer.getLevel());
        // Check if the player's UUID is not present in the outbreak player manager and set the initial timer.
        if (!outbreakPlayerManager.containsUUID(serverPlayer.getUUID()))
            outbreakPlayerManager.setTimeLeft(serverPlayer.getUUID(), outbreakTimer);
        int timeLeft = outbreakPlayerManager.getTimeLeft(serverPlayer.getUUID());
        if (timeLeft-- > 0) {
            outbreakPlayerManager.setTimeLeft(serverPlayer.getUUID(), timeLeft--);
            return;
        }

        // Spawn outbreak portals based on the outbreak count.
        for (int i = 0; i < outbreakCount; i++) {
            OutbreakPortalEntity outbreakPortal = new OutbreakPortalEntity(serverPlayer.getLevel(), serverPlayer);
            serverPlayer.level.addFreshEntity(outbreakPortal);
        }
        // Reset the timer back to the defined value in the config.
        outbreakPlayerManager.setTimeLeft(serverPlayer.getUUID(), outbreakTimer);
    }

    @SubscribeEvent
    public static void checkDespawn(EntityLeaveLevelEvent event) {
        if(event.getLevel().isClientSide || !(event.getEntity() instanceof PokemonEntity pokemonEntity)) return;
       ServerLevel serverLevel = (ServerLevel) event.getLevel();
       PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
       UUID pokemonUUID = pokemonEntity.getPokemon().getUuid();
       if (!outbreakManager.containsUUID(pokemonUUID)) return;
       UUID ownerUUID = outbreakManager.getOwnerUUID(pokemonUUID);
       outbreakManager.removePokemonUUID(pokemonUUID);
       outbreakManager.addPokemonWOwnerTemp(pokemonUUID, ownerUUID);
       return;
    }
    @SubscribeEvent
    public static void checkSpawn(EntityJoinLevelEvent event) {
        if(event.getLevel().isClientSide || !(event.getEntity() instanceof PokemonEntity pokemonEntity) || !event.loadedFromDisk()) return;
        ServerLevel serverLevel = (ServerLevel) event.getLevel();
        PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
        UUID pokemonUUID = pokemonEntity.getPokemon().getUuid();
        if (!outbreakManager.containsUUIDTemp(pokemonUUID)) return;
        UUID ownerUUID = outbreakManager.getOwnerUUIDTemp(pokemonUUID);
        outbreakManager.removePokemonUUIDTemp(pokemonUUID);
        outbreakManager.addPokemonWOwner(pokemonUUID, ownerUUID);
        return;
    }

    private static int flushTimer = 72000;

    @SubscribeEvent
    public static void flushOutbreakTempMap(TickEvent.LevelTickEvent event) {
        if ((event.level.isClientSide)  || event.getPhase().equals(TickEvent.Phase.END) || flushTimer-- > 0) return;
        PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(event.level);
        outbreakManager.clearTempMap();
        flushTimer = 72000;
    }


    @SubscribeEvent
    public static void onRegisterReloadListeners(AddReloadListenerEvent event){
        event.addListener(new OutbreaksJsonDataManager());
    }
}
