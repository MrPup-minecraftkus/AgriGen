package com.mrpup.agrigen.block.tile;

import com.mrpup.agrigen.block.MachineBlockTile;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.component.slot.ModSlotTypes;
import com.mrpup.agrigen.config.machine.PlantSynthesizerConfig;
import com.mrpup.agrigen.plant.AllHelper;
import com.mrpup.agrigen.fluid.ModFluids;
import com.mrpup.agrigen.item.genetic.GeneticTemplateItem;
import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.slot.SlotTypes;
import com.mrpup.clumapi.component.tank.FluidTankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class PlantSynthesizerTile extends MachineBlockTile {

    private static final int ENERGY_CAPACITY = PlantSynthesizerConfig.maxStoredPower;
    private static final int ENERGY_PER_TICK = PlantSynthesizerConfig.powerPerTick;
    private static final int PROCESS_TICKS = PlantSynthesizerConfig.maxProgress;
    private static final int TANK_CAPACITY = 4000;

    private final InventoryComponent output;
    private final FluidTankComponent dnaTank;
    private final InventoryComponent templateGeneInput;

    public PlantSynthesizerTile(BlockPos pos, BlockState state) {
        super(ModBlocks.PLANT_SYNTHESIZER.getBlockEntityType(), pos, state, 61,  41,  PROCESS_TICKS, ProgressTypes.ProgressType.LONG_ARROW);

        setMenuType(() -> ModBlocks.PLANT_SYNTHESIZER.getMenuType());

        energyComponent(ENERGY_CAPACITY,  ENERGY_PER_TICK);

        dnaTank = new FluidTankComponent("input_tank", 35, 51, TANK_CAPACITY, 5);
        addComponent(dnaTank);

        templateGeneInput = new InventoryComponent("template_gene_input", 36, 30, 1, 1, ModSlotTypes.SLOT_GENE_TEMPLATE);
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

    public void recipe() {
        ItemStack templateStack = templateGeneInput.getContainer().getItem(0);

        boolean hasTemplate = !templateStack.isEmpty()
                && GeneticTemplateItem.hasAllGenes(templateStack);

        boolean hasEnoughDna = dnaTank.getTank().getFluidAmount() >= 1000;

        boolean hasSlotForOutput = output.getContainer().getItem(0).isEmpty();

        if (hasTemplate && canRecipeBySettings() && hasEnoughDna && hasSlotForOutput) {
            boolean recipeComplete = advanceProgress(ENERGY_PER_TICK);

            if (recipeComplete) {
                synthesizerSeed(templateStack);
            }
        }
    }

    private void synthesizerSeed(ItemStack templateStack) {
        CompoundTag genes = GeneticTemplateItem.getGenesTag(templateStack);

        if (!genes.contains("speciesId")) {
            return;
        }

        String speciesId = genes.getString("speciesId");

        Item seedItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(speciesId));
        if (seedItem == Items.AIR) {
            return;
        }

        ItemStack resultStack = seedItem.getDefaultInstance();
        resultStack.setCount(1);

        CompoundTag genomeTag = new CompoundTag();
        for (String key : AllHelper.GENE_KEYS) {
            if (genes.contains(key)) {
                genomeTag.put(key, genes.get(key).copy());
            }
        }

        CompoundTag resultTag = new CompoundTag();
        CustomData existing = resultStack.get(DataComponents.CUSTOM_DATA);
        if (existing != null) {
            resultTag = existing.copyTag();
        }
        resultTag.putBoolean("analyzed", true);
        resultTag.put("genome", genomeTag);
        resultStack.set(DataComponents.CUSTOM_DATA, CustomData.of(resultTag));

        FluidStack drainStack = new FluidStack(ModFluids.LIQUID_DNA.getSource(), 1000);
        dnaTank.getTank().drain(drainStack, IFluidHandler.FluidAction.EXECUTE);

        output.getContainer().setItem(0, resultStack);

        setChanged();
    }
}
