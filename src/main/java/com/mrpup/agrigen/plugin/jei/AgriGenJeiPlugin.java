package com.mrpup.agrigen.plugin.jei;

import com.mrpup.agrigen.plugin.jei.category.CraftSynthesizerCategory;
import com.mrpup.agrigen.plugin.jei.category.CrossbreedCategory;
import com.mrpup.agrigen.recipe.ModRecipeTypes;
import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipe;
import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipeManager;
import com.mrpup.agrigen.recipe.machine.CraftSynthesizerRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class AgriGenJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("agrigen", "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new CraftSynthesizerCategory(registration.getJeiHelpers().getGuiHelper()),
                new CrossbreedCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        List<CraftSynthesizerRecipe> recipes = level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.CRAFT_SYNTHESIZER.get())
                .stream()
                .map(holder -> holder.value())
                .toList();

        registration.addRecipes(AgriGenRecipeTypes.CRAFT_SYNTHESIZER, recipes);

        List<CrossbreedRecipe> crossbreedRecipes = new ArrayList<>(CrossbreedRecipeManager.getAllRecipes());

        registration.addRecipes(AgriGenRecipeTypes.CROSSBREED, crossbreedRecipes);
    }
}
