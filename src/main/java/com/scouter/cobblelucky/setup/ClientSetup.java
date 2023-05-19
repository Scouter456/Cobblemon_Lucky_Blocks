package com.scouter.cobblelucky.setup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import com.scouter.cobblelucky.blocks.CLBlocks;

public class ClientSetup implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RenderLayerRegistration();
    }


    public static void RenderLayerRegistration(){
        RenderType cutoutMipped = RenderType.cutoutMipped();
        RenderType cutout = RenderType.cutout();
        RenderType translucent = RenderType.translucent();
        RenderType translucentnocrumb = RenderType.translucentNoCrumbling();
        RenderType solid = RenderType.solid();

        BlockRenderLayerMap.INSTANCE.putBlock(CLBlocks.COBBLEMON_LUCKY_BLOCK, cutout);
        BlockRenderLayerMap.INSTANCE.putBlock(CLBlocks.COBBLEMON_LUCKY_ITEM_BLOCK, cutout);


    }
    public static void init(){

    }
}
