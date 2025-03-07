package dev.imabad.confectioneering.machines.enrober;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.item.SmartInventory;
import dev.imabad.confectioneering.ConfectionRecipeTypes;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class EnrobingRecipe extends ProcessingRecipe<SmartInventory> {

    public EnrobingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(ConfectionRecipeTypes.ENROBING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 0;
    }

    @Override
    public boolean matches(SmartInventory smartInventory, Level level) {
        return false;
    }

}
