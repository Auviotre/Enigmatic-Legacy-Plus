package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.crafting.CursedShapedRecipe;
import auviotre.enigmatic.legacy.contents.crafting.ShapelessNoRemainRecipe;
import auviotre.enigmatic.legacy.contents.crafting.SpellstoneTableRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, EnigmaticLegacy.MODID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CursedShapedRecipe>> CURSED_SHAPED = RECIPE_SERIALIZERS.register("cursed_shaped", CursedShapedRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapelessNoRemainRecipe>> SHAPELESS_NO_REMAIN = RECIPE_SERIALIZERS.register("shapeless_no_remain", ShapelessNoRemainRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SpellstoneTableRecipe>> SPELLSTONE_TABLE = RECIPE_SERIALIZERS.register("spellstone_table", SpellstoneTableRecipe.Serializer::new);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, EnigmaticLegacy.MODID);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SpellstoneTableRecipe>> SPELLSTONE_CRAFTING = RECIPE_TYPES.register("spellstone_crafting", () -> new RecipeType<>() {
        public String toString() {
            return EnigmaticLegacy.MODID + ":spellstone_crafting";
        }
    });
}
