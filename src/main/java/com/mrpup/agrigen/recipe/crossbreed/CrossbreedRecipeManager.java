package com.mrpup.agrigen.recipe.crossbreed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CrossbreedRecipeManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();
    private static final String FOLDER = "crossbreed_recipes";

    private static Map<String, CrossbreedRecipe> RECIPES = Map.of();
    private static final Logger LOGGER = LogUtils.getLogger();

    public CrossbreedRecipeManager() {
        super(GSON, FOLDER);
    }

    private static boolean itemExists(String id) {
        ResourceLocation loc = ResourceLocation.tryParse(id);
        return loc != null && BuiltInRegistries.ITEM.containsKey(loc);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<String, CrossbreedRecipe> map = new HashMap<>();

        int skipped = 0;

        for (var entry : resourceLocationJsonElementMap.entrySet()) {
            try {
                JsonObject json = entry.getValue().getAsJsonObject();

                String parent1 = json.get("parent1").getAsString();
                String parent2 = json.get("parent2").getAsString();
                String result = json.get("result").getAsString();
                int chance = json.has("chance") ? json.get("chance").getAsInt() : 10;

                if (chance <= 0) {
                    LOGGER.info("Agrigen: crossbreed recipe {} has invalid chance {}, skipping", entry.getKey(), chance);
                    continue;
                }

                if (!itemExists(parent1) || !itemExists(parent2) || !itemExists(result)) {
                    skipped++;
                    continue;
                }

                map.put(makeKey(parent1, parent2), new CrossbreedRecipe(parent1, parent2, result, chance));
            } catch (Exception e) {
                LOGGER.info("Agrigen: failed to parse crossbreed recipe {}: {}", entry.getKey(), e.getMessage());
            }
        }

        RECIPES = map;
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
