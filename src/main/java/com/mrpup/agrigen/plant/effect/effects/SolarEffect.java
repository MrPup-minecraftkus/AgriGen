package com.mrpup.agrigen.plant.effect.effects;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.plant.effect.PlantEffect;
import net.minecraft.world.level.Level;

public class SolarEffect implements PlantEffect {
    public static final SolarEffect INSTANCE = new SolarEffect();
    public static final String ID = "solar";

    private static final float DAY_MULTIPLIER = 0.8f;
    private static final float NIGHT_MULTIPLIER = 1.3f;

    @Override
    public String id() { return ID; }

    @Override
    public float growthMultiplier(CropStickBlockEntity entity) {
        Level level = entity.getLevel();
        if (level == null) return 1f;
        return level.isDay() ? DAY_MULTIPLIER : NIGHT_MULTIPLIER;
    }
}
