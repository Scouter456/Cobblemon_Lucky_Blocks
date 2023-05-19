package com.scouter.cobblelucky.datagen;

import com.scouter.cobblelucky.CobbleLucky;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTagsGenerator extends BlockTagsProvider {
    public BlockTagsGenerator(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, CobbleLucky.MODID, helper);
    }

    @Override
    protected void addTags(){

    }


    @Override
    public String getName() { return "Cobblemon Lucky Blocks, Block Tags";}
}
