package dev.imabad.confectioneering.machines.dipper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.Rotate;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.engine_room.flywheel.lib.transform.Translate;
import dev.imabad.confectioneering.client.ConfectionPartialModels;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

public class DipperRenderer extends ShaftRenderer<DipperBlockEntity> {
    static final Vec3 pivot = VecHelper.voxelSpace(0, 11.25, 0.75);

    public DipperRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(DipperBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    protected void renderSafe(DipperBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.cutout());
        float grateProgress = be.getGrateProgress(partialTicks);
        DipperBlockEntity.State grateState = be.getState();
        float angle = 0;
        float yOffset = 0;
        if(grateState == DipperBlockEntity.State.FLIPPING || grateState == DipperBlockEntity.State.RESOLVING_FLIPPING) {
            angle = grateProgress * 70;
        } else if (grateState == DipperBlockEntity.State.DIPPING || grateState == DipperBlockEntity.State.RESOLVING_DIPPING) {
            yOffset = grateProgress * be.getFluidLevel();
        }

        if (!VisualizationManager.supportsVisualization(be.getLevel())) {
            super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
            SuperByteBuffer model = CachedBuffers.partial(ConfectionPartialModels.DIPPER_GRATE, be.getBlockState());
            applyGrateAngle(be, angle, yOffset, model);
            model.light(light)
                    .renderInto(ms, vertexBuilder);
        }
        PoseTransformStack msr = TransformStack.of(ms);

        renderFluid(be, partialTicks, ms, buffer, light);
        msr.rotateYCenteredDegrees(-180 + AngleHelper.horizontalAngle(be.getBlockState()
                    .getValue(DipperBlock.HORIZONTAL_FACING)));
        renderItem(be, partialTicks, yOffset, angle, ms, buffer, light, overlay);
    }

    static <T extends Translate<T> & Rotate<T>> void applyGrateAngle(KineticBlockEntity be, float angle, float yOffset, T tr) {
        applyGrateAngle(be, pivot, angle, yOffset,  tr);
    }

    static <T extends Translate<T> & Rotate<T>> void applyGrateAngle(KineticBlockEntity be, Vec3 rotationOffset, float angle, float yOffset, T tr) {
        tr.center()
                .rotateYDegrees(180 + AngleHelper.horizontalAngle(be.getBlockState()
                        .getValue(DipperBlock.HORIZONTAL_FACING)))
                .uncenter()
                .translate(rotationOffset)
                .rotateXDegrees(-angle)
                .translateBack(rotationOffset)
                .translateY(-yOffset);
    }

    protected void renderItem(DipperBlockEntity be, float partialTicks, float yOffset, float angle, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if(!be.inventory.isEmpty()){
            ms.pushPose();
            ms.translate(.5f, 0.5f, .5f);
            ms.mulPose(Axis.YP.rotationDegrees(180));
            ms.translate(-.5f, -0.5f, -.5f);
            ms.translate(.5f, (13.5 / 16f) - yOffset, .5f);
            for (int i = 0; i < be.inventory.getSlots(); i++) {
                ItemStack stack = be.inventory.getStackInSlot(i);
                if (stack.isEmpty())
                    continue;

                ItemRenderer itemRenderer = Minecraft.getInstance()
                        .getItemRenderer();
                BakedModel modelWithOverrides = itemRenderer.getModel(stack, be.getLevel(), null, 0);
                boolean blockItem = modelWithOverrides.isGui3d();

//                ms.translate(alongZ ? offset : .5, blockItem ? .925f : 13f / 16f, alongZ ? .5 : offset);

                ms.scale(.5f, .5f, .5f);
                ms.mulPose(Axis.XP.rotationDegrees(90));
                ms.translate(pivot.x, pivot.y, pivot.z);
                ms.mulPose(Axis.XP.rotationDegrees(angle));
                ms.translate(-pivot.x, -pivot.y, -pivot.z);
                itemRenderer.render(stack, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, modelWithOverrides);
                break;
            }
            ms.popPose();
        }
    }

    protected void renderFluid(DipperBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light) {
        SmartFluidTankBehaviour tank = be.internalTank;
        if (tank == null)
            return;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel()
                .getValue(partialTicks);

        if (!fluidStack.isEmpty() && level != 0) {
            float yMin = 5f / 16f;
            float min = 2f / 16f;
            float max = min + (12 / 16f);
            float yOffset = (7 / 16f) * level;
            ms.pushPose();
            ms.translate(0, yOffset, 0);
            FluidRenderer.renderFluidBox(fluidStack.getFluid(), fluidStack.getAmount(), min, yMin - yOffset, min, max, yMin, max, buffer, ms, light,
                    false, false);
            ms.popPose();
        }
    }
}
