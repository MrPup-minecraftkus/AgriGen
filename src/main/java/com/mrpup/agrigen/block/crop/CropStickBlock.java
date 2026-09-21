package com.mrpup.agrigen.block.crop;

import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import com.mrpup.agrigen.plant.AllHelper;
import com.mrpup.agrigen.plant.PlantDropHelper;
import com.mrpup.agrigen.plant.PlantRegistry;
import com.mrpup.agrigen.genetics.GenomeDefaults;
import com.mrpup.agrigen.item.ModItems;
import com.mrpup.clumapi.blocks.ComponentBlock;
import com.mrpup.clumapi.helper.BlockHelper;
import com.mrpup.clumapi.helper.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class CropStickBlock extends ComponentBlock<CropStickBlockEntity> implements EntityBlock, BonemealableBlock {

    public CropStickBlock() {
        super(Properties.of()
                .mapColor(MapColor.PLANT)
                .randomTicks()
                .noOcclusion()
                .noTerrainParticles()
                .strength(1f, 1f)
                .sound(SoundType.CROP), CropStickBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<CropStickBlockEntity> getTileFactory() {
        return CropStickBlockEntity::new;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack item = stack.getItem().asItem().getDefaultInstance();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (AllHelper.isPlantable(item)) {
            if (!level.isClientSide) {
                if (blockEntity instanceof CropStickBlockEntity cropEntity) {
                    if (!cropEntity.hasSeed() && !cropEntity.isWeed()) {

                        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);

                        if (customData != null && customData.copyTag().contains("genome")) {
                            cropEntity.setGenome(customData.copyTag().getCompound("genome"));
                        } else {
                            CompoundTag defaultGenome = GenomeDefaults.getDefaultGenome(stack.getItem());
                            cropEntity.setGenome(defaultGenome);
                        }

                        String id = ItemHelper.getItemName(stack.getItem());
                        cropEntity.setSeed(id);
                        stack.shrink(1);
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (item.is(ModBlocks.CROP_STICK.getBlock().asItem())) {
            if (!level.isClientSide) {
                if (blockEntity instanceof CropStickBlockEntity cropEntity) {
                    String variant = cropEntity.getCropStickVariant();
                    if ("single".equals(variant)) {
                        cropEntity.setCropStickVariant("double");
                        stack.shrink(1);
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (item.is(ModItems.HERBICIDE.item())) {
            if (blockEntity instanceof CropStickBlockEntity cropEntity) {
                if (cropEntity.isWeed()) {
                    return ItemInteractionResult.FAIL;
                }
                if (cropEntity.hasHerbicide() && cropEntity.hasMaxHerbicide()) {
                    return ItemInteractionResult.FAIL;
                }
                if (!level.isClientSide) {
                    player.getItemBySlot(EquipmentSlot.MAINHAND).hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                    cropEntity.setHerbicide(3);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (item.is(ModItems.FERTILIZER.item())) {
            if (!level.isClientSide) {
                if (blockEntity instanceof CropStickBlockEntity cropEntity) {
                    if (cropEntity.isWeed()) return ItemInteractionResult.FAIL;
                    if (cropEntity.hasMaxFertilizer()) {
                        return ItemInteractionResult.FAIL;
                    } else {
                        cropEntity.setFertilizer(15000);
                        stack.shrink(1);
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!level.isClientSide) {
            if (blockEntity instanceof CropStickBlockEntity cropEntity) {
                if (cropEntity.isFullyGrown()) {
                    String reason = "fullyGrown";
                    drop(reason, cropEntity);
                } else if (cropEntity.getCropStickVariant().equals("double")) {
                    String reason = "doubleStick";
                    cropEntity.setCropStickVariant("single");
                    drop(reason, cropEntity);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockBelow = level.getBlockState(pos.below());
        return blockBelow.getBlock() instanceof FarmBlock;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public ItemStack getGenomeSeed(CropStickBlockEntity cropEntity, Item seedItem) {
        Random random = new Random();
        int randomValue = random.nextInt(4);

        ItemStack stack = new ItemStack(seedItem, randomValue);

        CompoundTag genomeTag = cropEntity.getGenome();
        CompoundTag seedTag = new CompoundTag();

        seedTag.putString("seedId", cropEntity.getSeedId());
        seedTag.put("genome", genomeTag);

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(seedTag));

        return stack;
    }

    public void harvest(CropStickBlockEntity cropEntity, ServerLevel level, BlockPos pos) {
        harvest(cropEntity, level, pos, drop -> Block.popResource(level, pos, drop));
    }

    public void harvest(CropStickBlockEntity cropEntity, ServerLevel level, BlockPos pos, Consumer<ItemStack> output) {
        String seedId = cropEntity.getSeedId();
        int yieldBonus = cropEntity.getYield() / 3;

        ResourceLocation itemId = ResourceLocation.parse(seedId);
        Item seedItem = ItemHelper.getItemFromLoc(itemId);
        ItemStack seedStack = new ItemStack(seedItem);

        if (PlantRegistry.isStackingPlant(seedItem)) {
            harvestStackingPlant(cropEntity, level, pos, seedItem, output);
            cropEntity.setFullyGrown(false);
            cropEntity.setGrowthTicks(0);
            return;
        }

        if (PlantRegistry.isMushroom(seedItem)) {
            harvestMushroom(cropEntity, level, pos, seedItem, yieldBonus, output);
            cropEntity.setFullyGrown(false);
            cropEntity.setGrowthTicks(0);
            return;
        }

        if (PlantRegistry.isSapling(seedItem.getDefaultInstance())) {
            harvestSapling(cropEntity, level, pos, seedItem, yieldBonus, output);
            cropEntity.setFullyGrown(false);
            cropEntity.setGrowthTicks(0);
            return;
        }

        List<ItemStack> drops = PlantDropHelper.getHarvestDrops(level, seedStack, pos);

        for (ItemStack drop : drops) {
            if (drop.is(Tags.Items.SEEDS) || drop.is(Items.NETHER_WART)) {
                continue;
            }

            int totalCount = drop.getCount() * yieldBonus;

            while (totalCount > 0) {
                int stackSize = Math.min(totalCount, drop.getMaxStackSize());
                output.accept(drop.copyWithCount(stackSize));
                totalCount -= stackSize;
            }
        }

        ItemStack dropSeed = getGenomeSeed(cropEntity, seedItem);
        output.accept(dropSeed);

        cropEntity.setFullyGrown(false);
        cropEntity.setGrowthTicks(0);
    }

    private void harvestSapling(CropStickBlockEntity cropEntity, ServerLevel level, BlockPos pos, Item seedItem, int yieldBonus, Consumer<ItemStack> output) {
        if (!(seedItem instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof SaplingBlock saplingBlock)) {
            return;
        }

        Block logBlock = resolveLogBlock(saplingBlock);
        Block leavesBlock = resolveLeavesBlock(saplingBlock);

        if (logBlock == null) {
            logBlock = Blocks.OAK_LOG;
        }

        int logCount = 3 + (yieldBonus * yieldBonus / 3);
        dropLootFromBlock(level, pos, logBlock.defaultBlockState(), logCount, output);

        if (leavesBlock != null) {
            int leavesCount = 4 * (yieldBonus * yieldBonus / 3);
            dropLootFromBlock(level, pos, leavesBlock.defaultBlockState(), leavesCount, output);
        }

        ItemStack saplingBack = getGenomeSeed(cropEntity, seedItem);
        saplingBack.setCount(Math.max(1, saplingBack.getCount()));
        output.accept(saplingBack);
    }

    private void dropLootFromBlock(ServerLevel level, BlockPos pos, BlockState state, int repetitions, Consumer<ItemStack> output) {
        LootParams.Builder lootParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, null);

        for (int i = 0; i < repetitions; i++) {
            List<ItemStack> drops = state.getDrops(lootParams);
            for (ItemStack drop : drops) {
                output.accept(drop);
            }
        }
    }

    private Block resolveLogBlock(Block saplingBlock) {
        return resolveRelatedBlock(saplingBlock, "sapling", "log");
    }

    private Block resolveLeavesBlock(Block saplingBlock) {
        return resolveRelatedBlock(saplingBlock, "sapling", "leaves");
    }

    private Block resolveRelatedBlock(Block saplingBlock, String fromSuffix, String toSuffix) {
        ResourceLocation saplingId = BlockHelper.getBlockKey(saplingBlock);
        if (saplingId == null) {
            return null;
        }

        String path = saplingId.getPath();
        if (!path.endsWith("_" + fromSuffix)) {
            return null;
        }

        String basePath = path.substring(0, path.length() - fromSuffix.length());
        ResourceLocation candidateId = ResourceLocation.fromNamespaceAndPath(saplingId.getNamespace(), basePath + toSuffix);

        Block candidate = BlockHelper.getBlockFromLoc(candidateId);
        return candidate != Blocks.AIR ? candidate : null;
    }

    private void harvestMushroom(CropStickBlockEntity cropEntity, ServerLevel level, BlockPos pos, Item seedItem, int yieldBonus, Consumer<ItemStack> output) {
        ItemStack seedStack = new ItemStack(seedItem);
        List<ItemStack> drops = PlantDropHelper.getHarvestDrops(level, seedStack, pos);

        for (ItemStack drop : drops) {
            if (drop.is(seedItem)) {
                continue;
            }

            int totalCount = drop.getCount() * (1 + yieldBonus);
            while (totalCount > 0) {
                int stackSize = Math.min(totalCount, drop.getMaxStackSize());
                output.accept(drop.copyWithCount(stackSize));
                totalCount -= stackSize;
            }
        }

        int plainCount = yieldBonus;
        if (plainCount > 0) {
            ItemStack geneticSeed = getGenomeSeed(cropEntity, seedItem);
            output.accept(geneticSeed);
        }
    }

    private void harvestStackingPlant(CropStickBlockEntity cropEntity, ServerLevel level, BlockPos pos, Item seedItem, Consumer<ItemStack> output) {
        int segments = Math.min(1 + cropEntity.getStage() / 3, 3);
        int yieldBonus = cropEntity.getYield() / 3;

        int totalDrops = segments * (1 + yieldBonus);
        ItemStack drop = new ItemStack(seedItem, totalDrops);
        output.accept(drop);

        ItemStack geneticSeed = getGenomeSeed(cropEntity, seedItem);
        output.accept(geneticSeed);
    }

    public void drop(String reason, CropStickBlockEntity cropEntity) {
        ServerLevel level = (ServerLevel) cropEntity.getLevel();
        BlockPos pos = cropEntity.getBlockPos();

        if (reason.equals("fullyGrown")) {
            harvest(cropEntity, level, pos);
        }
        if (reason.equals("doubleStick")) {
            ItemStack drop = ModBlocks.CROP_STICK.getBlock().asItem().getDefaultInstance();
            Block.popResource(level, pos, drop);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {

    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CropStickBlockEntity(blockPos, blockState);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;

        if (type != ModBlocks.CROP_STICK.getBlockEntityType()) return null;

        return (BlockEntityTicker<T>) (BlockEntityTicker<CropStickBlockEntity>) CropStickBlockEntity::tick;
    }
}