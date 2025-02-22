package dev.imabad.confectioneering.content;

import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.imabad.confectioneering.Confectioneering;
import dev.imabad.confectioneering.data.ConfectionRecipes;
import dev.imabad.confectioneering.fluids.ConfectionFluids;
import dev.imabad.confectioneering.items.ConfectionItems;
import dev.imabad.confectioneering.items.PartyRingColours;
import dev.imabad.confectioneering.machines.dipper.DippingRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.imabad.confectioneering.items.ConfectionItems.forgeItemTag;

public class PartyRingsFeature {

    public static final CreateRegistrate REGISTRATE = Confectioneering.registrate();

    public static final RegistryEntry<Item> RINGED_BISCUIT_CUTTER = REGISTRATE.item("ringed_biscuit_cutter", Item::new)
            .defaultModel()
            .lang("Ringed Biscuit Cutter")
            .recipe((c, p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, c.get())
                            .pattern(" C ")
                            .pattern("C C")
                            .pattern(" C ")
                            .define('C', forgeItemTag("ingots/iron"))
                            .unlockedBy("has_ringed_biscuit_cutter", RegistrateRecipeProvider.has(c.get()))
                            .save(p)
            )
            .register();

    public static final RegistryEntry<Item> RINGED_CUT_BISCUIT_DOUGH = REGISTRATE.item("ringed_cut_biscuit_dough", Item::new)
            .defaultModel()
            .lang("Ringed Cut Biscuit Dough")
            .recipe((c, p) -> ConfectionRecipes.deploying("ringed_cut_biscuit_dough")
                    .require(ConfectionItems.FLAT_BISCUIT_DOUGH.get())
                    .require(RINGED_BISCUIT_CUTTER.get())
                    .toolNotConsumed()
                    .output(c.get(), 4)
                    .build(p))
            .register();

    public static final RegistryEntry<Item> RINGED_CUT_BISCUIT = REGISTRATE.item("ringed_cut_biscuit", Item::new)
            .defaultModel()
            .lang("Ringed Cut Biscuit")
            .recipe((c, p) -> SimpleCookingRecipeBuilder
                    .smoking(Ingredient.of(RINGED_CUT_BISCUIT_DOUGH.get()), RecipeCategory.FOOD, c.get(), 0, 100)
                    .unlockedBy("has_flour", RegistrateRecipeProvider.has(forgeItemTag("flour/wheat")))
                    .save(p))
            .register();

    public static final Map<PartyRingColours, RegistryEntry<Item>> PARTY_RINGS = Arrays.stream(PartyRingColours.values())
            .collect(Collectors.toMap(Function.identity(), item -> REGISTRATE.item(item.getName().toLowerCase() + "_party_ring", Item::new)
                    .defaultModel()
                    .properties(p -> p.food(new FoodProperties.Builder().nutrition(2)
                            .alwaysEat()
                            .saturationMod(0.8F)
                            .build()))
                    .lang(item.getName()+ " Party Ring")
                    .register()));

    public static final List<RegistryEntry<SequencedAssemblyItem>> ICED_RINGS = Arrays.stream(PartyRingColours.values())
            .map(partyRingColours ->
                    REGISTRATE.item(partyRingColours.getName().toLowerCase() + "_iced_ringed_biscuit", SequencedAssemblyItem::new)
                            .defaultModel()
                            .lang(partyRingColours.getName()+ " Iced Ringed Biscuit")
                            .recipe((c, p) -> {
                                new SequencedAssemblyRecipeBuilder(new ResourceLocation(Confectioneering.MOD_ID, partyRingColours.getName().toLowerCase() + "_iced_ringed_biscuit"))
                                        .require(RINGED_CUT_BISCUIT.get())
                                        .transitionTo(c.get())
                                        .addOutput(PARTY_RINGS.get(partyRingColours).get(), 1)
                                        .loops(1)
                                        .addStep(DippingRecipe::new, rb -> rb.require(ConfectionFluids.COLOURED_FLAVOURED_ICING_FLUIDS.get(partyRingColours.getDyeColor()).get(), 100))
                                        .addStep(FillingRecipe::new, rb -> rb.require(ConfectionFluids.COLOURED_FLAVOURED_ICING_FLUIDS.get(partyRingColours.getSecondaryColor()).get(), 50))
                                        .addStep(PressingRecipe::new, rb -> rb)
                                        .build(p);
                            })
                            .register()).collect(Collectors.toUnmodifiableList());

    public static void init(){}
}
