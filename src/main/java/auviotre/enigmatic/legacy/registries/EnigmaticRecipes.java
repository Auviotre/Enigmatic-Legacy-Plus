package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.crafting.CursedShapedRecipe;
import auviotre.enigmatic.legacy.contents.crafting.ShapelessNoRemainRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, EnigmaticLegacy.MODID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CursedShapedRecipe>> CURSED_SHAPED = RECIPE_SERIALIZER.register("cursed_shaped", CursedShapedRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapelessNoRemainRecipe>> SHAPELESS_NO_REMAIN = RECIPE_SERIALIZER.register("shapeless_no_remain", ShapelessNoRemainRecipe.Serializer::new);
}
