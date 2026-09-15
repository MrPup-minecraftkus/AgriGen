package com.mrpup.agrigen.recipe.crossbreed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CrossbreedRecipeManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();
    private static final String FOLDER = "crossbreed_recipes";

    private static Map<String, CrossbreedRecipe> RECIPES = Map.of();

    public CrossbreedRecipeManager() {
        super(GSON, FOLDER);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<String, CrossbreedRecipe> map = new HashMap<>();

        for (var entry : resourceLocationJsonElementMap.entrySet()) {
            try {
                JsonObject json = entry.getValue().getAsJsonObject();

                String parent1 = json.get("parent1").getAsString();
                String parent2 = json.get("parent2").getAsString();
                String result = json.get("result").getAsString();
                int chance = json.has("chance") ? json.get("chance").getAsInt() : 10;

                if (chance <= 0) {
                    System.err.println("Agrigen: crossbreed recipe " + entry.getKey() + " has invalid chance " + chance + ", skipping");
                    continue;
                }

                map.put(makeKey(parent1, parent2), new CrossbreedRecipe(parent1, parent2, result, chance));
            } catch (Exception e) {
                System.err.println("Agrigen: failed to parse crossbreed recipe " + entry.getKey() + ": " + e.getMessage());
            }
        }

        RECIPES = map;
        System.out.println("Agrigen: loaded " + RECIPES.size() + " crossbreed recipes");
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
