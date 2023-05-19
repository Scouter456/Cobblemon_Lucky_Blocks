package com.scouter.cobblelucky.blocks;

import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.util.CLTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class CobblemonLuckyItemBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();
    public CobblemonLuckyItemBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if(level.isClientSide()) return;

        ItemStack randomItem = new ItemStack((Registry.ITEM.getTag(CLTags.Items.COBBLEMON_ITEMS).flatMap(items -> items.getRandomElement(level.getRandom())).get()));

        if(randomItem.is(Items.AIR) || randomItem == null){
            player.sendSystemMessage(Component.translatable("cobblelucky.item_fail.get").withStyle(ChatFormatting.RED));
            return;
        }

        try {
            ItemEntity itemEntity = new ItemEntity((Level) level, pos.getX(), pos.getY(), pos.getZ(), randomItem);
            level.addFreshEntity(itemEntity);
        } catch (Exception e){
            LOGGER.error("Something went wrong generating a random item" , e);
        }
        super.playerWillDestroy(level, pos, state, player);
        return;
    }

}
