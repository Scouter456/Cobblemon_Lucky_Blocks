/*package com.scouter.cobbleoutbreaks.entity;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.logging.LogUtils;
import com.scouter.cobbleoutbreaks.config.CobblemonOutbreaksConfig;
import com.scouter.cobbleoutbreaks.data.OutbreaksJsonDataManager;
import com.scouter.cobbleoutbreaks.data.PokemonOutbreakManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

import static com.scouter.cobbleoutbreaks.CobblemonOutbreaks.prefix;

public class OutbreakPortalEntity extends Entity implements IEntityAdditionalSpawnData {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final EntityDataAccessor<Integer> WAVE = SynchedEntityData.defineId(OutbreakPortalEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MAX_WAVE = SynchedEntityData.defineId(OutbreakPortalEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> TICKS_ACTIVE = SynchedEntityData.defineId(OutbreakPortalEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MAX_TICKS_ACTIVE = SynchedEntityData.defineId(OutbreakPortalEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> HAS_SPAWNED_ONE = SynchedEntityData.defineId(OutbreakPortalEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private UUID ownerUUID;

    protected Queue<ItemStack> undroppedItems = new ArrayDeque<>();
    protected final Set<UUID> currentOutbreakWaveEntities = new HashSet<>();
    private PokemonOutbreakManager outbreakManager;
    private OutbreakPortal portal;
    private ResourceLocation resourceLocation;
    private static Map<ResourceLocation, OutbreakPortal> map;


    public OutbreakPortalEntity(Level level, Player placer,  ResourceLocation resourceLocation){
        super(COEntity.OUTBREAK_PORTAL.get(), level);
        if(OutbreaksJsonDataManager.getBiomeData().isEmpty()){
            if(!level.isClientSide)
                OutbreaksJsonDataManager.populateMap((ServerLevel) level);
        }
        populatePortalFromCommand(resourceLocation);
        sendMessageToPlayer(placer);
        this.ownerUUID = placer.getUUID();
    }

    public OutbreakPortalEntity(Level level, Player placer, Vec3 position) {
        super(COEntity.OUTBREAK_PORTAL.get(), level);
        if(OutbreaksJsonDataManager.getBiomeData().isEmpty()){
            if(!level.isClientSide)
                OutbreaksJsonDataManager.populateMap((ServerLevel) level);
        }

        setPos(position);
        populatePortal();
        sendMessageToPlayer(placer);
        outbreakSpawnSound();
        this.ownerUUID = placer.getUUID();
    }

    public OutbreakPortalEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    public void populatePortal(){
        if (!this.level.isClientSide) {

            if(CobblemonOutbreaksConfig.BIOME_SPECIFIC_SPAWNS.get()){
                this.map = OutbreaksJsonDataManager.getRandomPortalFromBiome(level, this.level.getBiome(this.blockPosition()).unwrapKey().get());
            } else{
                this.map = OutbreaksJsonDataManager.getRandomPortal(level);
            }

            this.resourceLocation = map.keySet().stream().toList().get(0);
            this.portal = map.get(resourceLocation);
            this.outbreakManager = PokemonOutbreakManager.get((ServerLevel) level);
        }
    }

    public void populatePortalFromCommand(ResourceLocation resourceLocation){
        if (!this.level.isClientSide) {
            this.resourceLocation = resourceLocation;
                this.portal = OutbreaksJsonDataManager.getPortalFromRl(resourceLocation, null);
            this.outbreakManager = PokemonOutbreakManager.get((ServerLevel) level);
        }
    }


    public void sendMessageToPlayer(Player player){
        if(CobblemonOutbreaksConfig.SEND_PORTAL_SPAWN_MESSAGE.get()) {
            if(CobblemonOutbreaksConfig.BIOME_SPECIFIC_SPAWNS_DEBUG.get()) {
                MutableComponent pokemonMessage = Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                MutableComponent biomeMessage = Component.literal(this.level.getBiome(this.blockPosition()).unwrapKey().get().location().toString().split(":")[1]).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.portal_biome_specific_spawn_debug", biomeMessage,pokemonMessage).withStyle(ChatFormatting.GREEN);
                player.sendSystemMessage(outBreakMessage);
            } else{
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.portal_spawn_near").withStyle(ChatFormatting.DARK_AQUA);
                MutableComponent pokemonMessage =  Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                player.sendSystemMessage(outBreakMessage.append(pokemonMessage));
            }

        }
    }

    public void outbreakSpawnSound(){
        if(CobblemonOutbreaksConfig.OUTBREAK_PORTAL_SPAWN_SOUND.get()) {
            float volume = CobblemonOutbreaksConfig.OUTBREAK_PORTAL_SPAWN_VOLUME.get();
            this.level.playSound(null, this.blockPosition(), SoundEvents.PORTAL_TRIGGER, SoundSource.AMBIENT, volume, 1);
        }
    }

    public Vec3 findSuitableSpawnPoint(Player player){
        int maxRange = CobblemonOutbreaksConfig.MAX_SPAWN_RADIUS.get();
        int minRange = CobblemonOutbreaksConfig.MIN_SPAWN_RADIUS.get();

        if(maxRange > 112 || maxRange < 49){
            maxRange = 64;
        }
        if(minRange > 48 || minRange < 16){
            minRange = 32;
        }

        int randomX = this.level.random.nextInt(minRange) + (this.level.random.nextBoolean() ? 5 : -5);
        int randomZ = this.level.random.nextInt(maxRange) + (this.level.random.nextBoolean() ? 5 : -5);



        int playerPosX = player.getBlockX();
        int playerPosY = player.getBlockY();
        int playerPosZ = player.getBlockZ();

        boolean changeModX = this.level.random.nextBoolean();
        boolean changeModZ = this.level.random.nextBoolean();

        if(changeModX){
            randomX = -randomX;
        }

        if(changeModZ){
            randomZ = -randomZ;
        }
        int y = this.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,playerPosX+ randomX, playerPosZ + randomZ);
        BlockPos blockPos = new BlockPos(playerPosX + randomX, y , playerPosZ + randomZ);

        return Vec3.atCenterOf(blockPos);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            boolean containsPokemon = pokemonStillValid();
            if (getWave() >= this.getOutbreakPortal().getWaves() && !containsPokemon) {
                if(!getHasSpawnedOne()){
                    if(this.ownerUUID != null && CobblemonOutbreaksConfig.SEND_PORTAL_SPAWN_MESSAGE.get()) {
                        MutableComponent argsComponent = Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                        MutableComponent message = Component.translatable("cobblemonoutbreaks.gate_failed_spawning", argsComponent).withStyle(ChatFormatting.DARK_RED);
                        if(this.level.getPlayerByUUID(this.ownerUUID) !=null) this.level.getPlayerByUUID(this.ownerUUID).sendSystemMessage(message);
                    }
                } else {
                    if(this.ownerUUID != null && CobblemonOutbreaksConfig.SEND_PORTAL_SPAWN_MESSAGE.get()) {
                        MutableComponent argsComponent = Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                        MutableComponent message = Component.translatable("cobblemonoutbreaks.gate_finished", argsComponent).withStyle(ChatFormatting.GREEN);
                        if(this.level.getPlayerByUUID(this.ownerUUID) !=null) this.level.getPlayerByUUID(this.ownerUUID).sendSystemMessage(message);
                    }
                }
                completeOutBreak(true);
            }
            if (this.tickCount % 100 == 0 && getWave() < this.getOutbreakPortal().getWaves() && !containsPokemon) {
                spawnWave();
                setWave(getWave() + 1);
            }

            if (getTicksActive() >= this.getOutbreakPortal().getMaxGateTime() && getHasSpawnedOne()) {
                if(this.ownerUUID != null && CobblemonOutbreaksConfig.SEND_PORTAL_SPAWN_MESSAGE.get()) {
                    MutableComponent argsComponent = Component.literal(this.getOutbreakPortal().getSpecies()).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                    MutableComponent message = Component.translatable("cobblemonoutbreaks.gate_time_finished", argsComponent).withStyle(ChatFormatting.RED);
                    if(this.level.getPlayerByUUID(this.ownerUUID) !=null) this.level.getPlayerByUUID(this.ownerUUID).sendSystemMessage(message);
                }
                completeOutBreak(false);
            }



            setTicksActive(getTicksActive() + 1);
        } else {
            if(CobblemonOutbreaksConfig.SPAWN_PORTAL_PARTICLES.get() && tickCount % 10 == 0) {
                level.addParticle(ParticleTypes.FLAME, true, this.getX(), this.getY(), this.getZ(), 0, 0.2, 0);
                level.addParticle(ParticleTypes.CRIT, true, this.getX() + this.level.random.nextDouble(), this.getY(), this.getZ() + this.level.random.nextDouble(), 0, 0.5, 0);
                level.addParticle(ParticleTypes.CRIT, true, this.getX() + this.level.random.nextDouble(), this.getY(), this.getZ() + this.level.random.nextDouble(), 0, 0.5, 0);
                level.addParticle(ParticleTypes.CRIT, true, this.getX() - this.level.random.nextDouble(), this.getY(), this.getZ() - this.level.random.nextDouble(), 0, 0.5, 0);
                level.addParticle(ParticleTypes.CRIT, true, this.getX() - this.level.random.nextDouble(), this.getY(), this.getZ() - this.level.random.nextDouble(), 0, 0.5, 0);
            }
        }

    }

    /**
     *
     * An extra check every ten minutes to check if the world still has the pokemon and then remove it from the map
     * Just an extra insurance to ensure that the portals wont be duds
     *
     * */
