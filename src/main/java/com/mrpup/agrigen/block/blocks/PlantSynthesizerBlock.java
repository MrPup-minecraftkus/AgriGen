package com.mrpup.agrigen.block.blocks;

import com.mrpup.agrigen.block.tile.PlantSynthesizerTile;
import com.mrpup.clumapi.blocks.ComponentBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PlantSynthesizerBlock extends ComponentBlock<PlantSynthesizerTile>  {

    public PlantSynthesizerBlock() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .noOcclusion()
                        .lightLevel(state -> 15),
                PlantSynthesizerTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<PlantSynthesizerTile> getTileFactory() {
        return PlantSynthesizerTile::new;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null :
                (level1, pos, state1, be) -> ((PlantSynthesizerTile) be).tick(level1, pos, state1);
    }
}
