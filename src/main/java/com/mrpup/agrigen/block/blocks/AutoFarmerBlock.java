package com.mrpup.agrigen.block.blocks;

import com.mrpup.agrigen.block.tile.AutoFarmerTile;
import com.mrpup.clumapi.blocks.ComponentBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AutoFarmerBlock extends ComponentBlock<AutoFarmerTile> {

    public AutoFarmerBlock(Properties properties) {
        super(properties
                        .noOcclusion()
                        .lightLevel(state -> 15),
                AutoFarmerTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<AutoFarmerTile> getTileFactory() {
        return AutoFarmerTile::new;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null :
                (level1, pos, state1, be) -> ((AutoFarmerTile) be).tick(level1, pos, state1);
    }
}
