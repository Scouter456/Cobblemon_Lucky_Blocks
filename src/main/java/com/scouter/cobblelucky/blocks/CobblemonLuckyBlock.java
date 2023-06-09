package com.scouter.cobblelucky.blocks;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class CobblemonLuckyBlock extends Block {

    private static final Logger LOGGER = LogUtils.getLogger();
    public CobblemonLuckyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if(level.isClientSide) return false;
        try {
            PokemonProperties randomProp = PokemonProperties.Companion.parse("species=random", " ", "=");
            randomProp.setLevel(level.random.nextInt(1,100));
            PokemonEntity pokemon = randomProp.create().sendOut((ServerLevel) level, Vec3.atCenterOf(pos), (m) -> null);

        } catch (Exception e){
            LOGGER.error("Something went wrong generating a random pokemon" , e);
        }
        level.destroyBlock(pos, false);
        return true;
    }
}
