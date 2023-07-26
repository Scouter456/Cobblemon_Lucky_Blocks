package com.scouter.cobbleoutbreaks.entity;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import javax.annotation.Nullable;

import static com.scouter.cobbleoutbreaks.CobblemonOutbreaks.prefix;

public class SpawnAlgorithms {
    public interface SpawnAlgorithm {
        /**
         * Spawns a Wave Entity, including all passengers.
         * @param level The level the entity will be spawned in.
         * @param pos The block position of the Gateway entity.
         * @return The newly-created entity, or null, if the entity could not be spawned or a suitable spawn location could not be found.
         */
        @Nullable
        Vec3 spawn(ServerLevel level, Vec3 pos, OutbreakPortalEntity outbreakPortal, PokemonEntity pokemon);
    }

    public static final BiMap<ResourceLocation, SpawnAlgorithm> NAMED_ALGORITHMS = HashBiMap.create();
    static {
        NAMED_ALGORITHMS.put(prefix("open_field"), SpawnAlgorithms::openField);
        NAMED_ALGORITHMS.put(prefix("inward_spiral"), SpawnAlgorithms::inwardSpiral);
        //NAMED_ALGORITHMS.put(prefix("checkerboard"), SpawnAlgorithms::checkerBoard);
        NAMED_ALGORITHMS.put(prefix("clustered"), SpawnAlgorithms::clustered);
    }

    public static final Codec<SpawnAlgorithm> CODEC = ExtraCodecs.stringResolverCodec(sa -> NAMED_ALGORITHMS.inverse().get(sa).toString(), key -> NAMED_ALGORITHMS.get(new ResourceLocation(key)));
    public static final int MAX_SPAWN_TRIES = 25;

    /**
     * The Open Field Algorithm selects random spawn positions within the spawn radius, and places entities on the ground.<br>
     * This algorithm will likely fail if the working area is not mostly empty.<br>
     */
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Vec3 openField(ServerLevel level, Vec3 pos, OutbreakPortalEntity outbreakPortalEntity, PokemonEntity pokemon){
        double spawnRange = outbreakPortalEntity.getOutbreakPortal().getSpawnRange();
        int tries = 0;
        double x = pos.x() + (-1 + 2 * level.random.nextDouble()) * spawnRange;
        double y = pos.y() + level.random.nextInt(3) - 1;
        double z = pos.z() + (-1 + 2 * level.random.nextDouble()) * spawnRange;
        while (!level.noCollision(getAABB(x,y,z, pokemon)) && tries++ < MAX_SPAWN_TRIES) {
            x = pos.x() + (level.random.nextDouble() - level.random.nextDouble()) * spawnRange + 0.5D;
            y = pos.y() + level.random.nextInt(3) + 1;
            z = pos.z() + (level.random.nextDouble() - level.random.nextDouble()) * spawnRange + 0.5D;
        }


        while (level.getBlockState(new BlockPos(x, y - 1, z)).isAir() && y > level.getMinBuildHeight()) {
            y--;
        }

        while (!level.noCollision(getAABB(x, y, z, pokemon))) {
            y++;
        }

        if (outbreakPortalEntity.distanceToSqr(x, y, z) > outbreakPortalEntity.getOutbreakPortal().getLeashRangeSq()) return null;

        if (level.noCollision(getAABB(x,y,z, pokemon))) return new Vec3(x, y, z);

        return null;
    }

    /**
     * The Inward Spiral Algorithm selects random spawn positions within the spawn radius, but reduces the spawn radius each attempt.<br>
     * On the final attempt, the wave entity will attempt to spawn exactly on the position of the Gateway itself.<br>
     * Spawned entities will still be placed on the ground.<br>
     * This algorithm will work in most scenarios, but may enable non-ideal cheese mechanisms such as dropping all wave entities into a mob grinder.
     */
    @Nullable
    public static Vec3 inwardSpiral(ServerLevel level, Vec3 pos, OutbreakPortalEntity outbreakPortal, PokemonEntity pokemon) {

        double spawnRange = outbreakPortal.getOutbreakPortal().getSpawnRange();

        int tries = 0;
        double x = pos.x() + (-1 + 2 * level.random.nextDouble()) * spawnRange;
        double y = pos.y() + level.random.nextInt(3) - 1;
        double z = pos.z() + (-1 + 2 * level.random.nextDouble()) * spawnRange;
        while (!level.noCollision(getAABB(x,y,z, pokemon)) && tries++ < MAX_SPAWN_TRIES) {
            float scaleFactor = (MAX_SPAWN_TRIES - 1 - tries) / (float) MAX_SPAWN_TRIES;
            x = pos.x() + scaleFactor * (level.random.nextDouble() - level.random.nextDouble()) * spawnRange + 0.5D;
            y = pos.y() + scaleFactor * level.random.nextInt(3) + 1;
            z = pos.z() + scaleFactor * (level.random.nextDouble() - level.random.nextDouble()) * spawnRange + 0.5D;
        }

        while (level.getBlockState(new BlockPos(x, y - 1, z)).isAir() && y > level.getMinBuildHeight()) {
            y--;
        }

        while (!level.noCollision(getAABB(x, y, z, pokemon))) {
            y++;
        }

        if (outbreakPortal.distanceToSqr(x, y, z) > outbreakPortal.getOutbreakPortal().getLeashRangeSq()) return null;

        if (level.noCollision(pokemon.getBoundingBox().inflate(x,y,z))) return new Vec3(x, y, z);

        return null;
    }

