package com.scouter.cobbleoutbreaks.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.scouter.cobbleoutbreaks.entity.OutbreakPortal;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.slf4j.Logger;

import java.util.*;

import static com.scouter.cobbleoutbreaks.CobblemonOutbreaks.prefix;

public class OutbreaksJsonDataManager extends SimpleJsonResourceReloadListener {


    private static final Gson STANDARD_GSON = new Gson();
    private static final Logger LOGGER = LogUtils.getLogger();


    protected static Map<ResourceLocation, OutbreakPortal> data = new HashMap<>();
    protected static Map<ResourceKey<Biome>, Map<ResourceLocation, OutbreakPortal>> biomeData = new HashMap<>();
    protected static List<ResourceLocation> resourceLocationList = new ArrayList<>();
    protected static Map<ResourceKey<Biome>, List<ResourceLocation>> resourceLocationMap = new HashMap();
    private final String folderName;
    public OutbreaksJsonDataManager()
    {
        this(prefix("outbreaks").getPath(), STANDARD_GSON);
    }


    public OutbreaksJsonDataManager(String folderName, Gson gson)
    {
        super(gson, folderName);
        this.folderName = folderName;
    }


    public static Map<ResourceKey<Biome>, Map<ResourceLocation, OutbreakPortal>> getBiomeData() {
        return biomeData;
    }

    public static OutbreakPortal getPortalFromRl(ResourceLocation resourceLocation, OutbreakPortal outbreakPortal) {
        return data.getOrDefault(resourceLocation, outbreakPortal);
    }

    public static Map<ResourceLocation, OutbreakPortal> getData() {
        return data;
    }

    public static Map<ResourceLocation, OutbreakPortal> getRandomPortalFromBiome(Level level, ResourceKey<Biome> biome) {
        Map<ResourceLocation, OutbreakPortal> map = new HashMap<>();
        ResourceLocation rl = getRandomResourceLocationFromBiome(level, biome);
        Map<ResourceLocation, OutbreakPortal> outbreakPortalMap = biomeData.getOrDefault(biome, new HashMap<>());
        OutbreakPortal outbreakPortal = outbreakPortalMap.getOrDefault(rl, null);

        if(outbreakPortal == null){
            outbreakPortalMap = getRandomPortal(level);
            rl = outbreakPortalMap.keySet().stream().toList().get(0);
            outbreakPortal = outbreakPortalMap.values().stream().toList().get(0);
        }


        map.put(rl, outbreakPortal);
        return map;
    }

    public static Map<ResourceLocation, OutbreakPortal> getRandomPortal(Level level) {
        Map<ResourceLocation, OutbreakPortal> map = new HashMap<>();
        ResourceLocation rl = getRandomResourceLocation(level);
        OutbreakPortal outbreakPortal = data.get(rl);
        map.put(rl, outbreakPortal);
        return map;
    }

    private static ResourceLocation getRandomResourceLocationFromBiome(Level level, ResourceKey<Biome> biome) {
        List<ResourceLocation> resourceLocations = resourceLocationMap.getOrDefault(biome, Collections.emptyList());
        if (resourceLocations.isEmpty()) {
            return getRandomResourceLocation(level);
        }
        return resourceLocations.get(level.random.nextInt(resourceLocations.size()));
    }

    private static ResourceLocation getRandomResourceLocation(Level level) {
        return resourceLocationList.get(level.random.nextInt(resourceLocationList.size()));
    }

