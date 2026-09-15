package com.mrpup.agrigen.plant.effect.effects;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.plant.effect.PlantEffect;
import net.minecraft.world.level.Level;

public class NocturnalEffect implements PlantEffect {
    public static final NocturnalEffect INSTANCE = new NocturnalEffect();
    public static final String ID = "nocturnal";

    private static final float DAY_MULTIPLIER = 1.3f;
    private static final float NIGHT_MULTIPLIER = 0.8f;

    @Override
    public String id() { return ID; }

    @Override
    public float growthMultiplier(CropStickBlockEntity entity) {
        Level level = entity.getLevel();
        if (level == null) return 1f;
        return level.isDay() ? DAY_MULTIPLIER : NIGHT_MULTIPLIER;
    }
}
