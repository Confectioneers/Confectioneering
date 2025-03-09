package dev.imabad.confectioneering.mixin;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import dev.imabad.confectioneering.MixinHandlers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BeltInventory.class, remap = false)
public class BeltInventoryMixin {

    @Final
    @Shadow
    BeltBlockEntity belt;

    @Inject(method = "getBeltProcessingAtSegment(I)Lcom/simibubi/create/content/kinetics/belt/behaviour/BeltProcessingBehaviour;", at=@At("TAIL"), cancellable = true)
    private void getBeltProcessingAtSegment(int segment, CallbackInfoReturnable<BeltProcessingBehaviour> cir){
        MixinHandlers.overrideBeltProcessingAtSegment(segment, belt, cir);
    }
}
