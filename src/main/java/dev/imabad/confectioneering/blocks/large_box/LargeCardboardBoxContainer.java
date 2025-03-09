package dev.imabad.confectioneering.blocks.large_box;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.imabad.confectioneering.blocks.ConfectionBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class LargeCardboardBoxContainer extends MenuBase<LargeCardboardBoxBlockEntity> {

    public LargeCardboardBoxContainer(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public LargeCardboardBoxContainer(MenuType<?> pMenuType, int pContainerId, Inventory inv, LargeCardboardBoxBlockEntity be) {
        super(pMenuType, pContainerId, inv, be);
        be.open(player);
    }

    public static LargeCardboardBoxContainer create(int id, Inventory inv, LargeCardboardBoxBlockEntity be) {
        return new LargeCardboardBoxContainer(ConfectionBlocks.LARGE_CARDBOARD_BOX_CONTAINER.get(), id, inv, be);
    }

    @Override
    protected LargeCardboardBoxBlockEntity createOnClient(FriendlyByteBuf extraData) {
        BlockPos readBlockPos = extraData.readBlockPos();
        CompoundTag readNbt = extraData.readNbt();

        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = world.getBlockEntity(readBlockPos);
        if (blockEntity instanceof LargeCardboardBoxBlockEntity largeCardboardBoxBlockEntity) {
            largeCardboardBoxBlockEntity.readClient(readNbt);
            return largeCardboardBoxBlockEntity;
        }

        return null;
    }

    @Override
    protected void initAndReadInventory(LargeCardboardBoxBlockEntity contentHolder) {

    }

    @Override
    protected void addSlots() {
        for(int k = 0; k < 2; ++k) {
            for(int l = 0; l < 6; ++l) {
                addSlot(new SlotItemHandler(contentHolder.inventory, l + k * 6, 8 + l * 18, 18 + k * 18));
            }
        }

        for(int i1 = 0; i1 < 3; ++i1) {
            for(int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 84 + i1 * 18));
            }
        }

        for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 142));
        }

    }

    @Override
    protected void saveData(LargeCardboardBoxBlockEntity contentHolder) {

    }


    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot clickedSlot = getSlot(pIndex);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();
        int size = contentHolder.inventory.getSlots();
        boolean success = false;
        if (pIndex < size) {
            success = !moveItemStackTo(stack, size, slots.size(), false);
//            contentHolder.inventory.onContentsChanged(pIndex);
        } else
            success = !moveItemStackTo(stack, 0, size, false);

        return success ? ItemStack.EMPTY : stack;
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        if (!playerIn.level().isClientSide)
            contentHolder.close(playerIn);
    }
}
