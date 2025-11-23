package auviotre.enigmatic.legacy.compat.jei.category;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.compat.jei.EnigmaticRecipeTypes;
import auviotre.enigmatic.legacy.compat.jei.JEIHandler;
import auviotre.enigmatic.legacy.contents.crafting.SpellstoneTableRecipe;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.List;

public class SpellstoneCraftingCategory implements IRecipeCategory<RecipeHolder<SpellstoneTableRecipe>> {
    private static final ResourceLocation SPRITE = EnigmaticLegacy.location("jei/spellstone_table_gui");
    private static final ResourceLocation ICON = EnigmaticLegacy.location("jei/spellstone_table_icon");
    private static final List<Pair<Integer, Integer>> OFFSETS = List.of(
            new Pair<>(38, 52),
            new Pair<>(31, 32),
            new Pair<>(38, 12),
            new Pair<>(58, 5),
            new Pair<>(78, 12),
            new Pair<>(85, 32),
            new Pair<>(78, 52)
    );
    private final IDrawable icon;

    public SpellstoneCraftingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(EnigmaticItems.SPELLCORE);
    }

    public RecipeType<RecipeHolder<SpellstoneTableRecipe>> getRecipeType() {
        return EnigmaticRecipeTypes.SPELLSTONE_CRAFTING;
    }

    public Component getTitle() {
        return Component.translatable("gui.enigmaticlegacy.jei.spellstone_crafting");
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    public int getWidth() {
        return 132;
    }

    public int getHeight() {
        return 80;
    }

    public void draw(RecipeHolder<SpellstoneTableRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blitSprite(SPRITE, 0, 0, getWidth(), getHeight());
        if (recipe.value().isAllDifferent()) guiGraphics.blitSprite(ICON, 124, 0, 8, 8);
    }

    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<SpellstoneTableRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (recipe.value().isAllDifferent() && mouseX >= 124 && mouseY >= 0 && mouseX <= 132 && mouseY <= 8) {
            tooltip.add(Component.translatable("gui.enigmaticlegacy.jei.spellstone_crafting.all_different"));
        }
    }

    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull RecipeHolder<SpellstoneTableRecipe> holder, IFocusGroup focuses) {
        SpellstoneTableRecipe recipe = holder.value();
        builder.addInputSlot(5, 32).addItemStack(EnigmaticItems.SPELLSTONE_DEBRIS.toStack(recipe.getCount()));
        builder.addInputSlot(111, 32);
        builder.addInputSlot(58, 32).addItemStack(EnigmaticItems.SPELLCORE.toStack());
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            Pair<Integer, Integer> pair = OFFSETS.get(i);
            builder.addInputSlot(pair.getA(), pair.getB()).addIngredients(ingredients.get(i));
        }
        builder.addOutputSlot(58, 59).addItemStack(recipe.getResultItem(JEIHandler.getLevel().registryAccess()));
    }
}
