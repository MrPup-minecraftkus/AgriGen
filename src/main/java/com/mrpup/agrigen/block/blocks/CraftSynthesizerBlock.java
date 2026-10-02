package com.mrpup.agrigen.block.blocks;

import com.mrpup.agrigen.block.tile.CraftSynthesizerTile;
import com.mrpup.clumapi.blocks.ComponentBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CraftSynthesizerBlock extends ComponentBlock<CraftSynthesizerTile>  {

    public CraftSynthesizerBlock(Properties properties) {
        super(properties
                        .noOcclusion()
                        .lightLevel(state -> 15),
                CraftSynthesizerTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<CraftSynthesizerTile> getTileFactory() {
        return CraftSynthesizerTile::new;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null :
                (level1, pos, state1, be) -> ((CraftSynthesizerTile) be).tick(level1, pos, state1);
    }
}
