package com.scouter.cobblelucky.setup;

import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.blocks.CLBlocks;
import com.scouter.cobblelucky.creativetabs.CLTabs;
import com.scouter.cobblelucky.items.CLItems;
import net.minecraft.world.item.ItemStack;
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
        CLTabs.TABS();


    }
}
