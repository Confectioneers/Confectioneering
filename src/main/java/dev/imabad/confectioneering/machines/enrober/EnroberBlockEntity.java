package dev.imabad.confectioneering.machines.enrober;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import dev.imabad.confectioneering.ConfectionRecipeTypes;
import dev.imabad.confectioneering.machines.dipper.DippingRecipe;
import dev.imabad.confectioneering.processing.InlineBeltProcessingBehaviour;
import dev.imabad.confectioneering.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EnroberBlockEntity extends SmartBlockEntity {

    private static final Object enrobingRecipesKey = new Object();
    SmartFluidTankBehaviour internalTank;
    protected BeltProcessingBehaviour beltProcessing;


    public EnroberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        beltProcessing = new InlineBeltProcessingBehaviour(this).whenItemEnters(((stack, inventory) -> BeltEnroberCallbacks.onItemReceived(stack, inventory, this)))
                .whileItemHeld((stack, inventory) -> BeltEnroberCallbacks.whenItemHeld(stack, inventory, this));
        behaviours.add(beltProcessing);
        behaviours.add(this.internalTank = SmartFluidTankBehaviour.single(this, 200).allowInsertion());
    }

    public List<? extends Recipe<?>> getRecipes(ItemStack stack) {
        Predicate<Recipe<?>> types = RecipeConditions.isOfType(ConfectionRecipeTypes.ENROBING.getType());

        List<Recipe<?>> startedSearch = RecipeFinder.get(enrobingRecipesKey, level, types);
        startedSearch = startedSearch.stream()
                .filter(RecipeConditions.firstIngredientMatches(stack))
                .filter(RecipeUtils.fluidMatches(internalTank.getPrimaryHandler().getFluid()))
                .filter(r -> !AllRecipeTypes.shouldIgnoreInAutomation(r))
                .collect(Collectors.toList());
        List<EnrobingRecipe> collect = SequencedAssemblyRecipe.getRecipes(level, stack, ConfectionRecipeTypes.ENROBING.getType(), EnrobingRecipe.class)
                .filter(RecipeUtils.fluidMatches(internalTank.getPrimaryHandler().getFluid())).toList();
        startedSearch.addAll(collect);
        return startedSearch;
    }


    @Override
    public void destroy() {
        super.destroy();
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return (side == null || side == Direction.UP) && this.isFluidHandlerCap(cap) ? this.internalTank.getCapability().cast() : super.getCapability(cap, side);
    }

    public void randomTick(){
        if(!internalTank.isEmpty()){
            internalTank.getPrimaryHandler().drain(10, IFluidHandler.FluidAction.EXECUTE);
            notifyUpdate();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide){
            return;
        }

        if(level.getServer().getTickCount() % 20 == 0){
            randomTick();
        }
    }
}
