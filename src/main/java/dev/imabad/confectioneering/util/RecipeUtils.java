package dev.imabad.confectioneering.util;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Predicate;

public class RecipeUtils {
    public static Predicate<Recipe<?>> fluidMatches(FluidStack fluidStack) {
        return r -> r instanceof ProcessingRecipe<?> processingRecipe && !processingRecipe.getFluidIngredients().isEmpty()
                && processingRecipe.getFluidIngredients().get(0).test(fluidStack);
    }
}
