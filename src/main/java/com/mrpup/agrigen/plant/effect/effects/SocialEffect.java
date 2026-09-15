package com.mrpup.agrigen.plant.effect.effects;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.plant.effect.PlantEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SocialEffect implements PlantEffect {
    public static final SocialEffect INSTANCE = new SocialEffect();
    public static final String ID = "social";
    private static final float BONUS_PER_NEIGHBOR = 0.1f;
    private static final float MIN_MULTIPLIER = 0.4f;

    @Override
    public String id() { return ID; }

    @Override
    public float growthMultiplier(CropStickBlockEntity entity) {
        Level level = entity.getLevel();
        if (level == null) return 1f;

        BlockPos pos = entity.getBlockPos();
        int neighborCount = 0;

        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            if (p.equals(pos)) continue;
            BlockEntity be = level.getBlockEntity(p);
            if (be instanceof CropStickBlockEntity crop && crop.hasSeed()) {
                neighborCount++;
            }
        }

        float multiplier = 1f - (neighborCount * BONUS_PER_NEIGHBOR);
        return Math.max(MIN_MULTIPLIER, multiplier);
    }
}
