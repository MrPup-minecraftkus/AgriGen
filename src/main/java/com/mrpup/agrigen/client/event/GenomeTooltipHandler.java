package com.mrpup.agrigen.client.event;

import com.mrpup.agrigen.AgriGen;
import com.mrpup.agrigen.plant.AllHelper;
import com.mrpup.clumapi.helper.ItemHelper;
import com.mrpup.clumapi.helper.PlayerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = AgriGen.MOD_ID)
public class GenomeTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (!stack.has(DataComponents.CUSTOM_DATA)) return;

        CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();

        boolean isFullGenome = tag.getBoolean("analyzed") && tag.contains("genome");
        boolean isGeneBlank = tag.contains("geneType") && tag.contains("geneValue");

        if (!isFullGenome && !isGeneBlank) return;

        List<Component> tooltip = event.getToolTip();

        if (!PlayerHelper.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.agrigen.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            return;
        }

        if (isFullGenome) {
            addGenomeTooltip(tooltip, tag.getCompound("genome"), AllHelper.GENE_KEYS);
        } else {
            addGeneBlankTooltip(tooltip, tag.getString("geneType"), tag);
        }
    }

    public static Component formatGeneLine(String geneKey, CompoundTag genes) {
        if ("speciesId".equals(geneKey)) {
            String speciesId = genes.getString(geneKey);
            ResourceLocation loc = ResourceLocation.tryParse(speciesId);

            if (loc != null) {
                Item item = ItemHelper.getItemFromLoc(loc);
                return Component.translatable("tooltip.agrigen.gene_species", item.getDescription());
            }
            return Component.translatable("tooltip.agrigen.gene_species", speciesId);
        }

        if ("effect".equals(geneKey)) {
            String effect = genes.getString(geneKey);
            String key = switch (effect) {
                case "solitary" -> "tooltip.agrigen.solitary";
                case "social" -> "tooltip.agrigen.social";
                case "parasitic" -> "tooltip.agrigen.parasitic";
                case "solar" -> "tooltip.agrigen.solar";
                case "nocturnal" -> "tooltip.agrigen.nocturnal";
                case "poisonous" -> "tooltip.agrigen.poisonous";
                default -> "tooltip.agrigen.none";
            };
            return Component.translatable(key);
        }

        int value = genes.getInt(geneKey);
        return switch (geneKey) {
            case "resistance" -> Component.translatable("tooltip.agrigen.gene_resistance", value);
            case "growSpeed" -> Component.translatable("tooltip.agrigen.gene_grow_speed", value);
            case "yield" -> Component.translatable("tooltip.agrigen.gene_yield", value);
            case "minTemp" -> Component.translatable("tooltip.agrigen.gene_temp_min", value);
            case "maxTemp" -> Component.translatable("tooltip.agrigen.gene_temp_max", value);
            case "dayCycleGrow" -> switch (value) {
                case 1 -> Component.translatable("tooltip.agrigen.gene_time_day");
                case 2 -> Component.translatable("tooltip.agrigen.gene_time_night");
                default -> Component.translatable("tooltip.agrigen.gene_time_any");
            };
            default -> null;
        };
    }

    public static void addGenomeTooltip(List<Component> tooltip, CompoundTag genome, String[] geneKeys) {
        tooltip.add(Component.translatable("tooltip.agrigen.genome_header").withStyle(ChatFormatting.GOLD));

        for (String geneKey : geneKeys) {
            if (!genome.contains(geneKey)) continue;

            Component line = formatGeneLine(geneKey, genome);
            if (line != null) {
                tooltip.add(line.copy().withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static void addGeneBlankTooltip(List<Component> tooltip, String geneType, CompoundTag tagWithGeneValue) {
        tooltip.add(Component.translatable("tooltip.agrigen.gene_blank_header").withStyle(ChatFormatting.GOLD));

        CompoundTag temp = new CompoundTag();
        if ("speciesId".equals(geneType) || "effect".equals(geneType)) {
            temp.putString(geneType, tagWithGeneValue.getString("geneValue"));
        } else {
            temp.putInt(geneType, tagWithGeneValue.getInt("geneValue"));
        }

        Component line = formatGeneLine(geneType, temp);
        if (line != null) {
            tooltip.add(line.copy().withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.literal(geneType + ": " + tagWithGeneValue.getInt("geneValue"))
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
