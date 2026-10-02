package com.mrpup.agrigen.block;

import com.mrpup.agrigen.item.upgrade.UpgradeHelper;
import com.mrpup.agrigen.item.upgrade.UpgradeType;
import com.mrpup.clumapi.blocks.BaseMachineTile;
import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.agrigen.component.slot.ModSlotTypes;
import com.mrpup.agrigen.component.upgrade.UpgradeComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MachineBlockTile extends BaseMachineTile {

    private final InventoryComponent upgrades;
    private final UpgradeComponent upgradesStorage;

    public MachineBlockTile(BlockEntityType<?> type, BlockPos pos, BlockState state, int xProg, int yProg, int progressTicks, ProgressTypes.ProgressType arrow) {
        super(type, pos, state, xProg, yProg, progressTicks, arrow);

        upgradesStorage = new UpgradeComponent(176, 4);
        addComponent(upgradesStorage);

        upgrades = new InventoryComponent("upgrades", 181, 10, 1, 4, ModSlotTypes.SLOT_UPGRADE);
        addComponent(upgrades);
    }

    public InventoryComponent getUpgradesInventory() {
        return upgrades;
    }

    @Override
    public float getSpeedMultiplier() {
        return UpgradeHelper.getTotalValue(upgrades, UpgradeType.SPEED, 1f);
    }

    @Override
    public float getEfficiencyMultiplier() {
        return UpgradeHelper.getTotalValue(upgrades, UpgradeType.EFFICIENCY, 1f);
    }

    @Override
    public float getRadiusBonus() {
        return UpgradeHelper.getTotalValue(upgrades, UpgradeType.RADIUS, 1f);
    }
}
