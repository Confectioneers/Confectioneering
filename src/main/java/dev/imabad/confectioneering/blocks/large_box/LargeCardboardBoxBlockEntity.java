package dev.imabad.confectioneering.blocks.large_box;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class LargeCardboardBoxBlockEntity extends SmartBlockEntity {

    LerpedFloat lidProgress;
    protected LazyOptional<IItemHandler> itemCapability;

    protected ItemStackHandler inventory;
    State state;
    public enum State {
        CLOSED, OPENING, OPEN, CLOSING
    }
    boolean open;

    public LargeCardboardBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        inventory = new ItemStackHandler(16);

        itemCapability = LazyOptional.empty();

        lidProgress = LerpedFloat.linear().startWithValue(0);
        this.state = State.CLOSED;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public float getLidProgress(float partialTicks){
        return lidProgress.getValue(partialTicks);
    }

    private float getOpenChaseSpeed() {
        return 0.1f;
    }

    public void open(){
        state = State.OPENING;
        notifyUpdate();
    }

    public void close(){
        state = State.CLOSING;
        notifyUpdate();
    }

    public void write(CompoundTag compound, boolean clientPacket) {
        compound.put("Inventory", inventory.serializeNBT());
        NBTHelper.writeEnum(compound, "State", state);
        compound.put("Lid", lidProgress.writeNBT());

        super.write(compound, clientPacket);
    }

    protected void read(CompoundTag compound, boolean clientPacket) {
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        State prevState = this.state;
        this.state = NBTHelper.readEnum(compound, "State", State.class);
        this.lidProgress.readNBT(compound.getCompound("Lid"), false);

        super.read(compound, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();

        boolean doLogic = !level.isClientSide || isVirtual();
        State prevState = state;

        if(state == State.OPENING){
            lidProgress.chase(1, getOpenChaseSpeed(), LerpedFloat.Chaser.LINEAR);
            lidProgress.tickChaser();
            if (lidProgress.getValue() > 1 - (1 / 16f) && doLogic) {
                state = State.OPEN;
                lidProgress.setValue(1);
            }
        }

        if(state == State.CLOSING){
            lidProgress.chase(0, getOpenChaseSpeed(), LerpedFloat.Chaser.LINEAR);
            lidProgress.tickChaser();
            if (lidProgress.getValue() < 1 - (15 / 16f) && doLogic) {
                state = State.CLOSED;
                lidProgress.setValue(0);
            }
        }

        if(state != prevState){
            notifyUpdate();
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (isItemHandlerCap(cap)) {
            initCapability();
            return itemCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    private void initCapability() {
        itemCapability = LazyOptional.of(() -> inventory);
    }

}
