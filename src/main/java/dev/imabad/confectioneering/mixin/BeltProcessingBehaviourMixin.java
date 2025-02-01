package dev.imabad.confectioneering.mixin;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import dev.imabad.confectioneering.machines.enrober.EnroberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeltProcessingBehaviour.class)
public class BeltProcessingBehaviourMixin {

    @Inject(method = "isBlocked(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", at=@At("HEAD"), cancellable = true)
    private static void injectIsBlocked(BlockGetter world, BlockPos processingSpace, CallbackInfoReturnable<Boolean> cir){
        if(world.getBlockState(processingSpace.above()).getBlock() instanceof EnroberBlock){
            cir.setReturnValue(false);
        }
    }

}
