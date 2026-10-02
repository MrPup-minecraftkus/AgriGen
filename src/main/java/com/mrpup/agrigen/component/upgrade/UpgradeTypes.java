package com.mrpup.agrigen.component.upgrade;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class UpgradeTypes {
    public static final Identifier BG =
            Identifier.fromNamespaceAndPath("clumapi", "textures/gui/background.png");

    private static final int TEX = 256;

    public record StorageTexture(Identifier texture, int u, int v, int width, int height) {

        public void render(GuiGraphicsExtractor g, int x, int y) {
            g.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, TEX, TEX);
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

        public Identifier getTexture() {
            return storageTexture.texture();
        }

        public void render(GuiGraphicsExtractor g, int x, int y) {
            storageTexture.render(g, x, y);
        }
    }
}
