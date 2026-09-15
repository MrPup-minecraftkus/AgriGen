package com.mrpup.agrigen.item.upgrade;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class RadiusUpgradeItem extends UpgradeItem {

    private final int tier;
    private final float radiusBonus;

    public RadiusUpgradeItem(Properties properties, int tier, float radiusBonus) {
        super(properties);
        this.tier = tier;
        this.radiusBonus = radiusBonus;
    }

    @Override
    public UpgradeType getUpgradeType() {
        return UpgradeType.RADIUS;
    }

    @Override
    public float getValue() {
        return radiusBonus;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("upgrade.agrigen.radius").withStyle(ChatFormatting.GRAY).append(Component.translatable("upgrade.agrigen.radius.bonus", getValue()).withStyle(ChatFormatting.GOLD)));
    }

    @Override
    public int getTier() {
        return tier;
    }
}

