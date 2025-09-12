package auviotre.enigmatic.legacy.compat.jei.extension;

import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.contents.crafting.CursedShapedRecipe;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class CursedRecipeExtension implements ICraftingCategoryExtension<CursedShapedRecipe> {

    public void setRecipe(RecipeHolder<CursedShapedRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        CursedShapedRecipe recipe = recipeHolder.value();
        List<List<ItemStack>> inputs = new ArrayList<>();
        for (Ingredient ingredient : recipe.pattern.ingredients()) {
            List<ItemStack> list = new ArrayList<>();
            for (ItemStack stack : ingredient.getItems()) {
                if (stack.getItem() instanceof ITaintable) stack.set(EnigmaticComponents.TAINTABLE.get(), true);
                list.add(stack);
            }
            inputs.add(list);
        }
        craftingGridHelper.createAndSetInputs(builder, inputs, recipe.getWidth(), recipe.getHeight());
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) throw new NullPointerException("level must not be null.");
        RegistryAccess registryAccess = level.registryAccess();
        ItemStack result = recipe.getResultItem(registryAccess);
        if (result.getItem() instanceof ITaintable) result.set(EnigmaticComponents.TAINTABLE.get(), true);
        craftingGridHelper.createAndSetOutputs(builder, List.of(result));
    }
}
