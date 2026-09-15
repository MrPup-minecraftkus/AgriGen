package com.mrpup.agrigen.plant.crossbreed;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.config.gene.GenesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrossbreedHelper {

    private static final int CROSSBREED_CHANCE = 10;
    private static final int SPREAD_CHANCE = 10;
    private static final int MUTATION_RANGE = 2;

    private record GeneBounds(int min, int max) {}

    private static final Map<String, GeneBounds> STAT_GENE_BOUNDS = Map.of(
            "growSpeed", new GeneBounds(1, GenesConfig.growSpeed),
            "resistance", new GeneBounds(1, GenesConfig.resistance),
            "yield", new GeneBounds(1, GenesConfig.yield)
    );

    private static final Direction[] HORIZONTAL_DIRECTIONS = {
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    public static void tryCrossbreed(Level level, BlockPos centerPos, CropStickBlockEntity centerEntity) {
        List<CropStickBlockEntity> validParents = new ArrayList<>();

        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            CropStickBlockEntity neighbor = getNeighborWithSeed(level, centerPos, dir);
            if (neighbor != null) {
                validParents.add(neighbor);
            }
        }

        if (validParents.size() < 2) return;

        RandomSource random = level.getRandom();
        if (random.nextInt(CROSSBREED_CHANCE) != 0) return;

        CompoundTag childGenome = crossGenomesMutation(validParents, random);

        String childSeedId = childGenome.getString("speciesId");

        centerEntity.setSeed(childSeedId);
        centerEntity.setGenome(childGenome);
    }

    public static void spreadWeed(Level level, BlockPos centerPos, CropStickBlockEntity centerEntity) {
        List<CropStickBlockEntity> validParents = new ArrayList<>();

        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            CropStickBlockEntity neighbor = getNeighborWithWeed(level, centerPos, dir);
            if (neighbor != null) {
                validParents.add(neighbor);
            }
        }

        if (validParents.isEmpty()) return;

        RandomSource random = level.getRandom();
        if (random.nextInt(SPREAD_CHANCE) != 0) return;

        centerEntity.setWeed();
    }

    public static void spreadSeed(Level level, BlockPos centerPos, CropStickBlockEntity centerEntity) {
        List<CropStickBlockEntity> validParents = new ArrayList<>();

        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            CropStickBlockEntity neighbor = getNeighborWithSeed(level, centerPos, dir);
            if (neighbor != null) {
                validParents.add(neighbor);
            }
        }

        if (validParents.isEmpty()) return;

        RandomSource random = level.getRandom();
        if (random.nextInt(CROSSBREED_CHANCE) != 0) return;

        CompoundTag childGenome = crossGenomes(validParents, random);

        String childSeedId = childGenome.getString("speciesId");

        centerEntity.setSeed(childSeedId);
        centerEntity.setGenome(childGenome);
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

    @Nullable
    private static CropStickBlockEntity getNeighborWithWeed(Level level, BlockPos centerPos, Direction direction) {
        BlockPos neighborPos = centerPos.relative(direction);
        BlockEntity be = level.getBlockEntity(neighborPos);

        if (be instanceof CropStickBlockEntity cropEntity && cropEntity.isWeed()) {
            return cropEntity;
        }
        return null;
    }

    private static CompoundTag crossGenomesMutation(List<CropStickBlockEntity> parents, RandomSource random) {
        CompoundTag result = new CompoundTag();

        result.putString("speciesId", inheritSpeciesIdGene(parents, random));

        result.putInt("resistance", crossValueMutation(parents, "resistance", random));
        result.putInt("growSpeed", crossValueMutation(parents, "growSpeed", random));
        result.putInt("yield", crossValueMutation(parents, "yield", random));

        result.putInt("dayCycleGrow", inheritGrowthTimeGene(parents, random));
        result.putInt("minTemp", crossTempValue(parents, "minTemp", random));
        result.putInt("maxTemp", crossTempValue(parents, "maxTemp", random));
        result.putString("effect", inheritEffectGene(parents, random));

        return result;
    }

    public static String inheritEffectGene(List<CropStickBlockEntity> parents, RandomSource random) {
        return parents.get(random.nextInt(parents.size())).getGenome().getString("effect");
    }

    public static int crossValueMutation(List<CropStickBlockEntity> parents, String geneKey, RandomSource random) {
        int sum = 0;
        for (CropStickBlockEntity parent : parents) {
            sum += parent.getGenome().getInt(geneKey);
        }

        int average = Math.round((float) sum / parents.size());
        int mutation = random.nextInt(MUTATION_RANGE * 2 + 1) - MUTATION_RANGE;

        GeneBounds bounds = STAT_GENE_BOUNDS.get(geneKey);
        return clamp(average + mutation, bounds.min(), bounds.max());
    }

    private static CompoundTag crossGenomes(List<CropStickBlockEntity> parents, RandomSource random) {
        CompoundTag result = new CompoundTag();
        result.putString("speciesId", inheritSpeciesIdGene(parents, random));

        result.putInt("resistance", crossValue(parents, "resistance"));
        result.putInt("growSpeed", crossValue(parents, "growSpeed"));
        result.putInt("yield", crossValue(parents, "yield"));

        result.putInt("dayCycleGrow", inheritGrowthTimeGene(parents, random));
        result.putInt("minTemp", crossTempValue(parents, "minTemp", random));
        result.putInt("maxTemp", crossTempValue(parents, "maxTemp", random));

        return result;
    }

    public static int crossValue(List<CropStickBlockEntity> parents, String geneKey) {
        int sum = 0;
        for (CropStickBlockEntity parent : parents) {
            sum += parent.getGenome().getInt(geneKey);
        }

        int average = Math.round((float) sum / parents.size());

        GeneBounds bounds = STAT_GENE_BOUNDS.get(geneKey);
        return clamp(average, bounds.min(), bounds.max());
    }

    public static int crossTempValue(List<CropStickBlockEntity> parents, String geneKey, RandomSource random) {
        int sum = 0;
        int parentMin = Integer.MAX_VALUE;
        int parentMax = Integer.MIN_VALUE;

        for (CropStickBlockEntity parent : parents) {
            int value = parent.getGenome().getInt(geneKey);
            sum += value;
            parentMin = Math.min(parentMin, value);
            parentMax = Math.max(parentMax, value);
        }

        int average = Math.round((float) sum / parents.size());
        int mutation = random.nextInt(3) - 1;

        return clamp(average + mutation, parentMin, parentMax);
    }

    public static int inheritGrowthTimeGene(List<CropStickBlockEntity> parents, RandomSource random) {
        return parents.get(random.nextInt(parents.size())).getGenome().getInt("dayCycleGrow");
    }

    private static String inheritSpeciesIdGene(List<CropStickBlockEntity> parents, RandomSource random) {
        CropStickBlockEntity chosenParent = parents.get(random.nextInt(parents.size()));
        return chosenParent.getGenome().getString("speciesId");
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
