package com.mrpup.agrigen.client.event;

import com.mrpup.agrigen.AgriGen;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = AgriGen.MOD_ID, value = Dist.CLIENT)
public class ClientModelEvents {

    @SubscribeEvent
    public static void onRegisterModels(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath(AgriGen.MOD_ID, "block/crop_stick_single")
        ));
        event.register(ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath(AgriGen.MOD_ID, "block/crop_stick_double")
        ));
        for (int stage = 0; stage <= 3; stage++) {
            event.register(ModelResourceLocation.standalone(
                    ResourceLocation.fromNamespaceAndPath(AgriGen.MOD_ID, "block/weed_stage" + stage)));
        }
    }
}
