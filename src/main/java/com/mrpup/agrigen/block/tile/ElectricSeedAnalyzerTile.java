package com.mrpup.agrigen.block.tile;

import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.slot.SlotTypes;
import com.mrpup.agrigen.block.MachineBlockTile;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.component.slot.ModSlotTypes;
import com.mrpup.agrigen.config.machine.ElectricSeedAnalyzerConfig;
import com.mrpup.agrigen.genetics.GenomeDefaults;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricSeedAnalyzerTile extends MachineBlockTile {

    private static final int ENERGY_CAPACITY = ElectricSeedAnalyzerConfig.maxStoredPower;
    private static final int ENERGY_PER_TICK = ElectricSeedAnalyzerConfig.powerPerTick;
    private static final int PROCESS_TICKS = ElectricSeedAnalyzerConfig.maxProgress;

    private final InventoryComponent output;
    private final InventoryComponent seedsInput;

    public ElectricSeedAnalyzerTile(BlockPos pos, BlockState state) {
        super(ModBlocks.ELECTRIC_SEED_ANALYZER.getBlockEntityType(), pos, state, 61,  41,  PROCESS_TICKS, ProgressTypes.ProgressType.LONG_ARROW);

        setMenuType(() -> ModBlocks.ELECTRIC_SEED_ANALYZER.getMenuType());

        energyComponent(ENERGY_CAPACITY,  ENERGY_PER_TICK);

        seedsInput = new InventoryComponent("seeds_input", 36, 41, 1, 1, ModSlotTypes.SLOT_SEEDS);
        addComponent(seedsInput);

        output = new InventoryComponent("output", 129, 40, 1, 1, SlotTypes.BIG_SLOT_ITEM);
        output.setAllOutput();
        addComponent(output);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        recipe();
        setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    public void recipe() {
        ItemStack inputStack = seedsInput.getContainer().getItem(0);
        boolean hasSeed = !inputStack.isEmpty();

        ItemStack outputStack = output.getContainer().getItem(0);
        boolean hasSlotForOutput = outputStack.isEmpty()
                || (hasSeed && ItemStack.isSameItemSameComponents(outputStack, getAnalyzedResult(inputStack))
                && outputStack.getCount() + inputStack.getCount() <= outputStack.getMaxStackSize());

        if (hasSeed && canRecipeBySettings() && hasSlotForOutput) {
            boolean recipeComplete = advanceProgress(ENERGY_PER_TICK);

            if (recipeComplete) {
                int count = inputStack.getCount();

                ItemStack analyzedTemplate = getAnalyzedResult(inputStack);

                seedsInput.getContainer().setItem(0, ItemStack.EMPTY);

                ItemStack currentOutput = output.getContainer().getItem(0);
                if (currentOutput.isEmpty()) {
                    ItemStack resultStack = analyzedTemplate.copy();
                    resultStack.setCount(count);
                    output.getContainer().setItem(0, resultStack);
                } else {
                    currentOutput.grow(count);
                    output.getContainer().setItem(0, currentOutput);
                }
            }
        } else if (!hasSeed) {
            setProgressTile(0);
        }
    }

    private ItemStack getAnalyzedResult(ItemStack inputStack) {
        ItemStack result = new ItemStack(inputStack.getItem(), 1);

        CompoundTag genomeTag;

        if (inputStack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag existingTag = inputStack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (existingTag.contains("genome")) {
                genomeTag = existingTag.getCompound("genome");
            } else {
                genomeTag = GenomeDefaults.getDefaultGenome(inputStack.getItem());
            }
        } else {
            genomeTag = GenomeDefaults.getDefaultGenome(inputStack.getItem());
        }

        CompoundTag resultTag = new CompoundTag();
        resultTag.putBoolean("analyzed", true);
        resultTag.put("genome", genomeTag);

        result.set(DataComponents.CUSTOM_DATA, CustomData.of(resultTag));

        return result;
    }
}
