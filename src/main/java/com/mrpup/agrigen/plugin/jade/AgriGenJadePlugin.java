package com.mrpup.agrigen.plugin.jade;

import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class AgriGenJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(CropStickJadeDataProvider.INSTANCE, CropStickBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CropStickJadeClientProvider.INSTANCE, ModBlocks.CROP_STICK.getBlock().getClass());
    }
}
