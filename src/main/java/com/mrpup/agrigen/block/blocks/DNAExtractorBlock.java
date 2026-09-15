package com.mrpup.agrigen.block.blocks;

import com.mrpup.agrigen.block.tile.DNAExtractorTile;
import com.mrpup.clumapi.blocks.ComponentBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DNAExtractorBlock extends ComponentBlock<DNAExtractorTile>  {

    public DNAExtractorBlock() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .noOcclusion()
                        .lightLevel(state -> 15),
                DNAExtractorTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<DNAExtractorTile> getTileFactory() {
        return DNAExtractorTile::new;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null :
                (level1, pos, state1, be) -> ((DNAExtractorTile) be).tick(level1, pos, state1);
    }
}
