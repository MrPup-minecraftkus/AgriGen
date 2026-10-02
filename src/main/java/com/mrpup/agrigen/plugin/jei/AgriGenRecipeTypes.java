package com.mrpup.agrigen.plugin.jei;

import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipe;
import mezz.jei.api.recipe.types.IRecipeType;

public class AgriGenRecipeTypes {
    public static IRecipeType<CrossbreedRecipe> CROSSBREED = IRecipeType.create("agrigen", "crossbreed", CrossbreedRecipe.class);
}
