package com.mrpup.agrigen.item.genetic;

import com.mrpup.agrigen.client.event.GenomeTooltipHandler;
import com.mrpup.agrigen.plant.AllHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class GeneticTemplateItem extends Item {

    public static final String GENES_KEY = "genes";

    public GeneticTemplateItem(Properties properties) {
        super(properties);
    }

    public static CompoundTag getGenesTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return new CompoundTag();
        CompoundTag root = data.copyTag();
        return root.contains(GENES_KEY) ? root.getCompound(GENES_KEY) : new CompoundTag();
    }

    public static void setGenesTag(ItemStack stack, CompoundTag genes) {
        CustomData existing = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag root = existing != null ? existing.copyTag() : new CompoundTag();
        root.put(GENES_KEY, genes);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }

    public static boolean hasAllGenes(ItemStack stack) {
        CompoundTag genes = getGenesTag(stack);
        for (String key : com.mrpup.agrigen.plant.AllHelper.GENE_KEYS) {
            if (!genes.contains(key)) return false;
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        CompoundTag genes = getGenesTag(stack);

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.agrigen.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            return;
        }

        if (genes.isEmpty()) return;

        GenomeTooltipHandler.addGenomeTooltip(tooltip, genes, AllHelper.GENE_KEYS);
    }
}
