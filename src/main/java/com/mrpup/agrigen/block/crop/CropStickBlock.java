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
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.*;
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

    public CropStickBlock(Properties properties) {
        super(properties
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
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack item = stack.getItem().asItem().getDefaultInstance();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (AllHelper.isPlantable(item)) {
            if (!level.isClientSide()) {
                if (blockEntity instanceof CropStickBlockEntity cropEntity) {
                    if (!cropEntity.hasSeed() && !cropEntity.isWeed() && !cropEntity.getCropStickVariant().equals("double")) {

                        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);

                        if (customData != null && customData.copyTag().contains("genome")) {
                            CompoundTag genome = customData.copyTag();
                            cropEntity.setGenome(genome);
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
            return InteractionResult.SUCCESS;
        }

        if (item.is(ModBlocks.CROP_STICK.getBlock().asItem())) {
            if (!level.isClientSide()) {
                if (blockEntity instanceof CropStickBlockEntity cropEntity) {
                    String variant = cropEntity.getCropStickVariant();
                    if ("single".equals(variant)) {
                        cropEntity.setCropStickVariant("double");
                        stack.shrink(1);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (item.is(ModItems.HERBICIDE.item())) {
            if (!level.isClientSide()) {
                if (blockEntity instanceof CropStickBlockEntity cropEntity) {
                    if (cropEntity.isWeed()) return InteractionResult.FAIL;
                    if (cropEntity.hasHerbicide()) {
                        if (cropEntity.hasMaxHerbicide()) {
                            return InteractionResult.FAIL;
                        } else {
                            item.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                            cropEntity.setHerbicide(3);
                        }
                    } else {
                        item.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                        cropEntity.setHerbicide(3);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (item.is(ModItems.FERTILIZER.item())) {
            if (!level.isClientSide()) {
                if (blockEntity instanceof CropStickBlockEntity cropEntity) {
                    if (cropEntity.isWeed()) return InteractionResult.FAIL;
                    if (cropEntity.hasMaxFertilizer()) {
                        return InteractionResult.FAIL;
                    } else {
                        cropEntity.setFertilizer(15000);
                        stack.shrink(1);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!level.isClientSide()) {
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
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockBelow = level.getBlockState(pos.below());
        return blockBelow.getBlock() instanceof FarmlandBlock;
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    public ItemStack getGenomeSeed(CropStickBlockEntity cropEntity, Item seedItem) {
        Random random = new Random();
        int randomValue = random.nextInt(4);

        ItemStack stack = new ItemStack(seedItem, randomValue);

        CompoundTag seedTag = new CompoundTag();

        seedTag.putString("seedId", cropEntity.getSeedId());
        seedTag.put("genome",  cropEntity.getGenome());

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(seedTag));

        return stack;
    }

    public void harvest(CropStickBlockEntity cropEntity, ServerLevel level, BlockPos pos) {
        harvest(cropEntity, level, pos, drop -> Block.popResource(level, pos, drop));
    }

    public void harvest(CropStickBlockEntity cropEntity, ServerLevel level, BlockPos pos, Consumer<ItemStack> output) {
        Item seedItem = ItemHelper.getItemFromString(cropEntity.getSeedId());
        int yieldBonus = cropEntity.getYield();

        if (AllHelper.isStackingPlant(seedItem)) {
            harvestStackingPlant(seedItem, yieldBonus, output);
        } else if (AllHelper.isMushroom(seedItem)) {
            harvestMushroom(level, pos, seedItem, yieldBonus, output);
        } else if (AllHelper.isSapling(seedItem)) {
            harvestSapling(level, pos, seedItem, yieldBonus, output);
        } else if (AllHelper.isStem(seedItem)) {
            harvestStem(level, pos, seedItem, yieldBonus, output);
        } else if (AllHelper.isBerries(seedItem)) {
            harvestBerries(level, pos, yieldBonus, output);
        } else {
            harvestCrop(level, pos, seedItem, yieldBonus, output);
        }

        output.accept(getGenomeSeed(cropEntity, seedItem));

        if (AllHelper.isBerries(seedItem)) {
            cropEntity.setGrowthTicks(2500);
        } else {
            cropEntity.setGrowthTicks(0);
        }

        cropEntity.setFullyGrown(false);
    }

    private void harvestStem(ServerLevel level, BlockPos pos, Item seedItem, int yieldBonus, Consumer<ItemStack> output) {
        Block fruit = PlantRegistry.getFruit(seedItem);
        if (fruit == null) return;

        dropLootFromBlock(level, pos, fruit.defaultBlockState(), 1 + yieldBonus, output);
    }

    private void harvestBerries(ServerLevel level, BlockPos pos, int yieldBonus, Consumer<ItemStack> output) {
        BlockState grownBush = Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3);

        dropLootFromBlock(level, pos, grownBush, 1 + yieldBonus, output);
    }

    private void harvestCrop(ServerLevel level, BlockPos pos, Item seedItem, int yieldBonus, Consumer<ItemStack> output) {
        for (ItemStack drop : PlantDropHelper.getHarvestDrops(level, new ItemStack(seedItem), pos)) {
            if (drop.is(Tags.Items.SEEDS) || drop.is(Items.NETHER_WART)) continue;
            outputSplit(drop, 1 + yieldBonus, output);
        }
    }

    private void outputSplit(ItemStack drop, int total, Consumer<ItemStack> output) {
        while (total > 0) {
            int size = Math.min(total, drop.getMaxStackSize());
            output.accept(drop.copyWithCount(size));
            total -= size;
        }
    }

    private void harvestSapling(ServerLevel level, BlockPos pos, Item seedItem, int yieldBonus, Consumer<ItemStack> output) {
        if (!(seedItem instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof SaplingBlock saplingBlock)) {
            return;
        }

        Block logBlock = resolveLogBlock(saplingBlock);
        Block leavesBlock = resolveLeavesBlock(saplingBlock);

        if (logBlock == null) {
            logBlock = Blocks.OAK_LOG;
        }

        int logCount = 6 + yieldBonus;
        dropLootFromBlock(level, pos, logBlock.defaultBlockState(), logCount, output);

        if (leavesBlock != null) {
            int leavesCount = yieldBonus * yieldBonus;
            dropLootFromBlock(level, pos, leavesBlock.defaultBlockState(), leavesCount, output);
        }
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
        Identifier saplingId = BlockHelper.getBlockKey(saplingBlock);
        if (saplingId == null) {
            return null;
        }

        String path = saplingId.getPath();
        if (!path.endsWith("_" + fromSuffix)) {
            return null;
        }

        String basePath = path.substring(0, path.length() - fromSuffix.length());
        Identifier candidateId = Identifier.fromNamespaceAndPath(saplingId.getNamespace(), basePath + toSuffix);

        Block candidate = BlockHelper.getBlockFromLoc(candidateId);
        return candidate != Blocks.AIR ? candidate : null;
    }

    private void harvestMushroom(ServerLevel level, BlockPos pos, Item seedItem, int yieldBonus, Consumer<ItemStack> output) {
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
    }

    private void harvestStackingPlant(Item seedItem, int yieldBonus, Consumer<ItemStack> output) {
        output.accept(new ItemStack(seedItem, 1 + yieldBonus));
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
        if (level.isClientSide()) return null;

        if (type != ModBlocks.CROP_STICK.getBlockEntityType()) return null;

        return (BlockEntityTicker<T>) (BlockEntityTicker<CropStickBlockEntity>) CropStickBlockEntity::tick;
    }
}