package com.scouter.cobbleoutbreaks.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OutbreakPlayerManager extends SavedData {

    // Map to store the remaining time for each player
    private Map<UUID, Integer> timeLeftMap = new ConcurrentHashMap<>();

    public static OutbreakPlayerManager get(Level level){
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        // Get the OutbreakPlayerManager if it already exists. Otherwise, create a new one.
        return storage.computeIfAbsent(OutbreakPlayerManager::new, OutbreakPlayerManager::new, "outbreakplayermanager");
    }

    public boolean containsUUID(UUID player){
        return timeLeftMap.containsKey(player);
    }

    public int getTimeLeft(UUID player){
        return timeLeftMap.get(player);
    }

    public void setTimeLeft(UUID player, int time){
        timeLeftMap.put(player, time);
        setDirty();
    }

    public OutbreakPlayerManager(){
    }

    public OutbreakPlayerManager(CompoundTag nbt) {
        // Load player data from the provided CompoundTag
        ListTag playerList = nbt.getList("timeLeftMap", 10);
        for (int i = 0; i < playerList.size(); i++) {
            CompoundTag playerEntry = playerList.getCompound(i);
            UUID playerUUID = playerEntry.getUUID("playerUUID");
            int timeLeft = playerEntry.getInt("time");
            timeLeftMap.put(playerUUID, timeLeft);
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        // Save player data to the provided CompoundTag
        ListTag playerList = new ListTag();
        for (Map.Entry<UUID, Integer> entry : timeLeftMap.entrySet()) {
            CompoundTag playerEntry = new CompoundTag();
            playerEntry.putUUID("playerUUID", entry.getKey());
            playerEntry.putInt("time", entry.getValue());
            playerList.add(playerEntry);
        }
        nbt.put("timeLeftMap", playerList);
        return nbt;
    }

}
