package dev.imabad.confectioneering.compat.jei;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Optional;

public class JeiHelpers {

    public static IRecipeSlotRichTooltipCallback fluidTooltip(int mbAmount){
        return (view, tooltipBuilder) -> {
            Optional<FluidStack> displayed = view.getDisplayedIngredient(ForgeTypes.FLUID_STACK);
            if (displayed.isEmpty())
                return;

            FluidStack fluidStack = displayed.get();

            if (fluidStack.getFluid().isSame(AllFluids.POTION.get())) {
                Component name = fluidStack.getDisplayName();
                tooltipBuilder.add(name);

                ArrayList<Component> potionTooltip = new ArrayList<>();
                PotionFluidHandler.addPotionTooltip(fluidStack, potionTooltip, 1);
                tooltipBuilder.addAll(potionTooltip.stream().toList());
            }

            int amount = mbAmount == -1 ? fluidStack.getAmount() : mbAmount;
            Component text = Components.literal(String.valueOf(amount)).append(Lang.translateDirect("generic.unit.millibuckets")).withStyle(ChatFormatting.GOLD);
            tooltipBuilder.add(text);
        };
    }
}
