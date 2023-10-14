package com.scouter.cobbleoutbreaks.entity;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.logging.LogUtils;
import com.scouter.cobbleoutbreaks.config.CobblemonOutbreaksConfig;
import com.scouter.cobbleoutbreaks.data.OutbreakManager;
import com.scouter.cobbleoutbreaks.data.OutbreaksJsonDataManager;
import com.scouter.cobbleoutbreaks.data.PokemonOutbreakManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

import static com.scouter.cobbleoutbreaks.CobblemonOutbreaks.prefix;

public class OutbreakPortalEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private UUID ownerUUID;
    private UUID outbreakUUID;
    protected final Set<UUID> currentOutbreakWaveEntities = new HashSet<>();
    private PokemonOutbreakManager outbreakManager;
    private OutbreakPortal portal;
    private ResourceLocation resourceLocation;
    private Map<ResourceLocation, OutbreakPortal> map;
    private BlockPos blockPosition = new BlockPos(0,0,0);
    private int tickCount =0;
    private boolean hasSpawnedOne;
    private int ticksActive;
    private int wave;
    private Level level;
    private boolean checkLevel = false;

    public OutbreakPortalEntity(Level level, Player placer, ResourceLocation resourceLocation, BlockPos pos){
        if(OutbreaksJsonDataManager.getBiomeData().isEmpty()){
            if(!level.isClientSide)
                OutbreaksJsonDataManager.populateMap((ServerLevel) level);
        }
        this.blockPosition = pos;
        this.level = level;
        populatePortalFromCommand(resourceLocation);
        sendMessageToPlayer(placer);
        this.ownerUUID = placer.getUUID();
        this.outbreakUUID = UUID.randomUUID();
        OutbreakManager outbreakManager1 = OutbreakManager.get(level);
        outbreakManager1.addPortal(outbreakUUID, this);
    }

    public OutbreakPortalEntity(Level level, Player placer, BlockPos position) {
        if(OutbreaksJsonDataManager.getBiomeData().isEmpty()){
            if(!level.isClientSide)
                OutbreaksJsonDataManager.populateMap((ServerLevel) level);
        }
        this.level = level;
        this.outbreakUUID = UUID.randomUUID();
        this.blockPosition = position;
        populatePortal(level);
        sendMessageToPlayer(placer);
        outbreakSpawnSound();
        this.ownerUUID = placer.getUUID();
        OutbreakManager outbreakManager1 = OutbreakManager.get(level);
        outbreakManager1.addPortal(outbreakUUID, this);
    }


    public void populatePortal(Level level){
        if (!level.isClientSide) {

            if(CobblemonOutbreaksConfig.BIOME_SPECIFIC_SPAWNS.get()){
                this.map = OutbreaksJsonDataManager.getRandomPortalFromBiome(level, level.getBiome(this.blockPosition).unwrapKey().get());
            } else{
                this.map = OutbreaksJsonDataManager.getRandomPortal(level);
            }

            this.resourceLocation = map.keySet().stream().toList().get(0);
            this.portal = map.get(resourceLocation);
            this.outbreakManager = PokemonOutbreakManager.get((ServerLevel) level);
        }
    }

    public void populatePortalFromCommand(ResourceLocation resourceLocation){
        if (!level.isClientSide) {
            this.resourceLocation = resourceLocation;
                this.portal = OutbreaksJsonDataManager.getPortalFromRl(resourceLocation, null);
            this.outbreakManager = PokemonOutbreakManager.get((ServerLevel) level);
        }
    }


    public void sendMessageToPlayer(Player player){
        if(CobblemonOutbreaksConfig.SEND_PORTAL_SPAWN_MESSAGE.get()) {
            if(CobblemonOutbreaksConfig.BIOME_SPECIFIC_SPAWNS_DEBUG.get()) {
                MutableComponent pokemonMessage = Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                MutableComponent biomeMessage = Component.literal(level.getBiome(this.blockPosition).unwrapKey().get().location().toString().split(":")[1]).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.portal_biome_specific_spawn_debug", biomeMessage,this.blockPosition,pokemonMessage).withStyle(ChatFormatting.GREEN);
                player.sendSystemMessage(outBreakMessage);
            } else if(!CobblemonOutbreaksConfig.BIOME_SPECIFIC_SPAWNS_DEBUG.get()){
                MutableComponent pokemonMessage =  Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                MutableComponent blockPos =  Component.literal(String.valueOf(this.blockPosition)).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.portal_spawn_near_blockpos", blockPos, pokemonMessage).withStyle(ChatFormatting.DARK_AQUA);

                player.sendSystemMessage(outBreakMessage);
            } else {
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.portal_spawn_near").withStyle(ChatFormatting.DARK_AQUA);
                MutableComponent pokemonMessage =  Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                player.sendSystemMessage(outBreakMessage.append(pokemonMessage));
            }

        }
    }

    public void outbreakSpawnSound(){
        if(CobblemonOutbreaksConfig.OUTBREAK_PORTAL_SPAWN_SOUND.get()) {
            float volume = CobblemonOutbreaksConfig.OUTBREAK_PORTAL_SPAWN_VOLUME.get();
            level.playSound(null, this.blockPosition, SoundEvents.PORTAL_TRIGGER, SoundSource.AMBIENT, volume, 1);
        }
    }

    public void tick() {
        tickCount++;
        if(!checkLevel){
            pokemonStillValidFirstTime();
        }
        if (!level.isClientSide) {
            boolean containsPokemon = pokemonStillValid();
            if (getWave() >= this.getOutbreakPortal().getWaves() && !containsPokemon) {
                if(!getHasSpawnedOne()){
                    if(this.ownerUUID != null && CobblemonOutbreaksConfig.SEND_PORTAL_SPAWN_MESSAGE.get()) {
                        MutableComponent argsComponent = Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                        MutableComponent message = Component.translatable("cobblemonoutbreaks.gate_failed_spawning", argsComponent).withStyle(ChatFormatting.DARK_RED);
                        if(level.getPlayerByUUID(this.ownerUUID) !=null) level.getPlayerByUUID(this.ownerUUID).sendSystemMessage(message);
                    }
                } else {
                    if(this.ownerUUID != null && CobblemonOutbreaksConfig.SEND_PORTAL_SPAWN_MESSAGE.get()) {
                        MutableComponent argsComponent = Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                        MutableComponent message = Component.translatable("cobblemonoutbreaks.gate_finished", argsComponent).withStyle(ChatFormatting.GREEN);
                        if(level.getPlayerByUUID(this.ownerUUID) !=null) level.getPlayerByUUID(this.ownerUUID).sendSystemMessage(message);
                    }
                }
                completeOutBreak(true);
            }
            if (this.tickCount % 100 == 0 && getWave() < this.getOutbreakPortal().getWaves() && !containsPokemon) {
                spawnWave();
                setWave(getWave() + 1);
                OutbreakManager outbreakManager1 = OutbreakManager.get(level);
                outbreakManager1.setDirty();

            }

            if (getTicksActive() >= this.getOutbreakPortal().getMaxGateTime() && getHasSpawnedOne()) {
                if(this.ownerUUID != null && CobblemonOutbreaksConfig.SEND_PORTAL_SPAWN_MESSAGE.get()) {
                    MutableComponent argsComponent = Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                    MutableComponent message = Component.translatable("cobblemonoutbreaks.gate_time_finished", argsComponent).withStyle(ChatFormatting.RED);
                    if(level.getPlayerByUUID(this.ownerUUID) !=null) level.getPlayerByUUID(this.ownerUUID).sendSystemMessage(message);
                }
                completeOutBreak(false);
            }



            setTicksActive(getTicksActive() + 1);
        }
        if(CobblemonOutbreaksConfig.SPAWN_PORTAL_PARTICLES.get() && tickCount % 10 == 0) {
            ServerLevel serverLevel = (ServerLevel) level;
            serverLevel.sendParticles(ParticleTypes.FLAME, blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), 2,0, 1, 0,0);
            serverLevel.sendParticles(ParticleTypes.CRIT, blockPosition.getX() + level.random.nextDouble(), blockPosition.getY(), this.blockPosition.getZ() + level.random.nextDouble(), 2,0, 0.3, 0, 0);
            serverLevel.sendParticles(ParticleTypes.CRIT, blockPosition.getX() + level.random.nextDouble(), blockPosition.getY(), this.blockPosition.getZ() + level.random.nextDouble(), 2, 0,0.3, 0, 0);
            serverLevel.sendParticles(ParticleTypes.CRIT, blockPosition.getX() - level.random.nextDouble(), blockPosition.getY(), this.blockPosition.getZ() - level.random.nextDouble(), 2, 0,0.3, 0, 0);
            serverLevel.sendParticles(ParticleTypes.CRIT, blockPosition.getX() - level.random.nextDouble(), blockPosition.getY(), this.blockPosition.getZ() - level.random.nextDouble(), 2,0, 0.3, 0, 0);
        }
    }

    /**
     *
     * An extra check every ten minutes to check if the world still has the pokemon and then remove it from the map
     * Just an extra insurance to ensure that the portals wont be duds
     *
     * */
    public boolean pokemonStillValid() {
        if(level.isClientSide) return false;
        ServerLevel serverLevel = (ServerLevel) level;
        boolean containsPokemon = currentOutbreakWaveEntities.stream().anyMatch(uuid -> outbreakManager.containsUUID(uuid));
        boolean worldHasPokemon = false;

        if(tickCount % 12000 == 0) return containsPokemon;
        Set<UUID> toRemove = new HashSet<>();
        for(UUID uuid1 : currentOutbreakWaveEntities){
            PokemonEntity entity = (PokemonEntity) serverLevel.getEntity(uuid1);
            if(entity == null)
            {
                PokemonOutbreakManager pokemonOutbreakManager = PokemonOutbreakManager.get(serverLevel);
                if(pokemonOutbreakManager.containsUUID(uuid1)){
                    pokemonOutbreakManager.removePokemonUUID(uuid1);
                }
                toRemove.add(uuid1);
            } else {
                worldHasPokemon = true;
            }
        }

        currentOutbreakWaveEntities.removeAll(toRemove);

        return containsPokemon && worldHasPokemon;
    }

    public void pokemonStillValidFirstTime() {
        if(level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level;
        Set<UUID> toRemove = new HashSet<>();
        for(UUID uuid1 : currentOutbreakWaveEntities){
            PokemonEntity entity = (PokemonEntity) serverLevel.getEntity(uuid1);
            PokemonOutbreakManager pokemonOutbreakManager = PokemonOutbreakManager.get(serverLevel);
            if(entity == null)
            {

                if(pokemonOutbreakManager.containsUUID(uuid1)){
                    pokemonOutbreakManager.removePokemonUUID(uuid1);
                }
                toRemove.add(uuid1);
            } else {
                pokemonOutbreakManager.removePokemonUUIDTemp(uuid1);
                pokemonOutbreakManager.addPokemonWOwner(uuid1, this.outbreakUUID);
            }
        }

        currentOutbreakWaveEntities.removeAll(toRemove);
        checkLevel = true;
    }

    public void spawnWave() {
        Vec3 pos = new Vec3(this.blockPosition.getX(), this.blockPosition.getY(), this.blockPosition.getZ());
        List<Pokemon> spawned = this.getOutbreakPortal().spawnWave((ServerLevel) level, pos, this, this.getOutbreakPortal().getSpecies());
        for (Pokemon e : spawned) {
            if(e.getEntity() == null) continue;
            this.currentOutbreakWaveEntities.add(e.getEntity().getUUID());
            outbreakManager.addPokemonWOwner(e.getEntity().getUUID(), this.getOutbreakUUID());

        }

        if(spawned.size() > 0){
            setHasSpawnedOne(true);
        }
    }

    protected void completeOutBreak(boolean rewards) {
        if (rewards) {
            double completionXp = this.getOutbreakPortal().getExperience();
            while (completionXp > 0) {
                int i = 20;
                completionXp -= i;
                level.addFreshEntity(new ExperienceOrb(level, this.blockPosition.getX(), this.blockPosition.getY(), this.blockPosition.getZ(), i));
            }
            this.getOutbreakPortal().spawnRewards((ServerLevel) level, this);
        }
        for (UUID uuid1 : currentOutbreakWaveEntities) {
            outbreakManager.removePokemonUUID(uuid1);
        }
        currentOutbreakWaveEntities.clear();
        if(!level.isClientSide){
            OutbreakManager outbreakManager1 = OutbreakManager.get(level);
            outbreakManager1.removePortal(outbreakUUID);
        }



        //this.remove(RemovalReason.DISCARDED);
    }

    public OutbreakPortal getOutbreakPortal() {
        if(this.portal != null){
            return portal;
        } else{
            MutableComponent argsComponent = Component.literal(resourceLocation.toString()).withStyle(ChatFormatting.YELLOW);
            MutableComponent message = Component.translatable("cobblemonoutbreaks.gate_not_able_to_load", argsComponent).withStyle(ChatFormatting.RED);
            if(this.ownerUUID != null) {
                if (level.getPlayerByUUID(this.ownerUUID) != null)
                    level.getPlayerByUUID(this.ownerUUID).sendSystemMessage(message);
            }
            LOGGER.error("Was not able to load outbreak, with the following resourcelocation {}, closing the gate", this.resourceLocation);
            completeOutBreak(false);
         return  null;
        }
    }

    public OutbreakPortalEntity(ResourceLocation resourceLocation, OutbreakPortal outbreakPortal, UUID ownerUUID, UUID outbreakUUID, Set<UUID> currentOutbreakWaveEntities, BlockPos pos, int ticksActive, int wave, boolean hasSpawnedOne, int tickCount ){
        this.resourceLocation = resourceLocation;
        this.portal = outbreakPortal;
        this.ownerUUID = ownerUUID;
        this.outbreakUUID = outbreakUUID;
        this.currentOutbreakWaveEntities.addAll(currentOutbreakWaveEntities);
        this.blockPosition = pos;
        this.ticksActive = ticksActive;
        this.wave = wave;
        this.hasSpawnedOne = hasSpawnedOne;
        this.tickCount = tickCount;
    }
    public static OutbreakPortalEntity serialize(Level level, CompoundTag tag) {
        ResourceLocation resourceLocation1 = prefix(tag.getString("gateLoc"));;
        OutbreakPortal outbreakPortal = OutbreaksJsonDataManager.getPortalFromRl(prefix(tag.getString("gateLoc")), null);
        UUID ownerUUID = null;
        UUID outbreakUUID = null;
        Set<UUID> currentOutbreakEntities = new HashSet<>();
        BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
        int ticksActive = tag.getInt("ticks_active");
        int wave = tag.getInt("wave");
        int tickCount = tag.getInt("tickCount");
        boolean hasSpawnedOne = tag.getBoolean("hasSpawned");


        if (tag.hasUUID("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }
        if (tag.hasUUID("Outbreak")) {
            outbreakUUID = tag.getUUID("Outbreak");
        }


        if(outbreakPortal == null){
            MutableComponent message = Component.translatable("cobblemonoutbreaks.gate_not_able_to_load").withStyle(ChatFormatting.RED);
            if(ownerUUID != null) {
                if (level.getPlayerByUUID(ownerUUID) != null)
                    level.getPlayerByUUID(ownerUUID).sendSystemMessage(message);
            }
            LOGGER.error("Was not able to load outbreak with the following resourcelocation {}, closing the gate", resourceLocation1);
            return null;
            //completeOutBreak(false);
        }


        ListTag uuidsTag = tag.getList("current_outbreak_wave_entities", 10);
        for (int i = 0; i < uuidsTag.size(); i++) {
            CompoundTag uuidTag = uuidsTag.getCompound(i);
            UUID uuid = uuidTag.getUUID("entity" + i);
            currentOutbreakEntities.add(uuid);
        }

        return new OutbreakPortalEntity(resourceLocation1, outbreakPortal, ownerUUID, outbreakUUID, currentOutbreakEntities, pos, ticksActive, wave, hasSpawnedOne, tickCount);
    }

    public CompoundTag deserialize() {
        CompoundTag tag = new CompoundTag();
        tag.putString("gateLoc", resourceLocation.toString().replace("cobblemonoutbreaks:", ""));

        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
        if (this.outbreakUUID != null) {
            tag.putUUID("Outbreak", this.outbreakUUID);
        }

        ListTag uuidsTag = new ListTag();
        int id = 0;
        for (UUID uuid : currentOutbreakWaveEntities) {
            CompoundTag uuidTag = new CompoundTag();
            uuidTag.putUUID("entity" + id, uuid);
            uuidsTag.add(uuidTag);
            id++;
        }
        tag.putBoolean("spawnParticles", CobblemonOutbreaksConfig.SPAWN_PORTAL_PARTICLES.get());
        tag.put("current_outbreak_wave_entities", uuidsTag);
        tag.putInt("ticks_active", getTicksActive());
        tag.putInt("tickCount", this.tickCount);
        tag.putInt("wave", getWave());
        tag.putBoolean("hasSpawned", getHasSpawnedOne());
        tag.put("pos", NbtUtils.writeBlockPos(this.blockPosition));
        return tag;
    }


    public void kill(Level level) {
        for (UUID e : currentOutbreakWaveEntities) {
            PokemonOutbreakManager.get(level).removePokemonUUID(e);
        }
    }


    public boolean getHasSpawnedOne() {
        return this.hasSpawnedOne;
    }

    public void setHasSpawnedOne(boolean spawnedOne) {
        this.hasSpawnedOne = spawnedOne;
    }
    public int getTicksActive() {
        return this.ticksActive;
    }

    public void setTicksActive(int active) {
        this.ticksActive = active;
    }

    public int getWave() {
        return this.wave;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public UUID getOutbreakUUID() {
        return outbreakUUID;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public void removeFromSet(UUID pokemon) {
        this.currentOutbreakWaveEntities.remove(pokemon);
    }

    public void setBlockPosition(BlockPos blockPosition) {
        this.blockPosition = blockPosition;
    }

    public void setBlockPosition(Vec3 blockPosition) {
        BlockPos pos = BlockPos.containing(blockPosition.x(), blockPosition.y(), blockPosition.z());
        this.blockPosition = pos;
    }


    public double getX(){
        return this.blockPosition.getX();
    }

    public double getY(){
        return this.blockPosition.getY();
    }

    public double getZ(){
        return this.blockPosition.getZ();
    }

    public double distanceToSqr(double x, double y, double z) {
        double d = this.getX() - x;
        double e = this.getY() - y;
        double f = this.getZ() - z;
        return d * d + e * e + f * f;
    }

    public Level getLevel() {
        return level;
    }

    public PokemonOutbreakManager getOutbreakManager() {
        return outbreakManager;
    }

    public void setOutbreakManager(PokemonOutbreakManager outbreakManager) {
        this.outbreakManager = outbreakManager;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public static void entityNotSimilar(Pokemon pokemon, OutbreakPortalEntity outbreakPortal) {
        LOGGER.error("Species trying to spawn is {}, species specified is {}. Something might be wrong with the json. Try checking {}", pokemon.getSpecies(), outbreakPortal.getOutbreakPortal().getSpecies(), outbreakPortal.resourceLocation);
    }
}
