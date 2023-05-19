package com.scouter.cobblelucky.blocks;

import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.setup.Registration;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLBlocks {
    public static final Logger LOGGER = LoggerFactory.getLogger("cobblelucky");
    public static final Block COBBLEMON_LUCKY_BLOCK = registerBlock("cobblemon_lucky_block", new CobblemonLuckyBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE).strength(0.4F).sound(SoundType.STONE)),  Registration.defaultBuilder);
    public static final Block COBBLEMON_LUCKY_ITEM_BLOCK = registerBlock("cobblemon_lucky_item_block", new CobblemonLuckyItemBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(0.4F).sound(SoundType.STONE)),  Registration.defaultBuilder);

    private static Block registerBlock(String name, Block block, CreativeModeTab group){
        return Registry.register(Registry.BLOCK, CobbleLucky.prefix(name), block);
    }

    public static void BLOCKS(){
        LOGGER.info("Registering Blocks for " + CobbleLucky.MODID);
    }
}
