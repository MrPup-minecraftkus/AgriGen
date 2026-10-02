package com.mrpup.agrigen.plugin.jade;

import com.mrpup.agrigen.AgriGen;
import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class CropStickJadeDataProvider implements IServerDataProvider<BlockAccessor> {

    public static final CropStickJadeDataProvider INSTANCE = new CropStickJadeDataProvider();
    public static final Identifier UID = Identifier.fromNamespaceAndPath(AgriGen.MOD_ID, "crop_stick");

    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor blockAccessor) {
        if (!(blockAccessor.getBlockEntity() instanceof CropStickBlockEntity crop)) {
            return;
        }

        data.putBoolean("hasSeed", crop.hasSeed());
        data.putBoolean("isWeed", crop.isWeed());

        if (!crop.hasSeed()) {
            return;
        }

        int stage = crop.getStage();
        int maxStage = CropStickBlockEntity.MAX_STAGE;

        data.putInt("stage", stage);
        data.putInt("maxStage", maxStage);

        if (stage >= maxStage) {
            data.putBoolean("fullyGrown", true);
            return;
        }


        data.putBoolean("fullyGrown", false);
        data.putFloat("progressToNext", crop.getStageProgress());
    }
}
