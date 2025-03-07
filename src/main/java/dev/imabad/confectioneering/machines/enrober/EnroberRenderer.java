package dev.imabad.confectioneering.machines.enrober;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class EnroberRenderer extends SafeBlockEntityRenderer<EnroberBlockEntity> {
    static final Vec3 pivot = VecHelper.voxelSpace(0, 11.25, 0.75);

    public EnroberRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public boolean shouldRenderOffScreen(EnroberBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    protected void renderSafe(EnroberBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.solid());

//
//        if (!Backend.canUseInstancing(be.getLevel())) {
//            SuperByteBuffer model = CachedBufferer.partial(ConfectionPartialModels.DIPPER_GRATE, be.getBlockState());
//            applyGrateAngle(be, angle, yOffset, model);
//            model.light(light)
//                    .renderInto(ms, vertexBuilder);
//        }
        PoseTransformStack msr = TransformStack.of(ms);
        msr.rotateYCenteredDegrees(AngleHelper.horizontalAngle(be.getBlockState()
                    .getValue(EnroberBlock.HORIZONTAL_FACING)) + 90f);
//        ms.translate(0.5, 0.5, 0.5);
//        ms.mulPose(Axis.YP.rotationDegrees()));
//        ms.translate(-0.5, -0.5, -0.5);
        renderFluid(be, partialTicks, ms, buffer, light);
//        renderItem(be, partialTicks, yOffset, angle, ms, buffer, light, overlay);
    }

    protected void renderFluid(EnroberBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light) {
        SmartFluidTankBehaviour tank = be.internalTank;
        if (tank == null)
            return;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel()
                .getValue(partialTicks);

        if (!fluidStack.isEmpty() && level != 0) {
            float yMin = 3f / 16f;
            float min = 2f / 16f;
            float max = min + (12 / 16f);
            float yOffset = (7 / 16f);
            Fluid fluid = fluidStack.getFluid();
            IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
            int color = clientFluid.getTintColor(fluidStack);
            TextureAtlasSprite fluidTexture = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(clientFluid.getFlowingTexture(fluidStack));
            VertexConsumer fluidBuilder = FluidRenderer.getFluidBuilder(buffer);
            ms.pushPose();
            ms.translate(0, 0, -(7 /16f));
            FluidRenderer.renderFlowingTiledFace(Direction.SOUTH, min, yMin - yOffset, max, yMin, 1, fluidBuilder, ms, light, color, fluidTexture);
            ms.popPose();
            ms.pushPose();
            ms.translate(0, 0, -(9 /16f));
            FluidRenderer.renderFlowingTiledFace(Direction.NORTH, min, yMin - yOffset, max, yMin, 1, fluidBuilder, ms, light, color, fluidTexture);
            ms.popPose();
//            FluidRenderer.renderFluidBox(fluidStack, min, yMin - yOffset, 7 / 16f, max, yMin, 9 / 16f, buffer, ms, light,
//                    false);
        }
    }
}
