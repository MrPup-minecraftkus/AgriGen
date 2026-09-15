package com.mrpup.agrigen.plant;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlantRegistry {

    private static final Map<Item, Block> SEED_TO_CROP_CACHE = new HashMap<>();
    private static final Map<Item, Block> SEED_TO_FRUIT_CACHE = new HashMap<>();
    private static final Set<Item> STACKING_PLANTS = new HashSet<>();
    private static final Set<Item> MUSHROOMS = new HashSet<>();

    private static boolean initialized = false;

    public static void register() {
        if (initialized) return;
        initialized = true;

        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof CropBlock cropBlock) {
                try {
                    BlockState defaultState = cropBlock.defaultBlockState();
                    ItemStack seedStack = block.getCloneItemStack(defaultState, null, null, null, null);
                    if (!seedStack.isEmpty()) {
                        SEED_TO_CROP_CACHE.put(seedStack.getItem(), cropBlock);
                    }
                } catch (Exception ignored) {}
            } else if (block instanceof StemBlock stemBlock) {
                try {
                    Block fruit = StemFruitResolver.resolveFruit(stemBlock);
                    if (fruit != null) {
                        ItemStack seedStack = block.getCloneItemStack(block.defaultBlockState(), null, null, null, null);
                        if (!seedStack.isEmpty()) {
                            SEED_TO_CROP_CACHE.put(seedStack.getItem(), stemBlock);
                            SEED_TO_FRUIT_CACHE.put(seedStack.getItem(), fruit);
                        }
                    }
                } catch (Exception ignored) {}
            } else if (block instanceof NetherWartBlock netherWartBlock) {
                try {
                    ItemStack seedStack = block.getCloneItemStack(block.defaultBlockState(), null, null, null, null);
                    if (!seedStack.isEmpty()) {
                        SEED_TO_CROP_CACHE.put(seedStack.getItem(), netherWartBlock);
                    }
                } catch (Exception ignored) {}
            }
        }

        registerStackingPlant(Items.SUGAR_CANE);
        registerStackingPlant(Items.CACTUS);
        registerMushroom(Items.BROWN_MUSHROOM);
        registerMushroom(Items.RED_MUSHROOM);
    }

    @Nullable
    public static BlockState resolveGrowthState(String seedId, int ourStage) {
        try {
            ResourceLocation itemId = ResourceLocation.parse(seedId);
            Item seedItem = BuiltInRegistries.ITEM.get(itemId);

            Block cropBlock = SEED_TO_CROP_CACHE.get(seedItem);
            if (cropBlock == null) return null;

            if (cropBlock instanceof CropBlock crop) {
                int maxAge = crop.getMaxAge();
                int scaledAge = Math.round((float) ourStage / CropStickBlockEntity.MAX_STAGE * maxAge);
                scaledAge = Math.min(scaledAge, maxAge);
                return crop.getStateForAge(scaledAge);
            } else if (cropBlock instanceof StemBlock stem) {
                int maxAge = 7;
                int scaledAge = Math.min(ourStage, maxAge);
                return stem.defaultBlockState().setValue(StemBlock.AGE, scaledAge);
            } else if (cropBlock instanceof NetherWartBlock crop) {
                int maxAge = 3;
                int scaledAge = Math.round((float) ourStage / CropStickBlockEntity.MAX_STAGE * maxAge);
                scaledAge = Math.min(scaledAge, maxAge);
                return crop.defaultBlockState().setValue(NetherWartBlock.AGE, scaledAge);
            }

            return cropBlock.defaultBlockState();
        } catch (Exception e) {
            return null;
        }
    }

    private static void registerStackingPlant(Item item) {
        STACKING_PLANTS.add(item);
    }

    public static boolean isStackingPlant(Item seedItem) {
        return STACKING_PLANTS.contains(seedItem);
    }

    private static void registerMushroom(Item item) {
        MUSHROOMS.add(item);
    }

    public static boolean isMushroom(Item seedItem) {
        return MUSHROOMS.contains(seedItem);
    }

    public static boolean isSapling(ItemStack item) {
        return item.is(ItemTags.SAPLINGS);
    }

    public static boolean isStemCrop(String seedId) {
        Item item = resolveItem(seedId);
        return item != null && SEED_TO_FRUIT_CACHE.containsKey(item);
    }

    @Nullable
    public static BlockState resolveFruitState(String seedId) {
        Item item = resolveItem(seedId);
        if (item == null) return null;

        Block fruit = SEED_TO_FRUIT_CACHE.get(item);
        return fruit != null ? fruit.defaultBlockState() : null;
    }

    @Nullable
    private static Item resolveItem(String seedId) {
        try {
            ResourceLocation itemId = ResourceLocation.parse(seedId);
            return BuiltInRegistries.ITEM.get(itemId);
        } catch (Exception e) {
            return null;
        }
    }
}
