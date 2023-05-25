package com.scouter.cobbleoutbreaks.entity;

import com.cobblemon.mod.common.api.entity.Despawner;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class CustomDespawner implements Despawner {

    @Override
    public void beginTracking(@NotNull Entity entity) {
    }

    @Override
    public boolean shouldDespawn(@NotNull Entity entity) {
        return false;
    }
}
