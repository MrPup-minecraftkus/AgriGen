package com.mrpup.agrigen.item.upgrade;

import com.mrpup.clumapi.blocks.entity.ComponentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class UpgradeItem extends Item {

    public UpgradeItem(Properties properties) {
        super(properties);
    }

    public abstract UpgradeType getUpgradeType();

    public abstract float getValue();

    public int getTier() {
        return 1;
    }

    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            BlockPos blockpos = context.getClickedPos();
            BlockEntity entity = context.getLevel().getBlockEntity(blockpos);
            if (entity instanceof ComponentBlockEntity) {

            }
        }

        return super.useOn(context);
    }
}
