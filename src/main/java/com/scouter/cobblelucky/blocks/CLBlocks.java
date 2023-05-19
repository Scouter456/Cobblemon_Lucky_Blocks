package com.scouter.cobblelucky.blocks;

import com.scouter.cobblelucky.CobbleLucky;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NyliumBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CLBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CobbleLucky.MODID);
    public static final RegistryObject<Block> COBBLEMON_LUCKY_BLOCK = BLOCKS.register("cobblemon_lucky_block", () -> new CobblemonLuckyBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE).strength(0.4F).sound(SoundType.STONE).randomTicks()));
    public static final RegistryObject<Block> COBBLEMON_LUCKY_ITEM_BLOCK = BLOCKS.register("cobblemon_lucky_item_block", () -> new CobblemonLuckyItemBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(0.4F).sound(SoundType.STONE).randomTicks()));

}
