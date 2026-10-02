package com.mrpup.agrigen.event;

import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipeManager;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

public class AgriGenGameBusEvents {

    @SubscribeEvent
    public static void onAddReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath("agrigen", "crossbreed_recipes"),
                new CrossbreedRecipeManager()
        );
    }
}
