package com.mrpup.agrigen.block.tile;

import com.mrpup.agrigen.config.machine.GeneExtractorConfig;
import com.mrpup.agrigen.plant.AllHelper;
import com.mrpup.agrigen.item.ModItems;
import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.slot.SlotTypes;
import com.mrpup.agrigen.block.MachineBlockTile;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.component.slot.ModSlotTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GeneExtractorTile extends MachineBlockTile {

    private static final int ENERGY_CAPACITY = GeneExtractorConfig.maxStoredPower;
    private static final int ENERGY_PER_TICK = GeneExtractorConfig.powerPerTick;
    private static final int PROCESS_TICKS = GeneExtractorConfig.maxProgress;

    private final InventoryComponent output;
    private final InventoryComponent seedsInput;
    private final InventoryComponent sampleGeneInput;

    public GeneExtractorTile(BlockPos pos, BlockState state) {
        super(ModBlocks.GENE_EXTRACTOR.getBlockEntityType(), pos, state, 61,  41,  PROCESS_TICKS, ProgressTypes.ProgressType.LONG_ARROW);

        setMenuType(() -> ModBlocks.GENE_EXTRACTOR.getMenuType());

        energyComponent(ENERGY_CAPACITY,  ENERGY_PER_TICK);

        seedsInput = new InventoryComponent("seeds_input", 36, 30, 1, 1, ModSlotTypes.SLOT_SEEDS);
        addComponent(seedsInput);

        sampleGeneInput = new InventoryComponent("sample_gene_input", 36, 51, 1, 1, ModSlotTypes.SLOT_BLANK_GENE_SAMPLE);
        addComponent(sampleGeneInput);

        output = new InventoryComponent("output", 127, 22, 1, 3, SlotTypes.OUTPUT_SLOT_ITEM);
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
        ItemStack inputSeedStack = seedsInput.getContainer().getItem(0);
        ItemStack inputBlankStack = sampleGeneInput.getContainer().getItem(0);
        boolean hasSeed = !inputSeedStack.isEmpty();
        boolean hasBlank = !inputBlankStack.isEmpty();

        ItemStack templateResult = ModItems.GENE_SAMPLE.get().getDefaultInstance();
        boolean hasSlotForOutput = output.canFit(templateResult);

        if (hasSeed && canRecipeBySettings() && hasSlotForOutput && hasBlank) {
            boolean recipeComplete = advanceProgress(ENERGY_PER_TICK);

            if (recipeComplete) {
                getExtractResult(inputSeedStack, inputBlankStack);
            }
        }
    }

    private void getExtractResult(ItemStack seedStack, ItemStack blankStack) {
        if (!seedStack.has(DataComponents.CUSTOM_DATA)) {
            return;
        }

        CompoundTag seedTag = seedStack.get(DataComponents.CUSTOM_DATA).copyTag();
        if (!seedTag.contains("genome")) {
            return;
        }

        CompoundTag genome = seedTag.getCompound("genome");

        RandomSource random = this.level.getRandom();
        String chosenGene = AllHelper.GENE_KEYS[random.nextInt(AllHelper.GENE_KEYS.length)];

        ItemStack resultStack = ModItems.GENE_SAMPLE.get().getDefaultInstance();
        resultStack.setCount(1);

        CompoundTag resultTag = new CompoundTag();
        CustomData existing = resultStack.get(DataComponents.CUSTOM_DATA);
        if (existing != null) {
            resultTag = existing.copyTag();
        }

        resultTag.putString("geneType", chosenGene);

        if ("speciesId".equals(chosenGene)) {
            String geneValueString = genome.getString(chosenGene);
            resultTag.putString("geneValue", geneValueString);
        } else {
            int geneValueInt = genome.getInt(chosenGene);
            resultTag.putInt("geneValue", geneValueInt);
        }

        resultStack.set(DataComponents.CUSTOM_DATA, CustomData.of(resultTag));

        if (!output.addOrStack(resultStack)) {
            return;
        }

        seedStack.shrink(1);
        blankStack.shrink(1);

        setChanged();
    }

}
