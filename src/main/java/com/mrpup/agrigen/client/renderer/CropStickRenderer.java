package com.mrpup.agrigen.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.plant.AllHelper;
import com.mrpup.agrigen.plant.PlantRegistry;
import com.mrpup.clumapi.helper.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CropStickRenderer implements BlockEntityRenderer<CropStickBlockEntity, CropStickRenderState> {

    private static final int MAX_SEGMENTS = 3;
    private static final float CORNER_OFFSET = 2.5f / 16f;
    private static final float CORNER_Y_OFFSET = -1f / 16f;
    private static final float CORNER_SCALE = 1.1f;
    private static final float CORNER_SCALE_WITH_SEGMENTS = 0.4f;
    private static final float FRUIT_MAX_SCALE = 0.6f;

    private final BlockModelResolver blockModelResolver;
    private final BlockModelRenderState scratchRenderState = new BlockModelRenderState();

    private int light = 15728880;

    public CropStickRenderer(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
    }

    @Override
    public CropStickRenderState createRenderState() {
        return new CropStickRenderState();
    }

    @Override
    public void extractRenderState(CropStickBlockEntity entity, CropStickRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPosition, breakProgress);

        state.variant = entity.getCropStickVariant();
        state.hasSeed = entity.hasSeed();
        state.seedId = entity.getSeedId();
        state.stage = entity.getStage();
        state.isWeed = entity.isWeed();
        state.isFullyGrown = entity.isFullyGrown();
        state.fruitGrowthProgress = entity.getFruitGrowthProgress();
    }

    @Override
    public void submit(CropStickRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        this.light = state.lightCoords;

        renderStandaloneModel(CropStickModels.stickKey(state.variant), poseStack, submitNodeCollector);

        if (state.hasSeed) {
            Item seedItem = ItemHelper.getItemFromString(state.seedId);

            if (seedItem != null) {
                if (AllHelper.isSapling(seedItem)) {
                    renderSaplingSmooth(state, seedItem, poseStack, submitNodeCollector);
                } else if (AllHelper.isFlower(seedItem)) {
                    renderFlowerCluster(state, seedItem, poseStack, submitNodeCollector);
                } else if (AllHelper.isStackingPlant(seedItem)) {
                    renderStackingPlantSmooth(state, seedItem, poseStack, submitNodeCollector);
                } else if (AllHelper.isMushroom(seedItem)) {
                    renderMushroomSmooth(state, seedItem, poseStack, submitNodeCollector);
                } else if (AllHelper.isStem(seedItem)) {
                    renderFruitSmooth(state, seedItem, poseStack, submitNodeCollector);
                } else if (AllHelper.isBerries(seedItem)) {
                    BlockState berriesState = PlantRegistry.resolveGrowthState(state.seedId, state.stage);
                    renderBerries(berriesState, poseStack, submitNodeCollector);
                } else {
                    BlockState cropState = PlantRegistry.resolveGrowthState(state.seedId, state.stage);
                    if (cropState != null) {
                        renderBlockState(cropState, poseStack, submitNodeCollector);
                    }
                }
            }
        }

        if (state.isWeed) {
            renderStandaloneModel(CropStickModels.weedKey(scaleStageToWeed(state.stage)), poseStack, submitNodeCollector);
        }
    }

    private void renderMushroomSmooth(CropStickRenderState state, Item seedItem, PoseStack poseStack, SubmitNodeCollector collector) {
        float progress = getStageBasedProgress(state);
        if (progress <= 0.02f) return;

        BlockState plantState;
        if (seedItem == Items.RED_MUSHROOM) {
            plantState = Blocks.RED_MUSHROOM.defaultBlockState();
        } else if (seedItem == Items.BROWN_MUSHROOM) {
            plantState = Blocks.BROWN_MUSHROOM.defaultBlockState();
        } else {
            plantState = Blocks.RED_MUSHROOM.defaultBlockState();
        }

        renderScaledBlockState(plantState, progress, poseStack, collector);
    }

    private void renderFlowerCluster(CropStickRenderState state, Item flowerItem, PoseStack poseStack, SubmitNodeCollector collector) {
        BlockState flowerState = flowerItem instanceof BlockItem blockItem
                ? blockItem.getBlock().defaultBlockState()
                : Blocks.AIR.defaultBlockState();

        float growProgress = getStageBasedProgress(state);

        renderCornerCluster(flowerState, poseStack, collector, 0f, CORNER_SCALE * growProgress);
    }

    private void renderStackingPlantSmooth(CropStickRenderState state, Item seedItem, PoseStack poseStack, SubmitNodeCollector collector) {
        BlockState plantState;
        if (seedItem == Items.SUGAR_CANE) {
            plantState = Blocks.SUGAR_CANE.defaultBlockState();
        } else if (seedItem == Items.CACTUS) {
            plantState = Blocks.CACTUS.defaultBlockState();
        } else {
            plantState = Blocks.BAMBOO.defaultBlockState();
        }

        float overallProgress = getStageBasedProgress(state) * MAX_SEGMENTS;
        int fullSegments = Math.min((int) overallProgress, MAX_SEGMENTS);
        float partialProgress = overallProgress - fullSegments;

        for (int i = 0; i < fullSegments; i++) {
            float segmentHeight = i / (float) MAX_SEGMENTS;
            renderCornerCluster(plantState, poseStack, collector, segmentHeight, CORNER_SCALE_WITH_SEGMENTS);
        }

        if (fullSegments < MAX_SEGMENTS && partialProgress > 0.02f) {
            float segmentHeight = fullSegments / (float) MAX_SEGMENTS;
            renderCornerCluster(plantState, poseStack, collector, segmentHeight, CORNER_SCALE_WITH_SEGMENTS * partialProgress);
        }
    }

    private void renderFruitSmooth(CropStickRenderState state, Item seedItem, PoseStack poseStack, SubmitNodeCollector collector) {
        float progress = getStageBasedProgress(state);
        if (progress <= 0.02f) return;

        Block fruit = PlantRegistry.getFruit(seedItem);
        if (fruit == null) return;

        renderScaledBlockState(fruit.defaultBlockState(), progress * FRUIT_MAX_SCALE, poseStack, collector);
    }

    private float getStageBasedProgress(CropStickRenderState state) {
        return Math.min((float) state.stage / CropStickBlockEntity.MAX_STAGE, 1f);
    }

    private static final float[][] CORNER_POSITIONS = {
            {CORNER_OFFSET, CORNER_OFFSET},
            {1f - CORNER_OFFSET, CORNER_OFFSET},
            {CORNER_OFFSET, 1f - CORNER_OFFSET},
            {1f - CORNER_OFFSET, 1f - CORNER_OFFSET}
    };

    private void renderCornerCluster(BlockState state, PoseStack poseStack, SubmitNodeCollector collector, float segmentHeight, float scale) {
        for (float[] corner : CORNER_POSITIONS) {
            poseStack.pushPose();
            poseStack.translate(corner[0], segmentHeight + CORNER_Y_OFFSET, corner[1]);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-0.5, 0, -0.5);
            renderBlockState(state, poseStack, collector);
            poseStack.popPose();
        }
    }

    private void renderSaplingSmooth(CropStickRenderState state, Item seedItem, PoseStack poseStack, SubmitNodeCollector collector) {
        float progress = getStageBasedProgress(state);
        if (progress <= 0.02f) return;

        BlockState saplingState = resolveSaplingState(seedItem);
        if (saplingState == null) return;

        renderScaledBlockState(saplingState, progress, poseStack, collector);
    }

    private void renderScaledBlockState(BlockState blockState, float scale, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        poseStack.translate(0.5, CORNER_Y_OFFSET, 0.5);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5, CORNER_Y_OFFSET, -0.5);

        renderBlockState(blockState, poseStack, collector);

        poseStack.popPose();
    }

    private void renderBerries(BlockState blockState, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        poseStack.translate(0, CORNER_Y_OFFSET, 0);

        renderBlockState(blockState, poseStack, collector);

        poseStack.popPose();
    }

    private void renderBlockState(BlockState blockState, PoseStack poseStack, SubmitNodeCollector collector) {
        blockModelResolver.update(scratchRenderState, blockState, BlockDisplayContext.create());
        poseStack.pushPose();
        scratchRenderState.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    private void renderStandaloneModel(StandaloneModelKey<BlockStateModel> key, PoseStack poseStack, SubmitNodeCollector collector) {
        BlockStateModel model = Minecraft.getInstance().getModelManager().getStandaloneModel(key);
        if (model == null) return;

        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(42L), parts);

        if (parts.isEmpty()) return;

        poseStack.pushPose();
        collector.submitBlockModel(
                poseStack,
                Sheets.cutoutBlockSheet(),
                parts,
                BlockModelRenderState.EMPTY_TINTS,
                light,
                OverlayTexture.NO_OVERLAY,
                0
        );
        poseStack.popPose();
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