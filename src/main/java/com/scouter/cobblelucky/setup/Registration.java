package com.scouter.cobblelucky.setup;

import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.blocks.CLBlocks;
import com.scouter.cobblelucky.items.CLItems;
import com.scouter.cobblelucky.lootmodifier.CLLootModifiers;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import static com.scouter.cobblelucky.items.CLItems.creativeTab;


public class Registration {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static void init(){

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        CLItems.ITEMS.register(bus);
        CLBlocks.BLOCKS.register(bus);
        CLLootModifiers.LOOT_MODIFIER.register(bus);

    }

    public static final Item.Properties defaultBuilder() {
        return new Item.Properties().tab(creativeTab);
    }

}
