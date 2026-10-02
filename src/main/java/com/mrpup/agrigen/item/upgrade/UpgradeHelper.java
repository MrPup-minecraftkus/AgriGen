package com.mrpup.agrigen.item.upgrade;

import com.mrpup.clumapi.component.InventoryComponent;
import net.minecraft.world.item.ItemStack;

public class UpgradeHelper {

    public static float getTotalValue(InventoryComponent upgradeSlots, UpgradeType type, float baseValue) {
        float total = baseValue;
        for (int i = 0; i < upgradeSlots.getContainer().getContainerSize(); i++) {
            ItemStack stack = upgradeSlots.getContainer().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof UpgradeItem upgrade
                    && upgrade.getUpgradeType() == type) {
                total += upgrade.getValue() * stack.getCount();
            }
        }
        return total;
    }

    public static boolean hasUpgrade(InventoryComponent upgradeSlots, UpgradeType type) {
        for (int i = 0; i < upgradeSlots.getContainer().getContainerSize(); i++) {
            ItemStack stack = upgradeSlots.getContainer().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof UpgradeItem upgrade
                    && upgrade.getUpgradeType() == type) {
                return true;
            }
        }
        return false;
    }
}
