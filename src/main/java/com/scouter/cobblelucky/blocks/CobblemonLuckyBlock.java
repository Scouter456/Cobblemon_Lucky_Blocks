package com.scouter.cobblelucky.blocks;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CobblemonLuckyBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();
    public CobblemonLuckyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(level.isClientSide()) return InteractionResult.CONSUME;
        try {
            PokemonProperties randomProp = PokemonProperties.Companion.parse("species=random", " ", "=");
            randomProp.setLevel(level.getRandom().nextInt(1,100));
            PokemonEntity pokemon = randomProp.create().sendOut((ServerLevel) level, Vec3.atCenterOf(pos), (m) -> null);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } catch (Exception e){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            LOGGER.error("Something went wrong generating a random pokemon" , e);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if(level.isClientSide()) return;
        try {
            PokemonProperties randomProp = PokemonProperties.Companion.parse("species=random", " ", "=");
            randomProp.setLevel(level.getRandom().nextInt(1,100));
            PokemonEntity pokemon = randomProp.create().sendOut((ServerLevel) level, Vec3.atCenterOf(pos), (m) -> null);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } catch (Exception e){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            LOGGER.error("Something went wrong generating a random pokemon" , e);
        }
        super.playerWillDestroy(level, pos, state, player);
        return;

    }
}
