package com.mrpup.agrigen.plugin.jei;

import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipe;
import com.mrpup.agrigen.recipe.machine.CraftSynthesizerRecipe;
import mezz.jei.api.recipe.RecipeType;

public class AgriGenRecipeTypes {
    public static RecipeType<CraftSynthesizerRecipe> CRAFT_SYNTHESIZER = RecipeType.create("agrigen", "craft_synthesizer", CraftSynthesizerRecipe.class);
    public static RecipeType<CrossbreedRecipe> CROSSBREED = RecipeType.create("agrigen", "crossbreed", CrossbreedRecipe.class);
}
