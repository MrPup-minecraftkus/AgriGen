package com.mrpup.agrigen.recipe.machine.builder;

import com.mrpup.agrigen.recipe.machine.CraftSynthesizerRecipe;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class CraftSynthesizerRecipeBuilder implements RecipeBuilder {

    private final ItemStack output;
    private final Ingredient[] inputs = new Ingredient[4];
    private SizedFluidIngredient fluidInput;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    private String group;

    private static final String FOLDER = "craft_synthesizer/";

    private CraftSynthesizerRecipeBuilder(ItemStack output) {
        this.output = output;
    }

    public static CraftSynthesizerRecipeBuilder create(ItemLike output) {
        return new CraftSynthesizerRecipeBuilder(new ItemStack(output.asItem()));
    }

    public static CraftSynthesizerRecipeBuilder create(ItemStack output) {
        return new CraftSynthesizerRecipeBuilder(output);
    }

    public CraftSynthesizerRecipeBuilder input(int slot, ItemLike item) {
        return input(slot, Ingredient.of(item));
    }

    public CraftSynthesizerRecipeBuilder input(int slot, TagKey<Item> tag) {
        return input(slot, Ingredient.of(tag));
    }

    public CraftSynthesizerRecipeBuilder input(int slot, Ingredient ingredient) {
        if (slot < 0 || slot > 3) {
            throw new IllegalArgumentException("Slot must be 0-3, was " + slot);
        }
        this.inputs[slot] = ingredient;
        return this;
    }

    public CraftSynthesizerRecipeBuilder fluid(Fluid fluid, int amount) {
        this.fluidInput = SizedFluidIngredient.of(fluid, amount);
        return this;
    }

    public CraftSynthesizerRecipeBuilder fluid(TagKey<Fluid> tag, int amount) {
        this.fluidInput = SizedFluidIngredient.of(tag, amount);
        return this;
    }

    @Override
    public CraftSynthesizerRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    @Override
    public CraftSynthesizerRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public CraftSynthesizerRecipeBuilder count(int count) {
        this.output.setCount(count);
        return this;
    }

    @Override
    public Item getResult() {
        return output.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i] == null) {
                throw new IllegalStateException("Recipe " + id + " is missing input slot " + i);
            }
        }
        if (fluidInput == null) {
            throw new IllegalStateException("Recipe " + id + " is missing a fluid input");
        }

        ResourceLocation outputId = id.withPrefix(FOLDER);

        CraftSynthesizerRecipe recipe = new CraftSynthesizerRecipe(
                inputs[0], inputs[1], inputs[2], inputs[3], fluidInput, output
        );

        recipeOutput.accept(
                outputId,
                recipe,
                advancement
                        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                        .rewards(AdvancementRewards.Builder.recipe(id))
                        .requirements(AdvancementRequirements.Strategy.OR)
                        .build(id.withPrefix("recipes/misc/"))
        );
    }
}
