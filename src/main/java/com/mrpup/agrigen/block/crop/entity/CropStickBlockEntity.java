package com.mrpup.agrigen.block.crop.entity;

import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.config.gene.GenesConfig;
import com.mrpup.agrigen.plant.crossbreed.CrossbreedHelper;
import com.mrpup.agrigen.plant.crossbreed.SpecialCrossbreedHelper;
import com.mrpup.agrigen.plant.effect.PlantEffectRegistry;
import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CropStickBlockEntity extends ComponentBlockEntity {

    public static final int GLOBAL_MULTIPLIER = 5;

    public static final int TICKS_PER_STAGE = 300 * GLOBAL_MULTIPLIER;
    public static final int MAX_STAGE = 7;
    public static final int FRUIT_GROWTH_TICKS = 200;

    public static final int MAX_FERTILIZER = 16000;

    private String cropStickVariant = "single";

    private String effect = "";
    private float effectGrowthMultiplier = 1f;

    private boolean hasSeed = false;
    private boolean isWeed = false;
    private String speciesId = "";
    private String seedId = "";
    private int herbicide = 0;
    private int fertilizer = 0;

    private int dayCycleGrow = 1;
    private int resistance = 1;
    private int growSpeed = 1;
    private int yield = 1;
    private int minTemp = -1;
    private int maxTemp = 1;

    private boolean fullyGrow = false;
    private int growthTicks = 0;
    private int fruitTicks = 0;

    private float fertilizerBonus = 1f;

    public CropStickBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CROP_STICK.getBlockEntityType(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CropStickBlockEntity entity) {
        int stageBefore = entity.getStage();
        int res = entity.getResistance();

        if (entity.hasSeed()) {
            entity.seedId = entity.speciesId;

            if (canGrowNow(level, pos, entity)) {
                entity.setChanged();

                if (entity.hasFertilizer()) {
                    entity.fertilizerBonus = 2f;
                    entity.setFertilizer(-1);
                    entity.setChanged();
                } else {
                    entity.fertilizerBonus = 1f;
                    entity.setChanged();
                }

                float growSpeedEffect = entity.getGrowSpeedBonusFromGene() * entity.effectGrowthMultiplier *  entity.fertilizerBonus;

                while (growSpeedEffect > 0) {
                    if (growSpeedEffect >= 1.0f) {
                        entity.growthTicks++;
                        growSpeedEffect -= 1.0f;
                    } else {
                        if (Math.random() < growSpeedEffect) {
                            entity.growthTicks++;
                        }
                        break;
                    }
                }

                if (entity.isFullyGrown()) {
                    entity.fullyGrow = true;
                    entity.setChanged();
                }
            }

            if (level.getGameTime() % 20 == 0) {
                entity.effectGrowthMultiplier = PlantEffectRegistry.getGrowthMultiplier(entity);
            }

            PlantEffectRegistry.tick(entity);

        } else if (res > 0 && "double".equals(entity.getCropStickVariant())) {
            int scaled = 2000 * (res / 3);
            if (scaled > 0) {
                if (level.getRandom().nextInt(scaled) < 3) {
                    CrossbreedHelper.tryCrossbreed(level, pos, entity);
                }
                if (level.getRandom().nextInt(scaled) < 2) {
                    SpecialCrossbreedHelper.trySpecialCrossbreed(level, pos, entity);
                }
            }
        }

        if (res > 0 && !entity.isWeed) {
            int scaled = 10000 * res * res * GLOBAL_MULTIPLIER;
            if (scaled > 0 && level.getRandom().nextInt(scaled) < 1) {
                if (entity.resistance != GenesConfig.resistance) {
                    if (entity.hasHerbicide()) {
                        entity.setHerbicide(entity.getHerbicide() - 1);
                    } else {
                        entity.setWeed();
                        entity.setChanged();
                    }
                }
            }
        }

        if (!entity.hasSeed() && "double".equals(entity.getCropStickVariant()) && !entity.isWeed() && level.getRandom().nextInt(1000 * GLOBAL_MULTIPLIER) < 1) {
            CrossbreedHelper.spreadSeed(level, pos, entity);
            entity.setChanged();
        }

        if (entity.hasSeed() && entity.isFullyGrown() && entity.fruitTicks < FRUIT_GROWTH_TICKS) {
            entity.fruitTicks++;
            entity.setChanged();
        }

        if (res > 0 && !entity.isWeed()) {
            int scaled = 5000 * res * res * GLOBAL_MULTIPLIER;
            if (scaled > 0 && level.getRandom().nextInt(scaled) < 1) {
                if (entity.resistance != GenesConfig.resistance) {
                    if (entity.hasHerbicide()) {
                        entity.setHerbicide(entity.getHerbicide() - 1);
                    } else {
                        CrossbreedHelper.spreadWeed(level, pos, entity);
                        entity.setChanged();
                    }
                }
            }
        }


        if (entity.isWeed()) {
            entity.growthTicks++;
            entity.setChanged();
        }

        if (entity.getStage() != stageBefore) {
            entity.syncToClient();
        }
    }

    public int getDayCycleGrow() {
        return this.dayCycleGrow;
    }

    public boolean isWeed() {
        return this.isWeed;
    }

    public void setWeed() {
        this.isWeed = true;
        setCropStickVariant("single");
        this.removeSeed();
        this.setChanged();
        syncToClient();
    }

    public void removeWeed() {
        this.isWeed = false;
        setGrowthTicks(0);
        this.setChanged();
        syncToClient();
    }

    public void removeSeed() {
        this.hasSeed = false;
        this.seedId = "";
        this.speciesId = "";
        this.effect = "";
        this.resistance = 1;
        this.growSpeed = 1;
        this.yield = 1;
        this.dayCycleGrow = 0;
        this.minTemp = -1;
        this.maxTemp = 1;
        this.fullyGrow = false;
        this.fruitTicks = 0;
        setGrowthTicks(0);
        this.setChanged();
        syncToClient();
    }

    public void setSeed(String id) {
        setCropStickVariant("single");
        this.speciesId = id;
        this.seedId = id;
        this.hasSeed = true;
        this.setChanged();
        syncToClient();
    }

    public String getEffect() {
        return this.effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
        this.setChanged();
    }

    public void setFertilizer(int val) {
        this.fertilizer = Math.max(0, Math.min(MAX_FERTILIZER, this.fertilizer + val));
        this.setChanged();
    }

    public int getFertilizer() {
        return this.fertilizer;
    }

    public boolean hasFertilizer() {
        return this.fertilizer > 0;
    }

    public boolean hasMaxFertilizer() {
        return this.fertilizer > 24000;
    }

    public boolean hasSeed() {
        return this.hasSeed;
    }

    public String getSeedId() {
        return this.seedId;
    }

    public int getYield() {
        return this.yield;
    }

    public int getResistance() {
        return this.resistance;
    }

    public int getGrowSpeed() {
        return this.growSpeed;
    }

    public float getGrowSpeedBonusFromGene() {
        return (float) this.growSpeed / 3;
    }

    public void setGrowthTicks(int ticks) {
        this.growthTicks = ticks;
        this.setChanged();
    }

    public int getGrowthTicks() {
        return this.growthTicks;
    }

    public int getTicksPerStage() {
        return TICKS_PER_STAGE;
    }

    public int getStage() {
        syncToClient();
        return Math.max(1, Math.min(this.growthTicks / getTicksPerStage(), MAX_STAGE));
    }

    public float getStageProgress() {
        int tps = getTicksPerStage();
        if (tps <= 0) return 0f;

        int stage = Math.max(1, Math.min(this.growthTicks / tps, MAX_STAGE));
        if (stage >= MAX_STAGE) return 1f;

        int start  = (stage == 1) ? 0 : stage * tps;
        int length = (stage == 1) ? 2 * tps : tps;

        return Math.min(1f, Math.max(0f, (this.growthTicks - start) / (float) length));
    }

    public boolean isFullyGrown() {
        return getStage() >= MAX_STAGE;
    }

    public void setFullyGrown(boolean var) {
        this.fullyGrow = var;
    }

    public int getFruitTicks() {
        return this.fruitTicks;
    }

    public int getMinTemp() {
        return this.minTemp;
    }

    public int getMaxTemp() {
        return this.maxTemp;
    }

    public float getFruitGrowthProgress() {
        syncToClient();
        return Math.min((float) fruitTicks / FRUIT_GROWTH_TICKS, 1f);
    }

    public void setCropStickVariant(String variant) {
        this.cropStickVariant = variant;
        this.setChanged();
        syncToClient();
    }

    public boolean hasHerbicide() {
        return this.herbicide > 0;
    }

    public boolean hasMaxHerbicide() {
        return this.herbicide == 3;
    }

    public int getHerbicide() {
        return this.herbicide;
    }

    public void setHerbicide(int val) {
        this.herbicide = val;
        this.setChanged();
    }

    public String getCropStickVariant() {
        return this.cropStickVariant;
    }

    public CompoundTag getGenome() {
        CompoundTag genomeTag = new CompoundTag();
        genomeTag.putString("seedId", this.seedId);
        genomeTag.putString("speciesId", this.speciesId);
        genomeTag.putInt("resistance", this.resistance);
        genomeTag.putInt("growSpeed", this.growSpeed);
        genomeTag.putInt("yield", this.yield);
        genomeTag.putInt("dayCycleGrow", this.dayCycleGrow);
        genomeTag.putInt("minTemp", this.minTemp);
        genomeTag.putInt("maxTemp", this.maxTemp);
        genomeTag.putString("effect", this.effect);
        return genomeTag;
    }

    public void setGenome(CompoundTag genomeTag) {
        this.seedId = genomeTag.getStringOr("seedId", "");
        this.speciesId = genomeTag.getStringOr("speciesId", "");
        this.resistance = genomeTag.getIntOr("resistance", 1);
        this.growSpeed = genomeTag.getIntOr("growSpeed", 1);
        this.yield = genomeTag.getIntOr("yield", 1);
        this.dayCycleGrow = genomeTag.getIntOr("dayCycleGrow", 1);
        this.minTemp = genomeTag.getIntOr("minTemp", -1);
        this.maxTemp = genomeTag.getIntOr("maxTemp", 1);
        this.effect = genomeTag.getStringOr("effect", "");
        this.setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putString("variant", this.cropStickVariant);
        output.putBoolean("hasSeed", this.hasSeed);
        output.putBoolean("isWeed", this.isWeed);
        output.putInt("herbicide", this.herbicide);
        output.putInt("fertilizer", this.fertilizer);

        ValueOutput seedOutput = output.child("seed");
        seedOutput.putString("seedId", this.seedId);
        seedOutput.putInt("ticks", this.growthTicks);
        seedOutput.putInt("fruitTicks", this.fruitTicks);
        seedOutput.putBoolean("fullyGrow", this.fullyGrow);

        ValueOutput genomeOutput = seedOutput.child("genome");
        genomeOutput.putString("speciesId", this.speciesId);
        genomeOutput.putInt("resistance", this.resistance);
        genomeOutput.putInt("growSpeed", this.growSpeed);
        genomeOutput.putInt("yield", this.yield);
        genomeOutput.putInt("dayCycleGrow", this.dayCycleGrow);
        genomeOutput.putInt("minTemp", this.minTemp);
        genomeOutput.putInt("maxTemp", this.maxTemp);
        genomeOutput.putString("effect", this.effect);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.hasSeed = input.getBooleanOr("hasSeed", false);
        this.isWeed = input.getBooleanOr("isWeed", false);
        this.cropStickVariant = input.getStringOr("variant", this.cropStickVariant);
        this.herbicide = input.getIntOr("herbicide", 0);
        this.fertilizer = input.getIntOr("fertilizer", 0);

        input.child("seed").ifPresent(seedInput -> {
            this.seedId = seedInput.getStringOr("seedId", "");
            this.growthTicks = seedInput.getIntOr("ticks", 0);
            this.fruitTicks = seedInput.getIntOr("fruitTicks", 0);
            this.fullyGrow = seedInput.getBooleanOr("fullyGrow", false);

            seedInput.child("genome").ifPresent(genomeInput -> {
                this.speciesId = genomeInput.getStringOr("speciesId", "");
                this.resistance = genomeInput.getIntOr("resistance", 0);
                this.growSpeed = genomeInput.getIntOr("growSpeed", 0);
                this.yield = genomeInput.getIntOr("yield", 0);
                this.dayCycleGrow = genomeInput.getIntOr("dayCycleGrow", 0);
                this.minTemp = genomeInput.getIntOr("minTemp", 0);
                this.maxTemp = genomeInput.getIntOr("maxTemp", 0);
                this.effect = genomeInput.getStringOr("effect", "");
            });
        });
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private static boolean canGrowNow(Level level, BlockPos pos, CropStickBlockEntity entity) {
        int timeGene = entity.getDayCycleGrow();

        if (timeGene == 1 && level.isDarkOutside()) return false;
        if (timeGene == 2 && !level.isDarkOutside()) return false;

        float biomeTemp = level.getBiome(pos).value().getBaseTemperature();
        float mappedTemp = mapBiomeTempToGeneScale(biomeTemp);

        if (mappedTemp < entity.getMinTemp() || mappedTemp > entity.getMaxTemp()) {
            return false;
        }

        return true;
    }

    private static float mapBiomeTempToGeneScale(float biomeTemp) {
        if (biomeTemp >= 2.0f) {
            return 1.99f;
        }
        else if (biomeTemp >= 1.2f) {
            return 1.15f;
        }
        else if (biomeTemp > 0.15f) {
            return (biomeTemp - 0.15f) / 1.05f;
        }
        else {
            return biomeTemp - 0.6f * 2;
        }
    }
}