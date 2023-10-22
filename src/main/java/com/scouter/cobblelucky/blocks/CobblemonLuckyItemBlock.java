package com.scouter.cobblelucky.blocks;

import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.util.CLTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class CobblemonLuckyItemBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();
    private TagKey<Item> items;
    public CobblemonLuckyItemBlock(Properties pProperties, TagKey<Item> items) {
        super(pProperties);
        this.items = items;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(level.isClientSide()) return InteractionResult.CONSUME;

        ItemStack randomItem = new ItemStack((BuiltInRegistries.ITEM.getTag(items).flatMap(items -> items.getRandomElement(level.getRandom())).get()));

        if(randomItem.is(Items.AIR) || randomItem == null){
            player.sendSystemMessage(Component.translatable("cobblelucky.item_fail.get").withStyle(ChatFormatting.RED));
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return InteractionResult.FAIL;
        }

        try {
            ItemEntity itemEntity = new ItemEntity((Level) level, pos.getX(), pos.getY(), pos.getZ(), randomItem);
            level.addFreshEntity(itemEntity);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } catch (Exception e){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            LOGGER.error("Something went wrong generating a random item" , e);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if(level.isClientSide()) return;

        ItemStack randomItem = new ItemStack((BuiltInRegistries.ITEM.getTag(items).flatMap(items -> items.getRandomElement(level.getRandom())).get()));

        if(randomItem.is(Items.AIR) || randomItem == null){
            player.sendSystemMessage(Component.translatable("cobblelucky.item_fail.get").withStyle(ChatFormatting.RED));
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return;
        }

        try {
            ItemEntity itemEntity = new ItemEntity((Level) level, pos.getX(), pos.getY(), pos.getZ(), randomItem);
            level.addFreshEntity(itemEntity);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } catch (Exception e){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            LOGGER.error("Something went wrong generating a random item" , e);
        }
        super.playerWillDestroy(level, pos, state, player);
        return;
    }

}
