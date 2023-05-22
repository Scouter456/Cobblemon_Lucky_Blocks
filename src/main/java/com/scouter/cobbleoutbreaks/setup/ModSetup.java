package com.scouter.cobbleoutbreaks.setup;

import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.mojang.logging.LogUtils;
import com.scouter.cobbleoutbreaks.CobblemonOutbreaks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

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
