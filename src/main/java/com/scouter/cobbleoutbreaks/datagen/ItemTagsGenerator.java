package com.scouter.cobbleoutbreaks.datagen;

import com.scouter.cobbleoutbreaks.CobblemonOutbreaks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ItemTagsGenerator extends ItemTagsProvider {
    public ItemTagsGenerator(DataGenerator generatorIn, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn, blockTagProvider, CobblemonOutbreaks.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        //for (Item i : Registry.ITEM) {
        //    if (ForgeRegistries.ITEMS.getKey(i).getNamespace().equals(Cobblemon.MODID)) {
        //        tag(COTags.Items.COBBLEMON_ITEMS).add(i);
        //    }
        //}
        //this.registerModTags();
    }

    private void registerModTags() {


    }

    private void registerForgeTags(){


    }

    @Override
    public String getName() { return "Cobblemon Outbreaks Blocks Item Tags";}
}
