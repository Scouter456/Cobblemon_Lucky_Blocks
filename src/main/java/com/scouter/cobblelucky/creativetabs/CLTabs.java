package com.scouter.cobblelucky.creativetabs;

import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.items.CLItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.scouter.cobblelucky.CobbleLucky.prefix;

public class CLTabs {

    public static final Logger LOGGER = LoggerFactory.getLogger("cobblelucky");
    private static final CreativeModeTab CL = FabricItemGroup
            .builder()
            .title(Component.translatable("itemGroup.cl"))
            .displayItems((enabledFeatures, entries) -> {
                entries.accept(CLItems.COBBLEMON_LUCKY_BLOCK);
                entries.accept(CLItems.COBBLEMON_LUCKY_ITEM_BLOCK);
            })
            .icon(CLItems.COBBLEMON_LUCKY_BLOCK::getDefaultInstance)
            .build();




    public static final CreativeModeTab CL_TAB = creativeModeTab("cl", CL);
    private static CreativeModeTab creativeModeTab(String name, CreativeModeTab item) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, prefix(name), item);
    }
    private static void generateEnchantsForBoots(CreativeModeTab.Output output, Item item, CreativeModeTab.TabVisibility tabVisibility) {
        ItemStack soulsuckerBoots = new ItemStack(item);
        soulsuckerBoots.enchant(Enchantments.SOUL_SPEED, 3);
        output.accept(soulsuckerBoots, tabVisibility);
    }

    public static void TABS(){
        LOGGER.info("Registering tabs for " + CobbleLucky.MODID);
    }
}
