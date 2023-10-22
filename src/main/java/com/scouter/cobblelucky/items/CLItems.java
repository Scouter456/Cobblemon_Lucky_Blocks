package com.scouter.cobblelucky.items;


import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.blocks.CLBlocks;
import com.scouter.cobblelucky.setup.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.scouter.cobblelucky.CobbleLucky.prefix;


public class CLItems {
    public static final Logger LOGGER = LoggerFactory.getLogger("cobblelucky");
    

    public static final Item COBBLEMON_LUCKY_BLOCK = registerBlockItem(CLBlocks.COBBLEMON_LUCKY_BLOCK);
    public static final Item COBBLEMON_LUCKY_ITEM_BLOCK = registerBlockItem(CLBlocks.COBBLEMON_LUCKY_ITEM_BLOCK);

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, prefix(name), item);
    }

    private static Item registerBlockItem(Block block){
        return Registry.register(BuiltInRegistries.ITEM, prefix(block.getDescriptionId().replace("block.cobblelucky.", "").toString()),
                new BlockItem(block, new FabricItemSettings().fireproof()));
    }
    public static void ITEMS(){
        LOGGER.info("Registering Items for " + CobbleLucky.MODID);
    }
}
