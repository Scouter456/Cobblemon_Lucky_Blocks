package com.scouter.cobblelucky.blocks;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public class CobblemonLuckyItemBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();
    private TagKey<Item> items;
    public CobblemonLuckyItemBlock(Properties pProperties, TagKey<Item> items) {
        super(pProperties);
        this.items = items;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pLevel.isClientSide) return InteractionResult.CONSUME;

        ItemStack randomItem = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(items).getRandomElement(pLevel.getRandom()).orElseGet(() -> Items.AIR)));

        if(randomItem.is(Items.AIR)){
            pPlayer.sendSystemMessage(Component.translatable("cobblelucky.item_fail.get").withStyle(ChatFormatting.RED));
            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
            return InteractionResult.FAIL;
        }

        try {
            ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), randomItem);
            pLevel.addFreshEntity(itemEntity);
            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
        } catch (Exception e){
            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
            LOGGER.error("Something went wrong generating a random item" , e);
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if(level.isClientSide) return false;

        ItemStack randomItem = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(items).getRandomElement(level.getRandom()).orElseGet(() -> Items.AIR)));

        if(randomItem.is(Items.AIR)){
            player.sendSystemMessage(Component.translatable("cobblelucky.item_fail.get").withStyle(ChatFormatting.RED));
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return false;
        }

        try {
            ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), randomItem);
            level.addFreshEntity(itemEntity);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } catch (Exception e){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            LOGGER.error("Something went wrong generating a random item" , e);
        }
        //level.destroyBlock(pos, false);
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
