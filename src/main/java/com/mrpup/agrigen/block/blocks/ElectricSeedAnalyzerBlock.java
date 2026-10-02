package com.mrpup.agrigen.block.blocks;

import com.mrpup.clumapi.blocks.ComponentBlock;
import com.mrpup.agrigen.block.tile.ElectricSeedAnalyzerTile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricSeedAnalyzerBlock extends ComponentBlock<ElectricSeedAnalyzerTile>  {

    public ElectricSeedAnalyzerBlock(Properties properties) {
        super(properties
                        .noOcclusion()
                        .lightLevel(state -> 15),
                ElectricSeedAnalyzerTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<ElectricSeedAnalyzerTile> getTileFactory() {
        return ElectricSeedAnalyzerTile::new;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null :
                (level1, pos, state1, be) -> ((ElectricSeedAnalyzerTile) be).tick(level1, pos, state1);
    }
}
