package dev.imabad.confectioneering;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.imabad.confectioneering.blocks.ConfectionBlocks;
import dev.imabad.confectioneering.client.ConfectioneeringClient;
import dev.imabad.confectioneering.data.ConfectionRecipes;
import dev.imabad.confectioneering.fluids.ConfectionFluids;
import dev.imabad.confectioneering.items.ConfectionItems;
import dev.imabad.confectioneering.machines.ConfectionMachines;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Confectioneering.MOD_ID)
public class Confectioneering {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "confectioneering";
    private static final ResourceLocation TAB_ID = location("confectioneering");
    public static final NonNullSupplier<CreateRegistrate> REGISTRATE = NonNullSupplier.lazy(() ->
            CreateRegistrate.create(MOD_ID).defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, TAB_ID)));

    public static ResourceLocation location(String location) {
        return new ResourceLocation(MOD_ID, location);
    }

    public Confectioneering(){
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        IEventBus modEventBus = FMLJavaModLoadingContext.get()
                .getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        registrate()
                .registerEventListeners(modEventBus)
                .generic(TAB_ID.getPath(), Registries.CREATIVE_MODE_TAB, () -> CreativeModeTab.builder()
                        .title(registrate().addLang("itemGroup", TAB_ID, "Confectioneering"))
                        .icon(() -> ConfectionItems.BISCUIT_DOUGH.get().getDefaultInstance()).build())
                .build()
                .addDataGenerator(ProviderType.RECIPE, registrateRecipeProvider -> {

                    ConfectionRecipes.mixing("gloopy_icing")
                            .require(ConfectionItems.ICING_SUGAR.get())
                            .require(Fluids.WATER, 200)
                            .output(ConfectionFluids.GLOOPY_ICING.get(), 100)
                            .build(registrateRecipeProvider);

                    ConfectionRecipes.mixing("flavoured_gloopy_icing")
                            .require(ConfectionItems.APPLE_FLAVOUR.get())
                            .require(ConfectionFluids.GLOOPY_ICING.get(), 100)
                            .output(ConfectionFluids.FLAVOURED_GLOOPY_ICING.get(), 100)
                            .build(registrateRecipeProvider);
                    for (int i = 0; i < DyeColor.values().length; i++) {
                        DyeColor color = DyeColor.byId(i);
                        FluidEntry<ForgeFlowingFluid.Flowing> flowingFluidEntry = ConfectionFluids.COLOURED_FLAVOURED_ICING_FLUIDS.get(color);
                        ConfectionRecipes.mixing(color.getName().toLowerCase() + "_flavoured_gloopy_icing")
                                .require(color.getTag())
                                .require(ConfectionFluids.FLAVOURED_GLOOPY_ICING.get(), 100)
                                .output(flowingFluidEntry.get(), 100)
                                .build(registrateRecipeProvider);
                    }
                }).addDataGenerator(ProviderType.LANG, (lang) -> {
                    lang.add(MOD_ID + ".recipe.assembly.dipping_dip_fluid", "Dip %1$s");
                });
        ConfectionRecipeTypes.register(modEventBus);
        ConfectionItems.init();
        ConfectionBlocks.init();
        ConfectionFluids.init();
        ConfectionMachines.init();


        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ConfectioneeringClient.clientConstruct(modEventBus, forgeEventBus));
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE.get();
    }
}
