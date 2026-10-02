package com.mrpup.agrigen.plant;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import static com.mrpup.agrigen.plant.PlantRegistry.getType;

public class AllHelper {

    public static boolean isPlantable(ItemStack stack) {
        if (stack.is(Tags.Items.SEEDS)
                || stack.is(ItemTags.FLOWERS)
                || stack.is(ItemTags.SAPLINGS)
                || stack.is(Items.BAMBOO)
                || stack.is(Items.NETHER_WART)
                || stack.is(Items.SUGAR_CANE)
                || stack.is(Items.BROWN_MUSHROOM)
                || stack.is(Items.RED_MUSHROOM)
                || stack.is(Items.CACTUS)
                || stack.is(Items.SWEET_BERRIES)) {
            return true;
        }

        if (stack.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock().builtInRegistryHolder().is(BlockTags.CROPS);
        }

        return false;
    }

    public static final String[] GENE_KEYS = {
            "speciesId", "growSpeed", "resistance", "yield", "dayCycleGrow", "minTemp", "maxTemp", "effect"
    };

    public static boolean isPlant(Item item) {
        return getType(item) != null;
    }

    public static boolean isCrop(Item item) {
        return getType(item) == PlantRegistry.PlantType.CROP;
    }

    public static boolean isStem(Item item) {
        return getType(item) == PlantRegistry.PlantType.STEM;
    }

    public static boolean isBerries(Item item) {
        return getType(item) == PlantRegistry.PlantType.BERRIES;
    }

    public static boolean isNetherWart(Item item) {
        return getType(item) == PlantRegistry.PlantType.NETHER_WART;
    }

    public static boolean isStackingPlant(Item item) {
        return getType(item) == PlantRegistry.PlantType.STACKING;
    }

    public static boolean isMushroom(Item item) {
        return getType(item) == PlantRegistry.PlantType.MUSHROOM;
    }

    public static boolean isFungus(Item item) {
        return getType(item) == PlantRegistry.PlantType.FUNGUS;
    }

    public static boolean isSapling(Item item) {
        return getType(item) == PlantRegistry.PlantType.SAPLING;
    }

    public static boolean isFlower(Item item) {
        return getType(item) == PlantRegistry.PlantType.FLOWER;
    }

}
