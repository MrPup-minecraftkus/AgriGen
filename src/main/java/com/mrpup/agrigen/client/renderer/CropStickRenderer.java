package com.mrpup.agrigen.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrpup.agrigen.AgriGen;
import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.plant.AllHelper;
import com.mrpup.agrigen.plant.PlantRegistry;
import com.mrpup.clumapi.helper.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CropStickRenderer implements BlockEntityRenderer<CropStickBlockEntity> {

    private static final int MAX_SEGMENTS = 3;
    private static final float CORNER_OFFSET = 2.5f / 16f;
    private static final float CORNER_Y_OFFSET = -1f / 16f;
    private static final float CORNER_SCALE = 1.1f;
    private static final float CORNER_SCALE_WITH_SEGMENTS = 0.4f;
    private static final float FRUIT_MAX_SCALE = 0.6f;

    public CropStickRenderer(BlockEntityRendererProvider.Context context) {

    }

    private static final ResourceLocation[] WEED_MODELS = {
            ResourceLocation.fromNamespaceAndPath(AgriGen.MOD_ID, "block/weed_stage0"),
            ResourceLocation.fromNamespaceAndPath(AgriGen.MOD_ID, "block/weed_stage1"),
            ResourceLocation.fromNamespaceAndPath(AgriGen.MOD_ID, "block/weed_stage2"),
            ResourceLocation.fromNamespaceAndPath(AgriGen.MOD_ID, "block/weed_stage3"),
    };

    @Override
    public void render(CropStickBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        renderStandaloneModel(getStickModel(entity.getCropStickVariant()), poseStack, bufferSource, packedLight, packedOverlay);

        if (entity.hasSeed()) {
            Item seedItem = ItemHelper.getItemFromString(entity.getSeedId());

            if (seedItem != null) {
                if (AllHelper.isSapling(seedItem)) {
                    renderSaplingSmooth(entity, seedItem, poseStack, bufferSource, packedLight, packedOverlay);
                } else if (AllHelper.isFlower(seedItem)) {
                    renderFlowerCluster(entity, seedItem, poseStack, bufferSource, packedLight, packedOverlay);
                } else if (AllHelper.isStackingPlant(seedItem)) {
                    renderStackingPlantSmooth(entity, seedItem, poseStack, bufferSource, packedLight, packedOverlay);
                } else if (AllHelper.isMushroom(seedItem)) {
                    renderMushroomSmooth(entity, seedItem, poseStack, bufferSource, packedLight, packedOverlay);
                } else if (AllHelper.isStem(seedItem)) {
                    renderFruitSmooth(entity, seedItem, poseStack, bufferSource, packedLight, packedOverlay);
                } else if (AllHelper.isBerries(seedItem)) {
                    BlockState berriesState = PlantRegistry.resolveGrowthState(entity.getSeedId(), entity.getStage());
                    renderBerries(berriesState, poseStack, bufferSource, packedLight, packedOverlay);
                } else {
                    BlockState cropState = PlantRegistry.resolveGrowthState(entity.getSeedId(), entity.getStage());
                    if (cropState != null) {
                        renderBlockState(cropState, poseStack, bufferSource, packedLight, packedOverlay);
                    }
                }
            }
        }

        if (entity.isWeed()) {
            renderStandaloneModel(WEED_MODELS[scaleStageToWeed(entity.getStage())], poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private void renderMushroomSmooth(CropStickBlockEntity entity, Item seedItem, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float progress = getStageBasedProgress(entity);
        if (progress <= 0.02f) return;

        BlockState plantState;

        if (seedItem == Items.RED_MUSHROOM) {
            plantState = Blocks.RED_MUSHROOM.defaultBlockState();
        } else if (seedItem == Items.BROWN_MUSHROOM) {
            plantState = Blocks.BROWN_MUSHROOM.defaultBlockState();
        } else {
            plantState = Blocks.RED_MUSHROOM.defaultBlockState();
        }

        renderScaledBlockState(plantState, progress, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderFlowerCluster(CropStickBlockEntity entity, Item flowerItem, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState flowerState = flowerItem instanceof BlockItem blockItem
                ? blockItem.getBlock().defaultBlockState()
                : Blocks.AIR.defaultBlockState();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(flowerState);

        float growProgress = getStageBasedProgress(entity);

        renderCornerCluster(model, flowerState, poseStack, bufferSource, packedLight, packedOverlay,
                0f, CORNER_SCALE * growProgress);
    }


    private void renderStackingPlantSmooth(CropStickBlockEntity entity, Item seedItem, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState plantState;

        if (seedItem == Items.SUGAR_CANE) {
            plantState = Blocks.SUGAR_CANE.defaultBlockState();
        } else if (seedItem == Items.CACTUS) {
            plantState = Blocks.CACTUS.defaultBlockState();
        } else {
            plantState = Blocks.BAMBOO.defaultBlockState();
        }

        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(plantState);

        float overallProgress = getStageBasedProgress(entity) * MAX_SEGMENTS;
        int fullSegments = Math.min((int) overallProgress, MAX_SEGMENTS);
        float partialProgress = overallProgress - fullSegments;

        for (int i = 0; i < fullSegments; i++) {
            float segmentHeight = i / (float) MAX_SEGMENTS;
            renderCornerCluster(model, plantState, poseStack, bufferSource, packedLight, packedOverlay,
                    segmentHeight, CORNER_SCALE_WITH_SEGMENTS);
        }

        if (fullSegments < MAX_SEGMENTS && partialProgress > 0.02f) {
            float segmentHeight = fullSegments / (float) MAX_SEGMENTS;
            renderCornerCluster(model, plantState, poseStack, bufferSource, packedLight, packedOverlay,
                    segmentHeight, CORNER_SCALE_WITH_SEGMENTS * partialProgress);
        }
    }

    private void renderFruitSmooth(CropStickBlockEntity entity, Item seedItem, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float progress = getStageBasedProgress(entity);
        if (progress <= 0.02f) return;

        Block fruit = PlantRegistry.getFruit(seedItem);
        if (fruit == null) return;

        renderScaledBlockState(fruit.defaultBlockState(), progress * FRUIT_MAX_SCALE, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private float getStageBasedProgress(CropStickBlockEntity entity) {
        return Math.min((float) entity.getStage() / CropStickBlockEntity.MAX_STAGE, 1f);
    }

    private static final float[][] CORNER_POSITIONS = {
            {CORNER_OFFSET, CORNER_OFFSET},
            {1f - CORNER_OFFSET, CORNER_OFFSET},
            {CORNER_OFFSET, 1f - CORNER_OFFSET},
            {1f - CORNER_OFFSET, 1f - CORNER_OFFSET}
    };

    private void renderCornerCluster(BakedModel model, BlockState state, PoseStack poseStack,
                                     MultiBufferSource bufferSource, int packedLight, int packedOverlay,
                                     float segmentHeight, float scale) {
        for (float[] corner : CORNER_POSITIONS) {
            poseStack.pushPose();
            poseStack.translate(corner[0], segmentHeight + CORNER_Y_OFFSET, corner[1]);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-0.5, 0, -0.5);
            renderModelRaw(model, state, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    private void renderSaplingSmooth(CropStickBlockEntity entity, Item seedItem, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float progress = getStageBasedProgress(entity);
        if (progress <= 0.02f) return;

        BlockState state = resolveSaplingState(seedItem);
        if (state == null) return;

        renderScaledBlockState(state, progress, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderScaledBlockState(BlockState state, float scale, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

        poseStack.pushPose();
        poseStack.translate(0.5, CORNER_Y_OFFSET, 0.5);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5, CORNER_Y_OFFSET, -0.5);

        renderModelRaw(model, state, poseStack, bufferSource, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void renderBerries(BlockState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

        poseStack.pushPose();
        poseStack.translate(0, CORNER_Y_OFFSET, 0);

        renderModelRaw(model, state, poseStack, bufferSource, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void renderModelRaw(BakedModel model, BlockState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.cutout()),
                state,
                model,
                1f, 1f, 1f,
                packedLight,
                packedOverlay
        );
    }

    private void renderBlockState(BlockState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        poseStack.pushPose();
        renderModelRaw(model, state, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void renderStandaloneModel(ResourceLocation modelLoc, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ModelResourceLocation modelLocation = ModelResourceLocation.standalone(modelLoc);
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);

        poseStack.pushPose();
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.cutout()),
                null,
                model,
                1f, 1f, 1f,
                packedLight,
                packedOverlay
        );
        poseStack.popPose();
    }

    private ResourceLocation getStickModel(String variant) {
        return ResourceLocation.fromNamespaceAndPath(AgriGen.MOD_ID, "block/crop_stick_" + variant);
    }

    private int scaleStageToWeed(int stage) {
        int maxWeedStage = 3;
        return Math.min(
                Math.round((float) stage / CropStickBlockEntity.MAX_STAGE * maxWeedStage),
                maxWeedStage
        );
    }

    private BlockState resolveSaplingState(Item seedItem) {
        if (seedItem instanceof BlockItem blockItem) {
            return blockItem.getBlock().defaultBlockState();
        }
        return null;
    }
}
