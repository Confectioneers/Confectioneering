package dev.imabad.confectioneering.machines;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.imabad.confectioneering.Confectioneering;
import dev.imabad.confectioneering.config.ConfectionStress;
import dev.imabad.confectioneering.machines.dipper.DipperBlock;
import dev.imabad.confectioneering.machines.dipper.DipperBlockEntity;
import dev.imabad.confectioneering.machines.dipper.DipperVisual;
import dev.imabad.confectioneering.machines.dipper.DipperRenderer;
import dev.imabad.confectioneering.machines.enrober.*;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class ConfectionMachines {
    public static final CreateRegistrate REGISTRATE = Confectioneering.registrate();


    public static final RegistryEntry<DipperBlock> DIPPER_BLOCK = REGISTRATE.block("dipper", DipperBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.COLOR_GRAY))
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p), 180))
            .item()
            .transform(customItemModel())
            .transform(ConfectionStress.setImpact(2.0))
            .transform(pickaxeOnly())
            .lang("Mechanical Dipper")
            .recipe((blockDipperBlockDataGenContext, registrateRecipeProvider) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blockDipperBlockDataGenContext.get())
                        .pattern(" B ")
                        .pattern("C#C")
                        .pattern("III")
                        .define('B', Items.IRON_BARS)
                        .define('C', AllBlocks.COGWHEEL)
                        .define('#', AllBlocks.BASIN)
                        .define('I', AllItems.IRON_SHEET)
                        .unlockedBy("has_flour", RegistrateRecipeProvider.has(AllItems.IRON_SHEET))
                        .save(registrateRecipeProvider);
            })
            .register();

    public static final BlockEntityEntry<DipperBlockEntity> DIPPER_BLOCK_ENTITY = REGISTRATE.blockEntity("dipper", DipperBlockEntity::new)
            .visual(() -> DipperVisual::new)
            .validBlock(DIPPER_BLOCK)
            .renderer(() -> DipperRenderer::new)
            .register();

    public static final BlockEntry<EnroberBlock> ENROBER_BLOCK = REGISTRATE.block("enrober", EnroberBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.COLOR_GRAY))
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p), 180))
            .item(EnroberItem::new)
            .transform(customItemModel())
            .transform(ConfectionStress.setImpact(2.0))
            .lang("Enrobing Machine")
            .recipe((blockDipperBlockDataGenContext, registrateRecipeProvider) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blockDipperBlockDataGenContext.get())
                        .pattern(" B ")
                        .pattern("C#C")
                        .pattern("III")
                        .define('B', Items.IRON_BARS)
                        .define('C', AllBlocks.COGWHEEL)
                        .define('#', AllBlocks.BASIN)
                        .define('I', AllItems.IRON_SHEET)
                        .unlockedBy("has_flour", RegistrateRecipeProvider.has(AllItems.IRON_SHEET))
                        .save(registrateRecipeProvider);
            })
            .register();

    public static final BlockEntityEntry<EnroberBlockEntity> ENROBER_BLOCK_ENTITY = REGISTRATE.blockEntity("enrober", EnroberBlockEntity::new)
            .validBlock(ENROBER_BLOCK)
            .renderer(() -> EnroberRenderer::new)
            .register();


    public static void init(){}
}
