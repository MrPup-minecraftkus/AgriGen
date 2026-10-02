package com.mrpup.agrigen.item.upgrade;

public class SpeedUpgradeItem extends UpgradeItem {

    private final int tier;
    private final float speedBonus;

    public SpeedUpgradeItem(Properties properties, int tier, float speedBonus) {
        super(properties);
        this.tier = tier;
        this.speedBonus = speedBonus;
    }

    @Override
    public UpgradeType getUpgradeType() {
        return UpgradeType.SPEED;
    }

    @Override
    public float getValue() {
        return speedBonus;
    }

    @Override
    public int getTier() {
        return tier;
    }
}
