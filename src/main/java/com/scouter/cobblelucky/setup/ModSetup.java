package com.scouter.cobblelucky.setup;

import com.scouter.cobblelucky.CobbleLucky;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@Mod.EventBusSubscriber(modid = CobbleLucky.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static void init(FMLCommonSetupEvent event){

    }

    public static void setup(){
        IEventBus bus = MinecraftForge.EVENT_BUS;
    }
}
