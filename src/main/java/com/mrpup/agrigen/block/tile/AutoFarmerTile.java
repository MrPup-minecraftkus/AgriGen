package com.mrpup.agrigen.block.tile;

import com.mrpup.agrigen.block.MachineBlockTile;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.block.crop.CropStickBlock;
import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.component.slot.ModSlotTypes;
import com.mrpup.agrigen.config.machine.AutoFarmerConfig;
import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.slot.SlotTypes;
import com.mrpup.clumapi.component.tank.FluidTankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.common.ticket.AABBTicket;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class AutoFarmerTile extends MachineBlockTile {

    private static final int ENERGY_CAPACITY = AutoFarmerConfig.maxStoredPower;
    private static final int ENERGY_PER_TICK = AutoFarmerConfig.powerPerTick;
    private static final int SCAN_INTERVAL = AutoFarmerConfig.scanInterval;

    private static final int RADIUS_Y = 2;

    private static final int TANK_CAPACITY = 10000;

    private final InventoryComponent fertilizerSlot;
    private final InventoryComponent herbicideSlot;
    private final InventoryComponent output;
    private final FluidTankComponent waterTank;

    private int scanCooldown = 0;

    private AABBTicket waterTicket;
    private int ticketRadiusXZ = -1;

    public AutoFarmerTile(BlockPos pos, BlockState state) {
        super(ModBlocks.AUTO_FARMER.getBlockEntityType(), pos, state, 99999, 99999, 1, ProgressTypes.ProgressType.ARROW);

        setMenuType(() -> ModBlocks.AUTO_FARMER.getMenuType());

        energyComponent(ENERGY_CAPACITY,  ENERGY_PER_TICK);

        waterTank = new FluidTankComponent("input_tank", 28, 20, TANK_CAPACITY, 8);
        addComponent(waterTank);

        herbicideSlot = new InventoryComponent("herbicide", 51, 22, 2, 1, ModSlotTypes.HERBICIDE_SLOT);
        addComponent(herbicideSlot);

        fertilizerSlot = new InventoryComponent("fertilizer", 51, 40, 2, 2, ModSlotTypes.FERTILIZER_SLOT);
        addComponent(fertilizerSlot);

        output = new InventoryComponent("output", 95, 22, 4, 3, SlotTypes.OUTPUT_SLOT_ITEM);
        output.setAllOutput();
        addComponent(output);
    }

    private int getEffectiveRadiusXZ() {
        return (int) (4 + getRadiusBonus());
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) return;

        if (canRecipeBySettings()) {
            boolean progressComplete = advanceProgress(ENERGY_PER_TICK);
            if (scanCooldown-- <= 0 && progressComplete) {
                scanCooldown = SCAN_INTERVAL;
                scanAndFarm(serverLevel, pos);
            }
        }

        setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    private void scanAndFarm(ServerLevel level, BlockPos center) {
        CropStickBlock cropBlock = (CropStickBlock) ModBlocks.CROP_STICK.getBlock();

        for (int dx = -getEffectiveRadiusXZ(); dx <= getEffectiveRadiusXZ(); dx++) {
            for (int dz = -getEffectiveRadiusXZ(); dz <= getEffectiveRadiusXZ(); dz++) {
                for (int dy = -RADIUS_Y; dy <= RADIUS_Y; dy++) {
                    BlockPos targetPos = center.offset(dx, dy, dz);
                    BlockEntity be = level.getBlockEntity(targetPos);

                    if (be instanceof CropStickBlockEntity cropEntity) {
                        tendCrop(level, targetPos, cropEntity, cropBlock);
                    }

                    FluidStack fluidInTank = getWaterTank().getTank().getFluid();
                    if (!fluidInTank.isEmpty() && fluidInTank.is(Fluids.WATER)) {
                        if (getWaterTank().getTank().getFluidAmount() >= 5) {
                            BlockState state = level.getBlockState(targetPos);
                            if (state.getBlock() instanceof FarmBlock) {
                                if (state.getValue(FarmBlock.MOISTURE) < 7) {
                                    level.setBlock(targetPos, state.setValue(FarmBlock.MOISTURE, 7), 2);
                                    FluidStack drainStack = new FluidStack(Fluids.WATER.getSource(), 2);
                                    getWaterTank().getTank().drain(drainStack, IFluidHandler.FluidAction.EXECUTE);
                                } else {
                                    updateWaterTicket(level, center);
                                    FluidStack drainStack = new FluidStack(Fluids.WATER.getSource(), 1);
                                    getWaterTank().getTank().drain(drainStack, IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateWaterTicket(ServerLevel level, BlockPos center) {
        int r = getEffectiveRadiusXZ();
        if (waterTicket == null || !waterTicket.isValid() || r != ticketRadiusXZ) {
            clearWaterTicket();
            AABB area = new AABB(center).inflate(r, RADIUS_Y, r);
            waterTicket = FarmlandWaterManager.addAABBTicket(level, area);
            ticketRadiusXZ = r;
        }
    }

    private void clearWaterTicket() {
        if (waterTicket != null) {
            waterTicket.invalidate();
            waterTicket = null;
        }
    }

    @Override
    public void setRemoved() {
        clearWaterTicket();
        super.setRemoved();
    }

    private void tendCrop(ServerLevel level, BlockPos pos, CropStickBlockEntity cropEntity, CropStickBlock cropBlock) {
        if (cropEntity.hasSeed() && !cropEntity.isWeed()) {
            if (!cropEntity.hasFertilizer()) {
                tryApplyFertilizer(cropEntity);
            }
            if (!cropEntity.hasHerbicide()) {
                tryApplyHerbicide(cropEntity);
            }
        }

        if (cropEntity.hasSeed() && cropEntity.isFullyGrown()) {
            cropBlock.harvest(cropEntity, level, pos, drop -> insertOrDrop(level, pos, drop));
        }
    }

    private void tryApplyFertilizer(CropStickBlockEntity cropEntity) {
        int slotIndex = InventoryComponent.findFirstNonEmptySlot(fertilizerSlot);
        if (slotIndex == -1) return;

        ItemStack stack = fertilizerSlot.getContainer().getItem(slotIndex);

        cropEntity.setFertilizer(15000);
        stack.shrink(1);
    }

    private void tryApplyHerbicide(CropStickBlockEntity cropEntity) {
        int slotIndex = InventoryComponent.findFirstNonEmptySlot(herbicideSlot);
        if (slotIndex == -1) return;

        ItemStack stack = herbicideSlot.getContainer().getItem(slotIndex);

        cropEntity.setHerbicide(3);
        if (cropEntity.getLevel() instanceof ServerLevel serverLevel) {
            stack.hurtAndBreak(1, serverLevel, null, (item) -> {
                herbicideSlot.getContainer().setItem(slotIndex, ItemStack.EMPTY);
            });
        }
    }

    private void insertOrDrop(ServerLevel level, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) return;

        output.addOrStack(stack);

        if (!stack.isEmpty()) {
            Block.popResource(level, pos, stack);
        }
    }

    public InventoryComponent getOutput() {
        return output;
    }

    public FluidTankComponent getWaterTank() {
        return waterTank;
    }
}
