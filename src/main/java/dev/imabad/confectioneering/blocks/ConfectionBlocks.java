package dev.imabad.confectioneering.blocks;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.imabad.confectioneering.Confectioneering;
import dev.imabad.confectioneering.blocks.large_box.LargeCardboardBoxBlock;
import dev.imabad.confectioneering.blocks.large_box.LargeCardboardBoxBlockEntity;
import dev.imabad.confectioneering.blocks.large_box.LargeCardboardBoxInstance;
import dev.imabad.confectioneering.blocks.large_box.LargeCardboardBoxRenderer;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ConfectionBlocks {

    public static final CreateRegistrate REGISTRATE = Confectioneering.registrate();


    public static final RegistryEntry<LargeCardboardBoxBlock> LARGE_CARDBOARD_BOX = REGISTRATE.block("large_cardboard_box", LargeCardboardBoxBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.COLOR_GRAY))
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p), 180))
            .item()
            .transform(customItemModel())
            .lang("Large Cardboard Box")
            .register();

    public static final BlockEntityEntry<LargeCardboardBoxBlockEntity> LARGE_CARDBOARD_BOX_BLOCK_ENTITY = REGISTRATE.blockEntity("large_cardboard_box", LargeCardboardBoxBlockEntity::new)
            .instance(() -> LargeCardboardBoxInstance::new)
            .validBlock(LARGE_CARDBOARD_BOX)
            .renderer(() -> LargeCardboardBoxRenderer::new)
            .register();

    public static void init(){}

}
