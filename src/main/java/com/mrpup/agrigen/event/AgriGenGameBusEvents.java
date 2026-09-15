package com.mrpup.agrigen.event;

import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class AgriGenGameBusEvents {

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new CrossbreedRecipeManager());
    }
}
