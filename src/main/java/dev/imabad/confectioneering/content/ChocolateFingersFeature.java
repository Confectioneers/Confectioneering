package dev.imabad.confectioneering.content;

import com.simibubi.create.AllFluids;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.imabad.confectioneering.Confectioneering;
import dev.imabad.confectioneering.data.ConfectionRecipes;
import dev.imabad.confectioneering.fluids.ConfectionFluids;
import dev.imabad.confectioneering.items.ConfectionItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import static dev.imabad.confectioneering.items.ConfectionItems.forgeItemTag;

public class ChocolateFingersFeature {

    public static final CreateRegistrate REGISTRATE = Confectioneering.registrate();

    public static final RegistryEntry<Item> FINGER_BISCUIT_CUTTER = REGISTRATE.item("finger_biscuit_cutter", Item::new)
            .defaultModel()
            .lang("Finger Biscuit Cutter")
            .recipe((c, p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, c.get())
                            .pattern("CCC")
                            .pattern("C C")
                            .pattern("CCC")
                            .define('C', forgeItemTag("ingots/iron"))
                            .unlockedBy("has_finger_biscuit_cutter", RegistrateRecipeProvider.has(c.get()))
                            .save(p)
            )
            .register();

    public static final RegistryEntry<Item> FINGER_CUT_BISCUIT_DOUGH = REGISTRATE.item("finger_cut_biscuit_dough", Item::new)
            .defaultModel()
            .lang("Finger Cut Biscuit Dough")
            .recipe((c, p) -> ConfectionRecipes.deploying("finger_cut_biscuit_dough")
                    .require(ConfectionItems.FLAT_BISCUIT_DOUGH.get())
                    .require(FINGER_BISCUIT_CUTTER.get())
                    .toolNotConsumed()
                    .output(c.get(), 6)
                    .build(p))
            .register();

    public static final RegistryEntry<Item> FINGER_CUT_BISCUIT = REGISTRATE.item("finger_cut_biscuit", Item::new)
            .defaultModel()
            .lang("Finger Cut Biscuit")
            .recipe((c, p) -> SimpleCookingRecipeBuilder
                    .smoking(Ingredient.of(FINGER_CUT_BISCUIT_DOUGH.get()), RecipeCategory.FOOD, c.get(), 0, 100)
                    .unlockedBy("has_flour", RegistrateRecipeProvider.has(forgeItemTag("flour/wheat")))
                    .save(p))
            .register();

    public static final RegistryEntry<Item> CHOCOLATE_FINGER_BISCUIT = REGISTRATE.item("chocolate_finger_biscuit", Item::new)
            .defaultModel()
            .properties(p -> p.food(new FoodProperties.Builder().nutrition(2)
                    .alwaysEat()
                    .saturationMod(0.8F)
                    .build()))
            .recipe((ctx, registrate) -> {
                ConfectionRecipes.enrobing("chocolate_finger_biscuit")
                        .require(AllFluids.CHOCOLATE.get(), 50)
                        .require(FINGER_CUT_BISCUIT.get())
                        .output(ctx.get(), 1)
                        .build(registrate);
            })
            .lang("Chocolate Finger Biscuit")
            .register();

    public static final RegistryEntry<Item> WHITE_CHOCOLATE_FINGER_BISCUIT = REGISTRATE.item("white_chocolate_finger_biscuit", Item::new)
            .defaultModel()
            .properties(p -> p.food(new FoodProperties.Builder().nutrition(2)
                    .alwaysEat()
                    .saturationMod(0.8F)
                    .build()))
            .recipe((ctx, registrate) -> {
                ConfectionRecipes.enrobing("white_chocolate_finger_biscuit")
                        .require(ConfectionFluids.WHITE_CHOCOLATE.get(), 50)
                        .require(FINGER_CUT_BISCUIT.get())
                        .output(ctx.get(), 1)
                        .build(registrate);
            })
            .lang("White Chocolate Finger Biscuit")
            .register();

    public static final RegistryEntry<Item> DARK_CHOCOLATE_FINGER_BISCUIT = REGISTRATE.item("dark_chocolate_finger_biscuit", Item::new)
            .defaultModel()
            .properties(p -> p.food(new FoodProperties.Builder().nutrition(2)
                    .alwaysEat()
                    .saturationMod(0.8F)
                    .build()))
            .recipe((ctx, registrate) -> {
                ConfectionRecipes.enrobing("dark_chocolate_finger_biscuit")
                        .require(ConfectionFluids.DARK_CHOCOLATE.get(), 50)
                        .require(FINGER_CUT_BISCUIT.get())
                        .output(ctx.get(), 1)
                        .build(registrate);
            })
            .lang("Dark Chocolate Finger Biscuit")
            .register();

    public static void init(){}
}
