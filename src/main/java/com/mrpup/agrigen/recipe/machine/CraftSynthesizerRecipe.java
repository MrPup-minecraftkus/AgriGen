package com.mrpup.agrigen.recipe.machine;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrpup.agrigen.recipe.ModRecipeSerializers;
import com.mrpup.agrigen.recipe.ModRecipeTypes;
import com.mrpup.clumapi.recipe.MachineRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public record CraftSynthesizerRecipe(Ingredient input1, Ingredient input2, Ingredient input3, Ingredient input4, SizedFluidIngredient fluidInput, ItemStackTemplate output) implements Recipe<CraftSynthesizerRecipe.Input> {

    public static final MapCodec<CraftSynthesizerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("input_1").forGetter(CraftSynthesizerRecipe::input1),
            Ingredient.CODEC.fieldOf("input_2").forGetter(CraftSynthesizerRecipe::input2),
            Ingredient.CODEC.fieldOf("input_3").forGetter(CraftSynthesizerRecipe::input3),
            Ingredient.CODEC.fieldOf("input_4").forGetter(CraftSynthesizerRecipe::input4),
            SizedFluidIngredient.CODEC.fieldOf("fluid_input").forGetter(CraftSynthesizerRecipe::fluidInput),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(CraftSynthesizerRecipe::output)
    ).apply(inst, CraftSynthesizerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CraftSynthesizerRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, CraftSynthesizerRecipe::input1,
            Ingredient.CONTENTS_STREAM_CODEC, CraftSynthesizerRecipe::input2,
            Ingredient.CONTENTS_STREAM_CODEC, CraftSynthesizerRecipe::input3,
            Ingredient.CONTENTS_STREAM_CODEC, CraftSynthesizerRecipe::input4,
            SizedFluidIngredient.STREAM_CODEC, CraftSynthesizerRecipe::fluidInput,
            ItemStackTemplate.STREAM_CODEC, CraftSynthesizerRecipe::output,
            CraftSynthesizerRecipe::new
    );

    public static MachineRecipeBuilder<CraftSynthesizerRecipe> create(ItemLike output) {
        return MachineRecipeBuilder.create(
                4,
                output,
                "craft_synthesizer/",
                builder -> new CraftSynthesizerRecipe(
                        builder.input(0),
                        builder.input(1),
                        builder.input(2),
                        builder.input(3),
                        builder.fluidInput(),
                        builder.output()
                )
        );
    }


    public static MachineRecipeBuilder<CraftSynthesizerRecipe> create(ItemStackTemplate output, Identifier outputId) {
        return MachineRecipeBuilder.create(
                4,
                output,
                outputId,
                "craft_synthesizer/",
                builder -> new CraftSynthesizerRecipe(
                        builder.input(0),
                        builder.input(1),
                        builder.input(2),
                        builder.input(3),
                        builder.fluidInput(),
                        builder.output()
                )
        );
    }

    @Override
    public boolean matches(Input input, Level level) {
        return input1.test(input.getItem(0))
                && input2.test(input.getItem(1))
                && input3.test(input.getItem(2))
                && input4.test(input.getItem(3))
                && fluidInput.test(input.fluid());
    }

    @Override
    public ItemStack assemble(Input input) {
        return output.create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }


    @Override
    public RecipeSerializer<CraftSynthesizerRecipe> getSerializer() {
        return ModRecipeSerializers.CRAFT_SYNTHESIZER.get();
    }

    @Override
    public RecipeType<CraftSynthesizerRecipe> getType() {
        return ModRecipeTypes.CRAFT_SYNTHESIZER.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(input1, input2, input3, input4));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }

    public record Input(List<ItemStack> items, FluidStack fluid) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return items.get(index);
        }

        @Override
        public int size() {
            return items.size();
        }
    }
}
