package com.mrpup.agrigen.recipe;

import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.item.ModItems;
import com.mrpup.agrigen.recipe.machine.builder.CraftSynthesizerRecipeBuilder;
import com.mrpup.clumapi.recipe.RegRecipes;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

public class ModRecipes {

    public static void registerRecipes() {

        RegRecipes.regRecipe("fertilizer_meal", p ->
                CraftSynthesizerRecipeBuilder.create(ModItems.FERTILIZER.get())
                        .count(1)
                        .input(0, Items.BONE_MEAL)
                        .input(1, Items.ROTTEN_FLESH)
                        .input(2, Items.SPIDER_EYE)
                        .input(3, Items.DIRT)
                        .fluid(Fluids.WATER.getSource(), 1000)
                        .unlockedBy("has_bone_meal", p.hasItem(Items.BONE_MEAL))
        );

        RegRecipes.regRecipe("fertilizer_waste", p ->
                CraftSynthesizerRecipeBuilder.create(ModItems.FERTILIZER.get())
                        .count(3)
                        .input(0, ModItems.GENETIC_WASTE.get())
                        .input(1, Items.BONE_MEAL)
                        .input(2, Items.BONE_MEAL)
                        .input(3, Items.DIRT)
                        .fluid(Fluids.WATER.getSource(), 1000)
                        .unlockedBy("has_bone_meal", p.hasItem(Items.BONE_MEAL))
        );

        RegRecipes.regRecipe("herbicide", p ->
                CraftSynthesizerRecipeBuilder.create(ModItems.HERBICIDE.get())
                        .count(1)
                        .input(0, Items.FERMENTED_SPIDER_EYE)
                        .input(1, Items.BLAZE_POWDER)
                        .input(2, Items.GUNPOWDER)
                        .input(3, Items.IRON_INGOT)
                        .fluid(Fluids.WATER.getSource(), 1000)
                        .unlockedBy("has_gunpowder", p.hasItem(Items.GUNPOWDER))
        );

        RegRecipes.regRecipe("crop_stick", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CROP_STICK.getBlock(), 16)
                        .pattern("   ")
                        .pattern("A A")
                        .pattern("A A")
                        .define('A', ItemTags.PLANKS)
                        .unlockedBy("has_planks", p.hasTag(ItemTags.PLANKS))
        );

