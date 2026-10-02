package com.mrpup.agrigen.plant.effect;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.plant.effect.effects.*;

import java.util.HashMap;
import java.util.Map;

public class PlantEffectRegistry {
    private static final Map<String, PlantEffect> EFFECTS = new HashMap<>();

    static {
        register(SolitaryEffect.INSTANCE);
        register(SocialEffect.INSTANCE);
        register(ParasiticEffect.INSTANCE);
        register(SolarEffect.INSTANCE);
        register(NocturnalEffect.INSTANCE);
        register(PoisonousEffect.INSTANCE);
    }

    public static void register(PlantEffect effect) {
        EFFECTS.put(effect.id(), effect);
    }

    public static float getGrowthMultiplier(CropStickBlockEntity entity) {
        PlantEffect effect = EFFECTS.get(entity.getEffect());
        return effect != null ? effect.growthMultiplier(entity) : 1f;
    }

    public static void tick(CropStickBlockEntity entity) {
        PlantEffect effect = EFFECTS.get(entity.getEffect());
        if (effect != null) effect.onTick(entity);
    }
}
