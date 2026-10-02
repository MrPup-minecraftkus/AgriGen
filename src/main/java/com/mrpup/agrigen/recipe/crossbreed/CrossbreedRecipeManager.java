package com.mrpup.agrigen.recipe.crossbreed;

import com.mojang.logging.LogUtils;
import com.mrpup.clumapi.helper.ItemHelper;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CrossbreedRecipeManager extends SimpleJsonResourceReloadListener<CrossbreedRecipe> {

    private static final String FOLDER = "crossbreed_recipes";

    private static Map<String, CrossbreedRecipe> RECIPES = Map.of();
    private static final Logger LOGGER = LogUtils.getLogger();

    public CrossbreedRecipeManager() {
        super(CrossbreedRecipe.CODEC, FileToIdConverter.json(FOLDER));
    }

    @Override
    protected void apply(Map<Identifier, CrossbreedRecipe> identifierCrossbreedRecipeMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<String, CrossbreedRecipe> result = new HashMap<>();

        int skipped = 0;

        for (var entry : identifierCrossbreedRecipeMap.entrySet()) {
            CrossbreedRecipe recipe = entry.getValue();
            if (recipe.chance() <= 0) {
                skipped++;
                LOGGER.info("Agrigen: crossbreed recipe {} has invalid chance {}, skipping", entry.getKey(), recipe.chance());
                continue;
            }
            if (!ItemHelper.itemExists(recipe.parent1()) || !ItemHelper.itemExists(recipe.parent2()) || !ItemHelper.itemExists(recipe.result())) {
                skipped++;
                continue;
            }
            result.put(makeKey(recipe.parent1(), recipe.parent2()), recipe);
        }

        RECIPES = result;
        LOGGER.info("Agrigen: loaded {} crossbreed recipes ({} skipped)", RECIPES.size(), skipped);
    }

    @Nullable
    public static CrossbreedRecipe find(String speciesA, String speciesB) {
        return RECIPES.get(makeKey(speciesA, speciesB));
    }

    public static Collection<CrossbreedRecipe> getAllRecipes() {
        return RECIPES.values();
    }

    private static String makeKey(String a, String b) {
        return a.compareTo(b) <= 0 ? a + "|" + b : b + "|" + a;
    }
}
