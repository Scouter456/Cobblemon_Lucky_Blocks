package com.scouter.cobblelucky.entity;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblelucky.config.CobblemonOutbreaksConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.scouter.cobblelucky.CobblemonOutbreaks.prefix;

public class OutbreakPortal {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Codec<OutbreakPortal> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.STRING.fieldOf("species").forGetter(t -> t.species),
                    Codec.INT.fieldOf("waves").forGetter(w -> w.waves),
                    Codec.intRange(1, 64).fieldOf("spawns_per_wave").forGetter(s -> s.spawnsPerWave),
                    ForgeRegistries.ITEMS.getCodec().listOf().optionalFieldOf("rewards", Collections.emptyList()).forGetter(i -> i.rewards),
                    Codec.intRange(20,72).optionalFieldOf("min_spawn_radius", 48).forGetter(e -> e.minSpawnRadius),
                    Codec.intRange(72, 256).optionalFieldOf("max_spawn_radius", 128).forGetter(e -> e.maxSpawnRadius),
                    Codec.doubleRange(1,10000000).optionalFieldOf("shiny_chance",1024D).forGetter(r -> r.shinyChance),
                    Codec.INT.optionalFieldOf("experience_reward", 0).forGetter(e -> e.experience),
                    Codec.doubleRange(15D,40D).fieldOf("spawn_range").forGetter(r -> r.spawnRange),
                    Codec.doubleRange(15D,40D).optionalFieldOf("leash_range", 32D).forGetter(g -> g.leashRange),
                    SpawnAlgorithms.CODEC.optionalFieldOf("spawn_algorithm", SpawnAlgorithms.NAMED_ALGORITHMS.get(prefix("clustered"))).forGetter(g -> g.spawnAlgo),
                    Codec.INT.optionalFieldOf("gate_timer", 36000).forGetter(t -> t.gateTimer),
                    Codec.intRange(2, 100).optionalFieldOf("max_pokemon_level", 100).forGetter(s -> s.maxPokemonLevel)

                    )
            .apply(inst, OutbreakPortal::new)
    );

    protected final String species;
    protected final List<Item> rewards;
    protected int experience;
    protected int waves;
    protected int spawnsPerWave;
    protected int minSpawnRadius;
    protected int maxSpawnRadius;
    protected int gateTimer;
    protected int maxPokemonLevel;
    protected double spawnRange;
    protected double leashRange;

    protected double shinyChance;

    protected final SpawnAlgorithms.SpawnAlgorithm spawnAlgo;

    /**
     * Creates an OutbreakPortal instance.
     *
     * @param species        The species of the entities to be spawned in this wave.
     * @param waves          The total number of waves in the gateway.
     * @param spawnsPerWave  The number of entities to be spawned in each wave.
     * @param rewards        The list of rewards that will be granted at the end of this wave.
     * @param minSpawnRadius The minimum spawn radius for the entities.
     * @param maxSpawnRadius The maximum spawn radius for the entities.
     * @param shinyChance    The chance of a spawned entity being shiny.
     * @param experience     The experience value associated with the wave.
     * @param spawnRange     The range within which the entities will be spawned.
     * @param leashRange     The maximum range within which the entities can move from their spawn position.
     * @param spawnAlgo      The algorithm used to determine the spawn position for the entities.
     * @param gateTimer      The time limit for completing this wave.
     * @param maxPokemonLevel The maximum level of the spawned entities.
     */
    public OutbreakPortal(String species, int waves, int spawnsPerWave, List<Item> rewards,
                          int minSpawnRadius, int maxSpawnRadius, double shinyChance,
                          int experience, double spawnRange, double leashRange,
                          SpawnAlgorithms.SpawnAlgorithm spawnAlgo, int gateTimer, int maxPokemonLevel) {
        this.species = species;
        this.rewards = rewards;
        this.experience = experience;
        this.waves = waves;
        this.spawnsPerWave = spawnsPerWave;
        this.spawnRange = spawnRange;
        this.leashRange = leashRange;
        this.spawnAlgo = spawnAlgo;
        this.minSpawnRadius = minSpawnRadius;
        this.maxSpawnRadius = maxSpawnRadius;
        this.shinyChance = shinyChance;
        this.gateTimer = gateTimer;
        this.maxPokemonLevel = maxPokemonLevel;
    }

    public List<Item> getRewards() {
        return this.rewards;
    }

    public String getSpecies() {
        return this.species;
    }

    public int getWaves() {
        return this.waves;
    }

    public int getSpawnCount() {
        return this.spawnsPerWave;
    }

    public double getSpawnRange() {
        return this.spawnRange;
    }

    public double getLeashRangeSq() {
        return this.leashRange * this.leashRange;
    }

    public SpawnAlgorithms.SpawnAlgorithm getSpawnAlgo() {
        return this.spawnAlgo;
    }

    public int getMinSpawnRadius() {
        return this.minSpawnRadius;
    }

    public int getMaxSpawnRadius() {
        return this.maxSpawnRadius;
    }

    public double getExperience() {
        return this.experience;
    }


    public double getShinyChance() {
        return this.shinyChance;
    }

    public double getMaxGateTime() {
        return this.gateTimer;
    }
    public int getMaxPokemonLevel() {
        return this.maxPokemonLevel;
    }
    /**
     * Spawns a wave of Pokémon.
     *
     * @param level The server level where the Pokémon will be spawned.
     * @param pos The origin position from where the Pokémon spawns will be determined.
     * @param outbreakPortalEntity The outbreak portal entity associated with the wave.
     * @param species The species of Pokémon to spawn.
     * @return The list of spawned Pokémon.
     */
    public List<Pokemon> spawnWave(ServerLevel level, Vec3 pos, OutbreakPortalEntity outbreakPortalEntity, String species) {
        List<Pokemon> spawned = new ArrayList<>();
        int spawnCount = outbreakPortalEntity.getOutbreakPortal().getSpawnCount();
        for (int i = 0; i < spawnCount; i++) {
            // If the species is something it can't find, it will put out a random Pokémon
            PokemonProperties pokemonProp = PokemonProperties.Companion.parse("species=" + species, " ", "=");
            int maxLevel = outbreakPortalEntity.getOutbreakPortal().getMaxPokemonLevel();
            pokemonProp.setLevel(level.random.nextInt(1, maxLevel));

            // We also create a Pokémon entity to get the bounding boxes since we couldn't find another correct way to do it.
            PokemonEntity pokemonEntity = pokemonProp.createEntity(level);
            Pokemon pokemon1 = pokemonProp.create();

            // If the species is something it can't find, it will put out a random Pokémon. We try to catch that here,
            // this can be due to an error in the JSON.
            if (!pokemon1.getSpecies().toString().equals(outbreakPortalEntity.getOutbreakPortal().getSpecies())) {
                outbreakPortalEntity.entityNotSimilar(pokemon1, outbreakPortalEntity);
                return Collections.emptyList();
            }

            double shinyChance = 1 / outbreakPortalEntity.getOutbreakPortal().getShinyChance();
            if (level.random.nextDouble() < shinyChance) {
                pokemon1.setShiny(true);
                level.playSound(null, outbreakPortalEntity.getX(), outbreakPortalEntity.getY(), outbreakPortalEntity.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 1.5F, 1);
            }

            // We log that spawning failed either due to the Pokémon being null (unlikely) or the spawn position being null.
            Vec3 spawnPos = outbreakPortalEntity.getOutbreakPortal().getSpawnAlgo().spawn(level, pos, outbreakPortalEntity, pokemonEntity);
            if (spawnPos == null || pokemon1 == null) {
                //Decided not to add this since it will spawn the logs otherwise.
                //LOGGER.info("Spawning for Pokémon {} failed, due to spawnPos {} or Pokémon {}", pokemon1.getSpecies(), spawnPos, pokemon1.getSpecies());
                continue;
            }

            pokemon1.sendOut(level, spawnPos, (m) -> null);
            if (CobblemonOutbreaksConfig.OUTBREAK_PORTAL_SPAWN_SOUND.get()) {
                level.playSound(null, spawnPos.x(), spawnPos.y(), spawnPos.z(), SoundEvents.PORTAL_TRAVEL, SoundSource.HOSTILE, 0.5F, 1);
            }

            spawned.add(pokemon1);
        }

        return spawned;
    }

    public List<ItemStack> spawnRewards(ServerLevel level, OutbreakPortalEntity gate) {
        List<ItemStack> stacks = new ArrayList<>();
        gate.getOutbreakPortal().getRewards().forEach(r -> stacks.add(new ItemStack(r)));
        stacks.forEach(s -> level.addFreshEntity(new ItemEntity(level,gate.getX(), gate.getY(), gate.getZ(),s)));
        return stacks;
    }
}
