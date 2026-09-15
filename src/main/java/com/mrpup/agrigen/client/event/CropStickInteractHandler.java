package com.mrpup.agrigen.client.event;

import com.mrpup.agrigen.AgriGen;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = AgriGen.MOD_ID)
public class CropStickInteractHandler {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        var level = event.getLevel();
        var pos = event.getPos();
        var player = event.getEntity();
        var state = level.getBlockState(pos);

        if (!state.is(ModBlocks.CROP_STICK.getBlock())) return;

        if (player.getMainHandItem().isEmpty()) {
            if (!level.isClientSide) {
                if (level.getBlockEntity(pos) instanceof CropStickBlockEntity cropEntity) {
                    if (cropEntity.isWeed()) {
                        event.setCanceled(true);
                        cropEntity.removeWeed();
                    } else if (cropEntity.hasSeed()){
                        event.setCanceled(true);
                        cropEntity.removeSeed();
                    }
                }
            }
        }
    }
}
