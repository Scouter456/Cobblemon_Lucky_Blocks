package com.scouter.cobbleoutbreaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.scouter.cobbleoutbreaks.data.OutbreaksJsonDataManager;
import com.scouter.cobbleoutbreaks.data.PokemonOutbreakManager;
import com.scouter.cobbleoutbreaks.entity.OutbreakPortalEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class OutbreakPortalCommand {

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_TYPE = (ctx, builder) -> {
        return SharedSuggestionProvider.suggest(OutbreaksJsonDataManager.getData().keySet().stream().map(ResourceLocation::toString), builder);
    };


    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("outbreakportal").requires(s -> s.hasPermission(2));

        builder.then(Commands.literal("flush_pokemon_map").executes(c -> {
            return flushPokemonMap(c);
        }));

        builder.then(Commands.argument("pos", Vec3Argument.vec3()).then(Commands.argument("type", ResourceLocationArgument.id()).suggests(SUGGEST_TYPE).executes(c -> {
            return openOutBreakPortal(c, Vec3Argument.getVec3(c, "pos"), ResourceLocationArgument.getId(c, "type"));
        })));
        pDispatcher.register(builder);
    }

    public static int openOutBreakPortal(CommandContext<CommandSourceStack> c, Vec3 pos, ResourceLocation type) {
        try {
            Entity nullableSummoner = c.getSource().getEntity();
            Player summoner = nullableSummoner instanceof Player ? (Player) nullableSummoner : c.getSource().getLevel().getNearestPlayer(pos.x(), pos.y(), pos.z(), 64, false);
            OutbreakPortalEntity outbreakPortalEntity = new OutbreakPortalEntity(c.getSource().getLevel(), summoner, type);
            outbreakPortalEntity.moveTo(pos);
            c.getSource().getLevel().addFreshEntity(outbreakPortalEntity);
        } catch (Exception ex) {
            c.getSource().sendFailure(Component.literal("Exception thrown - see log"));
            ex.printStackTrace();
        }
        return 0;
    }

    public static int flushPokemonMap(CommandContext<CommandSourceStack> c) {
        try {
            ServerLevel level = c.getSource().getLevel();
            level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("cobblemonoutbreaks.clearing_outbreaks_map").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
            PokemonOutbreakManager pokemonOutbreakManager = PokemonOutbreakManager.get(level);
            pokemonOutbreakManager.clearMap();
            pokemonOutbreakManager.clearTempMap();
        } catch (Exception ex) {
            c.getSource().sendFailure(Component.literal("Exception thrown - see log"));
            ex.printStackTrace();
        }
        return 0;
    }
}
