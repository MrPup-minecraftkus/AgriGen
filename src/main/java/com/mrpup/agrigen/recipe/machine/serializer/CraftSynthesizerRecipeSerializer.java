package com.mrpup.agrigen.recipe.machine.serializer;

import com.mojang.serialization.MapCodec;
import com.mrpup.agrigen.recipe.machine.CraftSynthesizerRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CraftSynthesizerRecipeSerializer implements RecipeSerializer<CraftSynthesizerRecipe> {
    @Override
    public MapCodec<CraftSynthesizerRecipe> codec() {
        return CraftSynthesizerRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CraftSynthesizerRecipe> streamCodec() {
        return CraftSynthesizerRecipe.STREAM_CODEC;
    }
}
