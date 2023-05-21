package com.scouter.cobblelucky.setup;

import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.CobblemonOutbreaks;
import com.scouter.cobblelucky.entity.OutbreakPortalEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = CobblemonOutbreaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static void init(FMLCommonSetupEvent event){

    }

    public static void setup(){
        IEventBus bus = MinecraftForge.EVENT_BUS;
    }

    public static void pokemonCaptured(PokemonCapturedEvent event){


    }
}
