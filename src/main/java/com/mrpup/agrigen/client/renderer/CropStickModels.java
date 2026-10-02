package com.mrpup.agrigen.client.renderer;

import com.mrpup.agrigen.AgriGen;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import java.util.HashMap;
import java.util.Map;

public final class CropStickModels {

    private static final Map<String, StandaloneModelKey<BlockStateModel>> STICK_KEYS = new HashMap<>();
    private static final StandaloneModelKey<BlockStateModel>[] WEED_KEYS = new StandaloneModelKey[4];

    public static StandaloneModelKey<BlockStateModel> stickKey(String variant) {
        return STICK_KEYS.computeIfAbsent(variant,
                v -> new StandaloneModelKey<>(() -> AgriGen.MOD_ID + ":crop_stick_" + v));
    }

    public static StandaloneModelKey<BlockStateModel> weedKey(int stage) {
        if (WEED_KEYS[stage] == null) {
            WEED_KEYS[stage] = new StandaloneModelKey<>(() -> AgriGen.MOD_ID + ":weed_stage" + stage);
        }
        return WEED_KEYS[stage];
    }

    public static void register(ModelEvent.RegisterStandalone event) {
        for (String variant : new String[]{"single", "double"}) {
            Identifier modelId = Identifier.fromNamespaceAndPath(AgriGen.MOD_ID, "block/crop_stick_" + variant);
            event.register(stickKey(variant), SimpleUnbakedStandaloneModel.blockStateModel(modelId));
        }

        for (int stage = 0; stage < 4; stage++) {
            Identifier modelId = Identifier.fromNamespaceAndPath(AgriGen.MOD_ID, "block/weed_stage" + stage);
            event.register(weedKey(stage), SimpleUnbakedStandaloneModel.blockStateModel(modelId));
        }
    }

    private CropStickModels() {}
}
