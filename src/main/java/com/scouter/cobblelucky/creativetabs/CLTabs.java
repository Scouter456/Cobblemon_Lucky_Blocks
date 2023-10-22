package com.scouter.cobblelucky.creativetabs;

import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.items.CLItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CLTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CobbleLucky.MODID);

    private static final CreativeModeTab CL = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 9)
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .title(Component.translatable("itemGroup.cl"))
            .icon(() -> new ItemStack(CLItems.COBBLEMON_LUCKY_BLOCK.get()))
            .displayItems((d, entries) ->{
                CLItems.ITEMS.getEntries().forEach(i -> entries.accept(i.get()));
            })
            .build();



    public static final RegistryObject<CreativeModeTab> CL_TAB = TABS.register("cl", () -> CL);
}
