package com.mrpup.agrigen.component.upgrade;

import com.mrpup.clumapi.component.IGuiComponent;
import com.mrpup.clumapi.menus.BaseComponentMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class UpgradeComponent implements IGuiComponent  {

    private final int xPos;
    private final int yPos;

    public UpgradeComponent(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    @Override
    public void addSlots(BaseComponentMenu menu) {

    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int leftPos, int topPos) {
        int x = leftPos + xPos;
        int y = topPos + yPos;

        UpgradeTypes.UpgradeType.UPGRADE_STORAGE.render(graphics, x, y);
    }

    @Override
    public int getX() {
        return xPos;
    }

    @Override
    public int getY() {
        return yPos;
    }
}