    /**
     * The checkerBoard algorithm randomly selects spawn positions within a specified spawn radius and places entities on the ground.
     * It aims to create a checkerboard pattern with alternating rows and columns.
     * However, it may not work effectively if the area where it operates is not mostly empty.
     * **/
    @Nullable
    public static Vec3 checkerBoard(ServerLevel level, Vec3 pos, OutbreakPortalEntity outbreakPortal, PokemonEntity pokemon) {
        double spawnRange = outbreakPortal.getOutbreakPortal().getSpawnRange();

        int tries = 0;
        double x = pos.x() - spawnRange / 2;
        double y = pos.y();
        double z = pos.z() - spawnRange / 2;
        BlockPos blockPos = new BlockPos(x, y, z);

        boolean spawnPosFound = false; // Flag to track if a valid spawn position is found

        for (int i = 0; i < spawnRange; i++) {
            for (int j = 0; j < spawnRange; j++) {
                double currentX = x + i;
                double currentZ = z + j;

                if (i % 2 == j % 2) {
                    level.setBlock(new BlockPos(currentX, -40, currentZ), Blocks.WHITE_WOOL.defaultBlockState(), 3);
                } else {
                    level.setBlock(new BlockPos(currentX, -40, currentZ), Blocks.BLACK_WOOL.defaultBlockState(), 3);

                    boolean setSpawnPos = level.random.nextInt(0, 100) <= 15;
                    LOGGER.info("canSpawn? " + setSpawnPos);
                    LOGGER.info("collision? " + !level.noCollision(getAABB(currentX, y, currentZ, pokemon)));
                    if (level.noCollision(getAABB(currentX, y, currentZ, pokemon)) && setSpawnPos) {
                        boolean changeModX = level.random.nextBoolean();
                        boolean changeModZ = level.random.nextBoolean();
                        int xMod = i;
                        int zMod = j;
                        if (changeModX) {
                            xMod = -xMod;
                        }

                        if (changeModZ) {
                            zMod = -zMod;
                        }
                        blockPos = new BlockPos(x + xMod, y, z + zMod);
                        spawnPosFound = true;
                        break;
                    }
                }
            }

            if (spawnPosFound) {
                break;
            }
        }

        while (level.getBlockState(new BlockPos(x, y - 1, z)).isAir() && y > level.getMinBuildHeight()) {
            y--;
        }

        while (!level.noCollision(getAABB(x, y, z, pokemon))) {
            y++;
        }
        blockPos = new BlockPos(blockPos.getX(), y, blockPos.getZ());
        if (outbreakPortal.distanceToSqr(x, y, z) > outbreakPortal.getOutbreakPortal().getLeashRangeSq()) return null;
        if (level.noCollision(getAABB(x, y, z, pokemon))) return Vec3.atCenterOf(blockPos);

        return null;
    }

    /**
     * The Cluster Algorithm selects random spawn point within the spawn radius, and tries finding entities on a spot
     * close to the spawn point.
     *
     */

    public static Vec3 clustered(ServerLevel level, Vec3 pos, OutbreakPortalEntity outbreakPortalEntity, PokemonEntity pokemon){
        double spawnRange = outbreakPortalEntity.getOutbreakPortal().getSpawnRange();
        int tries = 0;
        double x = pos.x() + (-1 + 2 * level.random.nextDouble()) * spawnRange;
        double y = pos.y() + level.random.nextInt(3) - 1;
        double z = pos.z() + (-1 + 2 * level.random.nextDouble()) * spawnRange;
        while (!level.noCollision(getAABB(x,y,z, pokemon)) && tries++ < MAX_SPAWN_TRIES) {
            x =  x + (level.random.nextDouble() - level.random.nextDouble()) * spawnRange + 0.5D;
            y = y + level.random.nextInt(3) + 1;
            z = z + (level.random.nextDouble() - level.random.nextDouble()) * spawnRange + 0.5D;
        }

        while (level.getBlockState(new BlockPos(x, y - 1, z)).isAir() && y > level.getMinBuildHeight()) {
            y--;
        }

        while (!level.noCollision(getAABB(x, y, z, pokemon))) {
            y++;
        }

        if (outbreakPortalEntity.distanceToSqr(x, y, z) > outbreakPortalEntity.getOutbreakPortal().getLeashRangeSq()) return null;
        if (level.noCollision(getAABB(x,y,z, pokemon))) return new Vec3(x, y, z);

        return null;
    }




    public static AABB getAABB(double pX, double pY, double pZ, PokemonEntity pokemon) {
        float f = pokemon.getBbWidth() / 2.0F;
        return new AABB(pX - (double)f, pY, pZ - (double)f, pX + (double)f, pY + (double)pokemon.getBbHeight(), pZ + (double)f);
    }
}
