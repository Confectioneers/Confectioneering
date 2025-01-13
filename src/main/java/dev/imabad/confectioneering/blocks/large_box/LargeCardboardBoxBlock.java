package dev.imabad.confectioneering.blocks.large_box;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.IBE;
import dev.imabad.confectioneering.blocks.ConfectionBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LargeCardboardBoxBlock extends HorizontalDirectionalBlock implements IBE<LargeCardboardBoxBlockEntity> {
    public LargeCardboardBoxBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection()
                        .getOpposite());
    }

    @Override
    public Class<LargeCardboardBoxBlockEntity> getBlockEntityClass() {
        return LargeCardboardBoxBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LargeCardboardBoxBlockEntity> getBlockEntityType() {
        return ConfectionBlocks.LARGE_CARDBOARD_BOX_BLOCK_ENTITY.get();
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        if(!worldIn.isClientSide()) {
            onBlockEntityUse(worldIn, pos, (be) -> {
                if (be.state == LargeCardboardBoxBlockEntity.State.CLOSED) {
                    be.open();
                } else if(be.state == LargeCardboardBoxBlockEntity.State.OPEN){
                    be.close();
                }
                return InteractionResult.PASS;
            });
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_,
                               CollisionContext p_220053_4_) {
        return Block.box(3, 0, 3, 13, 10, 13);
    }
}
