package com.scouter.cobbleoutbreaks.entity;

import com.scouter.cobbleoutbreaks.CobblemonOutbreaks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.scouter.cobbleoutbreaks.CobblemonOutbreaks.prefix;

public class COEntity {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CobblemonOutbreaks.MODID);
    public static final RegistryObject<EntityType<OutbreakPortalEntity>> OUTBREAK_PORTAL = ENTITY_TYPES.register("outbreak_portal",
            () -> EntityType.Builder.<OutbreakPortalEntity>of(OutbreakPortalEntity::new , MobCategory.MISC)
                    .fireImmune().sized(0.01F, 0.01F)
                    .clientTrackingRange(4)
                    .updateInterval(5)
                    //.setCustomClientFactory(OutbreakPortal::new)
                    .build(prefix("outbreak_portal").toString()));
}
