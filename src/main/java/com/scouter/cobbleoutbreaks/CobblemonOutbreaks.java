package com.scouter.cobbleoutbreaks;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.mojang.logging.LogUtils;
import com.scouter.cobbleoutbreaks.command.OutbreakPortalCommand;
import com.scouter.cobbleoutbreaks.config.CobblemonOutbreaksConfig;
import com.scouter.cobbleoutbreaks.data.PokemonOutbreakManager;
import com.scouter.cobbleoutbreaks.entity.OutbreakPortalEntity;
import com.scouter.cobbleoutbreaks.events.ForgeEvents;
import com.scouter.cobbleoutbreaks.setup.ClientSetup;
import com.scouter.cobbleoutbreaks.setup.ModSetup;
import com.scouter.cobbleoutbreaks.setup.Registration;
import kotlin.Unit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.UUID;

@Mod(CobblemonOutbreaks.MODID)
public class CobblemonOutbreaks {
    public static final String MODID = "cobblemonoutbreaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ServerLevel serverlevel;

    public CobblemonOutbreaks() {
        Registration.init();
        ModSetup.setup();
        //MinecraftForge.EVENT_BUS.addListener(this::setServer);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CobblemonOutbreaksConfig.CONFIG_BUILDER);
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientSetup::init));
        forgeBus.addListener(ForgeEvents::serverStarted);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::serverStarted);
        CobblemonOutbreaks.pokemonCapture();
        CobblemonOutbreaks.pokemonKO();
    }


    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name.toLowerCase(Locale.ROOT));
    }


    public void commands(RegisterCommandsEvent e) {
        OutbreakPortalCommand.register(e.getDispatcher());
    }

    /**
     * Subscribes to the POKEMON_CAPTURED event and performs actions when a Pokémon is captured.
     * Checks if the captured Pokémon UUID is present in the outbreak manager's map.
     * If present, retrieves the owner UUID and removes the Pokémon from the set in the outbreak portal entity.
     * Finally, removes the Pokémon UUID from the outbreak manager.
     */
    public static void pokemonCapture() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.HIGH, event -> {
            if (!(event.getPlayer().level instanceof ServerLevel serverLevel)) return Unit.INSTANCE;
            PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
            UUID pokemonUUID = event.getPokemon().getUuid();
            if (!outbreakManager.containsUUID(pokemonUUID)) return Unit.INSTANCE;
            UUID ownerUUID = outbreakManager.getOwnerUUID(pokemonUUID);
            OutbreakPortalEntity outbreakPortal = (OutbreakPortalEntity) serverLevel.getEntity(ownerUUID);
            if (outbreakPortal != null) {
                outbreakPortal.removeFromSet(pokemonUUID);
            }
            outbreakManager.removePokemonUUID(pokemonUUID);
            //LOGGER.info("This one was from a portal and captured!");
            return Unit.INSTANCE;
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
            if (event.getPokemon().getOwnerUUID() != null || event.getPokemon() == null || serverlevel == null) return Unit.INSTANCE;
            ServerLevel serverLevel = serverlevel;
            PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
            UUID pokemonUUID = event.getPokemon().getUuid();
            if (!outbreakManager.containsUUID(pokemonUUID)) return Unit.INSTANCE;
            UUID ownerUUID = outbreakManager.getOwnerUUID(pokemonUUID);
            OutbreakPortalEntity outbreakPortal = (OutbreakPortalEntity) serverLevel.getEntity(ownerUUID);
            if (outbreakPortal != null) {
                outbreakPortal.removeFromSet(pokemonUUID);
            }
            outbreakManager.removePokemonUUID(pokemonUUID);
            //LOGGER.info("This one fainted!");
            return Unit.INSTANCE;
        });
    }
}
