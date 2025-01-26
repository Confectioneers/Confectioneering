package dev.imabad.confectioneering.blocks;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.imabad.confectioneering.Confectioneering;
import dev.imabad.confectioneering.blocks.large_box.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ConfectionBlocks {

    public static final CreateRegistrate REGISTRATE = Confectioneering.registrate();


//    public static final RegistryEntry<LargeCardboardBoxBlock> LARGE_CARDBOARD_BOX = REGISTRATE.block("large_cardboard_box", LargeCardboardBoxBlock::new)
//            .initialProperties(SharedProperties::stone)
//            .properties(p -> p.noOcclusion().mapColor(MapColor.COLOR_GRAY))
//            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p), 180))
//            .item()
//            .transform(customItemModel())
//            .lang("Large Cardboard Box")
//            .register();

    public static final BlockEntityEntry<LargeCardboardBoxBlockEntity> LARGE_CARDBOARD_BOX_BLOCK_ENTITY = REGISTRATE.blockEntity("large_cardboard_box", LargeCardboardBoxBlockEntity::new)
            .instance(() -> LargeCardboardBoxInstance::new)
            .renderer(() -> LargeCardboardBoxRenderer::new)
            .register();

    public static final MenuEntry<LargeCardboardBoxContainer> LARGE_CARDBOARD_BOX_CONTAINER = registerMenu(
            "large_cardboard_box",
            LargeCardboardBoxContainer::new,
            () -> LargeCardboardBoxScreen::new
    );

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> registerMenu(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return REGISTRATE
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void init(){}

}
