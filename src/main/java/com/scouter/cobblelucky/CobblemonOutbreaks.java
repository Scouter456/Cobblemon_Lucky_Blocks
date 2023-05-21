package com.scouter.cobblelucky;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.config.CobblemonOutbreaksConfig;
import com.scouter.cobblelucky.data.OutbreaksJsonDataManager;
import com.scouter.cobblelucky.data.PokemonOutbreakManager;
import com.scouter.cobblelucky.entity.OutbreakPortalEntity;
import com.scouter.cobblelucky.events.ForgeEvents;
import com.scouter.cobblelucky.setup.ClientSetup;
import com.scouter.cobblelucky.setup.ModSetup;
import com.scouter.cobblelucky.setup.Registration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.UUID;

@Mod(CobblemonOutbreaks.MODID)
public class CobblemonOutbreaks {
    public static final String MODID = "cobblemonoutbreaks";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CobblemonOutbreaks() {
        Registration.init();
        ModSetup.setup();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CobblemonOutbreaksConfig.CONFIG_BUILDER);
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientSetup::init));
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
        CobblemonOutbreaks.pokemonCapture();
        CobblemonOutbreaks.pokemonKO();
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name.toLowerCase(Locale.ROOT));
    }


    /**
     * Subscribes to the POKEMON_CAPTURED event and performs actions when a Pokémon is captured.
     * Checks if the captured Pokémon UUID is present in the outbreak manager's map.
     * If present, retrieves the owner UUID and removes the Pokémon from the set in the outbreak portal entity.
     * Finally, removes the Pokémon UUID from the outbreak manager.
     */
    public static void pokemonCapture() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.HIGH, event -> {
            if (!(event.getPlayer().level instanceof ServerLevel serverLevel)) return null;
            PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
            UUID pokemonUUID = event.getPokemon().getUuid();
            if (!outbreakManager.containsUUID(pokemonUUID)) return null;
            UUID ownerUUID = outbreakManager.getOwnerUUID(pokemonUUID);
            OutbreakPortalEntity outbreakPortal = (OutbreakPortalEntity) serverLevel.getEntity(ownerUUID);
            outbreakPortal.removeFromSet(pokemonUUID);
            outbreakManager.removePokemonUUID(pokemonUUID);
            //LOGGER.info("This one was from a portal and captured!");
            return null;
        });
    }

    /**
     * Subscribes to the POKEMON_FAINTED event and performs actions when a Pokémon faints.
     * Checks if the fainted Pokémon UUID is present in the outbreak manager's map.
     * If present, retrieves the owner UUID and removes the Pokémon from the set in the outbreak portal entity.
     * Finally, removes the Pokémon UUID from the outbreak manager.
     */
    public static void pokemonKO() {
        CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.HIGH, event -> {
            ServerLevel serverLevel = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
            PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
            UUID pokemonUUID = event.getPokemon().getUuid();
            if (!outbreakManager.containsUUID(pokemonUUID)) return null;
            UUID ownerUUID = outbreakManager.getOwnerUUID(pokemonUUID);
            OutbreakPortalEntity outbreakPortal = (OutbreakPortalEntity) serverLevel.getEntity(ownerUUID);
            outbreakPortal.removeFromSet(pokemonUUID);
            outbreakManager.removePokemonUUID(pokemonUUID);
            //LOGGER.info("This one fainted!");
            return null;
        });
    }
}
