package dev.imabad.confectioneering.mixin;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import dev.imabad.confectioneering.MixinHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeltBlock.class)
public class BeltBlockMixin {

    @Inject(method = "isBlockCoveringBelt(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)Z", at = @At("TAIL"), cancellable = true)
    private static void isBlockCoveringBelt(LevelAccessor world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        MixinHandlers.isBlockCoveringBeltInt(world, pos, cir);
    }
}
