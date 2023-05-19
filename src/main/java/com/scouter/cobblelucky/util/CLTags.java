package com.scouter.cobblelucky.util;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static com.scouter.cobblelucky.CobbleLucky.prefix;

public class CLTags {
    public static class Items {
        public static final TagKey<Item> COBBLEMON_ITEMS = tag("cobblemon_items");

        private static TagKey<Item> tag(String name){
            return TagKey.create(Registry.ITEM_REGISTRY, prefix(name));

        }
    }
}
