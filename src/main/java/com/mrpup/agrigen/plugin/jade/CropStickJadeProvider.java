package com.mrpup.agrigen.plugin.jade;

import com.mrpup.agrigen.AgriGen;
import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CropStickJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AgriGen.MOD_ID, "crop_stick");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof CropStickBlockEntity crop)) {
            return;
        }

        data.putBoolean("hasSeed", crop.hasSeed());
        data.putBoolean("isWeed", crop.isWeed());

        if (!crop.hasSeed()) {
            return;
        }

        int stage = crop.getStage();
        int maxStage = CropStickBlockEntity.MAX_STAGE;
        int ticksPerStage = crop.getTicksPerStage();
        int growthTicks = crop.getGrowthTicks();

        data.putInt("stage", stage);
        data.putInt("maxStage", maxStage);

        if (stage >= maxStage) {
            data.putBoolean("fullyGrown", true);
            return;
        }

        int ticksIntoStage = growthTicks - stage * ticksPerStage;
        if (stage == 1) ticksIntoStage = growthTicks;
        float progress = ticksPerStage > 0 ? Math.min(1f, Math.max(0f, ticksIntoStage / (float) ticksPerStage)) : 0f;

        data.putBoolean("fullyGrown", false);
        data.putFloat("progressToNext", progress);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();

        if (!data.contains("hasSeed") || !data.getBoolean("hasSeed")) {
            return;
        }

        if (data.getBoolean("isWeed")) {
            tooltip.add(Component.translatable("jade.agrigen.weed"));
            return;
        }

        int stage = data.getInt("stage");
        int maxStage = data.getInt("maxStage");

        tooltip.add(Component.translatable("jade.agrigen.stage", stage, maxStage));

        if (data.getBoolean("fullyGrown")) {
            tooltip.add(Component.translatable("jade.agrigen.ready"));
            return;
        }

        float progress = data.getFloat("progressToNext");
        float remainingPercent = progress * 100f;

        tooltip.add(Component.translatable(
                "jade.agrigen.remaining",
                String.format("%.1f", remainingPercent) + "%"
        ));
    }
}
