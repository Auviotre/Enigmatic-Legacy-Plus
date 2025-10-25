package auviotre.enigmatic.legacy.compat.jei;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.compat.jei.category.TaintingCategory;
import auviotre.enigmatic.legacy.contents.crafting.SpellstoneTableRecipe;
import auviotre.enigmatic.legacy.registries.EnigmaticRecipes;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;

public class EnigmaticRecipeTypes {
    public static final RecipeType<TaintingCategory.Recipe> TAINTING = RecipeType.create(EnigmaticLegacy.MODID, "tainting", TaintingCategory.Recipe.class);
    public static final RecipeType<RecipeHolder<SpellstoneTableRecipe>> SPELLSTONE_CRAFTING = RecipeType.createFromVanilla(EnigmaticRecipes.SPELLSTONE_CRAFTING.get());

}
