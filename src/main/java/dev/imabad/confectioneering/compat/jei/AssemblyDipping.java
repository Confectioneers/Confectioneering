package dev.imabad.confectioneering.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;

public class AssemblyDipping extends SequencedAssemblySubCategory {

    AnimatedDipper dipper;

    public AssemblyDipping() {
        super(25);
        dipper = new AnimatedDipper();
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        dipper.offset = index;
        ms.pushPose();
        ms.translate(-7, 50, 0);
        ms.scale(.75f, .75f, .75f);
        dipper.draw(graphics, getWidth() / 2, 0);
        ms.popPose();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
        FluidIngredient fluidIngredient = recipe.getRecipe()
                .getFluidIngredients()
                .get(0);

        builder
                .addSlot(RecipeIngredientRole.INPUT, x + 4, 15)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(ForgeTypes.FLUID_STACK, CreateRecipeCategory.withImprovedVisibility(fluidIngredient.getMatchingFluidStacks()))
                .addRichTooltipCallback((JeiHelpers.fluidTooltip(fluidIngredient.getRequiredAmount())));
    }

}
