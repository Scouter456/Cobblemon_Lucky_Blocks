package com.scouter.cobblelucky;

import com.scouter.cobblelucky.setup.ClientSetup;
import com.scouter.cobblelucky.setup.Registration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Set;

public class CobbleLucky implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MODID = "cobblelucky";
    public static final Logger LOGGER = LoggerFactory.getLogger("cobblelucky");

    @Override
    public void onInitialize() {
        Registration.init();
        ClientSetup.init();
        this.registerLootTable();
    }


    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name.toLowerCase(Locale.ROOT));
    }

    protected void registerLootTable() {

        Set<ResourceLocation> chestsId = Set.of(
                BuiltInLootTables.ABANDONED_MINESHAFT
        );


        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            ResourceLocation injectId = new ResourceLocation(CobbleLucky.MODID, "inject/" + id.getPath());
            if (chestsId.contains(id)) {
                tableBuilder.pool(LootPool.lootPool().add(LootTableReference.lootTableReference(injectId).setWeight(1).setQuality(0)).build());
            }

        });
    }
}
