package com.scouter.cobbleoutbreaks.events;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.logging.LogUtils;
import com.scouter.cobbleoutbreaks.CobblemonOutbreaks;
import com.scouter.cobbleoutbreaks.config.CobblemonOutbreaksConfig;
import com.scouter.cobbleoutbreaks.data.OutbreakManager;
import com.scouter.cobbleoutbreaks.data.OutbreakPlayerManager;
import com.scouter.cobbleoutbreaks.data.OutbreaksJsonDataManager;
import com.scouter.cobbleoutbreaks.data.PokemonOutbreakManager;
import com.scouter.cobbleoutbreaks.entity.OutbreakPortalEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;



public class ForgeEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static OutbreakManager outbreakManager;

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
        if (!(event.player instanceof ServerPlayer serverPlayer) || event.phase == TickEvent.Phase.END) return;




        if(!serverPlayer.level().isClientSide){
        OutbreakPlayerManager outbreakPlayerManager = OutbreakPlayerManager.get((ServerLevel) serverPlayer.level());
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
            BlockPos pos = findSuitableSpawnPoint(serverPlayer);


            int y = (int) pos.getY();
            if (serverPlayer.level().dimension() == Level.NETHER) {
                if (y <= 0) {
                    sendMessageToPlayer(serverPlayer, y);
                    continue;
                }
            }
            if (serverPlayer.level().dimension() == Level.END) {
                if (y <= 0) {
                    sendMessageToPlayer(serverPlayer, y);
                    continue;
                }
            }
            if (serverPlayer.level().dimension() == Level.OVERWORLD) {
                if (y <= -64) {
                    sendMessageToPlayer(serverPlayer, y);
                    continue;
                }
            }
            OutbreakPortalEntity outbreakPortal = new OutbreakPortalEntity(serverPlayer.level(), serverPlayer, pos);
        }

            //serverPlayer.level.addFreshEntity(outbreakPortal);
            outbreakPlayerManager.setTimeLeft(serverPlayer.getUUID(), outbreakTimer);
        }
        // Reset the timer back to the defined value in the config.

    }

    @SubscribeEvent
    public static void tickOutbreaks(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide || event.phase == TickEvent.Phase.START || !CobblemonOutbreaks.serverStarted) return;
        ServerLevel serverLevel = (ServerLevel) event.level;
        OutbreakManager outbreakManager = OutbreakManager.get(serverLevel);
        outbreakManager.setLevel(serverLevel);
        Map<UUID, OutbreakPortalEntity> outbreaks = outbreakManager.getOutbreakPortalEntityMap();
        for(Map.Entry<UUID, OutbreakPortalEntity> entry : outbreaks.entrySet()){
            OutbreakPortalEntity outbreakPortal = entry.getValue();
            if(outbreakPortal.getLevel() == null) outbreakPortal.setLevel(serverLevel);
            if(outbreakPortal.getOutbreakManager() == null) outbreakPortal.setOutbreakManager(PokemonOutbreakManager.get(serverLevel));
            outbreakPortal.tick();
        }
    }



    public static BlockPos findSuitableSpawnPoint(Player player){
        int maxRange = CobblemonOutbreaksConfig.MAX_SPAWN_RADIUS.get();
        int minRange = CobblemonOutbreaksConfig.MIN_SPAWN_RADIUS.get();



        if(maxRange > 112 || maxRange < 49){
            maxRange = 64;
        }
        if(minRange > 48 || minRange < 16){
            minRange = 32;
        }

        int randomX = player.level().random.nextInt(minRange) + (player.level().random.nextBoolean() ? 5 : -5);
        int randomZ = player.level().random.nextInt(maxRange) + (player.level().random.nextBoolean() ? 5 : -5);



        int playerPosX = player.getBlockX();
        int playerPosY = player.getBlockY();
        int playerPosZ = player.getBlockZ();

        boolean changeModX = player.level().random.nextBoolean();
        boolean changeModZ = player.level().random.nextBoolean();

        if(changeModX){
            randomX = -randomX;
        }

        if(changeModZ){
            randomZ = -randomZ;
        }
        int y = (int)player.getY();
        while ((player.level().getBlockState(new BlockPos(playerPosX + randomX, y, playerPosZ + randomZ)).isAir() && player.level().getBlockState(new BlockPos(playerPosX + randomX, y - 1, playerPosZ + randomZ)).isAir()) ||
                (!player.level().getBlockState(new BlockPos(playerPosX + randomX, y, playerPosZ + randomZ)).isAir() && !player.level().getBlockState(new BlockPos(playerPosX + randomX, y - 1, playerPosZ + randomZ)).isAir()) ||
                (!player.level().getBlockState(new BlockPos(playerPosX + randomX, y, playerPosZ + randomZ)).isAir() && player.level().getBlockState(new BlockPos(playerPosX + randomX, y - 1, playerPosZ + randomZ)).isAir())) {

            if(y < -64) break;
            if(!player.level().getBlockState(new BlockPos(playerPosX + randomX, y, playerPosZ + randomZ)).getFluidState().isEmpty()) break;
            y--;
        }

        BlockPos blockPos = new BlockPos(playerPosX + randomX, y , playerPosZ + randomZ);
        return blockPos;
    }

    public static void sendMessageToPlayer(Player player, int y){
        if(CobblemonOutbreaksConfig.SEND_PORTAL_SPAWN_MESSAGE.get()) {
            if(CobblemonOutbreaksConfig.BIOME_SPECIFIC_SPAWNS_DEBUG.get()) {
                MutableComponent yLevel = Component.literal(String.valueOf(y)).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.unlucky_spawn_debug", yLevel).withStyle(ChatFormatting.DARK_AQUA);
                player.sendSystemMessage(outBreakMessage);
            } else{
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.unlucky_spawn").withStyle(ChatFormatting.DARK_AQUA);
                player.sendSystemMessage(outBreakMessage);
            }
        }
    }


    @SubscribeEvent
    public static void checkDespawn(EntityLeaveLevelEvent event) {
        if(event.getLevel().isClientSide || !(event.getEntity() instanceof PokemonEntity pokemonEntity)) return;
       ServerLevel serverLevel = (ServerLevel) event.getLevel();
       PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
       UUID pokemonUUID = pokemonEntity.getUUID();
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
        UUID pokemonUUID = pokemonEntity.getUUID();
        if (!outbreakManager.containsUUIDTemp(pokemonUUID)) return;
        UUID ownerUUID = outbreakManager.getOwnerUUIDTemp(pokemonUUID);
        outbreakManager.removePokemonUUIDTemp(pokemonUUID);
        outbreakManager.addPokemonWOwner(pokemonUUID, ownerUUID);
        return;
    }

    private static int flushTimerTempMap = CobblemonOutbreaksConfig.TEMP_OUTBREAKS_MAP_FLUSH_TIMER.get();
    private static int flushTimerMap = CobblemonOutbreaksConfig.OUTBREAKS_MAP_FLUSH_TIMER.get();

    @SubscribeEvent
    public static void flushOutbreakTempMap(TickEvent.LevelTickEvent event) {
        if ((event.level.isClientSide)  || event.phase == TickEvent.Phase.END) return;
        ServerLevel serverLevel = (ServerLevel) event.level;
        tickTempFlushTimer(serverLevel);
        tickFlushTimer(serverLevel);
    }

    public static void tickTempFlushTimer(ServerLevel serverLevel){
        if (flushTimerTempMap-- > 0) return;
        PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
        outbreakManager.clearTempMap();
        flushTimerTempMap =  CobblemonOutbreaksConfig.TEMP_OUTBREAKS_MAP_FLUSH_TIMER.get();
    }

    public static void tickFlushTimer(ServerLevel serverLevel){
        if (flushTimerMap-- > 0) return;
        serverLevel.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("cobblemonoutbreaks.clearing_pokemon_outbreaks_map").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
        PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
        outbreakManager.clearTempMap();
        flushTimerMap =  CobblemonOutbreaksConfig.OUTBREAKS_MAP_FLUSH_TIMER.get();
    }

    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event){
        CobblemonOutbreaks.serverlevel = event.getServer().getLevel(Level.OVERWORLD);
        OutbreakManager.get(event.getServer().getLevel(Level.OVERWORLD));
        PokemonOutbreakManager.get(event.getServer().getLevel(Level.OVERWORLD));
    }

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event){
       // CobblemonOutbreaks.serverStarted = true;
    }

    @SubscribeEvent
    public static void levelLoaded(LevelEvent.Load event){
        CobblemonOutbreaks.serverStarted = true;
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(AddReloadListenerEvent event){
        event.addListener(new OutbreaksJsonDataManager());
    }
}
