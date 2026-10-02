package com.mrpup.agrigen.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class CropStickRenderState extends BlockEntityRenderState {
    public String variant;
    public boolean hasSeed;
    public String seedId;
    public int stage;
    public boolean isWeed;
    public boolean isFullyGrown;
    public float fruitGrowthProgress;
}
