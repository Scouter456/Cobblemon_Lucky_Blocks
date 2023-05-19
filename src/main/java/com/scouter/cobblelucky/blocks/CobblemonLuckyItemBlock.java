package com.scouter.cobblelucky.blocks;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.logging.LogUtils;
import com.scouter.cobblelucky.util.CLTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public class CobblemonLuckyItemBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();
    public CobblemonLuckyItemBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if(level.isClientSide) return false;

        ItemStack randomItem = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(CLTags.Items.COBBLEMON_ITEMS).getRandomElement(level.getRandom()).orElseGet(() -> Items.AIR)));

        if(randomItem.is(Items.AIR)){
            player.sendSystemMessage(Component.translatable("cobblelucky.item_fail.get").withStyle(ChatFormatting.RED));
        }

        try {
            ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), randomItem);
            level.addFreshEntity(itemEntity);
        } catch (Exception e){
            LOGGER.error("Something went wrong generating a random item" , e);
        }
        level.destroyBlock(pos, false);
        return true;
    }
}
