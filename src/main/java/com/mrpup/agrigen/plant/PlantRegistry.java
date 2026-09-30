package com.mrpup.agrigen.plant;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.clumapi.helper.ItemHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlantRegistry {

    public enum PlantType {
        CROP,
        //STEM,
        NETHER_WART,
        STACKING,
        MUSHROOM,
        FUNGUS,
        SAPLING,
        FLOWER
    }

    private static final Map<Item, PlantType> TYPES = new HashMap<>();
    private static final Map<Item, Block> SEED_TO_CROP_CACHE = new HashMap<>();
    //private static final Map<Item, Block> SEED_TO_FRUIT_CACHE = new HashMap<>();

    private static boolean initialized = false;

    public static void register() {
        if (initialized) return;
        initialized = true;

        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof CropBlock) {
                registerBlockPlant(block, PlantType.CROP);
            } /*
            else if (block instanceof StemBlock stem) {
                Block fruit = safeResolveFruit(stem);
                if (fruit != null) {
                    Item seed = registerBlockPlant(block, PlantType.STEM, true);
                    if (seed != null) SEED_TO_FRUIT_CACHE.put(seed, fruit);
                }

            } */ else if (block instanceof NetherWartBlock) {
                registerBlockPlant(block, PlantType.NETHER_WART);
            }
        }

        registerPlant(Items.SUGAR_CANE, PlantType.STACKING);
        registerPlant(Items.CACTUS, PlantType.STACKING);
        registerPlant(Items.BAMBOO, PlantType.STACKING);

        registerPlant(Items.BROWN_MUSHROOM, PlantType.MUSHROOM);
        registerPlant(Items.RED_MUSHROOM, PlantType.MUSHROOM);

        registerPlant(Items.CRIMSON_FUNGUS, PlantType.FUNGUS);
        registerPlant(Items.WARPED_FUNGUS, PlantType.FUNGUS);
    }

    public static void registerPlant(Item item, PlantType type) {
        TYPES.put(item, type);
    }

    @Nullable
    private static Item registerBlockPlant(Block block, PlantType type) {
        ItemStack seedStack = block.getCloneItemStack(block.defaultBlockState(), null, null, null, null);
        if (seedStack.isEmpty()) return null;

        Item seed = seedStack.getItem();
        TYPES.put(seed, type);
        SEED_TO_CROP_CACHE.put(seed, block);
        return seed;
    }

    public static PlantType getType(Item item) {
        PlantType type = TYPES.get(item);
        if (type != null) return type;

        ItemStack stack = item.getDefaultInstance();
        if (stack.is(ItemTags.SAPLINGS)) return PlantType.SAPLING;
        if (stack.is(ItemTags.FLOWERS)) return PlantType.FLOWER;
        return null;
    }

    @Nullable
    public static BlockState resolveGrowthState(String seedId, int ourStage) {
        Item seedItem = ItemHelper.getItemFromString(seedId);

        Block cropBlock = SEED_TO_CROP_CACHE.get(seedItem);
        if (cropBlock == null) return null;

        if (cropBlock instanceof CropBlock crop) {
            int maxAge = crop.getMaxAge();
            int scaledAge = Math.round((float) ourStage / CropStickBlockEntity.MAX_STAGE * maxAge);
            scaledAge = Math.min(scaledAge, maxAge);
            return crop.getStateForAge(scaledAge);
        } /*else if (cropBlock instanceof StemBlock stem) {
                int scaledAge = Math.min(ourStage, 7);
                return stem.defaultBlockState().setValue(StemBlock.AGE, scaledAge);
            }
            */ else if (cropBlock instanceof NetherWartBlock crop) {
            int maxAge = 3;
            int scaledAge = Math.round((float) ourStage / CropStickBlockEntity.MAX_STAGE * maxAge);
            scaledAge = Math.min(scaledAge, maxAge);
            return crop.defaultBlockState().setValue(NetherWartBlock.AGE, scaledAge);
        }

        return cropBlock.defaultBlockState();
    }
}