/*
    public boolean pokemonStillValid() {
        if(this.level.isClientSide) return false;
        ServerLevel serverLevel = (ServerLevel) this.level;
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

    public void spawnWave() {
        List<Pokemon> spawned = this.getOutbreakPortal().spawnWave((ServerLevel) this.level, this.position(), this, this.getOutbreakPortal().getSpecies());
        for (Pokemon e : spawned) {
            this.currentOutbreakWaveEntities.add(e.getEntity().getUUID());
            outbreakManager.addPokemonWOwner(e.getEntity().getUUID(), this.getUUID());
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
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY(), this.getZ(), i));
            }
            this.getOutbreakPortal().spawnRewards((ServerLevel) this.level, this);
        }
        for (UUID uuid1 : currentOutbreakWaveEntities) {
            outbreakManager.removePokemonUUID(uuid1);
        }
        currentOutbreakWaveEntities.clear();


        this.remove(RemovalReason.DISCARDED);
    }

    public OutbreakPortal getOutbreakPortal() {
        if(this.portal != null){
            return portal;
        } else{
            MutableComponent argsComponent = Component.literal(resourceLocation.toString()).withStyle(ChatFormatting.YELLOW);
            MutableComponent message = Component.translatable("cobblemonoutbreaks.gate_not_able_to_load", argsComponent).withStyle(ChatFormatting.RED);
            if(this.ownerUUID != null) {
                if (this.level.getPlayerByUUID(this.ownerUUID) != null)
                    this.level.getPlayerByUUID(this.ownerUUID).sendSystemMessage(message);
            }
            LOGGER.error("Was not able to load outbreak, with the following resourcelocation {}, closing the gate", this.resourceLocation);
            completeOutBreak(false);
         return  null;
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(MAX_TICKS_ACTIVE, 0);
        this.entityData.define(TICKS_ACTIVE, 0);
        this.entityData.define(WAVE, 0);
        this.entityData.define(MAX_WAVE, 0);

        this.entityData.define(HAS_SPAWNED_ONE, false);
        //this.spawnParticles = CobblemonOutbreaksConfig.SPAWN_PORTAL_PARTICLES.get();
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if(resourceLocation == null){
            resourceLocation = prefix(tag.getString("gateLoc"));
        }
        if(!level.isClientSide) {
            this.outbreakManager = PokemonOutbreakManager.get(level);
        }
        this.portal = OutbreaksJsonDataManager.getPortalFromRl(prefix(tag.getString("gateLoc")), portal);

        if(this.portal == null){
            MutableComponent message = Component.translatable("cobblemonoutbreaks.gate_not_able_to_load").withStyle(ChatFormatting.RED);
            if(this.ownerUUID != null) {
                if (this.level.getPlayerByUUID(this.ownerUUID) != null)
                    this.level.getPlayerByUUID(this.ownerUUID).sendSystemMessage(message);
            }
            LOGGER.error("Was not able to load outbreak with the following resourcelocation {}, closing the gate", this.resourceLocation);
            completeOutBreak(false);


        }

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }

        ListTag uuidsTag = tag.getList("current_outbreak_wave_entities", 10);
        for (int i = 0; i < uuidsTag.size(); i++) {
            CompoundTag uuidTag = uuidsTag.getCompound(i);
            UUID uuid = uuidTag.getUUID("entity" + i);
            currentOutbreakWaveEntities.add(uuid);
        }
        if (!level.isClientSide) {
            this.outbreakManager = PokemonOutbreakManager.get((ServerLevel) this.level);
        }

        setMaxTicksActive(tag.getInt("max_ticks_active"));
        setTicksActive(tag.getInt("ticks_active"));
        setMaxWave(tag.getInt("max_wave"));
        setWave(tag.getInt("wave"));
        setHasSpawnedOne(tag.getBoolean("hasSpawned"));
        ListTag stacks = tag.getList("queued_stacks", Tag.TAG_COMPOUND);
        for (Tag inbt : stacks) {
            undroppedItems.add(ItemStack.of((CompoundTag) inbt));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("gateLoc", resourceLocation.toString().replace("cobblemonoutbreaks:", ""));

        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
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
        tag.putInt("max_ticks_active", getMaxTicksActive());
        tag.putInt("max_wave", getMaxWave());
        tag.putInt("wave", getWave());
        tag.putBoolean("hasSpawned", getHasSpawnedOne());
        ListTag stacks = new ListTag();
        for (ItemStack s : this.undroppedItems) {
            stacks.add(s.serializeNBT());
        }
        tag.put("queued_stacks", stacks);
    }


    @Override
    public void kill() {
        for (UUID e : currentOutbreakWaveEntities) {
            PokemonOutbreakManager.get(this.level).removePokemonUUID(e);
        }

        super.kill();
    }


    public boolean getHasSpawnedOne() {
        return this.entityData.get(HAS_SPAWNED_ONE);
    }

    public void setHasSpawnedOne(boolean spawnedOne) {
        this.entityData.set(HAS_SPAWNED_ONE, spawnedOne);
    }
    public int getTicksActive() {
        return this.entityData.get(TICKS_ACTIVE);
    }

    public void setTicksActive(int active) {
        this.entityData.set(TICKS_ACTIVE, active);
    }

    public int getMaxTicksActive() {
        return this.entityData.get(MAX_TICKS_ACTIVE);
    }

    public void setMaxTicksActive(int maxActive) {
        this.entityData.set(MAX_TICKS_ACTIVE, maxActive);
    }

    public int getMaxWave() {
        return this.entityData.get(MAX_WAVE);
    }

    public void setMaxWave(int maxWave) {
        this.entityData.set(MAX_WAVE, maxWave);
    }

    public int getWave() {
        return this.entityData.get(WAVE);
    }

    public void setWave(int wave) {
        this.entityData.set(WAVE, wave);
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.resourceLocation);
    }


    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        // this.portal = OutbreaksJsonDataManager.getPortalFromRl(buf.readResourceLocation());
        if (this.portal == null) throw new RuntimeException("Invalid portal received on client!");
    }


    @Override
    protected int getPermissionLevel() {
        return 2;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public void removeFromSet(UUID pokemon) {
        this.currentOutbreakWaveEntities.remove(pokemon);
    }

    public static void entityNotSimilar(Pokemon pokemon, OutbreakPortalEntity outbreakPortal) {
        LOGGER.error("Species trying to spawn is {}, species specified is {}. Something might be wrong with the json. Try checking {}", pokemon.getSpecies(), outbreakPortal.getOutbreakPortal().getSpecies(), outbreakPortal.resourceLocation);
    }
}
*/