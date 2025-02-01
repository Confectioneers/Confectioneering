package dev.imabad.confectioneering.fluids;

import com.simibubi.create.AllFluids;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import dev.imabad.confectioneering.Confectioneering;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ConfectionFluids {

    public static final CreateRegistrate REGISTRATE = Confectioneering.registrate();
    private static final ResourceLocation FLAVOURED_GLOOPY_STILL_TEXTURE = new ResourceLocation(Confectioneering.MOD_ID, "fluid/flavoured_gloopy_icing_still");
    private static final ResourceLocation FLAVOURED_GLOOPY_FLOW_TEXTURE = new ResourceLocation(Confectioneering.MOD_ID, "fluid/flavoured_gloopy_icing_flow");

    private static FluidBuilder<ForgeFlowingFluid.Flowing, CreateRegistrate> basicFluid(String name) {
        return REGISTRATE.standardFluid(name, SolidRenderedFluidType.create());
    }
    private static FluidBuilder<ForgeFlowingFluid.Flowing, CreateRegistrate> tintedBasicFluid(DyeColor color, String name) {
        return REGISTRATE.fluid(name, FLAVOURED_GLOOPY_STILL_TEXTURE, FLAVOURED_GLOOPY_FLOW_TEXTURE,
                DyedFluidAttributes.create(color));
    }

    public static final FluidEntry<ForgeFlowingFluid.Flowing> GLOOPY_ICING = basicFluid("gloopy_icing")
            .properties(b -> b.viscosity(1500)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .lang("Gloopy Icing")
            .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> FLAVOURED_GLOOPY_ICING = basicFluid("flavoured_gloopy_icing")
            .properties(b -> b.viscosity(1500)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .lang("Flavoured Gloopy Icing")
            .register();

    public static final Map<DyeColor, FluidEntry<ForgeFlowingFluid.Flowing>> COLOURED_FLAVOURED_ICING_FLUIDS = new HashMap<>();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> WHITE_CHOCOLATE = basicFluid("white_chocolate")
            .properties(b -> b.viscosity(1500)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .lang("White Chocolate")
            .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> DARK_CHOCOLATE = basicFluid("dark_chocolate")
            .properties(b -> b.viscosity(1500)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .lang("Dark Chocolate")
            .register();

    private static class DyedFluidAttributes extends AllFluids.TintedFluidType {

        private final DyeColor dyeColor;

        public static FluidBuilder.FluidTypeFactory create(DyeColor color) {
            return (p, s, f) -> new DyedFluidAttributes(p, s, f, color);
        }

        public DyedFluidAttributes(Properties properties,
                                   ResourceLocation stillTexture,
                                   ResourceLocation flowingTexture,
                                   DyeColor dyeColor) {
            super(properties, stillTexture, flowingTexture);
            this.dyeColor = dyeColor;
        }

        @Override
        protected int getTintColor(FluidStack fluidStack) {
            return dyeColor.getFireworkColor() + (255 << 24);
        }

        @Override
        protected int getTintColor(FluidState fluidState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos) {
            return dyeColor.getFireworkColor();
        }
    }

    private static class SolidRenderedFluidType extends AllFluids.TintedFluidType {

        public static FluidBuilder.FluidTypeFactory create() {
            return SolidRenderedFluidType::new;
        }

        public SolidRenderedFluidType(Properties properties,
                                   ResourceLocation stillTexture,
                                   ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        @Override
        protected int getTintColor(FluidStack fluidStack) {
            return NO_TINT;
        }

        @Override
        protected int getTintColor(FluidState fluidState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos) {
            return 0x00ffffff;
        }
    }

    public static void init(){
        for (DyeColor value : DyeColor.values()) {;
            COLOURED_FLAVOURED_ICING_FLUIDS.put(
                    value,
                    tintedBasicFluid(value,value.getName().toLowerCase() + "_flavoured_gloopy_icing")
                            .lang(StringUtils.capitalize(value.getName()) + " Flavoured Gloopy Icing")
                            .source(ForgeFlowingFluid.Source::new)
                            .bucket()
                            .model((itemBucketItemDataGenContext, registrateItemModelProvider) -> {
                                registrateItemModelProvider.basicItem(itemBucketItemDataGenContext.get())
                                        .parent(registrateItemModelProvider.getExistingFile(new ResourceLocation("forge", "item/bucket")))
                                        .customLoader(DynamicFluidContainerModelBuilder::begin)
                                        .fluid(itemBucketItemDataGenContext.get().getFluid())
                                        .applyTint(true)
                                        .end();
                            })
                            .color(() -> DynamicFluidContainerModel.Colors::new)
                            .build().register());
        }
    }
}
