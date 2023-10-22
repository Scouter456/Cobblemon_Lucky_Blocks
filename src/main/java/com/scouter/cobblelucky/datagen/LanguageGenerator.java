package com.scouter.cobblelucky.datagen;

import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.blocks.CLBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.data.LanguageProvider;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class LanguageGenerator extends LanguageProvider {
    public LanguageGenerator(PackOutput output) {
        super(output, CobbleLucky.MODID, "en_us");
    }
    private static final Logger LOGGER = LogUtils.getLogger();
    @Override
    protected void addTranslations(){

        //BLOCKS
        addBlock(CLBlocks.COBBLEMON_LUCKY_BLOCK, "Cobblemon Lucky Block");
        addBlock(CLBlocks.COBBLEMON_LUCKY_ITEM_BLOCK, "Cobblemon Lucky Item Block");

        //TABS
        add("itemGroup.cl", "Cobblemon Lucky Blocks");

        add("cobblelucky.item_fail.get", "You failed to obtain an item!");
    }

    @Override
    public String getName() {
        return "Cobblemon Lucky Block Languages: en_us";
    }

    public void addTabName(CreativeModeTab key, String name){
        add(key.getDisplayName().getString(), name);
    }

    public void add(CreativeModeTab key, String name) {
        add(key.getDisplayName().getString(), name);
    }

    public void addPotion(Supplier<? extends Potion> key, String name, String regName) {
        add(key.get(), name, regName);
    }

    public void add(Potion key, String name, String regName) {
        add("item.minecraft.potion.effect." + regName, name);
        add("item.minecraft.splash_potion.effect." + regName, "Splash " + name);
        add("item.minecraft.lingering_potion.effect." + regName, "Lingering " + name);
    }
}
