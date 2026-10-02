package com.mrpup.agrigen.plant.effect.effects;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.plant.effect.PlantEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class ParasiticEffect implements PlantEffect {
    public static final ParasiticEffect INSTANCE = new ParasiticEffect();
    public static final String ID = "parasitic";

    private static final int STEAL_AMOUNT = 20;

    @Override
    public String id() { return ID; }

    @Override
    public void onTick(CropStickBlockEntity entity) {
        Level level = entity.getLevel();
        if (level == null || level.isClientSide()) return;

        BlockPos pos = entity.getBlockPos();
        String species = entity.getSeedId();

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(dir);

            if (level.getBlockEntity(neighborPos) instanceof CropStickBlockEntity neighbor
                    && neighbor != entity
                    && neighbor.hasSeed()
                    && !neighbor.isWeed()
                    && species.equals(neighbor.getSeedId())
                    && neighbor.getGrowthTicks() > 0) {

                int stolen = Math.min(STEAL_AMOUNT, neighbor.getGrowthTicks());
                neighbor.setGrowthTicks(neighbor.getGrowthTicks() - stolen);
                entity.setGrowthTicks(entity.getGrowthTicks() + stolen);
                return;
            }
        }
    }
}