    public static void populateMap(ServerLevel level) {
        Map<ResourceKey<Biome>, Map<ResourceLocation, OutbreakPortal>> newBiomeData = new HashMap<>();
        Map<ResourceKey<Biome>, List<ResourceLocation>> resourceLocationBiomeMap = new HashMap<>();
        for (OutbreakPortal portal : data.values()) {
            List<ResourceLocation> tagsRL = portal.getSpawnBiomeTags();
            List<ResourceLocation> biomesRL = portal.getSpawnBiome();
            for (ResourceLocation tag : tagsRL) {
                TagKey<Biome> biomeTagKey = TagKey.create(Registry.BIOME_REGISTRY, tag);
                level.registryAccess().registry(Registry.BIOME_REGISTRY).ifPresent(reg -> {
                    Iterable<Holder<Biome>> biomeHolder = reg.getTagOrEmpty(biomeTagKey);
                    for(Holder<Biome> biome : biomeHolder){
                        ResourceKey<Biome> biomeResourceKey = biome.unwrapKey().get();
                        Map<ResourceLocation, OutbreakPortal> mapToPut = newBiomeData.computeIfAbsent(biomeResourceKey, k -> new HashMap<>());
                        mapToPut.put(portal.getJsonLocation(), portal);
                        newBiomeData.put(biomeResourceKey, mapToPut);

                        List<ResourceLocation> resourceLocations = resourceLocationBiomeMap.getOrDefault(biomeResourceKey, new ArrayList<>());
                        resourceLocations.add(portal.getJsonLocation());
                        resourceLocationBiomeMap.put(biomeResourceKey, resourceLocations);
                    }

                    if (!biomeHolder.iterator().hasNext()) {
                        LOGGER.error("Tag for {} does not have any biomes!", biomeTagKey);
                        LOGGER.error("Outbreak for {} might not have any biomes assigned!", portal.getJsonLocation());
                    }
                });
            }

            for (ResourceLocation biome : biomesRL) {
                ResourceKey<Biome> biomeResourceKey = null;
                try {
                    biomeResourceKey = ResourceKey.create(Registry.BIOME_REGISTRY, biome);
                } catch (Exception e) {
                    LOGGER.error("Could not find biome {} in portal for {} due to {}", biome, portal.getJsonLocation(), e);
                }
                if (biomeResourceKey == null) {
                    LOGGER.error("Could not find biome {} in portal for {}, skipping!", biome, portal.getJsonLocation());
                    continue;
                }

                List<ResourceLocation> resourceLocations = resourceLocationBiomeMap.getOrDefault(biomeResourceKey, new ArrayList<>());
                resourceLocations.add(portal.getJsonLocation());
                resourceLocationBiomeMap.put(biomeResourceKey, resourceLocations);

                Map<ResourceLocation, OutbreakPortal> mapToPut = newBiomeData.computeIfAbsent(biomeResourceKey, k -> new HashMap<>());
                mapToPut.put(portal.getJsonLocation(), portal);
                newBiomeData.put(biomeResourceKey, mapToPut);
            }

            int minLevel = portal.getMinPokemonLevel();
            int maxLevel = portal.getMaxPokemonLevel();

            if(minLevel > maxLevel){
                LOGGER.error("Portal with {}, has a bigger min_pokemon_level than max_pokemon_level", portal.getJsonLocation());
            }
        }
        LOGGER.info("Registered {} biomes with pokemon!", newBiomeData.keySet().size());
        biomeData.putAll(newBiomeData);
        resourceLocationMap.putAll(resourceLocationBiomeMap);
        newBiomeData.clear();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        LOGGER.info("Beginning loading of data for data loader: {}", this.folderName);

        Map<ResourceLocation, OutbreakPortal> newMap = new HashMap<>();
        List<ResourceLocation> newResourceLocationList = new ArrayList<>();
        Map<ResourceKey<Biome>, List<ResourceLocation>> resourceLocationBiomeMap = new HashMap<>();
        Map<ResourceKey<Biome>, Map<ResourceLocation, OutbreakPortal>> newBiomeData = new HashMap<>();
        data.clear();
        biomeData.clear();
        resourceLocationMap.clear();
        resourceLocationList.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
            ResourceLocation key = entry.getKey();
            JsonElement element = entry.getValue();

            // if we fail to parse json, log an error and continue
            // if we succeeded, add the resulting T to the ma
            OutbreakPortal.CODEC.decode(JsonOps.INSTANCE, element)
                    .get()
                    .ifLeft(result -> {
                        OutbreakPortal portal = result.getFirst();
                        newMap.put(key, portal);
                        portal.setJsonLocation(key);
                        List<ResourceLocation> spawnBiome = portal.getSpawnBiome();
                        spawnBiome.forEach(biome -> {
                            ResourceKey<Biome> biomeResourceKey = null;
                            try {
                                biomeResourceKey = ResourceKey.create(Registry.BIOME_REGISTRY, biome);
                            } catch (Exception e) {
                                LOGGER.error("Could not find biome {} in {} due to ", biome, key, e);
                            }
                            if (biomeResourceKey == null) {
                                LOGGER.error("Could not find biome {} in {}", biome, key);
                            }

                            List<ResourceLocation> resourceLocations = resourceLocationBiomeMap.getOrDefault(biomeResourceKey, new ArrayList<>());
                            resourceLocations.add(key);
                            resourceLocationBiomeMap.put(biomeResourceKey, resourceLocations);
                        });
                        newResourceLocationList.add(key);
                    })
                    .ifRight(partial -> LOGGER.error("Failed to parse data json for {} due to: {}", key, partial.message()));

        }
        this.resourceLocationList = newResourceLocationList;
        this.data = newMap;
        LOGGER.info("Data loader for {} loaded {} jsons", this.folderName, this.data.size());
    }
}
