package com.mrpup.agrigen.recipe;

import com.mrpup.agrigen.recipe.machine.CraftSynthesizerRecipe;
import com.mrpup.clumapi.recipe.RegRecipes;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class ModRecipeTypes {
    public static final Supplier<RecipeType<CraftSynthesizerRecipe>> CRAFT_SYNTHESIZER =
            RegRecipes.regRecipeType("craft_synthesizer");

    public static void register() {}
}
