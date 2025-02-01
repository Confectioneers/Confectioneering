package dev.imabad.confectioneering;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.imabad.confectioneering.machines.enrober.EnroberBlock;
import dev.imabad.confectioneering.processing.InlineBeltProcessingBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class MixinHandlers {

    public static void isBlockCoveringBeltInt(LevelAccessor world, BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        BlockState blockState = world.getBlockState(pos);
        if(blockState.getBlock() instanceof EnroberBlock){
            cir.setReturnValue(false);
        }
    }

    public static void overrideBeltProcessingAtSegment(int segment, BeltBlockEntity belt, CallbackInfoReturnable<BeltProcessingBehaviour> cir) {
        InlineBeltProcessingBehaviour inlineBeltProcessingBehaviour = BlockEntityBehaviour.get(belt.getLevel(), BeltHelper.getPositionForOffset(belt, segment)
                .above(), InlineBeltProcessingBehaviour.TYPE);
        if(inlineBeltProcessingBehaviour != null){
            cir.setReturnValue(inlineBeltProcessingBehaviour);
        }
    }
}
