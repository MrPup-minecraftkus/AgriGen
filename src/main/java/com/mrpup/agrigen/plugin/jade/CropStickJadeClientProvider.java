package com.mrpup.agrigen.plugin.jade;

import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class CropStickJadeClientProvider implements IBlockComponentProvider {

    public static final CropStickJadeClientProvider INSTANCE = new CropStickJadeClientProvider();

    @Override
    public Identifier getUid() {
        return CropStickJadeDataProvider.UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();

        if (!data.getBooleanOr("hasSeed", false)) {
            return;
        }

        if (data.getBooleanOr("isWeed", false)) {
            tooltip.add(Component.translatable("jade.agrigen.weed"));
            return;
        }

        int stage = data.getIntOr("stage", 1);
        int maxStage = data.getIntOr("maxStage", CropStickBlockEntity.MAX_STAGE);

        tooltip.add(Component.translatable("jade.agrigen.stage", stage, maxStage));

        if (data.getBooleanOr("fullyGrown", false)) {
            tooltip.add(Component.translatable("jade.agrigen.ready"));
            return;
        }

        float progress = data.getFloatOr("progressToNext", 1f);
        float remainingPercent = progress * 100f;

        tooltip.add(Component.translatable(
                "jade.agrigen.remaining",
                String.format("%.1f", remainingPercent) + "%"
        ));
    }
}

