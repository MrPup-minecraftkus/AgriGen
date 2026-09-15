package com.mrpup.agrigen.block;

import com.mrpup.agrigen.item.upgrade.UpgradeHelper;
import com.mrpup.agrigen.item.upgrade.UpgradeType;
import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.lock.ILockable;
import com.mrpup.clumapi.component.lock.LockComponent;
import com.mrpup.clumapi.component.progress.ProgressComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.redstone.IRedstoneControllable;
import com.mrpup.clumapi.component.redstone.RedstoneComponent;
import com.mrpup.clumapi.component.storage.EnergyShowerComponent;
import com.mrpup.clumapi.component.storage.EnergyStorageComponent;
import com.mrpup.agrigen.component.slot.ModSlotTypes;
import com.mrpup.agrigen.component.upgrade.UpgradeComponent;
import com.mrpup.clumapi.component.storage.EnergyStorageProvider;
import com.mrpup.clumapi.component.storage.IEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class MachineBlockTile extends ComponentBlockEntity implements EnergyStorageProvider, ILockable, IRedstoneControllable, IEnergy {

    private final InventoryComponent upgrades;
    private final UpgradeComponent upgradesStorage;
    private final EnergyStorageComponent energyStorage;
    private final EnergyShowerComponent energyShowerComponent;
    private final ProgressComponent progressBar;
    private final RedstoneComponent redstoneModeComponent;
    private final LockComponent lockModeComponent;

    private String ownerName = "";
    private UUID ownerUUID = null;

    private int capacity = 10000;
    private int perTickEnergy = 40;
    private int receive = 1000;

    private int redstoneMode = 0;
    private int lockMode = 0;
    private int lockMethod = 0;

    private float progressAccumulator = 0f;

    public void energyComponent(int cap, int perTick) {
        this.capacity = cap;
        this.perTickEnergy = perTick;
    }

    public static final int ENERGY_X = 6, ENERGY_Y = 20;
    public static int PROGRESS_X = 6;
    public static int PROGRESS_Y = 20;
    public static ProgressTypes.ProgressType ARROW_TYPE;

    public MachineBlockTile(BlockEntityType<?> type, BlockPos pos, BlockState state, int xProg, int yProg, int progressTicks, ProgressTypes.ProgressType arrow) {
        super(type, pos, state);

        upgradesStorage = new UpgradeComponent(176, 4);
        addComponent(upgradesStorage);

        upgrades = new InventoryComponent("upgrades", 181, 10, 1, 4, ModSlotTypes.SLOT_UPGRADE);
        addComponent(upgrades);

        energyStorage = new EnergyStorageComponent(capacity, receive, ENERGY_X, ENERGY_Y);
        addComponent(energyStorage);

        energyShowerComponent = new EnergyShowerComponent(6, 78, this);
        addComponent(energyShowerComponent);

        progressBar = new ProgressComponent(xProg, yProg, progressTicks, arrow);
        addComponent(progressBar);

        PROGRESS_X = xProg;
        PROGRESS_Y = yProg;
        ARROW_TYPE = arrow;

        redstoneModeComponent = new RedstoneComponent(150, 78, this, 0);
        addComponent(redstoneModeComponent);

        lockModeComponent = new LockComponent(-27, 4, this, 1, 2);
        addComponent(lockModeComponent);
    }

    public static int getProgressX() {
        return PROGRESS_X;
    }

    public static int getProgressY() {
        return PROGRESS_Y;
    }

    public static ProgressTypes.ProgressType getArrowType() {
        return ARROW_TYPE;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPerTickEnergy() {
        return perTickEnergy;
    }

    public boolean canRecipeBySettings() {
        return canRecipeByEnergy() && canRecipeByRedstone();
    }

    public boolean canRecipeByEnergy() {
        return energyStorage.getEnergyStored() >=  getPerTickEnergy();
    }

    public boolean canRecipeByRedstone() {
        boolean hasSignal = this.level.hasNeighborSignal(this.worldPosition);

        if (this.level != null && !this.level.isClientSide) {
            if (redstoneMode == 0) {
                return true;
            } else if (redstoneMode == 1) {
                return hasSignal;
            } else if (redstoneMode == 2) {
                return !hasSignal;
            }
        }
        return true;
    }

    public EnergyStorageComponent getEnergyStorageMachine() {
        return energyStorage;
    }

    public int getEnergy() {
        return energyStorage.getEnergyStored();
    }

    public int extractEnergy(int toExtract, boolean simulate) {
        return energyStorage.extractEnergy(toExtract, simulate);
    }

    public ProgressComponent getProgressBar() {
        return progressBar;
    }

    public void setProgressTile(int prog) {
        progressBar.setProgress(prog);;
    }

    public int getProgressTile() {
        return progressBar.getProgress();
    }

    public int getMaxProgressTile() {
        return progressBar.getMaxProgress();
    }

    public int getRedstoneMode() {
        return redstoneMode;
    }

    public void cycleRedstoneMode() {
        this.redstoneMode = (this.redstoneMode + 1) % 3;
        setChanged();
    }

    public int getLockMode() {
        return lockMode;
    }

    public void cycleLockMode(Player player) {
        this.lockMode = (this.lockMode + 1) % 2;

        if (this.lockMode == 1 && player != null) {
            this.ownerName = player.getGameProfile().getName();
            this.ownerUUID = player.getUUID();
        }

        setChanged();
    }

    public int getLockMethod() {
        return lockMethod;
    }

    public void cycleLockMethod() {
        this.lockMethod = (this.lockMethod + 1) % 2;
        setChanged();
    }

    public boolean canPlayerAccess(Player player) {
        if (lockMode == 0) {
            return true;
        }

        if (player == null) {
            return false;
        }

        if (lockMethod == 0) {
            return ownerName != null && ownerName.equalsIgnoreCase(player.getGameProfile().getName());
        } else {
            return ownerUUID != null && ownerUUID.equals(player.getUUID());
        }
    }

    public String getOwnerName() {
        return ownerName;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public InventoryComponent getUpgradesInventory() {
        return upgrades;
    }

    public float getSpeedMultiplier() {
        return UpgradeHelper.getTotalValue(upgrades, UpgradeType.SPEED, 1f);
    }

    public float getEfficiencyMultiplier() {
        return UpgradeHelper.getTotalValue(upgrades, UpgradeType.EFFICIENCY, 1f);
    }

    public float getRadiusBonus() {
        return UpgradeHelper.getTotalValue(upgrades, UpgradeType.RADIUS, 1f);
    }

    public int getEffectiveEnergyCost(int baseCost) {
        return Math.max(1, Math.round(baseCost / getEfficiencyMultiplier()));
    }

    public boolean advanceProgress(int baseEnergyCost) {
        isWork = true;
        int energyCost = getEffectiveEnergyCost(baseEnergyCost) + ( 10 * (int) getSpeedMultiplier());
        extractEnergy(energyCost, false);

        progressAccumulator += getSpeedMultiplier();
        int wholeProgress = (int) progressAccumulator;
        progressAccumulator -= wholeProgress;

        setProgressTile(getProgressTile() + wholeProgress);

        if (this.level != null) {
            this.level.getLightEngine().checkBlock(this.worldPosition);
        }

        if (getProgressTile() >= getMaxProgressTile()) {
            setProgressTile(0);
            progressAccumulator = 0f;
            isWork = false;
            if (this.level != null) {
                this.level.getLightEngine().checkBlock(this.worldPosition);
            }
            return true;
        }
        return false;
    }

    @Override
    public EnergyStorageComponent getEnergyStorage() {
        return getEnergyStorageMachine();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", getEnergy());
        tag.putInt("progress", getProgressTile());
        tag.putInt("redstoneMode", getRedstoneMode());
        tag.putInt("lockMode", getLockMode());
        tag.putInt("lockMethod", getLockMethod());
        tag.putString("ownerName", ownerName == null ? "" : ownerName);
        if (ownerUUID != null) {
            tag.putUUID("ownerUUID", ownerUUID);
        }
        tag.putFloat("progressAccumulator", progressAccumulator);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("energy")) {
            energyStorage.setEnergyStored(tag.getInt("energy"));
        }
        if (tag.contains("progress")) {
            progressBar.setProgress(tag.getInt("progress"));
        }
        if (tag.contains("redstoneMode")) {
            redstoneMode = tag.getInt("redstoneMode");
        }
        if (tag.contains("lockMode")) {
            lockMode = tag.getInt("lockMode");
        }
        if (tag.contains("lockMethod")) {
            lockMethod = tag.getInt("lockMethod");
        }
        if (tag.contains("ownerName")) {
            ownerName = tag.getString("ownerName");
        }
        if (tag.hasUUID("ownerUUID")) {
            ownerUUID = tag.getUUID("ownerUUID");
        }
        if (tag.contains("progressAccumulator")) {
            progressAccumulator = tag.getFloat("progressAccumulator");
        }
    }
}
