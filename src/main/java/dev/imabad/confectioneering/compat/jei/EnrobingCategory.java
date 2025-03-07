package dev.imabad.confectioneering.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.imabad.confectioneering.compat.jei.animations.AnimatedEnrober;
import dev.imabad.confectioneering.machines.enrober.EnrobingRecipe;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EnrobingCategory extends CreateRecipeCategory<EnrobingRecipe> {

    AnimatedEnrober enrober;

    public EnrobingCategory(Info<EnrobingRecipe> info) {
        super(info);
        enrober = new AnimatedEnrober();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnrobingRecipe recipe, IFocusGroup iFocusGroup) {
        FluidIngredient fluidIngredient = recipe.getFluidIngredients()
                .get(0);

        builder
                .addSlot(RecipeIngredientRole.INPUT, 85, 25)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(ForgeTypes.FLUID_STACK, CreateRecipeCategory.withImprovedVisibility(fluidIngredient.getMatchingFluidStacks()))
                .addRichTooltipCallback((JeiHelpers.fluidTooltip(fluidIngredient.getRequiredAmount())));
        builder
                .addSlot(RecipeIngredientRole.INPUT, 50, 75)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().get(0));

        for (ProcessingOutput result : recipe.getRollableResults()) {
            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, 120, 75)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                    .addItemStack(result.getStack());
        }
    }

    @Override
    public void draw(EnrobingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(-10, 50, 0);
        ms.scale(1f, 1f,1f);
        enrober.draw(graphics, getWidth() / 2, 0);
        ms.popPose();
    }
}
