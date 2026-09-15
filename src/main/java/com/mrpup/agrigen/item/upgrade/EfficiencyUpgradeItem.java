package com.mrpup.agrigen.item.upgrade;

public class EfficiencyUpgradeItem extends UpgradeItem {

    private final int tier;
    private final float efficiencyBonus;

    public EfficiencyUpgradeItem(Properties properties, int tier, float efficiencyBonus) {
        super(properties);
        this.tier = tier;
        this.efficiencyBonus = efficiencyBonus;
    }

    @Override
    public UpgradeType getUpgradeType() {
        return UpgradeType.EFFICIENCY;
    }

    @Override
    public float getValue() {
        return efficiencyBonus;
    }

    @Override
    public int getTier() {
        return tier;
    }
}

