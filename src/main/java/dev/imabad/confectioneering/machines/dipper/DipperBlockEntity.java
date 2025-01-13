package dev.imabad.confectioneering.machines.dipper;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.drain.ItemDrainItemHandler;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.imabad.confectioneering.ConfectionRecipeTypes;
import dev.imabad.confectioneering.Confectioneering;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DipperBlockEntity extends KineticBlockEntity {

    private static final Object dippingRecipesKey = new Object();
    SmartFluidTankBehaviour internalTank;

    protected int processingTicks;
    public ProcessingInventory inventory;
    private int recipeIndex;
    private final LazyOptional<IItemHandler> invProvider;
    LerpedFloat grateProgress;
    State state;
    float fluidLevel = 1 - (8f / 16);

    public enum State {
        NORMAL, DIPPING, RESOLVING_DIPPING, FLIPPING, RESOLVING_FLIPPING;
    }

    public DipperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        inventory = new ProcessingInventory(this::start).withSlotLimit(true);
        inventory.remainingTime = -1;
        recipeIndex = 0;
        invProvider = LazyOptional.of(() -> inventory);
        grateProgress = LerpedFloat.linear().startWithValue(1);
        this.state = State.NORMAL;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add((new DirectBeltInputBehaviour(this)).allowingBeltFunnels());
        behaviours.add(this.internalTank = SmartFluidTankBehaviour.single(this, 1500).allowExtraction().allowInsertion());
        this.internalTank.whenFluidUpdates(() -> {
            if(!inventory.appliedRecipe && inventory.recipeDuration == 10){
                inventory.remainingTime = -1;
            }
        });
    }

    // This is a vague implementation of recipe handling, should put some more effort in and make it support simulation!
    private void applyRecipe() {
        List<? extends Recipe<?>> recipes = getRecipes();
        if (recipes.isEmpty())
            return;
        if (recipeIndex >= recipes.size())
            recipeIndex = 0;

        Recipe<?> recipe = recipes.get(recipeIndex);

        if(!(recipe instanceof DippingRecipe)) {
            return;
        }
        DippingRecipe dippingRecipe = (DippingRecipe) recipe;

        List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
        List<FluidIngredient> fluidIngredients = dippingRecipe.getFluidIngredients();

        Ingredients: for(int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            for(int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack extracted = inventory.getStackInSlot(slot);
                if(!ingredient.test(extracted)) {
                    continue;
                }
                continue Ingredients;
            }

            // Not all ingredients found!
            return;
        }

        boolean fluidsAffected = false;
        FluidIngredients: for(int i = 0; i < fluidIngredients.size(); i++) {
            FluidIngredient ingredient = fluidIngredients.get(i);
            int amountRequired = ingredient.getRequiredAmount();

            for (int tank = 0; tank < internalTank.getTanks().length; tank++) {
                FluidStack fluidStack = internalTank.getPrimaryHandler().getFluidInTank(tank);
                if(!ingredient.test(fluidStack)) {
                    continue;
                }
                int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
                fluidStack.shrink(drainedAmount);
                fluidsAffected = true;
                amountRequired -= drainedAmount;
                if(amountRequired != 0)
                    continue;
                continue FluidIngredients;
            }

            // Not all ingredients found!
            return;
        }

        if(fluidsAffected){
            internalTank.forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
        }

        inventory.setStackInSlot(0, dippingRecipe.rollResults().get(0).copy());
        notifyUpdate();
    }

    private List<? extends Recipe<?>> getRecipes() {
        Predicate<Recipe<?>> types = RecipeConditions.isOfType(ConfectionRecipeTypes.DIPPING.getType());

        List<Recipe<?>> startedSearch = RecipeFinder.get(dippingRecipesKey, level, types);
        startedSearch = startedSearch.stream()
                .filter(RecipeConditions.firstIngredientMatches(inventory.getStackInSlot(0)))
                .filter(fluidMatches(internalTank.getPrimaryHandler().getFluid()))
                .filter(r -> !AllRecipeTypes.shouldIgnoreInAutomation(r))
                .collect(Collectors.toList());
        Optional<DippingRecipe> assemblyRecipe =
                SequencedAssemblyRecipe.getRecipe(level, inventory.getStackInSlot(0), ConfectionRecipeTypes.DIPPING.getType(), DippingRecipe.class);
        if(assemblyRecipe.isPresent()){
            startedSearch.add(assemblyRecipe.get());
        }
        return startedSearch;
    }

    public static Predicate<Recipe<?>> fluidMatches(FluidStack fluidStack) {
        return r -> r instanceof ProcessingRecipe<?> processingRecipe && !processingRecipe.getFluidIngredients().isEmpty()
                && processingRecipe.getFluidIngredients().get(0).test(fluidStack);
    }

    public void start(ItemStack inserted) {
        if (inventory.isEmpty())
            return;
        if (level.isClientSide && !isVirtual())
            return;

        List<? extends Recipe<?>> recipes = getRecipes();
        boolean valid = !recipes.isEmpty();
        int time = 50;
        if(recipes.isEmpty()){
            inventory.remainingTime = inventory.recipeDuration = 10;
            inventory.appliedRecipe = false;
            sendData();
            return;
        }

        if(valid){
            recipeIndex++;
            if(recipeIndex >= recipes.size())
                recipeIndex = 0;
        }

        Recipe<?> recipe = recipes.get(recipeIndex);
//        if(recipe instanceof DippingRecipe dippingRecipe){
//            time = dippingRecipe.getProcessingDuration();
//        }

        inventory.remainingTime = time * Math.max(1, (inserted.getCount() / 5));
        inventory.recipeDuration = inventory.remainingTime;
        inventory.appliedRecipe = false;
        state = State.DIPPING;
        grateProgress.setValue(0);
        grateProgress.updateChaseSpeed(0);
        notifyUpdate();
    }

    public void invalidate() {
        super.invalidate();
        invProvider.invalidate();
    }

    public float getFluidLevel(){
        return this.fluidLevel;
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inventory);
    }

    public void write(CompoundTag compound, boolean clientPacket) {
        compound.put("Inventory", inventory.serializeNBT());
        compound.putInt("RecipeIndex", recipeIndex);
        compound.putInt("ProcessingTicks", this.processingTicks);
        NBTHelper.writeEnum(compound, "State", state);
        compound.put("Grate", grateProgress.writeNBT());

        super.write(compound, clientPacket);
    }

    protected void read(CompoundTag compound, boolean clientPacket) {
        this.processingTicks = compound.getInt("ProcessingTicks");
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        recipeIndex = compound.getInt("RecipeIndex");
        State prevState = this.state;
        this.state = NBTHelper.readEnum(compound, "State", State.class);
        if(prevState != state && state == State.DIPPING){
            fluidLevel = Mth.map((float) this.internalTank.getPrimaryHandler().getFluidAmount(),
                    0, this.internalTank.getPrimaryHandler().getCapacity(), 1 - (7f / 16), 0.08f);
            grateProgress.setValue(0);
            grateProgress.updateChaseSpeed(0);
        }
        this.grateProgress.readNBT(compound.getCompound("Grate"), false);

        super.read(compound, clientPacket);
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (side != null && side.getAxis().isHorizontal() && this.isItemHandlerCap(cap)) {
            return invProvider.cast();
        } else {
            return side != Direction.UP && this.isFluidHandlerCap(cap) ? this.internalTank.getCapability().cast() : super.getCapability(cap, side);
        }
    }

    public float getGrateProgress(float pt) {
        return grateProgress.getValue(pt);
    }

    public State getState() {
        return state;
    }

    private float getChaseSpeed() {
        return Mth.clamp(Math.abs(getSpeed()) / 24 / 14, 0, 0.8f);
    }

    private float getFlipChaseSpeed() {
        return Mth.clamp(Math.abs(getSpeed()) / 24 / 2, 0, 0.8f);
    }

    @Override
    public void tick() {
        super.tick();

        if(getSpeed() == 0)
            return;

        boolean doLogic = !level.isClientSide || isVirtual();
        State prevState = state;

        if(inventory.remainingTime == -1 && prevState == State.NORMAL){
            if(!inventory.isEmpty() && !inventory.appliedRecipe) {
                start(inventory.getStackInSlot(0));
            }
        } else {
            float processingSpeed = Mth.clamp(Math.abs(getSpeed()) / 24, 0, 128);
            inventory.remainingTime -= processingSpeed;
        }

        if(state == State.FLIPPING){
            grateProgress.chase(1, getFlipChaseSpeed(), LerpedFloat.Chaser.LINEAR);
            grateProgress.tickChaser();
            if (grateProgress.getValue() > 1 - (1 / 16f) && doLogic) {
                getRidOfItems(false);
                state = State.RESOLVING_FLIPPING;
                grateProgress.setValue(1);
            }
        }

        if(state == State.RESOLVING_FLIPPING){
            grateProgress.chase(0, getFlipChaseSpeed(), LerpedFloat.Chaser.EXP);
            grateProgress.tickChaser();
            if (grateProgress.getValue() < 1 - (15 / 16f) && doLogic) {
                state = State.NORMAL;
                grateProgress.setValue(0);
                grateProgress.updateChaseSpeed(0);
            }
        }

        if(state == State.DIPPING){
            grateProgress.chase(1, getChaseSpeed(), LerpedFloat.Chaser.LINEAR);
            grateProgress.tickChaser();
            if (grateProgress.getValue() >= 1  && doLogic) {
                state = State.RESOLVING_DIPPING;
                applyRecipe();
                inventory.appliedRecipe = true;
                inventory.recipeDuration = 20;
                inventory.remainingTime = 20;
                grateProgress.setValue(1);
            }
        }

        if(state == State.RESOLVING_DIPPING){
            grateProgress.chase(0, getChaseSpeed(), LerpedFloat.Chaser.EXP);
            grateProgress.tickChaser();
            if (grateProgress.getValue() < 1 - (15 / 16f) && doLogic) {
                state = State.NORMAL;
                grateProgress.setValue(0);
            }
        }
        if(state == State.NORMAL) {
            if(!inventory.isEmpty()){
                if(getRidOfItems(true)){
                    state = State.FLIPPING;
                    grateProgress.setValue(0);
                }
            }
        }

        if(prevState != state)
            notifyUpdate();

    }

    public boolean getRidOfItems(boolean simulate){
        Vec3 itemMovement = getItemMovementVec();
        Direction itemMovementFacing = Direction.getNearest(itemMovement.x, itemMovement.y, itemMovement.z);
        if (inventory.remainingTime > 0)
            return false;
        inventory.remainingTime = 0;

        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty())
                continue;
            ItemStack tryExportingToBeltFunnel = getBehaviour(DirectBeltInputBehaviour.TYPE)
                    .tryExportingToBeltFunnel(stack, itemMovementFacing.getOpposite(), simulate);
            if (tryExportingToBeltFunnel != null) {
                if (tryExportingToBeltFunnel.getCount() != stack.getCount()) {
                    if(!simulate) {
                        inventory.setStackInSlot(slot, tryExportingToBeltFunnel);
                        notifyUpdate();
                    }
                    return true;
                }
                if (!tryExportingToBeltFunnel.isEmpty())
                    return false;
            }
        }

        BlockPos nextPos = worldPosition.relative(itemMovementFacing);
        DirectBeltInputBehaviour behaviour = BlockEntityBehaviour.get(level, nextPos, DirectBeltInputBehaviour.TYPE);
        if (behaviour != null) {
            boolean changed = false;
            if (!behaviour.canInsertFromSide(itemMovementFacing))
                return false;
            if (level.isClientSide && !isVirtual())
                return false;
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack.isEmpty())
                    continue;
                ItemStack remainder = behaviour.handleInsertion(stack, itemMovementFacing, simulate);
                if (remainder.equals(stack, false))
                    continue;
                if(!simulate){
                    inventory.setStackInSlot(slot, remainder);
                }
                changed = true;
            }
            if (changed && !simulate) {
                setChanged();
                sendData();
            }
            return changed;
        }

        // Eject Items

        Vec3 outPos = VecHelper.getCenterOf(worldPosition)
                .add(itemMovement.scale(.5f)
                        .add(0, .5, 0));
        Vec3 outMotion = itemMovement.scale(.0625)
                .add(0, .125, 0);
        boolean anyGone = false;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty())
                continue;
            anyGone = true;
            if(!simulate) {
                ItemEntity entityIn = new ItemEntity(level, outPos.x, outPos.y, outPos.z, stack);
                entityIn.setDeltaMovement(outMotion);
                level.addFreshEntity(entityIn);
            }
        }
        if(!simulate) {
            inventory.clear();
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            inventory.remainingTime = -1;
            sendData();
        }
        return anyGone;
    }

    public Vec3 getItemMovementVec() {
        return Vec3.atLowerCornerOf(getBlockState().getValue(DipperBlock.HORIZONTAL_FACING).getNormal());
    }
}
