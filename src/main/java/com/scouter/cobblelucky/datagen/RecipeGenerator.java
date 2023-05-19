package com.scouter.cobblelucky.datagen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.item.CobblemonItem;
import com.cobblemon.mod.common.item.PokemonItem;
import com.scouter.cobblelucky.items.CLItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(CLItems.COBBLEMON_LUCKY_BLOCK.get())
               .define('F', CobblemonItems.FOCUS_BAND.get())
                .define('N', CobblemonItems.NEVER_MELT_ICE.get())
                .define('d', CobblemonItems.DREAM_BALL.get())
                .define('s', CobblemonItems.CHARCOAL.get())

                .define('p', CobblemonItems.POKE_BALL.get())

                .define('L', Items.DIAMOND)

               .pattern("FsN")
                .pattern("pdp")
                .pattern("LpL")
                .unlockedBy("has_focus_band", has(CobblemonItems.FOCUS_BAND.get()))
                .unlockedBy("has_dream_ball", has(CobblemonItems.DREAM_BALL.get()))
                .unlockedBy("has_charcoal", has(CobblemonItems.CHARCOAL.get()))

                .unlockedBy("has_never_melt_ice", has(CobblemonItems.NEVER_MELT_ICE.get()))
                .unlockedBy("has_pokeball", has(CobblemonItems.POKE_BALL.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(CLItems.COBBLEMON_LUCKY_ITEM_BLOCK.get())
                .define('M', CobblemonItems.MOON_STONE.get())
                .define('L', CobblemonItems.LEAF_STONE.get())
                .define('D', CobblemonItems.DUSK_STONE.get())
                .define('F', CobblemonItems.FIRE_STONE.get())

                .define('p', CobblemonItems.POKE_BALL.get())
                .define('s', Items.GUNPOWDER)

                .pattern("MsL")
                .pattern("sps")
                .pattern("DsF")

                .unlockedBy("has_moon_stone", has(CobblemonItems.MOON_STONE.get()))
                .unlockedBy("has_leaf_stone", has(CobblemonItems.LEAF_STONE.get()))
                .unlockedBy("has_dusk_stone", has(CobblemonItems.DUSK_STONE.get()))

                .unlockedBy("has_poke_ball", has(CobblemonItems.POKE_BALL.get()))
                .unlockedBy("has_fire_stone", has(CobblemonItems.FIRE_STONE.get()))

                .save(consumer);
    }
}
