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
import com.mrpup.clumapi.helper.ItemHelper;
import com.mrpup.clumapi.helper.OutputHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

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
        if (level.isClientSide()) return;
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

        ItemStack result = (hasSeed && hasTemplate) ? buildResult(inputSeedStack, templateStack) : ItemStack.EMPTY;

        boolean canOutput = !result.isEmpty() && OutputHelper.canFit(output.getContainer(), result);

        if (canOutput && canRecipeBySettings()) {
            boolean recipeComplete = advanceProgress(ENERGY_PER_TICK);

            if (recipeComplete) {
                inputSeedStack.shrink(1);
                OutputHelper.insertAll(output.getContainer(), List.of(result));
                setChanged();
            }
        } else {
            setProgressTile(0);
        }
    }

    private ItemStack buildResult(ItemStack seedStack, ItemStack templateStack) {
        CustomData data = seedStack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return ItemStack.EMPTY;

        CompoundTag seedTag = data.copyTag();
        if (!seedTag.contains("genome")) return ItemStack.EMPTY;

        CompoundTag genome = seedTag.getCompoundOrEmpty("genome");
        CompoundTag templateGenes = GeneticTemplateItem.getGenesTag(templateStack);
        if (templateGenes.isEmpty()) return ItemStack.EMPTY;

        for (String geneKey : AllHelper.GENE_KEYS) {
            if (!templateGenes.contains(geneKey)) continue;
            Tag value = templateGenes.get(geneKey);
            if (value != null) {
                genome.put(geneKey, value.copy());
            }
        }
        seedTag.put("genome", genome);

        Item finalItem = seedStack.getItem();

        Optional<String> speciesIdOpt = genome.getString("speciesId");
        if (speciesIdOpt.isPresent()) {
            Identifier loc = Identifier.tryParse(speciesIdOpt.get());
            if (loc != null) {
                Optional<Holder.Reference<Item>> holder = ItemHelper.getItemHolderFromLoc(loc);
                if (holder.isPresent() && holder.get().value() != Items.AIR) {
                    finalItem = holder.get().value();
                }
            }
        }

        ItemStack resultStack = new ItemStack(finalItem, 1);
        resultStack.set(DataComponents.CUSTOM_DATA, CustomData.of(seedTag));
        return resultStack;
    }
}
