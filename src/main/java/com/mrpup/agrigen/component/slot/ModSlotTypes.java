package com.mrpup.agrigen.component.slot;

import com.mrpup.agrigen.plant.AllHelper;
import com.mrpup.agrigen.item.ModItems;
import com.mrpup.clumapi.component.slot.SlotTypes;
import com.mrpup.agrigen.item.upgrade.UpgradeItem;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class ModSlotTypes {

    private static boolean isBlankGeneSample(ItemStack stack) {
        return stack.is(ModItems.BLANK_GENE_SAMPLE.item());
    }

    private static boolean isGeneSample(ItemStack stack) {
        return stack.is(ModItems.GENE_SAMPLE.item());
    }

    private static boolean isGeneTemplate(ItemStack stack) {
        return stack.is(ModItems.GENETIC_TEMPLATE.item());
    }

    public static final SlotTypes.SlotType SLOT_SEEDS = new SlotTypes.SlotType() {
        private final SlotTypes.SlotTexture texture =
                new SlotTypes.SlotTexture(SlotTypes.BG, 1, 204, 18, 18);

        @Override
        public boolean accepts(ItemStack stack) {
            return AllHelper.isPlantable(stack);
        }

        @Override
        public SlotTypes.SlotTexture getSlotTexture() {
            return texture;
        }

        @Override
        public float[] getColor() {
            int colorInt = DyeColor.LIME.getFireworkColor();

            float r = FastColor.ARGB32.red(colorInt) / 255.0f;
            float g = FastColor.ARGB32.green(colorInt) / 255.0f;
            float b = FastColor.ARGB32.blue(colorInt) / 255.0f;

            return new float[]{r, g, b};
        }
    };

    public static final SlotTypes.SlotType SLOT_UPGRADE = new SlotTypes.SlotType() {
        private final SlotTypes.SlotTexture texture =
                new SlotTypes.SlotTexture(SlotTypes.BG, 20, 204, 18, 18);

        @Override
        public boolean accepts(ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() instanceof UpgradeItem;
        }

        @Override
        public SlotTypes.SlotTexture getSlotTexture() {
            return texture;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public float[] getColor() {
            int colorInt = DyeColor.LIGHT_BLUE.getFireworkColor();

            float r = FastColor.ARGB32.red(colorInt) / 255.0f;
            float g = FastColor.ARGB32.green(colorInt) / 255.0f;
            float b = FastColor.ARGB32.blue(colorInt) / 255.0f;

            return new float[]{r, g, b};
        }
    };

    public static final SlotTypes.SlotType SLOT_BLANK_GENE_SAMPLE = new SlotTypes.SlotType() {
        private final SlotTypes.SlotTexture texture =
                new SlotTypes.SlotTexture(SlotTypes.BG, 39, 204, 18, 18);

        @Override
        public boolean accepts(ItemStack stack) {
            return !stack.isEmpty() && isBlankGeneSample(stack);
        }

        @Override
        public SlotTypes.SlotTexture getSlotTexture() {
            return texture;
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public float[] getColor() {
            int colorInt = DyeColor.MAGENTA.getFireworkColor();

            float r = FastColor.ARGB32.red(colorInt) / 255.0f;
            float g = FastColor.ARGB32.green(colorInt) / 255.0f;
            float b = FastColor.ARGB32.blue(colorInt) / 255.0f;

            return new float[]{r, g, b};
        }
    };

    public static final SlotTypes.SlotType OUTPUT_SLOT_GENE_SAMPLE = new SlotTypes.SlotType() {
        private final SlotTypes.SlotTexture texture =
                new SlotTypes.SlotTexture(SlotTypes.BG, 39, 204, 18, 18);

        @Override
        public boolean accepts(ItemStack stack) {
            return !stack.isEmpty() && isGeneSample(stack);
        }

        @Override
        public SlotTypes.SlotTexture getSlotTexture() {
            return texture;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public float[] getColor() {
            int colorInt = DyeColor.ORANGE.getFireworkColor();

            float r = FastColor.ARGB32.red(colorInt) / 255.0f;
            float g = FastColor.ARGB32.green(colorInt) / 255.0f;
            float b = FastColor.ARGB32.blue(colorInt) / 255.0f;

            return new float[]{r, g, b};
        }

        @Override
        public SlotTypes.SlotType getOutputVariant() {
            return this;
        }
    };

    public static final SlotTypes.SlotType SLOT_GENE_TEMPLATE = new SlotTypes.SlotType() {
        private final SlotTypes.SlotTexture texture =
                new SlotTypes.SlotTexture(SlotTypes.BG, 58, 204, 18, 18);

        @Override
        public boolean accepts(ItemStack stack) {
            return !stack.isEmpty() && isGeneTemplate(stack);
        }

        @Override
        public SlotTypes.SlotTexture getSlotTexture() {
            return texture;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public float[] getColor() {
            int colorInt = DyeColor.MAGENTA.getFireworkColor();

            float r = FastColor.ARGB32.red(colorInt) / 255.0f;
            float g = FastColor.ARGB32.green(colorInt) / 255.0f;
            float b = FastColor.ARGB32.blue(colorInt) / 255.0f;

            return new float[]{r, g, b};
        }
    };

    public static final SlotTypes.SlotType FERTILIZER_SLOT = new SlotTypes.SlotType() {
        private final SlotTypes.SlotTexture texture =
                new SlotTypes.SlotTexture(SlotTypes.BG, 39, 185, 18, 18);

        @Override
        public boolean accepts(ItemStack stack) {
            return stack.is(ModItems.FERTILIZER.item());
        }

        @Override
        public SlotTypes.SlotTexture getSlotTexture() {
            return texture;
        }

        @Override
        public float[] getColor() {
            int colorInt = DyeColor.BROWN.getFireworkColor();

            float r = FastColor.ARGB32.red(colorInt) / 255.0f;
            float g = FastColor.ARGB32.green(colorInt) / 255.0f;
            float b = FastColor.ARGB32.blue(colorInt) / 255.0f;

            return new float[]{r, g, b};
        }
    };

    public static final SlotTypes.SlotType HERBICIDE_SLOT = new SlotTypes.SlotType() {
        private final SlotTypes.SlotTexture texture =
                new SlotTypes.SlotTexture(SlotTypes.BG, 20, 185, 18, 18);

        @Override
        public boolean accepts(ItemStack stack) {
            return stack.is(ModItems.HERBICIDE.item());
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public SlotTypes.SlotTexture getSlotTexture() {
            return texture;
        }

        @Override
        public float[] getColor() {
            int colorInt = DyeColor.LIME.getFireworkColor();

            float r = FastColor.ARGB32.red(colorInt) / 255.0f;
            float g = FastColor.ARGB32.green(colorInt) / 255.0f;
            float b = FastColor.ARGB32.blue(colorInt) / 255.0f;

            return new float[]{r, g, b};
        }
    };
}
