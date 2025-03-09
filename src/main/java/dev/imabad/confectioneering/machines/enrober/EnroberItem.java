package dev.imabad.confectioneering.machines.enrober;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import dev.imabad.confectioneering.machines.ConfectionMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class EnroberItem extends BlockItem {

    public EnroberItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext ctx, BlockState state) {
        Player player = ctx.getPlayer();
        CollisionContext context =
                player == null ? CollisionContext.empty() : CollisionContext.of(player);
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        return (!this.mustSurvive() || ConfectionMachines.ENROBER_BLOCK.get()
                .isValidPositionForPlacement(state, world, pos)) && world.isUnobstructed(state, pos, context);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player player, ItemStack itemStack,
                                                 BlockState state) {
        boolean flag = super.updateCustomBlockEntityTag(pos, world, player, itemStack, state);
        if (!world.isClientSide) {
            BeltBlockEntity belt = BeltHelper.getSegmentBE(world, pos.below());
            if (belt != null && belt.casing == BeltBlockEntity.CasingType.NONE)
                belt.setCasingType(BeltBlockEntity.CasingType.ANDESITE);
        }
        return flag;
    }
}
