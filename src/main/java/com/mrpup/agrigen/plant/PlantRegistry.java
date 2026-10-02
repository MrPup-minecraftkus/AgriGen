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

import java.util.HashMap;
import java.util.Map;

public class PlantRegistry {

    public enum PlantType {
        CROP,
        BERRIES,
        STEM,
        NETHER_WART,
        STACKING,
        MUSHROOM,
        FUNGUS,
        SAPLING,
        FLOWER
    }

    private static final Map<Item, PlantType> TYPES = new HashMap<>();
    private static final Map<Item, Block> SEED_TO_CROP_CACHE = new HashMap<>();
    private static final Map<Item, Block> SEED_TO_FRUIT_CACHE = new HashMap<>();
    private static final Map<Block, Block> STEM_TO_FRUIT = new HashMap<>();

    private static boolean initialized = false;

    public static void registerStemFruit(Block stem, Block fruit) {
        STEM_TO_FRUIT.put(stem, fruit);
    }

    public static void register() {
        if (initialized) return;
        initialized = true;

        registerStemFruit(Blocks.PUMPKIN_STEM, Blocks.PUMPKIN);
        registerStemFruit(Blocks.MELON_STEM, Blocks.MELON);

        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof CropBlock) {
                registerBlockPlant(block, PlantType.CROP);
            } else if (block instanceof StemBlock) {
                Block fruit = STEM_TO_FRUIT.get(block);
                if (fruit != null) {
                    Item seed = registerBlockPlant(block, PlantType.STEM);
                    if (seed != null) SEED_TO_FRUIT_CACHE.put(seed, fruit);
                }
            } else if (block instanceof NetherWartBlock) {
                registerBlockPlant(block, PlantType.NETHER_WART);
            } else if (block instanceof SweetBerryBushBlock) {
                registerBlockPlant(block, PlantType.BERRIES);
            }
        }

        registerPlant(Items.SUGAR_CANE, PlantType.STACKING);
        registerPlant(Items.CACTUS, PlantType.STACKING);
        registerPlant(Items.BAMBOO, PlantType.STACKING);

        registerPlant(Items.BROWN_MUSHROOM, PlantType.MUSHROOM);
        registerPlant(Items.RED_MUSHROOM, PlantType.MUSHROOM);

        /*
        registerPlant(Items.CRIMSON_FUNGUS, PlantType.FUNGUS);
        registerPlant(Items.WARPED_FUNGUS, PlantType.FUNGUS);
         */
        registerPlant(Items.SWEET_BERRIES, PlantType.BERRIES);
    }

    public static void registerPlant(Item item, PlantType type) {
        TYPES.put(item, type);
    }

    @Nullable
    private static Item registerBlockPlant(Block block, PlantType type) {
        Item seed = block.asItem();
        if (seed == Items.AIR) {
            return null;
        }

        TYPES.put(seed, type);
        SEED_TO_CROP_CACHE.put(seed, block);
        return seed;
    }

    @Nullable
    public static Block getFruit(Item seed) {
        return SEED_TO_FRUIT_CACHE.get(seed);
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
            return crop.getStateForAge(scale(ourStage, maxAge));
        } else if (cropBlock instanceof StemBlock stem) {
            return stem.defaultBlockState()
                    .setValue(StemBlock.AGE, scale(ourStage, StemBlock.MAX_AGE));
        } else if (cropBlock instanceof NetherWartBlock wart) {
            return wart.defaultBlockState()
                    .setValue(NetherWartBlock.AGE, scale(ourStage, 3));
        } else if (cropBlock instanceof SweetBerryBushBlock bush) {
            return bush.defaultBlockState()
                    .setValue(SweetBerryBushBlock.AGE, scale(ourStage, 3));
        }

        return cropBlock.defaultBlockState();
    }

    private static int scale(int ourStage, int maxAge) {
        int scaled = Math.round((float) ourStage / CropStickBlockEntity.MAX_STAGE * maxAge);
        return Math.max(0, Math.min(scaled, maxAge));
    }
}
