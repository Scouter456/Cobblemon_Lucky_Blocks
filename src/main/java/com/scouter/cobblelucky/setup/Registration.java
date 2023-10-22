package com.scouter.cobblelucky.setup;

import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.blocks.CLBlocks;
import com.scouter.cobblelucky.creativetabs.CLTabs;
import com.scouter.cobblelucky.items.CLItems;
import com.scouter.cobblelucky.lootmodifier.CLLootModifiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


public class Registration {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static void init(){

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        CLItems.ITEMS.register(bus);
        CLBlocks.BLOCKS.register(bus);
        CLTabs.TABS.register(bus);
        CLLootModifiers.LOOT_MODIFIER.register(bus);

    }

}
