package dev.imabad.confectioneering.items;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.imabad.confectioneering.Confectioneering;
import dev.imabad.confectioneering.data.ConfectionRecipes;
import dev.imabad.confectioneering.fluids.ConfectionFluids;
import dev.imabad.confectioneering.machines.dipper.DippingRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfectionItems {

    public static final CreateRegistrate REGISTRATE = Confectioneering.registrate();

    public static final RegistryEntry<Item> BISCUIT_DOUGH = REGISTRATE.item("biscuit_dough", Item::new)
            .defaultModel()
            .lang("Biscuit Dough")
            .recipe((c, p) -> {
                ShapelessRecipeBuilder
                    .shapeless(RecipeCategory.FOOD, c.get())
                    .requires(
                            Ingredient.of(forgeItemTag("flour/wheat"))
                    )
                    .requires(
                            Items.SUGAR
                    )
                    .requires(
                            Fluids.WATER.getBucket()
                    )
                    .unlockedBy("has_flour", RegistrateRecipeProvider.has(forgeItemTag("flour/wheat")))
                    .save(p);
                    ConfectionRecipes.mixing("mixing_biscuit_dough")
                        .require(Ingredient.of(forgeItemTag("flour/wheat")))
                        .require(Items.SUGAR)
                        .require(Fluids.WATER, 1000)
                        .output(c.get())
                        .build(p);
            })
            .register();

    public static final RegistryEntry<Item> FLAT_BISCUIT_DOUGH = REGISTRATE.item("flat_biscuit_dough", Item::new)
            .defaultModel()
            .lang("Flat Biscuit Dough")
            .recipe((c, p) -> ConfectionRecipes.pressing("flat_biscuit_dough")
                    .require(BISCUIT_DOUGH.get())
                    .output(c.get())
                    .averageProcessingDuration()
                    .build(p))
            .register();

    public static final RegistryEntry<Item> ICING_SUGAR = REGISTRATE.item("icing_sugar", Item::new)
            .defaultModel()
            .lang("Icing Sugar")
            .recipe((c, p) ->
                    ConfectionRecipes.crushing("icing_sugar")
                            .require(Items.SUGAR)
                            .output(c.get(), 2)
                            .averageProcessingDuration()
                            .build(p))
            .register();

    public static final RegistryEntry<Item> APPLE_FLAVOUR = REGISTRATE.item("apple_flavour", Item::new)
            .defaultModel()
            .lang("Apple Flavouring")
            .recipe((c, p) ->
                    ConfectionRecipes.pressing("apple_flavour")
                            .require(Items.APPLE)
                            .output(c.get(), 1)
                            .build(p))
            .register();

//    public static final RegistryEntry<SequencedAssemblyItem> INCOMPLETE_CARDBOARD_PIECE = REGISTRATE.item("incomplete_cardboard_piece", SequencedAssemblyItem::new)
//            .defaultModel()
//            .lang("Incomplete Cardboard Piece")
//            .register();
//
//    public static final RegistryEntry<Item> CARDBOARD_PIECE = REGISTRATE.item("cardboard_piece", Item::new)
//            .defaultModel()
//            .lang("Cardboard Piece")
//            .recipe((c, p) -> {
//                new SequencedAssemblyRecipeBuilder(new ResourceLocation(Confectioneering.MOD_ID, "cardboard_piece"))
//                        .require(Items.PAPER)
//                        .transitionTo(INCOMPLETE_CARDBOARD_PIECE.get())
//                        .addOutput(new ItemStack(c.get(), 3), 1)
//                        .loops(5)
//                        .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllItems.SUPER_GLUE).toolNotConsumed())
//                        .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.PAPER))
//                        .addStep(PressingRecipe::new, rb -> rb)
//                        .build(p);
//            })
//            .register();
//
//    public static final RegistryEntry<Item> CARDBOARD_BOX = REGISTRATE.item("cardboard_box", Item::new)
//            .defaultModel()
//            .lang("Cardboard Box")
//            .recipe((c, p) -> {
//                MechanicalCraftingRecipeBuilder.shapedRecipe(c.get())
//                        .key('C', CARDBOARD_PIECE.get())
//                        .patternLine(" C ")
//                        .patternLine("C C")
//                        .patternLine(" C ")
//                        .build(p);
//            })
//            .register();
//
//    public static final RegistryEntry<SequencedAssemblyItem> PARTIALLY_FILLED_BOX = REGISTRATE.item("partially_filled_cardboard_box", SequencedAssemblyItem::new)
//            .defaultModel()
//            .lang("Partially Filled Box")
//            .register();
//
//    public static final RegistryEntry<FilledBoxItem> PARTY_RINGS_BOX = REGISTRATE.item("party_ring_box", FilledBoxItem::new)
//            .defaultModel()
//            .lang("Party Ring Box")
//            .properties(properties -> properties.stacksTo(16))
//            .recipe((c, p) -> {
//                SequencedAssemblyRecipeBuilder partyRingBoxBuilder = new SequencedAssemblyRecipeBuilder(new ResourceLocation(Confectioneering.MOD_ID, "party_ring_box"))
//                        .require(CARDBOARD_BOX.get())
//                        .transitionTo(PARTIALLY_FILLED_BOX.get())
//                        .addOutput(c.get(), 1)
//                        .loops(4);
//                for (PartyRingColours value : PartyRingColours.values()) {
//                    partyRingBoxBuilder.addStep(DeployerApplicationRecipe::new, rb -> rb.require(PARTY_RINGS.get(value).get()));
//                }
//                partyRingBoxBuilder
//                        .build(p);
//            })
//            .register();

    public static <T> TagKey<T> optionalTag(IForgeRegistry<T> registry, ResourceLocation id) {
        return registry.tags().createOptionalTagKey(id, Collections.emptySet());
    }

    public static <T> TagKey<T> forgeTag(IForgeRegistry<T> registry, String path){
        return optionalTag(registry, new ResourceLocation("forge", path));
    }

    public static TagKey<Item> forgeItemTag(String path) {
        return forgeTag(ForgeRegistries.ITEMS, path);
    }

    public static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags){
        return REGISTRATE.item(name, Item::new)
                .tag(tags)
                .register();
    }

    public static void init(){}
}
