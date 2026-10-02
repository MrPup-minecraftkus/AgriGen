package com.mrpup.agrigen.genetics;

import com.mrpup.agrigen.plugin.agriculture.GenomeDefaultsAgriculture;
import com.mrpup.agrigen.plugin.farmersdelight.GenomeDefaultsDelight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.Map;

public class GenomeDefaults {
    private static final Map<Item, GenomeProfile> DEFAULTS = new HashMap<>();

    public record GenomeProfile(
            String speciesId,
            int growSpeed,
            int resistance,
            int yield,
            int dayCycleGrow,
            int minTemp,
            int maxTemp,
            String effect
    ) {
        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("speciesId", speciesId);
            tag.putInt("growSpeed", growSpeed);
            tag.putInt("resistance", resistance);
            tag.putInt("yield", yield);
            tag.putInt("dayCycleGrow", dayCycleGrow);
            tag.putInt("minTemp", minTemp);
            tag.putInt("maxTemp", maxTemp);
            tag.putString("effect", effect);
            return tag;
        }
    }

    public static void register(Item seedItem, GenomeProfile profile) {
        DEFAULTS.put(seedItem, profile);
    }

    public static CompoundTag getDefaultGenome(Item seedItem) {
        GenomeProfile profile = DEFAULTS.get(seedItem);

        if (profile == null) {
            String id = BuiltInRegistries.ITEM.getKey(seedItem).toString();
            profile = new GenomeProfile(id, 3, 3, 3, 1, -1, 1, "");
        }
        return profile.toTag();
    }

    public static void registerSeed(Item item, int growSpeed, int resistance, int yield, int dayCycleGrow, int minTemp, int maxTemp) {
        String registryName = BuiltInRegistries.ITEM.getKey(item).toString();
        register(item, new GenomeProfile(registryName, growSpeed, resistance, yield, dayCycleGrow, minTemp, maxTemp, ""));
    }

    public static void registerSeed(Item item, int growSpeed, int resistance, int yield, int dayCycleGrow, int minTemp, int maxTemp, String effect) {
        String registryName = BuiltInRegistries.ITEM.getKey(item).toString();
        register(item, new GenomeProfile(registryName, growSpeed, resistance, yield, dayCycleGrow, minTemp, maxTemp, effect));
    }


    public static void bootstrap() {
        registerSeed(Items.WHEAT_SEEDS, 5, 3, 3, 1, -1, 1, "solar");
        registerSeed(Items.CARROT, 3, 4, 4, 1, -1, 1, "social");
        registerSeed(Items.POTATO, 2, 5, 3, 1, -1, 1, "social");
        registerSeed(Items.CACTUS, 1, 6, 2, 1, 1, 2, "solitary");
        registerSeed(Items.SUGAR_CANE, 4, 3, 3, 1, 0, 2);
        registerSeed(Items.BROWN_MUSHROOM, 2, 4, 2, 2, -2, 0);
        registerSeed(Items.RED_MUSHROOM, 2, 4, 2, 2, -2, 0, "poisonous");
        registerSeed(Items.ACACIA_SAPLING, 2, 8, 3, 1, 0, 2, "solitary");
        registerSeed(Items.SPRUCE_SAPLING, 2, 8, 3, 1, -1, 1, "solitary");
        registerSeed(Items.BIRCH_SAPLING, 2, 7, 3, 1, -1, 1, "solitary");
        registerSeed(Items.CHERRY_SAPLING, 2, 7, 3, 1, -1, 1, "solitary");
        registerSeed(Items.OAK_SAPLING, 2, 8, 3, 1, -1, 1, "solitary");
        registerSeed(Items.DARK_OAK_SAPLING, 2, 9, 4, 1, 0, 2, "solitary");
        registerSeed(Items.JUNGLE_SAPLING, 2, 8, 5, 1, 0, 2, "solitary");
        registerSeed(Items.NETHER_WART, 6, 4, 4, 0, 1, 2);
        registerSeed(Items.SWEET_BERRIES, 5, 4, 5, 1, -2, 1, "social");
        registerSeed(Items.MELON_SEEDS, 3, 6, 5, 1, -1, 1, "solitary");
        registerSeed(Items.PUMPKIN_SEEDS, 3, 6, 5, 1, -1, 1, "solitary");


        if (ModList.get().isLoaded("farmersdelight")) {
            GenomeDefaultsDelight.bootstrap();
        }

        if (ModList.get().isLoaded("mysticalagriculture")) {
            GenomeDefaultsAgriculture.bootstrap();
        }
    }
}
