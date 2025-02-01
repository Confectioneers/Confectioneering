package dev.imabad.confectioneering.machines.enrober;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.Components;
import dev.imabad.confectioneering.ConfectionRecipeTypes;
import dev.imabad.confectioneering.Confectioneering;
import dev.imabad.confectioneering.compat.jei.AssemblyDipping;
import dev.imabad.confectioneering.machines.ConfectionMachines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class EnrobingRecipe extends ProcessingRecipe<SmartInventory> implements IAssemblyRecipe {

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

    @Override
    public Component getDescriptionForAssembly() {
        List<FluidStack> matchingFluidStacks = fluidIngredients.get(0)
                .getMatchingFluidStacks();
        if (matchingFluidStacks.isEmpty())
            return Components.literal("Invalid");
        return Components.translatable(Confectioneering.MOD_ID + ".recipe.assembly.enrobing_enrobe_fluid",
                matchingFluidStacks.get(0).getDisplayName().getString());
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(ConfectionMachines.ENROBER_BLOCK.get());
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {
    }

    @Override
    public void addAssemblyFluidIngredients(List<FluidIngredient> list) {
        list.add(fluidIngredients.get(0));
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> AssemblyDipping::new;
    }
}
