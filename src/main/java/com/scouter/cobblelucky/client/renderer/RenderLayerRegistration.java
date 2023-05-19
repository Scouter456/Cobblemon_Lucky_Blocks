package com.scouter.cobblelucky.client.renderer;

import com.scouter.cobblelucky.blocks.CLBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class RenderLayerRegistration {
    public static void init() {
        RenderType cutoutMipped = RenderType.cutoutMipped();
        RenderType cutout = RenderType.cutout();
        RenderType translucent = RenderType.translucent();
        RenderType translucentnocrumb = RenderType.translucentNoCrumbling();
        RenderType solid = RenderType.solid();

        ItemBlockRenderTypes.setRenderLayer(CLBlocks.COBBLEMON_LUCKY_BLOCK.get(),solid);
    }
}
