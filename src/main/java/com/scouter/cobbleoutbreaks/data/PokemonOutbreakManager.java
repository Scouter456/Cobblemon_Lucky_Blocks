package com.scouter.cobbleoutbreaks.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PokemonOutbreakManager extends SavedData {

    // Map to store the ownership information of Pokemon
    private Map<UUID, UUID> pokemonOwnershipMap = new HashMap<>();
    private Map<UUID, UUID> pokemonOwnerShipMapTemp = new HashMap<>();
    public static PokemonOutbreakManager get(Level level){
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        // Get the PokemonOutbreakManager if it already exists. Otherwise, create a new one.
        return storage.computeIfAbsent(PokemonOutbreakManager::new, PokemonOutbreakManager::new, "pokemonoutbreakmanager");
    }

    public void clearMap(){
        pokemonOwnershipMap.clear();
        setDirty();
        return;
    }

    public boolean containsUUID(UUID pokemon){
        return pokemonOwnershipMap.containsKey(pokemon);
    }

    public UUID getOwnerUUID(UUID pokemonUUID){
        return pokemonOwnershipMap.get(pokemonUUID);
    }

    public void addPokemonWOwner(UUID pokemonUUID, UUID ownerUUID){
        pokemonOwnershipMap.put(pokemonUUID, ownerUUID);
        setDirty();
    }

    public void removePokemonUUID(UUID pokemonUUID){
        pokemonOwnershipMap.remove(pokemonUUID);
        setDirty();
    }

    public void clearTempMap(){
        pokemonOwnerShipMapTemp.clear();
        setDirty();
        return;
    }

    public boolean containsUUIDTemp(UUID pokemon){
        return pokemonOwnerShipMapTemp.containsKey(pokemon);
    }

    public UUID getOwnerUUIDTemp(UUID pokemonUUID){
        return pokemonOwnerShipMapTemp.get(pokemonUUID);
    }

    public void addPokemonWOwnerTemp(UUID pokemonUUID, UUID ownerUUID){
        pokemonOwnerShipMapTemp.put(pokemonUUID, ownerUUID);
        setDirty();
    }

    public void removePokemonUUIDTemp(UUID pokemonUUID){
        pokemonOwnerShipMapTemp.remove(pokemonUUID);
        setDirty();
    }

    public PokemonOutbreakManager(){
    }

    public PokemonOutbreakManager(CompoundTag nbt) {
        // Load Pokemon ownership data from the provided CompoundTag
        ListTag pokemonList = nbt.getList("pokemonList", 10);
        for (int i = 0; i < pokemonList.size(); i++) {
            CompoundTag pokemonEntry = pokemonList.getCompound(i);
            UUID pokemonUUID = pokemonEntry.getUUID("pokemonUUID");
            UUID ownerUUID = pokemonEntry.getUUID("ownerUUID");
            pokemonOwnershipMap.put(pokemonUUID, ownerUUID);
        }

        ListTag pokemonListTemp = nbt.getList("pokemonListTemp", 10);
        for (int i = 0; i < pokemonListTemp.size(); i++) {
            CompoundTag pokemonEntry = pokemonListTemp.getCompound(i);
            UUID pokemonUUID = pokemonEntry.getUUID("pokemonUUID");
            UUID ownerUUID = pokemonEntry.getUUID("ownerUUID");
            pokemonOwnerShipMapTemp.put(pokemonUUID, ownerUUID);
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        // Save Pokemon ownership data to the provided CompoundTag
        ListTag pokemonList = new ListTag();
        for (Map.Entry<UUID, UUID> entry : pokemonOwnershipMap.entrySet()) {
            CompoundTag pokemonEntry = new CompoundTag();
            pokemonEntry.putUUID("pokemonUUID", entry.getKey());
            pokemonEntry.putUUID("ownerUUID", entry.getValue());
            pokemonList.add(pokemonEntry);
        }
        nbt.put("pokemonList", pokemonList);

        ListTag pokemonListTemp = new ListTag();
        for (Map.Entry<UUID, UUID> entry : pokemonOwnerShipMapTemp.entrySet()) {
            CompoundTag pokemonEntryTemp = new CompoundTag();
            pokemonEntryTemp.putUUID("pokemonUUID", entry.getKey());
            pokemonEntryTemp.putUUID("ownerUUID", entry.getValue());
            pokemonListTemp.add(pokemonEntryTemp);
        }

        nbt.put("pokemonListTemp", pokemonListTemp);

        return nbt;
    }

}
