package dev.imabad.confectioneering.machines.dipper;

import com.simibubi.create.*;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import dev.imabad.confectioneering.machines.ConfectionMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;

public class DipperBlock extends HorizontalKineticBlock implements IWrenchable, IBE<DipperBlockEntity> {
    public DipperBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<DipperBlockEntity> getBlockEntityClass() {
        return DipperBlockEntity.class;
    }

    @Override
    public BlockEntityType<DipperBlockEntity> getBlockEntityType() {
        return ConfectionMachines.DIPPER_BLOCK_ENTITY.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return getRotationAxis(state) == face.getAxis();
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

        return onBlockEntityUse(worldIn, pos, be -> {
            if (hit.getDirection() != Direction.UP)
                return InteractionResult.PASS;
            if (worldIn.isClientSide)
                return InteractionResult.SUCCESS;

            boolean wasEmptyHanded = heldItem.isEmpty();
            boolean shouldntPlaceItem = AllBlocks.MECHANICAL_ARM.isIn(heldItem) || AllItems.WRENCH.isIn(heldItem);

            ItemStack mainItemStack = be.inventory.getStackInSlot(0);
            if (!mainItemStack.isEmpty()) {
                player.getInventory()
                        .placeItemBackInInventory(mainItemStack);
                be.inventory.setStackInSlot(0, ItemStack.EMPTY);
                worldIn.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                        1f + Create.RANDOM.nextFloat());
            }
            ItemStackHandler outputs = be.inventory;
            for (int i = 0; i < outputs.getSlots(); i++)
                player.getInventory()
                        .placeItemBackInInventory(outputs.extractItem(i, 64, false));

            if (!wasEmptyHanded && !shouldntPlaceItem) {
                ItemStack returned = be.inventory.insertItem(0, heldItem, false);
                player.setItemInHand(handIn, returned);
            }
            if(!wasEmptyHanded && shouldntPlaceItem){
                return InteractionResult.PASS;
            }

            be.notifyUpdate();
            return InteractionResult.SUCCESS;
        });
    }

    protected InteractionResult tryExchange(Level worldIn, Player player, InteractionHand handIn, ItemStack heldItem,
                                            DipperBlockEntity be) {
        if (FluidHelper.tryEmptyItemIntoBE(worldIn, player, handIn, heldItem, be))
            return InteractionResult.SUCCESS;
        if (GenericItemEmptying.canItemBeEmptied(worldIn, heldItem))
            return InteractionResult.SUCCESS;
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_,
                               CollisionContext p_220053_4_) {
        return AllShapes.CASING_13PX.get(Direction.UP);
    }
}
