package com.mrpup.agrigen.plant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class StemFruitResolver {

    private static final Map<Block, Block> KNOWN_STEM_TO_FRUIT = Map.of(
            Blocks.MELON_STEM, Blocks.MELON,
            Blocks.PUMPKIN_STEM, Blocks.PUMPKIN
    );

    @Nullable
    public static Block resolveFruit(Block stemBlock) {
        if (KNOWN_STEM_TO_FRUIT.containsKey(stemBlock)) {
            return KNOWN_STEM_TO_FRUIT.get(stemBlock);
        }

        ResourceLocation stemId = BuiltInRegistries.BLOCK.getKey(stemBlock);
        String path = stemId.getPath();

        if (path.endsWith("_stem")) {
            String fruitPath = path.substring(0, path.length() - "_stem".length());
            ResourceLocation fruitId = ResourceLocation.fromNamespaceAndPath(stemId.getNamespace(), fruitPath);

            if (BuiltInRegistries.BLOCK.containsKey(fruitId)) {
                return BuiltInRegistries.BLOCK.get(fruitId);
            }
        }

        return null;
    }
}
