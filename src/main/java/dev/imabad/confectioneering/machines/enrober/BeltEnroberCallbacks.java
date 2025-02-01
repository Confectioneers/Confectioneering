package dev.imabad.confectioneering.machines.enrober;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.stream.Collectors;

public class BeltEnroberCallbacks {
    public static BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack s, TransportedItemStackHandlerBehaviour i,
                                                                          EnroberBlockEntity blockEntity) {
        if(blockEntity.internalTank.isEmpty()){
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        if(blockEntity.getRecipe(s.stack) == null){
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }

        return BeltProcessingBehaviour.ProcessingResult.HOLD;
    }


    public static BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack s, TransportedItemStackHandlerBehaviour i,
                                                                        EnroberBlockEntity blockEntity) {
        if (blockEntity.internalTank.isEmpty())
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        Recipe<?> recipe = blockEntity.getRecipe(s.stack);
        if (recipe == null)
            return BeltProcessingBehaviour.ProcessingResult.PASS;

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
                            copy.angle = centered ? 180 : Create.RANDOM.nextInt(360);
                            return copy;
                        })
                        .map(t -> {
                            t.locked = false;
                            return t;
                        })
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
