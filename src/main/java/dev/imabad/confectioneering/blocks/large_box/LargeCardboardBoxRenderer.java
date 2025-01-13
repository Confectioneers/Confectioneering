package dev.imabad.confectioneering.blocks.large_box;

import com.jozufozu.flywheel.util.transform.Rotate;
import com.jozufozu.flywheel.util.transform.Translate;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import dev.imabad.confectioneering.machines.dipper.DipperBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class LargeCardboardBoxRenderer extends SafeBlockEntityRenderer<LargeCardboardBoxBlockEntity> {
    static final Vec3 pivot = VecHelper.voxelSpace(8.4, 9.9, 3.6);

    public LargeCardboardBoxRenderer(BlockEntityRendererProvider.Context context) {

    }
    @Override
    protected void renderSafe(LargeCardboardBoxBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {

    }

    static <T extends Translate<T> & Rotate<T>> void applyLidAngle(LargeCardboardBoxBlockEntity be, int index, float angle, float progress, T tr) {
        applyLidAngle(be, pivot, index, angle, progress, tr);
    }

    static <T extends Translate<T> & Rotate<T>> void applyLidAngle(LargeCardboardBoxBlockEntity be, Vec3 rotationOffset, int index, float angle, float progress, T tr) {
        tr
                .centre()
                .rotateY(180 + AngleHelper.horizontalAngle(be.getBlockState()
                        .getValue(LargeCardboardBoxBlock.FACING)))
                .rotateY(index * 90f)
                .unCentre()
                .translateZ(progress * -0.05f)
                .translateY(progress * 0.035f)
                .translate(rotationOffset)
                .rotateX(angle)
                .translateBack(rotationOffset);
    }
}
