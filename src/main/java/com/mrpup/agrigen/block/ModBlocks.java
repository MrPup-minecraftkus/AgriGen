package com.mrpup.agrigen.block;

import com.mrpup.agrigen.block.blocks.*;
import com.mrpup.agrigen.block.tile.*;
import com.mrpup.clumapi.blocks.BlocksProperties;
import com.mrpup.clumapi.blocks.ComponentBlockHolder;
import com.mrpup.clumapi.blocks.RegBlocks;
import com.mrpup.agrigen.block.crop.CropStickBlock;
import com.mrpup.agrigen.block.crop.entity.CropStickBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static final ComponentBlockHolder<CropStickBlockEntity> CROP_STICK =
            RegBlocks.regComponent(
                    "crop_stick",
                    CropStickBlock::new,
                    BlocksProperties.copy(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_WOOD).strength(1f))
                            .skipBlockstatesGen()
                            .skipModelGenItem()
                            .skipModelGen()
            );


    public static final ComponentBlockHolder<SeedAnalyzerTile> SEED_ANALYZER =
            RegBlocks.regComponent(
                    "seed_analyzer",
                    SeedAnalyzerBlock::new,
                    BlocksProperties.copy(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_WOOD).requiresCorrectToolForDrops().strength(3f))
            );

    public static final ComponentBlockHolder<ElectricSeedAnalyzerTile> ELECTRIC_SEED_ANALYZER =
            RegBlocks.regComponent(
                    "electric_seed_analyzer",
                    ElectricSeedAnalyzerBlock::new,
                    BlocksProperties.copy(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3f))
            );

    public static final ComponentBlockHolder<GeneExtractorTile> GENE_EXTRACTOR =
            RegBlocks.regComponent(
                    "gene_extractor",
                    GeneExtractorBlock::new,
                    BlocksProperties.copy(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3f))
            );

    public static final ComponentBlockHolder<GeneImporterTile> GENE_IMPORTER=
            RegBlocks.regComponent(
                    "gene_importer",
                    GeneImporterBlock::new,
                    BlocksProperties.copy(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3f))
            );

    public static final ComponentBlockHolder<DNAExtractorTile> DNA_EXTRACTOR =
            RegBlocks.regComponent(
                    "dna_extractor",
                    DNAExtractorBlock::new,
                    BlocksProperties.copy(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3f))
            );

    public static final ComponentBlockHolder<PlantSynthesizerTile> PLANT_SYNTHESIZER =
            RegBlocks.regComponent(
                    "plant_synthesizer",
                    PlantSynthesizerBlock::new,
                    BlocksProperties.copy(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3f))
            );

    public static final ComponentBlockHolder<CraftSynthesizerTile> CRAFT_SYNTHESIZER =
            RegBlocks.regComponent(
                    "craft_synthesizer",
                    CraftSynthesizerBlock::new,
                    BlocksProperties.copy(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3f))
            );

    public static final ComponentBlockHolder<AutoFarmerTile> AUTO_FARMER =
            RegBlocks.regComponent(
                    "auto_farmer",
                    AutoFarmerBlock::new,
                    BlocksProperties.copy(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(3f))
            );

    public static void register() {

    }
}
