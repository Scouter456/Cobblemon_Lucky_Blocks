package com.scouter.cobblelucky.blocks;

import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.util.CLTags;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.scouter.cobblelucky.CobbleLucky.prefix;

public class CLBlocks {
    public static final Logger LOGGER = LoggerFactory.getLogger("cobblelucky");
    public static final Block COBBLEMON_LUCKY_BLOCK = registerBlock("cobblemon_lucky_block", new CobblemonLuckyBlock(FabricBlockSettings.copy(Blocks.STONE).strength(0.4F).sound(SoundType.STONE)));
    public static final Block COBBLEMON_LUCKY_ITEM_BLOCK = registerBlock("cobblemon_lucky_item_block", new CobblemonLuckyItemBlock(FabricBlockSettings.copy(Blocks.STONE).strength(0.4F).sound(SoundType.STONE), CLTags.Items.COBBLEMON_ITEMS));

    private static Block registerBlock(String name, Block block){
        return Registry.register(BuiltInRegistries.BLOCK, prefix(name), block);
    }

    public static void BLOCKS(){
        LOGGER.info("Registering Blocks for " + CobbleLucky.MODID);
    }
}
