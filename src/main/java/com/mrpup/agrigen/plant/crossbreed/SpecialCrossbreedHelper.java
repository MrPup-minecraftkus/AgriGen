package com.mrpup.agrigen.plant.crossbreed;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipe;
import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpecialCrossbreedHelper {

    private static final Direction[] HORIZONTAL_DIRECTIONS = {
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    public static void trySpecialCrossbreed(Level level, BlockPos centerPos, CropStickBlockEntity centerEntity) {
        if (!"double".equals(centerEntity.getCropStickVariant()) || centerEntity.hasSeed()) {
            return;
        }

        List<CropStickBlockEntity> validParents = new ArrayList<>();

        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            CropStickBlockEntity neighbor = getNeighborWithSeed(level, centerPos, dir);
            if (neighbor != null) {
                validParents.add(neighbor);
            }
        }

        if (validParents.size() < 2) return;

        RandomSource random = level.getRandom();

        for (int i = 0; i < validParents.size(); i++) {
            for (int j = i + 1; j < validParents.size(); j++) {
                CropStickBlockEntity parentA = validParents.get(i);
                CropStickBlockEntity parentB = validParents.get(j);

                String speciesA = parentA.getGenome().getString("speciesId");
                String speciesB = parentB.getGenome().getString("speciesId");

                if (speciesA.equals(speciesB)) continue;

                CrossbreedRecipe recipe = CrossbreedRecipeManager.find(speciesA, speciesB);
                if (recipe == null) continue;

                if (random.nextInt(recipe.chance()) != 0) continue;

                CompoundTag childGenome = crossGenomes(validParents, recipe.result(), random);

                centerEntity.setSeed(recipe.result());
                centerEntity.setGenome(childGenome);
                return;
            }
        }
    }

    private static CompoundTag crossGenomes(List<CropStickBlockEntity> parents, String resultSpeciesId, RandomSource random) {
        CompoundTag result = new CompoundTag();

        result.putString("speciesId", resultSpeciesId);

        result.putInt("resistance", CrossbreedHelper.crossValueMutation(parents, "resistance", random));
        result.putInt("growSpeed", CrossbreedHelper.crossValueMutation(parents, "growSpeed", random));
        result.putInt("yield", CrossbreedHelper.crossValueMutation(parents, "yield", random));

        result.putInt("dayCycleGrow", CrossbreedHelper.inheritGrowthTimeGene(parents, random));
        result.putInt("minTemp", CrossbreedHelper.crossTempValue(parents, "minTemp", random));
        result.putInt("maxTemp", CrossbreedHelper.crossTempValue(parents, "maxTemp", random));
        result.putString("effect", CrossbreedHelper.inheritEffectGene(parents, random));

        return result;
    }

    @Nullable
    private static CropStickBlockEntity getNeighborWithSeed(Level level, BlockPos centerPos, Direction direction) {
        BlockPos neighborPos = centerPos.relative(direction);
        BlockEntity be = level.getBlockEntity(neighborPos);

        if (be instanceof CropStickBlockEntity cropEntity && "single".equals(cropEntity.getCropStickVariant()) && cropEntity.hasSeed()) {
            return cropEntity;
        }
        return null;
    }
}
