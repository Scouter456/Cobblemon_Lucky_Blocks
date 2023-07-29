package com.scouter.cobbleoutbreaks.entity;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import static com.scouter.cobbleoutbreaks.CobblemonOutbreaks.prefix;

public class SpawnLevelAlgorithms {
    public static final Logger LOGGER = LogUtils.getLogger();
    public interface SpawnLevelAlgorithm
    {

        int getLevel(ServerLevel level, Player player, OutbreakPortalEntity outbreakPortal);
    }


    public static final BiMap<ResourceLocation, SpawnLevelAlgorithm> NAMED_ALGORITHMS = HashBiMap.create();
    static {
        NAMED_ALGORITHMS.put(prefix("random"), SpawnLevelAlgorithms::random);
        NAMED_ALGORITHMS.put(prefix("scaled"), SpawnLevelAlgorithms::scaled);
        NAMED_ALGORITHMS.put(prefix("min_max"), SpawnLevelAlgorithms::minMax);
    }

    public static final Codec<SpawnLevelAlgorithm> CODEC = ExtraCodecs.stringResolverCodec(sa -> NAMED_ALGORITHMS.inverse().get(sa).toString(), key -> NAMED_ALGORITHMS.get(new ResourceLocation(key)));


    public static int random(ServerLevel level, Player player, OutbreakPortalEntity outbreakPortal) {
        return level.random.nextInt(1,100);
    }

    public static int scaled(ServerLevel level, Player player, OutbreakPortalEntity outbreakPortal) {
        if(player == null) return random(level, player, outbreakPortal);
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty((ServerPlayer) player);

        int totalLevel = 0;
        int count = 0;
        for (Pokemon pokemon : party) {
            totalLevel += pokemon.getLevel();
            count++;
        }

        if(count == 0 || totalLevel == 0){
            return random(level, player, outbreakPortal);
        }
        int scaledLevel = totalLevel/count;
        scaledLevel += level.random.nextInt(-5, 5);

        if(scaledLevel <= 0){
            scaledLevel = 5;
        }
        if(scaledLevel > 100){
            scaledLevel = 100;
        }
        return scaledLevel;
    }

    public static int minMax(ServerLevel level, Player player, OutbreakPortalEntity outbreakPortal) {
        int maxPokemonLevel = outbreakPortal.getOutbreakPortal().getMaxPokemonLevel();
        int minPokemonLevel = outbreakPortal.getOutbreakPortal().getMinPokemonLevel();

        if(minPokemonLevel > maxPokemonLevel)
        {
            LOGGER.warn("Portal with {}, has a bigger min_pokemon_level than max_pokemon_level, setting min to 1", outbreakPortal.getResourceLocation());
            minPokemonLevel = 1;
        }
        if(minPokemonLevel == maxPokemonLevel)
        {
            LOGGER.warn("Portal with {}, has the same value for min_pokemon_level and max_pokemon_level, adding 1 to max_pokemon_level", outbreakPortal.getResourceLocation());
            maxPokemonLevel += 1;
        }


        return level.random.nextInt(minPokemonLevel, maxPokemonLevel);
    }
}
