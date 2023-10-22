package com.scouter.cobblelucky.datagen;

import com.cobblemon.mod.common.CobblemonItems;
import com.scouter.cobblelucky.items.CLItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider implements IConditionBuilder {
    public RecipeGenerator(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC , CLItems.COBBLEMON_LUCKY_BLOCK.get())
                .define('F', CobblemonItems.FOCUS_BAND)
                .define('N', CobblemonItems.NEVER_MELT_ICE)
                .define('d', CobblemonItems.DREAM_BALL)
                .define('s', CobblemonItems.CHARCOAL)

                .define('p', CobblemonItems.POKE_BALL)

                .define('L', Items.DIAMOND)

                .pattern("FsN")
                .pattern("pdp")
                .pattern("LpL")
                .unlockedBy("has_focus_band", has(CobblemonItems.FOCUS_BAND))
                .unlockedBy("has_dream_ball", has(CobblemonItems.DREAM_BALL))
                .unlockedBy("has_charcoal", has(CobblemonItems.CHARCOAL))

                .unlockedBy("has_never_melt_ice", has(CobblemonItems.NEVER_MELT_ICE))
                .unlockedBy("has_pokeball", has(CobblemonItems.POKE_BALL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC , CLItems.COBBLEMON_LUCKY_ITEM_BLOCK.get())
                .define('M', CobblemonItems.MOON_STONE)
                .define('L', CobblemonItems.LEAF_STONE)
                .define('D', CobblemonItems.DUSK_STONE)
                .define('F', CobblemonItems.FIRE_STONE)

                .define('p', CobblemonItems.POKE_BALL)
                .define('s', Items.GUNPOWDER)

                .pattern("MsL")
                .pattern("sps")
                .pattern("DsF")

                .unlockedBy("has_moon_stone", has(CobblemonItems.MOON_STONE))
                .unlockedBy("has_leaf_stone", has(CobblemonItems.LEAF_STONE))
                .unlockedBy("has_dusk_stone", has(CobblemonItems.DUSK_STONE))

                .unlockedBy("has_poke_ball", has(CobblemonItems.POKE_BALL))
                .unlockedBy("has_fire_stone", has(CobblemonItems.FIRE_STONE))

                .save(consumer);
    }

    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }
}
