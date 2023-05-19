package com.scouter.cobblelucky.datagen;

import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.CobbleLucky;
import com.scouter.cobblelucky.blocks.CLBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public class BlockstateGenerator extends BlockStateProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_ANGLE_OFFSET = 180;

    public BlockstateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, CobbleLucky.MODID, exFileHelper);
    }

    private String blockName(Block block) {
        return block.getLootTable().getPath();
    }

    public ResourceLocation resourceBlock(String path) {
        return new ResourceLocation(CobbleLucky.MODID, "block/" + path);
    }

    public ModelFile existingModel(Block block) {
        return new ModelFile.ExistingModelFile(resourceBlock(blockName(block)), models().existingFileHelper);
    }

    public ModelFile existingModel(String path) {
        return new ModelFile.ExistingModelFile(resourceBlock(path), models().existingFileHelper);
    }
    @Override
    protected void registerStatesAndModels(){
        simpleBlock(CLBlocks.COBBLEMON_LUCKY_BLOCK.get());
        simpleBlock(CLBlocks.COBBLEMON_LUCKY_ITEM_BLOCK.get());
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }




    @Override
    public String getName() {
        return "Block States: " + CobbleLucky.MODID;
    }
}
