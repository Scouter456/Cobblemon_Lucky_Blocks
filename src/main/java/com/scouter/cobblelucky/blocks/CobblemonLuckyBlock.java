package com.scouter.cobblelucky.blocks;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class CobblemonLuckyBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();
    public CobblemonLuckyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pLevel.isClientSide) return InteractionResult.CONSUME;
        try {
            PokemonProperties randomProp = PokemonProperties.Companion.parse("species=random", " ", "=");
            randomProp.setLevel(pLevel.random.nextInt(1,100));
            PokemonEntity pokemon = randomProp.createEntity(pLevel);
            Pokemon pokemon1 = pokemon.getPokemon();
            pokemon1.sendOut((ServerLevel) pLevel, Vec3.atLowerCornerOf(pPos), (m) -> null);
            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
        } catch (Exception e){
            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
            LOGGER.error("Something went wrong generating a random pokemon" , e);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if(level.isClientSide) return false;
        try {
            PokemonProperties randomProp = PokemonProperties.Companion.parse("species=random", " ", "=");
            randomProp.setLevel(level.random.nextInt(1,100));
            PokemonEntity pokemon = randomProp.createEntity(level);
            Pokemon pokemon1 = pokemon.getPokemon();
            pokemon1.sendOut((ServerLevel) level, Vec3.atLowerCornerOf(pos), (m) -> null);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } catch (Exception e){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            LOGGER.error("Something went wrong generating a random pokemon" , e);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