        RegRecipes.regRecipe("seed_analyzer", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SEED_ANALYZER.getBlock())
                        .pattern("PGP")
                        .pattern("PMP")
                        .pattern("PRP")
                        .define('P', ItemTags.PLANKS)
                        .define('G', Items.REDSTONE)
                        .define('M', Items.SPYGLASS)
                        .define('R', Items.IRON_INGOT)
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("electric_seed_analyzer", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ELECTRIC_SEED_ANALYZER.getBlock())
                        .pattern("PGP")
                        .pattern("PMP")
                        .pattern("PPP")
                        .define('P', Items.IRON_INGOT)
                        .define('G', Items.REDSTONE)
                        .define('M', ModBlocks.SEED_ANALYZER.getBlock())
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("dna_extractor", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DNA_EXTRACTOR.getBlock())
                        .pattern("PGP")
                        .pattern("PMP")
                        .pattern("PSP")
                        .define('P', Items.IRON_INGOT)
                        .define('G', Items.REDSTONE)
                        .define('S', Items.PISTON)
                        .define('M', Items.BUCKET)
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("gene_extractor", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GENE_EXTRACTOR.getBlock())
                        .pattern("PGP")
                        .pattern("PMP")
                        .pattern("PSP")
                        .define('P', Items.IRON_INGOT)
                        .define('G', Items.REDSTONE)
                        .define('M', ModItems.BLANK_GENE_SAMPLE.item())
                        .define('S', Items.HOPPER)
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("gene_importer", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GENE_IMPORTER.getBlock())
                        .pattern("PGP")
                        .pattern("PMP")
                        .pattern("PSP")
                        .define('P', Items.IRON_INGOT)
                        .define('G', Items.HOPPER)
                        .define('M', ModItems.BLANK_GENE_SAMPLE.item())
                        .define('S', Items.REDSTONE)
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("plant_synthesizer", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.PLANT_SYNTHESIZER.getBlock())
                        .pattern("PGP")
                        .pattern("PMP")
                        .pattern("PSP")
                        .define('P', Items.IRON_INGOT)
                        .define('G', Items.REDSTONE)
                        .define('M', ModItems.GENETIC_TEMPLATE.item())
                        .define('S', Items.CRAFTER)
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("craft_synthesizer", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CRAFT_SYNTHESIZER.getBlock())
                        .pattern("PGP")
                        .pattern("SMS")
                        .pattern("PSP")
                        .define('P', Items.IRON_INGOT)
                        .define('G', Items.HOPPER)
                        .define('M', Items.CRAFTER)
                        .define('S', Items.HOPPER)
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("auto_farmer", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.AUTO_FARMER.getBlock())
                        .pattern("PGP")
                        .pattern("MSM")
                        .pattern("PSP")
                        .define('P', Items.IRON_INGOT)
                        .define('G', Items.BUCKET)
                        .define('M', Items.DIAMOND_HOE)
                        .define('S', Items.REDSTONE_BLOCK)
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );


        RegRecipes.regRecipe("blank_gene_sample", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLANK_GENE_SAMPLE.item())
                        .pattern(" A ")
                        .pattern("ASA")
                        .pattern(" A ")
                        .define('A', Items.IRON_INGOT)
                        .define('S', Items.PAPER)
                        .unlockedBy("has_iron_ingot", p.hasItem(Items.IRON_INGOT))
        );

        RegRecipes.regRecipe("genetic_template", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GENETIC_TEMPLATE.item())
                        .pattern("BAB")
                        .pattern("ASA")
                        .pattern("BAB")
                        .define('A', ModItems.BLANK_GENE_SAMPLE.item())
                        .define('B', Items.REDSTONE)
                        .define('S', Items.DIAMOND)
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("blank_upgrade", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLANK_UPGRADE.item())
                        .pattern(" A ")
                        .pattern("ASA")
                        .pattern(" A ")
                        .define('A', Items.IRON_INGOT)
                        .define('S', Items.QUARTZ)
                        .unlockedBy("has_iron_ingot", p.hasItem(Items.IRON_INGOT))
        );

        RegRecipes.regRecipe("efficiency_upgrade_1", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EFFICIENCY_UPGRADE_1.item())
                        .pattern("BAB")
                        .pattern("ASA")
                        .pattern("BAB")
                        .define('A', Items.LAPIS_LAZULI)
                        .define('B', Items.REDSTONE)
                        .define('S', ModItems.BLANK_UPGRADE.item())
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("efficiency_upgrade_2", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EFFICIENCY_UPGRADE_2.item())
                        .pattern("BAB")
                        .pattern("ASA")
                        .pattern("BAB")
                        .define('A', Items.GOLD_INGOT)
                        .define('B', Items.REDSTONE)
                        .define('S', ModItems.EFFICIENCY_UPGRADE_1.item())
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("efficiency_upgrade_3", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EFFICIENCY_UPGRADE_3.item())
                        .pattern("BAB")
                        .pattern("ASA")
                        .pattern("BAB")
                        .define('A', Items.DIAMOND)
                        .define('B', Items.REDSTONE)
                        .define('S', ModItems.EFFICIENCY_UPGRADE_2.item())
                        .unlockedBy("has_redstone", p.hasItem(Items.REDSTONE))
        );

        RegRecipes.regRecipe("speed_upgrade_1", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPEED_UPGRADE_1.item())
                        .pattern("BAB")
                        .pattern("ASA")
                        .pattern("BAB")
                        .define('A', Items.LAPIS_LAZULI)
                        .define('B', Items.SUGAR)
                        .define('S', ModItems.BLANK_UPGRADE.item())
                        .unlockedBy("has_sugar", p.hasItem(Items.SUGAR))
        );

        RegRecipes.regRecipe("speed_upgrade_2", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPEED_UPGRADE_2.item())
                        .pattern("BAB")
                        .pattern("ASA")
                        .pattern("BAB")
                        .define('A', Items.GOLD_INGOT)
                        .define('B', Items.SUGAR)
                        .define('S', ModItems.SPEED_UPGRADE_1.item())
                        .unlockedBy("has_sugar", p.hasItem(Items.SUGAR))
        );

        RegRecipes.regRecipe("speed_upgrade_3", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPEED_UPGRADE_3.item())
                        .pattern("BAB")
                        .pattern("ASA")
                        .pattern("BAB")
                        .define('A', Items.DIAMOND)
                        .define('B', Items.SUGAR)
                        .define('S', ModItems.SPEED_UPGRADE_2.item())
                        .unlockedBy("has_sugar", p.hasItem(Items.SUGAR))
        );

        RegRecipes.regRecipe("radius_upgrade_1", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RADIUS_UPGRADE_1.item())
                        .pattern("BAB")
                        .pattern("ASA")
                        .pattern("BAB")
                        .define('A', Items.LAPIS_LAZULI)
                        .define('B', Items.GUNPOWDER)
                        .define('S', ModItems.BLANK_UPGRADE.item())
                        .unlockedBy("has_gunpowder", p.hasItem(Items.GUNPOWDER))
        );

        RegRecipes.regRecipe("radius_upgrade_2", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RADIUS_UPGRADE_2.item())
                        .pattern("BAB")
                        .pattern("ASA")
                        .pattern("BAB")
                        .define('A', Items.GOLD_INGOT)
                        .define('B', Items.GUNPOWDER)
                        .define('S', ModItems.RADIUS_UPGRADE_1.item())
                        .unlockedBy("has_gunpowder", p.hasItem(Items.GUNPOWDER))
        );

        RegRecipes.regRecipe("radius_upgrade_3", p ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RADIUS_UPGRADE_3.item())
                        .pattern("BAB")
                        .pattern("ASA")
                        .pattern("BAB")
                        .define('A', Items.DIAMOND)
                        .define('B', Items.GUNPOWDER)
                        .define('S', ModItems.RADIUS_UPGRADE_2.item())
                        .unlockedBy("has_gunpowder", p.hasItem(Items.GUNPOWDER))
        );
    }
}
