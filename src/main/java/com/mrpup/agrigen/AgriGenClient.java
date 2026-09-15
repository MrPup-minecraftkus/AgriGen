package com.mrpup.agrigen;

import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.client.renderer.CropStickRenderer;
import com.mrpup.agrigen.plant.PlantRegistry;
import com.mrpup.clumapi.ClumAPIClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = AgriGen.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AgriGen.MOD_ID, value = Dist.CLIENT)
public class AgriGenClient {

    public AgriGenClient(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        ClumAPIClient.initClientAPI(modEventBus);
    }


    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(PlantRegistry::register);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlocks.CROP_STICK.getBlockEntityType(), CropStickRenderer::new);
    }
}
