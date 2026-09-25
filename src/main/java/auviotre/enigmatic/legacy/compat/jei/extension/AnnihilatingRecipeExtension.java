package auviotre.enigmatic.legacy.compat.jei.extension;

import auviotre.enigmatic.legacy.contents.crafting.AnnihilatingRecipe;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnnihilatingRecipeExtension implements ICraftingCategoryExtension<AnnihilatingRecipe> {
    public void setRecipe(@NotNull RecipeHolder<AnnihilatingRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        AnnihilatingRecipe recipe = recipeHolder.value();
        List<IRecipeSlotBuilder> inputs = craftingGridHelper.createAndSetInputs(builder, List.of(), 3, 3);
        Ingredient ingredient = Ingredient.of(EnigmaticTags.Items.ABYSSAL_ITEMS);
        inputs.get(0).addIngredients(ingredient);
        inputs.get(1).addItemStack(EnigmaticItems.EVIL_INGOT.toStack());
        inputs.get(2).addIngredients(ingredient);
        inputs.get(3).addItemStack(ItemStack.EMPTY);
        ItemStack abyssalHeart = EnigmaticItems.ABYSSAL_HEART.toStack();
        abyssalHeart.set(EnigmaticComponents.TAINTABLE, true);
        inputs.get(4).addItemStack(abyssalHeart);
        inputs.get(5).addItemStack(ItemStack.EMPTY);
        inputs.get(6).addIngredients(ingredient);
        ItemStack twistedHeart = EnigmaticItems.TWISTED_HEART.toStack();
        twistedHeart.set(EnigmaticComponents.TAINTABLE, true);
        inputs.get(7).addItemStack(twistedHeart);
        inputs.get(8).addIngredients(ingredient);
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) throw new NullPointerException("level must not be null.");
        RegistryAccess registryAccess = level.registryAccess();
        ItemStack result = recipe.getResultItem(registryAccess);
        craftingGridHelper.createAndSetOutputs(builder, List.of(result));
    }
}
