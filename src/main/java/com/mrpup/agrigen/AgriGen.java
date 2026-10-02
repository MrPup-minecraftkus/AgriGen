package com.mrpup.agrigen;

import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.client.renderer.CropStickModels;
import com.mrpup.agrigen.event.AgriGenGameBusEvents;
import com.mrpup.agrigen.fluid.ModFluids;
import com.mrpup.agrigen.genetics.GenomeDefaults;
import com.mrpup.agrigen.item.ModItemGroups;
import com.mrpup.agrigen.item.ModItems;
import com.mrpup.agrigen.recipe.ModRecipeSerializers;
import com.mrpup.agrigen.recipe.ModRecipeTypes;
import com.mrpup.agrigen.recipe.ModRecipes;
import com.mrpup.clumapi.ClumAPI;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AgriGen.MOD_ID)
public class AgriGen {
    public static final String MOD_ID = "agrigen";

    private static final Logger LOGGER = LogUtils.getLogger();

    public AgriGen(IEventBus modEventBus, ModContainer modContainer) {
        ClumAPI.initAPI(MOD_ID, modEventBus);

        ModBlocks.register();
        ModItems.register();
        ModItemGroups.register(modEventBus);
        ModRecipes.registerRecipes();
        ModRecipeSerializers.register();
        ModRecipeTypes.register();
        ModFluids.register();
        BestCat();

        modEventBus.register(this);
        NeoForge.EVENT_BUS.register(AgriGenGameBusEvents.class);
    }

    private void BestCat() {
        LOGGER.debug("all cats are beautiful, my cat -> assets/agrigen/cat.png");
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(GenomeDefaults::bootstrap);
    }
}
