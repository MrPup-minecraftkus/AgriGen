package com.mrpup.agrigen.block.blocks;

import com.mrpup.agrigen.block.tile.GeneExtractorTile;
import com.mrpup.clumapi.blocks.ComponentBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GeneExtractorBlock extends ComponentBlock<GeneExtractorTile>  {

    public GeneExtractorBlock() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .noOcclusion()
                        .lightLevel(state -> 15),
                GeneExtractorTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<GeneExtractorTile> getTileFactory() {
        return GeneExtractorTile::new;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null :
                (level1, pos, state1, be) -> ((GeneExtractorTile) be).tick(level1, pos, state1);
    }
}
