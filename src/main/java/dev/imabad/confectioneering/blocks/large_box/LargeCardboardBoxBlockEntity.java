package dev.imabad.confectioneering.blocks.large_box;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LargeCardboardBoxBlockEntity extends SmartBlockEntity implements MenuProvider {

    LerpedFloat lidProgress;
    protected LazyOptional<IItemHandler> itemCapability;

    protected ItemStackHandler inventory;
    State state;

    @Override
    public Component getDisplayName() {
        return Component.literal("Large Cardboard Box");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return LargeCardboardBoxContainer.create(pContainerId, pPlayerInventory, this);
    }

    public enum State {
        CLOSED, OPENING, OPEN, CLOSING
    }
    int openCount;

    public LargeCardboardBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        inventory = new ItemStackHandler(12);

        itemCapability = LazyOptional.empty();

        lidProgress = LerpedFloat.linear().startWithValue(1);
        openCount = 0;
        this.state = State.OPEN;
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

    public void open(Player player){
        if(player.isSpectator())
            return;
        if (openCount < 0)
            openCount = 0;
        openCount++;
        sendData();
    }

    public void close(Player player){
        if(player.isSpectator())
            return;
        openCount--;
        sendData();
    }

    public void write(CompoundTag compound, boolean clientPacket) {
        compound.put("Inventory", inventory.serializeNBT());
        NBTHelper.writeEnum(compound, "State", state);
        compound.put("Lid", lidProgress.writeNBT());

        if (clientPacket)
            compound.putInt("OpenCount", openCount);
        super.write(compound, clientPacket);
    }

    protected void read(CompoundTag compound, boolean clientPacket) {
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        super.read(compound, clientPacket);
        State prevState = this.state;
        this.state = NBTHelper.readEnum(compound, "State", State.class);
        this.lidProgress.readNBT(compound.getCompound("Lid"), false);
        if (clientPacket)
            openCount = compound.getInt("OpenCount");
    }

    void updateOpenCount() {
        if (level.isClientSide)
            return;
        if (openCount == 0)
            return;

        int prevOpenCount = openCount;
        openCount = 0;

        for (Player playerentity : level.getEntitiesOfClass(Player.class, new AABB(worldPosition).inflate(8)))
            if (playerentity.containerMenu instanceof LargeCardboardBoxContainer
                    && ((LargeCardboardBoxContainer) playerentity.containerMenu).contentHolder == this)
                openCount++;

        if (prevOpenCount != openCount)
            sendData();
    }

    @Override
    public void tick() {
        super.tick();

        boolean doLogic = !level.isClientSide || isVirtual();
        State prevState = state;
        updateOpenCount();

        if(openCount > 0 && state == State.CLOSED){
            state = State.OPENING;
            sendData();
        } else if(openCount <= 0 && state == State.OPEN){
            state = State.CLOSING;
            openCount = 0;
            sendData();
        }

        if(state == State.OPENING){
            lidProgress.chase(0, getOpenChaseSpeed(), LerpedFloat.Chaser.LINEAR);
            lidProgress.tickChaser();
            if (lidProgress.getValue() < 1 - (15 / 16f) && doLogic) {
                state = State.OPEN;
                lidProgress.setValue(0);
            }
        }

        if(state == State.CLOSING){
            lidProgress.chase(1, getOpenChaseSpeed(), LerpedFloat.Chaser.LINEAR);
            lidProgress.tickChaser();
            if (lidProgress.getValue() > 1 - (1 / 16f) && doLogic) {
                state = State.CLOSED;
                lidProgress.setValue(1);
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
