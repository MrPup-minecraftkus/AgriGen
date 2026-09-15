package com.mrpup.agrigen.block.tile;

import com.mrpup.agrigen.block.MachineBlockTile;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.component.slot.ModSlotTypes;
import com.mrpup.agrigen.config.machine.DNAExtractorConfig;
import com.mrpup.agrigen.fluid.ModFluids;
import com.mrpup.agrigen.item.ModItems;
import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.slot.SlotTypes;
import com.mrpup.clumapi.component.tank.FluidTankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class DNAExtractorTile extends MachineBlockTile {

    private static final int ENERGY_CAPACITY = DNAExtractorConfig.maxStoredPower;
    private static final int ENERGY_PER_TICK = DNAExtractorConfig.powerPerTick;
    private static final int PROCESS_TICKS = DNAExtractorConfig.maxProgress;
    private static final int DNA_PER_SEED = 100;
    private static final int TANK_CAPACITY = 10000;

    private final InventoryComponent output;
    private final InventoryComponent seedsInput;
    private final FluidTankComponent dnaTank;

    public DNAExtractorTile(BlockPos pos, BlockState state) {
        super(ModBlocks.DNA_EXTRACTOR.getBlockEntityType(), pos, state, 61,  41,  PROCESS_TICKS, ProgressTypes.ProgressType.LONG_ARROW);

        setMenuType(() -> ModBlocks.DNA_EXTRACTOR.getMenuType());

        energyComponent(ENERGY_CAPACITY,  ENERGY_PER_TICK);

        seedsInput = new InventoryComponent("seeds_input", 36, 41, 1, 1, ModSlotTypes.SLOT_SEEDS);
        addComponent(seedsInput);

        output = new InventoryComponent("output", 127, 22, 1, 3, SlotTypes.OUTPUT_SLOT_ITEM);
        output.setAllOutput();
        addComponent(output);

        dnaTank = new FluidTankComponent("output_tank", 152, 20, TANK_CAPACITY, 4);
        addComponent(dnaTank);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        recipe();
        setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    public void recipe() {
        ItemStack inputSeedStack = seedsInput.getContainer().getItem(0);
        boolean hasSeed = !inputSeedStack.isEmpty();
        boolean hasTankSpace = dnaTank.getTank().getFluidAmount() + DNA_PER_SEED <= dnaTank.getCapacity();

        ItemStack wasteStack = ModItems.GENETIC_WASTE.get().getDefaultInstance();
        boolean hasSlotForOutput = output.canFit(wasteStack);

        if (hasSeed && canRecipeBySettings() && hasTankSpace && hasSlotForOutput) {
            boolean recipeComplete = advanceProgress(ENERGY_PER_TICK);

            if (recipeComplete) {
                FluidStack dnaFluid = new FluidStack(ModFluids.LIQUID_DNA.getSource(), DNA_PER_SEED);
                dnaTank.getTank().fill(dnaFluid, IFluidHandler.FluidAction.EXECUTE);

                output.addOrStack(wasteStack.copyWithCount(1));

                inputSeedStack.shrink(1);
                setChanged();
            }
        }
    }
}
