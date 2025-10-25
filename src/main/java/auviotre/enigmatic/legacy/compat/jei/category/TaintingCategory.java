package auviotre.enigmatic.legacy.compat.jei.category;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.compat.jei.EnigmaticRecipeTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class TaintingCategory implements IRecipeCategory<TaintingCategory.Recipe> {
    private static final ResourceLocation SPRITE = EnigmaticLegacy.location("jei/tainting_gui");
    private final IDrawable icon;

    public TaintingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(EnigmaticItems.EARTH_HEART);
    }

    @Contract("_, _ -> new")
    public static @NotNull Recipe create(ItemStack input, Ingredient condition) {
        return new Recipe(input, condition);
    }

    public RecipeType<Recipe> getRecipeType() {
        return EnigmaticRecipeTypes.TAINTING;
    }

    public Component getTitle() {
        return Component.translatable("gui.enigmaticlegacy.jei.tainting");
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    public int getWidth() {
        return 108;
    }

    public int getHeight() {
        return 42;
    }

    public void draw(Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blitSprite(SPRITE, 0, 0, getWidth(), getHeight());
    }

    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull TaintingCategory.Recipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(13, 13).addItemStack(recipe.input()).setStandardSlotBackground();
        ItemStack output = recipe.input().copy();
        if (output.getItem() instanceof ITaintable) output.set(EnigmaticComponents.TAINTABLE.get(), true);
        builder.addOutputSlot(79, 13).addItemStack(output).setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.CATALYST, 46, 23).addIngredients(recipe.condition());
    }

    public record Recipe(ItemStack input, Ingredient condition) {
    }
}
