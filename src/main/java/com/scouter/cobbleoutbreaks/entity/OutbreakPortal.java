package com.scouter.cobbleoutbreaks.entity;

import com.cobblemon.mod.common.api.entity.Despawner;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobbleoutbreaks.config.CobblemonOutbreaksConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.scouter.cobbleoutbreaks.CobblemonOutbreaks.prefix;

public class OutbreakPortal {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Codec<OutbreakPortal> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.STRING.fieldOf("species").forGetter(t -> t.species),
                    Codec.INT.fieldOf("waves").forGetter(w -> w.waves),
                    Codec.intRange(1, 64).fieldOf("spawns_per_wave").forGetter(s -> s.spawnsPerWave),
                    BuiltInRegistries.ITEM.byNameCodec().listOf().optionalFieldOf("rewards", Collections.emptyList()).forGetter(i -> i.rewards),
                    Codec.doubleRange(1,10000000).optionalFieldOf("shiny_chance",1024D).forGetter(r -> r.shinyChance),
                    Codec.INT.optionalFieldOf("experience_reward", 0).forGetter(e -> e.experience),
                    Codec.doubleRange(15D,40D).fieldOf("spawn_range").forGetter(r -> r.spawnRange),
                    Codec.doubleRange(15D,40D).optionalFieldOf("leash_range", 32D).forGetter(g -> g.leashRange),
                    SpawnAlgorithms.CODEC.optionalFieldOf("spawn_algorithm", SpawnAlgorithms.NAMED_ALGORITHMS.get(prefix("clustered"))).forGetter(g -> g.spawnAlgo),
                    SpawnLevelAlgorithms.CODEC.optionalFieldOf("level_algorithm", SpawnLevelAlgorithms.NAMED_ALGORITHMS.get(prefix("scaled"))).forGetter(g -> g.spawnLevelAlgo),
                    Codec.INT.optionalFieldOf("gate_timer", 36000).forGetter(t -> t.gateTimer),
                    Codec.intRange(1, 99).optionalFieldOf("min_pokemon_level", 100).forGetter(s -> s.minPokemonLevel),
                    Codec.intRange(2, 100).optionalFieldOf("max_pokemon_level", 100).forGetter(s -> s.maxPokemonLevel),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("biome", Collections.emptyList()).forGetter(t -> t.biomeTags)
            )
            .apply(inst, OutbreakPortal::new)
    );

    protected final String species;
    protected final List<Item> rewards;
    protected int experience;
    protected int waves;
    protected int spawnsPerWave;
    protected int gateTimer;
    protected int maxPokemonLevel;
    protected int minPokemonLevel;
    protected double spawnRange;
    protected double leashRange;

    protected double shinyChance;

    protected final SpawnAlgorithms.SpawnAlgorithm spawnAlgo;
    protected final SpawnLevelAlgorithms.SpawnLevelAlgorithm spawnLevelAlgo;
    protected final List<ResourceLocation> spawnBiome;
    protected final List<ResourceLocation> spawnBiomeTags;
    protected final List<ExtraCodecs.TagOrElementLocation> biomeTags;

    private ResourceLocation jsonLocation;

    /**
     * Creates an OutbreakPortal instance.
     *
     * @param species        The species of the entities to be spawned in this wave.
     * @param waves          The total number of waves in the gateway.
     * @param spawnsPerWave  The number of entities to be spawned in each wave.
     * @param rewards        The list of rewards that will be granted at the end of this wave.
     * @param shinyChance    The chance of a spawned entity being shiny.
     * @param experience     The experience value associated with the wave.
     * @param spawnRange     The range within which the entities will be spawned.
     * @param leashRange     The maximum range within which the entities can move from their spawn position.
     * @param spawnAlgo      The algorithm used to determine the spawn position for the entities.
     * @param gateTimer      The time limit for completing this wave.
     * @param maxPokemonLevel The maximum level of the spawned entities.
     * @param spawnBiome     The biomes the entity can spawn in.
     */
    public OutbreakPortal(String species, int waves, int spawnsPerWave, List<Item> rewards,
                          double shinyChance, int experience, double spawnRange, double leashRange,
                          SpawnAlgorithms.SpawnAlgorithm spawnAlgo, SpawnLevelAlgorithms.SpawnLevelAlgorithm spawnLevelAlgo,
                          int gateTimer, int minPokemonLevel, int maxPokemonLevel, List<ExtraCodecs.TagOrElementLocation> spawnBiome) {
        this.species = species;
        this.rewards = rewards;
        this.experience = experience;
        this.waves = waves;
        this.spawnsPerWave = spawnsPerWave;
        this.spawnRange = spawnRange;
        this.leashRange = leashRange;
        this.spawnAlgo = spawnAlgo;
        this.spawnLevelAlgo = spawnLevelAlgo;
        this.shinyChance = shinyChance;
        this.gateTimer = gateTimer;
        this.minPokemonLevel = minPokemonLevel;
        this.maxPokemonLevel = maxPokemonLevel;
        List<ResourceLocation> spawnBiomeTags = new ArrayList<>();
        List<ResourceLocation> spawnBiomes = new ArrayList<>();
        for(ExtraCodecs.TagOrElementLocation tagOrElementLocation : spawnBiome){
            if(tagOrElementLocation.tag()){
                spawnBiomeTags.add(tagOrElementLocation.id());
            } else {
                spawnBiomes.add(tagOrElementLocation.id());
            }
        }

        this.spawnBiome = spawnBiomes;
        this.spawnBiomeTags = spawnBiomeTags;

        this.biomeTags = spawnBiome;
    }


    public void setJsonLocation(ResourceLocation location){
        this.jsonLocation = location;
    }


    public ResourceLocation getJsonLocation() {
        try {
            return jsonLocation;
        } catch (Exception e){
            LOGGER.error("Could not find jsonLocation due to {}", e);
        }
        return new ResourceLocation("");
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

    public SpawnLevelAlgorithms.SpawnLevelAlgorithm getSpawnLevelAlgo() {
        return this.spawnLevelAlgo;
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

    public int getMinPokemonLevel() {
        return this.minPokemonLevel;
    }

    public List<ResourceLocation> getSpawnBiomeTags() {
        return this.spawnBiomeTags;
    }

    public List<ResourceLocation> getSpawnBiome() {
        return this.spawnBiome;
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
    protected Despawner despawner = new CustomDespawner();

    public List<Pokemon> spawnWave(ServerLevel level, Vec3 pos, OutbreakPortalEntity outbreakPortalEntity, String species) {
        List<Pokemon> spawned = new ArrayList<>();
        int spawnCount = outbreakPortalEntity.getOutbreakPortal().getSpawnCount();
        for (int i = 0; i < spawnCount; i++) {
            // If the species is something it can't find, it will put out a random Pokémon
            PokemonProperties pokemonProp = PokemonProperties.Companion.parse("species=" + species, " ", "=");
            Player player = level.getPlayerByUUID(outbreakPortalEntity.getOwnerUUID());
            int spawnLevel = outbreakPortalEntity.getOutbreakPortal().getSpawnLevelAlgo().getLevel(level, player, outbreakPortalEntity);

            pokemonProp.setLevel(spawnLevel);
            // We also create a Pokémon entity to get the bounding boxes since we couldn't find another correct way to do it.



            if(pokemonProp.getSpecies() == null){
                for(int j = 0; j < 4; j++){
                    LOGGER.error("Species from {} is null, the species: {} is probably spelled incorrectly", outbreakPortalEntity.getResourceLocation(), species);
                }
                outbreakPortalEntity.completeOutBreak(false);
                return Collections.emptyList();
            }
            // If the species is something it can't find, it will put out a random Pokémon. We try to catch that here,
            // this can be due to an error in the JSON.
            //if (!pokemonProp.getSpecies().toString().equals(outbreakPortalEntity.getOutbreakPortal().getSpecies())) {
            //    outbreakPortalEntity.entityNotSimilar(pokemonProp.create(), outbreakPortalEntity);
            //    return Collections.emptyList();
            //}

            double shinyChance = 1 / outbreakPortalEntity.getOutbreakPortal().getShinyChance();
            if (level.random.nextDouble() < shinyChance) {
                pokemonProp.setShiny(true);
                level.playSound(null, outbreakPortalEntity.getX(), outbreakPortalEntity.getY(), outbreakPortalEntity.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 1.5F, 1);
            }

            PokemonEntity pokemonEntity = pokemonProp.createEntity(level);

            pokemonEntity.setDespawner(despawner);
            pokemonEntity.setPersistenceRequired();

            Pokemon pokemon1 = pokemonEntity.getPokemon();

            // We log that spawning failed either due to the Pokémon being null (unlikely) or the spawn position being null.
            Vec3 spawnPos = outbreakPortalEntity.getOutbreakPortal().getSpawnAlgo().spawn(level, pos, outbreakPortalEntity, pokemonEntity);
            if (spawnPos == null || pokemon1 == null) {
                //Decided not to add this since it will spawn the logs otherwise.
                //LOGGER.info("Spawning for Pokémon {} failed, due to spawnPos {} or Pokémon {}", pokemon1.getSpecies(), spawnPos, pokemon1.getSpecies());
                continue;
            }

            pokemon1.sendOut(level, spawnPos, (m) -> null);
            if (CobblemonOutbreaksConfig.OUTBREAK_PORTAL_SPAWN_SOUND.get()) {
                float volume = CobblemonOutbreaksConfig.OUTBREAK_PORTAL_POKEMON_SPAWN_VOLUME.get();
                level.playSound(null, spawnPos.x(), spawnPos.y(), spawnPos.z(), SoundEvents.PORTAL_TRAVEL, SoundSource.HOSTILE, volume, 1);
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
