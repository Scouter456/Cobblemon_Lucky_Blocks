package com.scouter.cobblelucky.items;


import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.blocks.CLBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class CLItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CobbleLucky.MODID);


    public static final RegistryObject<Item> COBBLEMON_LUCKY_BLOCK = fromBlock(CLBlocks.COBBLEMON_LUCKY_BLOCK);
    public static final RegistryObject<Item> COBBLEMON_LUCKY_ITEM_BLOCK = fromBlock(CLBlocks.COBBLEMON_LUCKY_ITEM_BLOCK);
    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(),new Item.Properties()));
    }

}
