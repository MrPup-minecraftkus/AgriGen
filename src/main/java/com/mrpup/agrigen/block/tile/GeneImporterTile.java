package com.mrpup.agrigen.block.tile;

import com.mrpup.agrigen.block.MachineBlockTile;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.component.slot.ModSlotTypes;
import com.mrpup.agrigen.config.machine.GeneImporterConfig;
import com.mrpup.agrigen.plant.AllHelper;
import com.mrpup.agrigen.item.ModItems;
import com.mrpup.agrigen.item.genetic.GeneticTemplateItem;
import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.slot.SlotTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GeneImporterTile extends MachineBlockTile {

    private static final int ENERGY_CAPACITY = GeneImporterConfig.maxStoredPower;
    private static final int ENERGY_PER_TICK = GeneImporterConfig.powerPerTick;
    private static final int PROCESS_TICKS = GeneImporterConfig.maxProgress;

    private final InventoryComponent output;
    private final InventoryComponent seedsInput;
    private final InventoryComponent templateGeneInput;

    public GeneImporterTile(BlockPos pos, BlockState state) {
        super(ModBlocks.GENE_IMPORTER.getBlockEntityType(), pos, state, 61,  41,  PROCESS_TICKS, ProgressTypes.ProgressType.LONG_ARROW);

        setMenuType(() -> ModBlocks.GENE_IMPORTER.getMenuType());

        energyComponent(ENERGY_CAPACITY,  ENERGY_PER_TICK);

        seedsInput = new InventoryComponent("seeds_input", 36, 30, 1, 1, ModSlotTypes.SLOT_SEEDS);
        addComponent(seedsInput);

        templateGeneInput = new InventoryComponent("template_gene_input", 36, 51, 1, 1, ModSlotTypes.SLOT_GENE_TEMPLATE);
        addComponent(templateGeneInput);

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

    private boolean hasGenesToImplant(ItemStack templateStack) {
        if (!templateStack.is(ModItems.GENETIC_TEMPLATE.get())) {
            return false;
        }
        return !GeneticTemplateItem.getGenesTag(templateStack).isEmpty();
    }

    public void recipe() {
        ItemStack inputSeedStack = seedsInput.getContainer().getItem(0);
        ItemStack templateStack = templateGeneInput.getContainer().getItem(0);

        boolean hasSeed = !inputSeedStack.isEmpty();
        boolean hasTemplate = !templateStack.isEmpty() && hasGenesToImplant(templateStack);

        ItemStack outputStack = output.getContainer().getItem(0);
        boolean hasSlotForOutput = outputStack.isEmpty();

        if (hasSeed && canRecipeBySettings() && hasSlotForOutput && hasTemplate) {
            boolean recipeComplete = advanceProgress(ENERGY_PER_TICK);

            if (recipeComplete) {
                implantGenes(inputSeedStack, templateStack);
            }
        }
    }

    private void implantGenes(ItemStack seedStack, ItemStack templateStack) {
        if (!seedStack.has(DataComponents.CUSTOM_DATA)) {
            return;
        }

        CompoundTag seedTag = seedStack.get(DataComponents.CUSTOM_DATA).copyTag();
        if (!seedTag.contains("genome")) {
            return;
        }

        CompoundTag genome = seedTag.getCompound("genome");
        CompoundTag templateGenes = GeneticTemplateItem.getGenesTag(templateStack);

        if (templateGenes.isEmpty()) {
            return;
        }

        for (String geneKey : AllHelper.GENE_KEYS) {
            if (!templateGenes.contains(geneKey)) continue;
            Tag value = templateGenes.get(geneKey);
            if (value != null) {
                genome.put(geneKey, value.copy());
            }
        }

        seedTag.put("genome", genome);

        Item finalItem = seedStack.getItem();

        if (genome.contains("speciesId")) {
            String newSpeciesId = genome.getString("speciesId");
            ResourceLocation loc = ResourceLocation.tryParse(newSpeciesId);

            if (loc != null) {
                Item targetItem = BuiltInRegistries.ITEM.get(loc);


                if (targetItem != BuiltInRegistries.ITEM.get(BuiltInRegistries.ITEM.getDefaultKey())) {
                    finalItem = targetItem;
                }
            }
        }

        ItemStack resultStack = new ItemStack(finalItem, 1);
        resultStack.set(DataComponents.CUSTOM_DATA, CustomData.of(seedTag));

        seedStack.shrink(1);

        output.getContainer().setItem(0, resultStack);

        setChanged();
    }
}
