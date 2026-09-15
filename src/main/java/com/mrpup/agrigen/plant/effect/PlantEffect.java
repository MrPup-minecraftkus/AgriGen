package com.mrpup.agrigen.plant.effect;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;

public interface PlantEffect {
    String id();
    default float growthMultiplier(CropStickBlockEntity entity) {return 1f;};
    default void onTick(CropStickBlockEntity entity) {}
}
