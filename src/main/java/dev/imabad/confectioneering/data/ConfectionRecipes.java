package dev.imabad.confectioneering.data;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.imabad.confectioneering.ConfectionRecipeTypes;
import dev.imabad.confectioneering.Confectioneering;
import net.minecraft.resources.ResourceLocation;

public class ConfectionRecipes {

    public static ProcessingRecipeBuilder<?> processing(IRecipeTypeInfo info, ResourceLocation id) {
        ProcessingRecipeSerializer<?> serializer = info.getSerializer();
        return new ProcessingRecipeBuilder<>(serializer.getFactory(), id);
    }

    public static ProcessingRecipeBuilder<?> crushing(String path) {
        return processing(AllRecipeTypes.CRUSHING, Confectioneering.location(path));
    }

    public static ProcessingRecipeBuilder<?> mixing(String path) {
        return processing(AllRecipeTypes.MIXING, Confectioneering.location(path));
    }

    public static ProcessingRecipeBuilder<?> pressing(String path) {
        return processing(AllRecipeTypes.PRESSING, Confectioneering.location(path));
    }

    public static ProcessingRecipeBuilder<?> deploying(String path) {
        return processing(AllRecipeTypes.DEPLOYING, Confectioneering.location(path));
    }

    public static ProcessingRecipeBuilder<?> dipping(String path) {
        return processing(ConfectionRecipeTypes.DIPPING, Confectioneering.location(path));
    }
}
