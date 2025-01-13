package dev.imabad.confectioneering.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import dev.imabad.confectioneering.client.ConfectionPartialModels;
import dev.imabad.confectioneering.machines.ConfectionMachines;
import net.minecraft.client.gui.GuiGraphics;

public class AnimatedDipper extends AnimatedKinetics {

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        blockElement(ConfectionMachines.DIPPER_BLOCK.get().defaultBlockState())
                .atLocal(0, 2, 0)
                .scale(20)
                .render(graphics);

        blockElement(ConfectionPartialModels.DIPPER_GRATE)
                .atLocal(0, 2 + -getGrate(), 0)
                .scale(20)
                .render(graphics);

        matrixStack.popPose();
    }

    private float getGrate() {
        float cycle = (AnimationTickHolder.getRenderTime() - offset * 8) % 30;
        if (cycle < 10) {
            float progress = cycle / 18;
            return -(progress * progress * progress);
        }
        return 0;
    }
}
