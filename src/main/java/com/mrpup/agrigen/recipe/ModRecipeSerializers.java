package com.mrpup.agrigen.recipe;

import com.mrpup.agrigen.recipe.machine.CraftSynthesizerRecipe;
import com.mrpup.agrigen.recipe.machine.serializer.CraftSynthesizerRecipeSerializer;
import com.mrpup.clumapi.recipe.RegRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import java.util.function.Supplier;

public class ModRecipeSerializers {

    public static final Supplier<SimpleCraftingRecipeSerializer<GeneticTemplateCombineRecipe>> GENETIC_TEMPLATE_COMBINE_SERIALIZER =
            RegRecipes.regCraftingSerializer("genetic_template_combine", GeneticTemplateCombineRecipe::new);

    public static final Supplier<RecipeSerializer<CraftSynthesizerRecipe>> CRAFT_SYNTHESIZER =
            RegRecipes.regRecipeSerializer("craft_synthesizer", CraftSynthesizerRecipeSerializer::new);

    public static void register() {}
}
