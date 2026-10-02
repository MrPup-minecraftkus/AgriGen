package com.mrpup.agrigen.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlantDropHelper {
    public static List<ItemStack> getHarvestDrops(ServerLevel level, ItemStack seedStack, BlockPos pos) {
        Block cropBlock = resolveCropBlock(seedStack);
        if (cropBlock == null) return List.of();

        BlockState fullyGrownState = getFullyGrownState(cropBlock);

        LootParams.Builder params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withParameter(LootContextParams.BLOCK_STATE, fullyGrownState)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, null);

        return fullyGrownState.getDrops(params);
    }

    @Nullable
    private static Block resolveCropBlock(ItemStack seedStack) {
        Item item = seedStack.getItem();

        if (item instanceof BlockItem blockItem) {
            return blockItem.getBlock();
        }

        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof CropBlock cropBlock) {
                BlockState defaultState = cropBlock.defaultBlockState();
                ItemStack pickResult = block.getCloneItemStack(null, null, defaultState, true, null);
                if (!pickResult.isEmpty() && pickResult.is(item)) {
                    return cropBlock;
                }
            }
        }

        return null;
    }

    private static BlockState getFullyGrownState(Block cropBlock) {
        if (cropBlock instanceof CropBlock crop) {
            return crop.getStateForAge(crop.getMaxAge());
        }

        return cropBlock.defaultBlockState();
    }
}
