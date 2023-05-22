package com.scouter.cobbleoutbreaks.config;

import com.scouter.cobbleoutbreaks.CobblemonOutbreaks;
import net.minecraftforge.common.ForgeConfigSpec;

public class CobblemonOutbreaksConfig {

    public static final ForgeConfigSpec CONFIG_BUILDER;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        CONFIG_BUILDER = configBuilder.build();
    }

    public static ForgeConfigSpec.ConfigValue<Boolean> OUTBREAK_PORTAL_SPAWN_SOUND;
    public static ForgeConfigSpec.ConfigValue<Integer> OUTBREAK_SPAWN_TIMER;
    public static ForgeConfigSpec.ConfigValue<Integer> OUTBREAK_SPAWN_COUNT;
    public static ForgeConfigSpec.ConfigValue<Boolean> SEND_PORTAL_SPAWN_MESSAGE;
    public static ForgeConfigSpec.ConfigValue<Boolean> SPAWN_PORTAL_PARTICLES;
    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        builder.comment(CobblemonOutbreaks.MODID + " Config");

        OUTBREAK_PORTAL_SPAWN_SOUND = builder.comment("Turns the portal sound on or off when a pokemon is spawned in an outbreak").define("outbreak_portal_spawn_sound", true);
        OUTBREAK_SPAWN_TIMER = builder.comment("Time it takes for an outbreak to spawn around the player (in ticks 36000 being 30 minutes)").define("outbreak_spawn_timer", 36000);
        OUTBREAK_SPAWN_COUNT = builder.comment("Amount of outbreaks that spawn when the timer runs out, 0 spawns nothing").define("outbreak_spawn_count", 3);
        SEND_PORTAL_SPAWN_MESSAGE = builder.comment("Whether or not a message should be sent when an outbreak spawns, finishes and gets removed").define("send_outbreak_portal_spawn_message", true);
        SPAWN_PORTAL_PARTICLES = builder.comment("Turn particles on or off for the outbreak portal, this will make it easier to find them").define("spawn_portal_particles", false);

    }
}
