package dev.imabad.confectioneering.machines.enrober;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.util.transform.Rotate;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.jozufozu.flywheel.util.transform.Translate;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import dev.imabad.confectioneering.client.ConfectionPartialModels;
import dev.imabad.confectioneering.machines.dipper.DipperBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

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
        TransformStack msr = TransformStack.cast(ms);

        renderFluid(be, partialTicks, ms, buffer, light);
//        msr.centre()
//                .rotateY(-180 + AngleHelper.horizontalAngle(be.getBlockState()
//                        .getValue(DipperBlock.HORIZONTAL_FACING)))
//                .unCentre();
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
