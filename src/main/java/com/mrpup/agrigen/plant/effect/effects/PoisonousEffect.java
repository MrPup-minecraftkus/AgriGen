package com.mrpup.agrigen.plant.effect.effects;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.plant.effect.PlantEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class PoisonousEffect implements PlantEffect {
    public static final PoisonousEffect INSTANCE = new PoisonousEffect();
    public static final String ID = "poisonous";

    private static final int WEED_CHANCE = 12000;
    private static final int PLAYER_POISON_DURATION = 100;
    private static final int PLAYER_POISON_AMPLIFIER = 0;
    private static final double PLAYER_RADIUS = 1.0;

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void onTick(CropStickBlockEntity entity) {
        Level level = entity.getLevel();
        if (level == null || level.isClientSide()) return;

        BlockPos pos = entity.getBlockPos();

        if (level.getRandom().nextInt(WEED_CHANCE) == 0) {
            poisonRandomNeighbor(level, pos);
        }

        poisonNearbyPlayers(level, pos);
    }

    private void poisonRandomNeighbor(Level level, BlockPos pos) {
        List<CropStickBlockEntity> candidates = new ArrayList<>();

        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            if (p.equals(pos)) continue;

            if (level.getBlockEntity(p) instanceof CropStickBlockEntity neighbor
                    && neighbor.hasSeed()
                    && !neighbor.isWeed()) {
                candidates.add(neighbor);
            }
        }

        if (candidates.isEmpty()) return;

        CropStickBlockEntity target = candidates.get(level.getRandom().nextInt(candidates.size()));
        target.setWeed();
    }

    private void poisonNearbyPlayers(Level level, BlockPos pos) {
        List<Player> players = level.getEntitiesOfClass(
                Player.class,
                new AABB(pos).inflate(PLAYER_RADIUS)
        );

        for (Player player : players) {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, PLAYER_POISON_DURATION, PLAYER_POISON_AMPLIFIER));
        }
    }
}
