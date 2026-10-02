package com.mrpup.agrigen.item;

import com.mrpup.agrigen.AgriGen;
import com.mrpup.agrigen.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItemGroups {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AgriGen.MOD_ID);

    public static final Supplier<CreativeModeTab> AGRIGEN = CREATIVE_MODE_TAB.register("agrigen",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.CROP_STICK.getBlock().asItem()))
                    .title(Component.translatable("itemgroup.agrigen.agrigen"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.CROP_STICK.getBlock().asItem());
                        output.accept(ModBlocks.SEED_ANALYZER.getBlock().asItem());
                        output.accept(ModBlocks.ELECTRIC_SEED_ANALYZER.getBlock().asItem());
                        output.accept(ModBlocks.GENE_EXTRACTOR.getBlock().asItem());
                        output.accept(ModBlocks.GENE_IMPORTER.getBlock().asItem());
                        output.accept(ModBlocks.DNA_EXTRACTOR.getBlock().asItem());
                        output.accept(ModBlocks.PLANT_SYNTHESIZER.getBlock().asItem());
                        output.accept(ModBlocks.CRAFT_SYNTHESIZER.getBlock().asItem());
                        output.accept(ModBlocks.AUTO_FARMER.getBlock().asItem());
                        output.accept(ModItems.BLANK_UPGRADE.get());
                        output.accept(ModItems.EFFICIENCY_UPGRADE_1.get());
                        output.accept(ModItems.EFFICIENCY_UPGRADE_2.get());
                        output.accept(ModItems.EFFICIENCY_UPGRADE_3.get());
                        output.accept(ModItems.SPEED_UPGRADE_1.get());
                        output.accept(ModItems.SPEED_UPGRADE_2.get());
                        output.accept(ModItems.SPEED_UPGRADE_3.get());
                        output.accept(ModItems.RADIUS_UPGRADE_1.get());
                        output.accept(ModItems.RADIUS_UPGRADE_2.get());
                        output.accept(ModItems.RADIUS_UPGRADE_3.get());
                        output.accept(ModItems.BLANK_GENE_SAMPLE.get());
                        output.accept(ModItems.GENE_SAMPLE.get());
                        output.accept(ModItems.GENETIC_TEMPLATE.get());
                        output.accept(ModItems.GENETIC_WASTE.get());
                        output.accept(ModItems.HERBICIDE.get());
                        output.accept(ModItems.FERTILIZER.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
