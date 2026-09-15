package com.mrpup.agrigen.block.tile;

import com.mrpup.agrigen.block.MachineBlockTile;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.config.machine.CraftSynthesizerConfig;
import com.mrpup.agrigen.recipe.ModRecipeTypes;
import com.mrpup.agrigen.recipe.machine.CraftSynthesizerRecipe;
import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.slot.SlotTypes;
import com.mrpup.clumapi.component.tank.FluidTankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;

public class CraftSynthesizerTile extends MachineBlockTile {

    private static final int ENERGY_CAPACITY = CraftSynthesizerConfig.maxStoredPower;
    private static final int ENERGY_PER_TICK = CraftSynthesizerConfig.powerPerTick;
    private static final int PROCESS_TICKS = CraftSynthesizerConfig.maxProgress;
    private static final int TANK_CAPACITY = 4000;
    private static final int TANK_CAPACITY_OUTPUT = 10000;

    private final InventoryComponent output;
    private final FluidTankComponent dnaTank;
    private final FluidTankComponent dnaTank_output;
    private final InventoryComponent input1;
    private final InventoryComponent input2;
    private final InventoryComponent input3;
    private final InventoryComponent input4;

    public static final int INPUT_1_X = 52, INPUT_1_Y = 20;
    public static final int INPUT_2_X = 30, INPUT_2_Y = 42;
    public static final int INPUT_3_X = 74, INPUT_3_Y = 42;
    public static final int INPUT_4_X = 52, INPUT_4_Y = 65;
    public static final int FLUID_IN_X = 51, FLUID_IN_Y = 41;
    public static final int OUTPUT_X = 127, OUTPUT_Y = 22;
    public static final int FLUID_OUT_X = 152, FLUID_OUT_Y = 20;

    public CraftSynthesizerTile(BlockPos pos, BlockState state) {
        super(ModBlocks.CRAFT_SYNTHESIZER.getBlockEntityType(), pos, state, 97,  41,  PROCESS_TICKS, ProgressTypes.ProgressType.ARROW);

        setMenuType(() -> ModBlocks.CRAFT_SYNTHESIZER.getMenuType());

        energyComponent(ENERGY_CAPACITY,  ENERGY_PER_TICK);

        dnaTank = new FluidTankComponent("input_tank", FLUID_IN_X, FLUID_IN_Y, TANK_CAPACITY, 6);
        addComponent(dnaTank);

        input1 = new InventoryComponent("input_1", INPUT_1_X, INPUT_1_Y, 1, 1, SlotTypes.SLOT_ITEM);
        addComponent(input1);

        input2 = new InventoryComponent("input_2", INPUT_2_X, INPUT_2_Y, 1, 1, SlotTypes.SLOT_ITEM);
        addComponent(input2);

        input3 = new InventoryComponent("input_3", INPUT_3_X, INPUT_3_Y, 1, 1, SlotTypes.SLOT_ITEM);
        addComponent(input3);

        input4 = new InventoryComponent("input_4", INPUT_4_X, INPUT_4_Y, 1, 1, SlotTypes.SLOT_ITEM);
        addComponent(input4);

        dnaTank_output = new FluidTankComponent("output_tank", FLUID_OUT_X, FLUID_OUT_Y, TANK_CAPACITY_OUTPUT, 7);
        addComponent(dnaTank_output);


        output = new InventoryComponent("output", OUTPUT_X, OUTPUT_Y, 1, 3, SlotTypes.OUTPUT_SLOT_ITEM);
        output.setAllOutput();
        addComponent(output);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        recipe(level);
        setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    private void recipe(Level level) {
        CraftSynthesizerRecipe.Input recipeInput = buildRecipeInput();

        Optional<RecipeHolder<CraftSynthesizerRecipe>> match =
                level.getRecipeManager().getRecipeFor(ModRecipeTypes.CRAFT_SYNTHESIZER.get(), recipeInput, level);

        if (match.isEmpty()) {
            return;
        }

        CraftSynthesizerRecipe recipe = match.get().value();

        if (!canFitOutput(recipe.output())) {
            return;
        }

        boolean recipeComplete = advanceProgress(ENERGY_PER_TICK);

        if (recipeComplete) {
            craftRecipe(level, recipe, recipeInput);
        }
    }

    private CraftSynthesizerRecipe.Input buildRecipeInput() {
        return new CraftSynthesizerRecipe.Input(
                List.of(
                        input1.getContainer().getItem(0),
                        input2.getContainer().getItem(0),
                        input3.getContainer().getItem(0),
                        input4.getContainer().getItem(0)
                ),
                dnaTank.getTank().getFluid()
        );
    }

    private boolean canFitOutput(ItemStack result) {
        ItemStack current = output.getContainer().getItem(0);
        if (current.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(current, result)
                && current.getCount() + result.getCount() <= current.getMaxStackSize();
    }

    private void craftRecipe(Level level, CraftSynthesizerRecipe recipe, CraftSynthesizerRecipe.Input input) {
        ItemStack result = recipe.assemble(input, level.registryAccess());

        input1.getContainer().removeItem(0, 1);
        input2.getContainer().removeItem(0, 1);
        input3.getContainer().removeItem(0, 1);
        input4.getContainer().removeItem(0, 1);

        FluidStack tankFluid = dnaTank.getTank().getFluid();
        FluidStack drainStack = new FluidStack(tankFluid.getFluid(), recipe.fluidInput().amount());
        dnaTank.getTank().drain(drainStack, IFluidHandler.FluidAction.EXECUTE);

        ItemStack current = output.getContainer().getItem(0);
        if (current.isEmpty()) {
            output.getContainer().setItem(0, result);
        } else {
            current.grow(result.getCount());
        }

        setChanged();
    }
}
