package dev.imabad.confectioneering.machines.enrober;

import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.stream.Collectors;

public class BeltEnroberCallbacks {
    public static BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack s, TransportedItemStackHandlerBehaviour i,
                                                                          EnroberBlockEntity blockEntity) {
        if(blockEntity.internalTank.isEmpty()){
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        if(blockEntity.getRecipes(s.stack).isEmpty()){
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }

        return BeltProcessingBehaviour.ProcessingResult.HOLD;
    }


    public static BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack s, TransportedItemStackHandlerBehaviour i,
                                                                        EnroberBlockEntity blockEntity) {
        if (blockEntity.internalTank.isEmpty())
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        List<? extends Recipe<?>> recipes = blockEntity.getRecipes(s.stack);
        if (recipes.isEmpty())
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        Recipe<?> recipe = recipes.get(0);

        activate(s, i, blockEntity, recipe);
        return BeltProcessingBehaviour.ProcessingResult.HOLD;
    }

    public static void activate(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler,
                                EnroberBlockEntity blockEntity, Recipe<?> recipe) {

        List<TransportedItemStack> collect =
                RecipeApplier.applyRecipeOn(blockEntity.getLevel(), ItemHandlerHelper.copyStackWithSize(transported.stack, 1), recipe)
                        .stream()
                        .map(stack -> {
                            TransportedItemStack copy = transported.copy();
                            boolean centered = BeltHelper.isItemUpright(stack);
                            copy.stack = stack;
                            copy.locked = true;
                            copy.angle = centered ? 180 : blockEntity.getLevel().getRandom().nextInt(360);
                            return copy;
                        })
                        .peek(t -> t.locked = false)
                        .collect(Collectors.toList());

        TransportedItemStack left = transported.copy();
        left.stack.shrink(1);

        if (collect.isEmpty()) {
            handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(left));
        } else {
            handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(collect, left));
        }
        blockEntity.sendData();
    }
}
