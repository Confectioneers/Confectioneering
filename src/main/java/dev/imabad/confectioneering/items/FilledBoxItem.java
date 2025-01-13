package dev.imabad.confectioneering.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FilledBoxItem extends Item {

    private List<Component> tooltips;

    public FilledBoxItem(Properties pProperties) {
        super(pProperties);
        tooltips = new ArrayList<>();
    }

    public void addTooltip(Component component) {
        tooltips.add(component);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.addAll(tooltips);
    }
}
