package com.mrpup.agrigen.component.upgrade;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class UpgradeTypes {
    public static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;

    public record StorageTexture(ResourceLocation texture, int u, int v, int width, int height) {

        public void render(GuiGraphics g, int x, int y) {
            g.blit(texture, x, y, u, v, width, height, TEX, TEX);
        }
    }

    public enum UpgradeType {

        UPGRADE_STORAGE(new UpgradeTypes.StorageTexture(UpgradeTypes.BG, 211, 94, 30, 84));

        private final UpgradeTypes.StorageTexture storageTexture;

        UpgradeType(UpgradeTypes.StorageTexture slotTexture) {
            this.storageTexture = slotTexture;
        }

        public UpgradeTypes.StorageTexture getSlotTexture() {
            return storageTexture;
        }

        public ResourceLocation getTexture() {
            return storageTexture.texture();
        }

        public void render(GuiGraphics g, int x, int y) {
            storageTexture.render(g, x, y);
        }
    }
}
