package com.scouter.cobblelucky.setup;


import com.scouter.cobblelucky.CobblemonOutbreaks;
import com.scouter.cobblelucky.entity.COEntity;
import com.scouter.cobblelucky.entity.OutbreakPortalRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CobblemonOutbreaks.MODID, value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(FMLClientSetupEvent event){
        EntityRenderers.register(COEntity.OUTBREAK_PORTAL.get(), OutbreakPortalRenderer::new);
    }
}

