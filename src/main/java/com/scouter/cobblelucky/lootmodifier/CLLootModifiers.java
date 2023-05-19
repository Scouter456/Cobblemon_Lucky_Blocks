package com.scouter.cobblelucky.lootmodifier;

import com.mojang.serialization.Codec;
import com.scouter.cobblelucky.CobbleLucky;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CLLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CobbleLucky.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_TO_LOOT_TABLE =
            LOOT_MODIFIER.register("add_to_loot_table", LootTableLootModifier.CODEC);

}
