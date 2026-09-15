package com.mrpup.agrigen.plant;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class AllHelper {

    public static boolean isPlantable(ItemStack stack) {
        if (stack.is(Items.MELON_SEEDS) || stack.is(Items.PUMPKIN_SEEDS)) {
            return false;
        }

        if (stack.is(Tags.Items.SEEDS)
                || stack.is(ItemTags.FLOWERS)
                || stack.is(ItemTags.SAPLINGS)
                || stack.is(Items.BAMBOO)
                || stack.is(Items.NETHER_WART)
                || stack.is(Items.SUGAR_CANE)
                || stack.is(Items.BROWN_MUSHROOM)
                || stack.is(Items.RED_MUSHROOM)
                || stack.is(Items.CACTUS)) {
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

}
