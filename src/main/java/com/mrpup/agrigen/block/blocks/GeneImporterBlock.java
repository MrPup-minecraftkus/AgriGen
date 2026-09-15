package com.mrpup.agrigen.block.blocks;

import com.mrpup.agrigen.block.tile.GeneImporterTile;
import com.mrpup.clumapi.blocks.ComponentBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GeneImporterBlock extends ComponentBlock<GeneImporterTile>  {

    public GeneImporterBlock() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .noOcclusion()
                        .lightLevel(state -> 15),
                GeneImporterTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<GeneImporterTile> getTileFactory() {
        return GeneImporterTile::new;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null :
                (level1, pos, state1, be) -> ((GeneImporterTile) be).tick(level1, pos, state1);
    }
}
