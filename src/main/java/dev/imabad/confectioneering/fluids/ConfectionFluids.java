package dev.imabad.confectioneering.fluids;

import com.simibubi.create.AllFluids;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfectionFluids {

    public static final CreateRegistrate REGISTRATE = Confectioneering.registrate();

    private static FluidBuilder<ForgeFlowingFluid.Flowing, CreateRegistrate> basicFluid(String name) {
        return REGISTRATE.fluid(name, Confectioneering.location("block/" + name + "_still"), Confectioneering.location("block/" + name + "_flow"));
    }
    private static FluidBuilder<ForgeFlowingFluid.Flowing, CreateRegistrate> tintedBasicFluid(DyeColor color, String name) {
        return REGISTRATE.fluid(name, Confectioneering.location("block/flavoured_gloopy_icing_still"), Confectioneering.location("block/flavoured_gloopy_icing_flow"), DyedFluidAttributes.create(color));
    }

    public static final FluidEntry<ForgeFlowingFluid.Flowing> GLOOPY_ICING = basicFluid("gloopy_icing")
            .lang("Gloopy Icing")
            .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> FLAVOURED_GLOOPY_ICING = basicFluid("flavoured_gloopy_icing")
            .lang("Flavoured Gloopy Icing")
            .register();

    public static final Map<DyeColor, FluidEntry<ForgeFlowingFluid.Flowing>> COLOURED_FLAVOURED_ICING_FLUIDS = new HashMap<>();

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
