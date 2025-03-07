package dev.imabad.confectioneering.machines.enrober;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import dev.imabad.confectioneering.machines.ConfectionMachines;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class EnroberBlock extends Block implements IWrenchable, IBE<EnroberBlockEntity> {

    public static final Property<Direction> HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static VoxelShape block = box(0, -5, 0, 16, 16, 16);

    private static VoxelShaper opening = VoxelShaper.forHorizontal(box(2, -5, 14, 14, 10, 16),
            Direction.SOUTH);

    private static final VoxelShaper STRAIGHT = VoxelShaper.forHorizontalAxis(Shapes.join(block,
                    Shapes.or(opening.get(Direction.SOUTH), opening.get(Direction.NORTH)), BooleanOp.NOT_SAME),
            Direction.Axis.Z);
    public EnroberBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<EnroberBlockEntity> getBlockEntityClass() {
        return EnroberBlockEntity.class;
    }

    @Override
    public BlockEntityType<EnroberBlockEntity> getBlockEntityType() {
        return ConfectionMachines.ENROBER_BLOCK_ENTITY.get();
    }
    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        BlockState newState = originalState;

        if (targetedFace.getAxis() == Direction.Axis.Y) {
            if (originalState.hasProperty(HORIZONTAL_FACING))
                return originalState.setValue(HORIZONTAL_FACING, originalState
                        .getValue(HORIZONTAL_FACING).getClockWise(targetedFace.getAxis()));
        }
        return newState;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
        super.createBlockStateDefinition(builder);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(HORIZONTAL_FACING, context.getHorizontalDirection()
                        .getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getBlockEntity(pPos) instanceof EnroberBlockEntity be)
            be.randomTick();
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);

        if (heldItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                .isPresent()){
            return onBlockEntityUse(worldIn, pos, be -> {
                if (!heldItem.isEmpty()) {
                    be.internalTank.allowInsertion();
                    InteractionResult tryExchange = tryExchange(worldIn, player, handIn, heldItem, be);
                    be.internalTank.forbidInsertion();
                    if (tryExchange.consumesAction())
                        return tryExchange;
                }
                return InteractionResult.PASS;
            });
        }
        return InteractionResult.PASS;
    }

    protected InteractionResult tryExchange(Level worldIn, Player player, InteractionHand handIn, ItemStack heldItem,
                                            EnroberBlockEntity be) {
        if (FluidHelper.tryEmptyItemIntoBE(worldIn, player, handIn, heldItem, be))
            return InteractionResult.SUCCESS;
        if (GenericItemEmptying.canItemBeEmptied(worldIn, heldItem))
            return InteractionResult.SUCCESS;
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter p_220053_2_, BlockPos p_220053_3_,
                               CollisionContext p_220053_4_) {
        return STRAIGHT.get(blockState.getValue(HORIZONTAL_FACING).getClockWise().getAxis());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos.below());
        if (!isValidPositionForPlacement(state, worldIn, pos))
            return false;
        if (!blockState.getValue(BeltBlock.CASING))
            return false;
        return true;
    }

    public boolean isValidPositionForPlacement(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos.below());
        if (!AllBlocks.BELT.has(blockState))
            return false;
        if (blockState.getValue(BeltBlock.SLOPE) != BeltSlope.HORIZONTAL)
            return false;
        return true;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block pNeighborBlock, BlockPos fromPos, boolean pMovedByPiston) {
        if (level.isClientSide())
            return;

        if (fromPos.equals(pos.below())) {
            if (!canSurvive(state, level, pos)) {
                level.destroyBlock(pos, true);
                return;
            }
        }
    }
}
