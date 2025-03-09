package dev.imabad.confectioneering.compat.jei.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import dev.imabad.confectioneering.machines.ConfectionMachines;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class AnimatedEnrober extends AnimatedKinetics {

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        blockElement(AllBlocks.BELT.getDefaultState()
                .setValue(BeltBlock.CASING, true)
                .setValue(BeltBlock.PART, BeltPart.MIDDLE)
                .setValue(BeltBlock.HORIZONTAL_FACING, Direction.EAST))
                .atLocal(0, 2, 0)
                .scale(20)
                .render(graphics);

        blockElement(ConfectionMachines.ENROBER_BLOCK.get().defaultBlockState())
                .atLocal(0, 1, 0)
                .scale(20)
                .render(graphics);

        matrixStack.popPose();
    }
}
