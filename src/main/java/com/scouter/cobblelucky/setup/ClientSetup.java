package com.scouter.cobblelucky.setup;


import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.client.renderer.RenderLayerRegistration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CobbleLucky.MODID, value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(FMLClientSetupEvent event){
        RenderLayerRegistration.init();

    }
}

