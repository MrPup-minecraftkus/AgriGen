package com.mrpup.agrigen.plugin.jei;

import com.mrpup.agrigen.AgriGen;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.plugin.jei.category.CraftSynthesizerCategory;
import com.mrpup.agrigen.plugin.jei.category.CrossbreedCategory;
import com.mrpup.agrigen.recipe.ModRecipeTypes;
import com.mrpup.agrigen.recipe.ModRecipes;
import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipe;
import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipeManager;
import com.mrpup.agrigen.recipe.machine.CraftSynthesizerRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class AgriGenJeiPlugin implements IModPlugin {
    private static RecipeMap syncedRecipes = RecipeMap.EMPTY;

    public static final IRecipeType<RecipeHolder<CraftSynthesizerRecipe>> CRAFT_SYNTHESIZER = createRecipeHolderType("craft_synthesizer");

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath("agrigen", "jei_plugin");
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
        List<CrossbreedRecipe> crossbreedRecipes = new ArrayList<>(CrossbreedRecipeManager.getAllRecipes());
        registration.addRecipes(AgriGenRecipeTypes.CROSSBREED, crossbreedRecipes);

        registration.addRecipes(CRAFT_SYNTHESIZER, this.getRecipes(syncedRecipes, ModRecipeTypes.CRAFT_SYNTHESIZER.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(
                CRAFT_SYNTHESIZER,
                ModBlocks.CRAFT_SYNTHESIZER.getBlock().asItem()
        );
        registration.addCraftingStation(
                AgriGenRecipeTypes.CROSSBREED,
                ModBlocks.CROP_STICK.getBlock().asItem()
        );
    }

    // From Occultism
    // Under MIT License
    @SuppressWarnings({"unchecked", "rawtypes"})
    private <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getRecipes(RecipeMap recipeMap, RecipeType<T> type) {
        return (List) recipeMap.byType(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> IRecipeType<T> createRecipeHolderType(String path) {
        return (IRecipeType<T>) IRecipeType.create(Identifier.fromNamespaceAndPath(AgriGen.MOD_ID, path), RecipeHolder.class);
    }

    @EventBusSubscriber(modid = AgriGen.MOD_ID)
    public static class ServerRecipeSync {
        @SubscribeEvent
        public static void onDatapackSync(OnDatapackSyncEvent event) {
            event.sendRecipes(
                    ModRecipeTypes.CRAFT_SYNTHESIZER.get()
            );
        }
    }

    @EventBusSubscriber(modid = AgriGen.MOD_ID, value = Dist.CLIENT)
    public static class ClientRecipeSync {
        @SubscribeEvent
        public static void onRecipeReceived(RecipesReceivedEvent event) {
            syncedRecipes = event.getRecipeMap();
        }
    }
}
