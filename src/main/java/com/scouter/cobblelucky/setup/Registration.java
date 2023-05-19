package com.scouter.cobblelucky.setup;

import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.items.CLItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import com.scouter.cobblelucky.blocks.CLBlocks;
import org.slf4j.Logger;

import java.util.Optional;


public class Registration {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static  Optional<ItemStack> COBBLEMON_LUCKY_BLOCK;
    private static Optional<ItemStack> GUARDIAN_SOUP;
    public static void init(){

        COBBLEMON_LUCKY_BLOCK = Optional.ofNullable(new ItemStack(CLItems.COBBLEMON_LUCKY_BLOCK));
        CLItems.ITEMS();
        CLBlocks.BLOCKS();



    }

    public static CreativeModeTab defaultBuilder = FabricItemGroupBuilder.build(CobbleLucky.prefix("cobblemon_lucky_block"), () -> COBBLEMON_LUCKY_BLOCK.get());
}
