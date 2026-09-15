package com.mrpup.agrigen.recipe;

import com.mrpup.agrigen.item.ModItems;
import com.mrpup.agrigen.item.genetic.GeneticTemplateItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class GeneticTemplateCombineRecipe extends CustomRecipe {

    public GeneticTemplateCombineRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean foundTemplate = false;
        boolean foundGene = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModItems.GENETIC_TEMPLATE.get())) {
                if (foundTemplate) return false;
                foundTemplate = true;
            } else if (stack.is(ModItems.GENE_SAMPLE.get())) {
                foundGene = true;
            } else {
                return false;
            }
        }

        return foundTemplate && foundGene;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack templateStack = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.is(ModItems.GENETIC_TEMPLATE.get())) {
                templateStack = stack.copyWithCount(1);
                break;
            }
        }
        if (templateStack.isEmpty()) return ItemStack.EMPTY;

        CompoundTag genes = GeneticTemplateItem.getGenesTag(templateStack);

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty() || !stack.is(ModItems.GENE_SAMPLE.get())) continue;
            if (!stack.has(DataComponents.CUSTOM_DATA)) continue;

            CompoundTag geneTag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            if (!geneTag.contains("geneType")) continue;

            String geneType = geneTag.getString("geneType");
            Tag geneValueTag = geneTag.get("geneValue");
            if (geneValueTag != null) {
                genes.put(geneType, geneValueTag.copy());
            }
        }

        GeneticTemplateItem.setGenesTag(templateStack, genes);
        return templateStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.GENETIC_TEMPLATE_COMBINE_SERIALIZER.get();
    }
}
