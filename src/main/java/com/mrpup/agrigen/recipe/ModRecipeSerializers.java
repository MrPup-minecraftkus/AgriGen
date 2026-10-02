package com.mrpup.agrigen.recipe;

import com.mojang.serialization.MapCodec;
import com.mrpup.agrigen.recipe.machine.CraftSynthesizerRecipe;
import com.mrpup.clumapi.recipe.RegRecipes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class ModRecipeSerializers {

    public static final Supplier<RecipeSerializer<GeneticTemplateCombineRecipe>> GENETIC_TEMPLATE_COMBINE_SERIALIZER =
            RegRecipes.regCraftingSerializer(
                    "genetic_template_combine",
                    MapCodec.unit(GeneticTemplateCombineRecipe.INSTANCE),
                    StreamCodec.unit(GeneticTemplateCombineRecipe.INSTANCE),
                    () -> GeneticTemplateCombineRecipe.INSTANCE
            );

    public static final Supplier<RecipeSerializer<CraftSynthesizerRecipe>> CRAFT_SYNTHESIZER =
            RegRecipes.regRecipeSerializer(
                    "craft_synthesizer",
                    () -> new RecipeSerializer<>(
                            CraftSynthesizerRecipe.CODEC,
                            CraftSynthesizerRecipe.STREAM_CODEC
                    )
            );

    public static void register() {}
}
