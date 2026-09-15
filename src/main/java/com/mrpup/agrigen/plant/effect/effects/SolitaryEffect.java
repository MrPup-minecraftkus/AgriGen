package com.mrpup.agrigen.plant.effect.effects;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.plant.effect.PlantEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SolitaryEffect implements PlantEffect {
    public static final SolitaryEffect INSTANCE = new SolitaryEffect();
    public static final String ID = "solitary";
    private static final float PENALTY_PER_NEIGHBOR = 0.1f;

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

        return 1f + (neighborCount * PENALTY_PER_NEIGHBOR);
    }
}
