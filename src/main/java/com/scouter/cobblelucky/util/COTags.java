package com.scouter.cobblelucky.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.scouter.cobblelucky.CobblemonOutbreaks.prefix;

public class COTags {
    public static class Blocks {


        private static TagKey<Block> tag(String name){
            return BlockTags.create(prefix(name));

        }
        private static TagKey<Block> forgeTag(String name){
            return BlockTags.create(new ResourceLocation("forge", name));

        }
    }

    public static class Items {

        public static final TagKey<Item> COBBLEMON_ITEMS = tag("cobblemon_items");

        private static TagKey<Item> tag(String name){
            return ItemTags.create(prefix(name));

        }
        private static TagKey<Item> forgeTag(String name){
            return ItemTags.create(new ResourceLocation("forge", name));

        }
    }
}
