package com.scouter.cobblelucky.datagen;

import com.cobblemon.mod.common.Cobblemon;
import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.util.CLTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class ItemTagsGenerator extends ItemTagsProvider {
    public ItemTagsGenerator(DataGenerator generatorIn, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn, blockTagProvider, CobbleLucky.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        for (Item i : Registry.ITEM) {
            if (ForgeRegistries.ITEMS.getKey(i).getNamespace().equals(Cobblemon.MODID)) {
                tag(CLTags.Items.COBBLEMON_ITEMS).add(i);
            }
        }
        this.registerModTags();
    }

    private void registerModTags() {


    }

    private void registerForgeTags(){


    }

    @Override
    public String getName() { return "Cobblemon Lucky Blocks Item Tags";}
}
