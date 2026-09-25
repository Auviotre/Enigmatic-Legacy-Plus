package auviotre.enigmatic.legacy.contents.crafting;

import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticRecipes;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

public class AnnihilatingRecipe extends CustomRecipe {
    public AnnihilatingRecipe(CraftingBookCategory category) {
        super(category);
    }

    public boolean matches(CraftingInput input, Level level) {
        if (input.size() < 9) return false;
        if (!input.getItem(0).is(EnigmaticTags.Items.ABYSSAL_ITEMS)) return false;
        if (!input.getItem(1).is(EnigmaticItems.EVIL_INGOT)) return false;
        if (!input.getItem(2).is(EnigmaticTags.Items.ABYSSAL_ITEMS)) return false;
        if (!input.getItem(3).isEmpty()) return false;
        if (!input.getItem(5).isEmpty()) return false;
        if (!input.getItem(6).is(EnigmaticTags.Items.ABYSSAL_ITEMS)) return false;
        if (!input.getItem(7).is(EnigmaticItems.TWISTED_HEART)) return false;
        if (!input.getItem(8).is(EnigmaticTags.Items.ABYSSAL_ITEMS)) return false;
        ItemStack heart = input.getItem(4);
        return heart.is(EnigmaticItems.ABYSSAL_HEART) && ITaintable.isTainted(heart);
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
        return EnigmaticItems.ANNIHILATING_SWORD.toStack();
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> list = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        Player player = CommonHooks.getCraftingPlayer();
        if (EnigmaticHandler.isTheWorthyOne(player)) {
            CompoundTag data = EnigmaticHandler.getPersistedData(player);
            int gained = data.getInt("AbyssalHeartsGained");
            if (gained > 4) list.set(0, input.getItem(0).copy());
            if (gained > 3) list.set(2, input.getItem(2).copy());
            if (gained > 2) list.set(6, input.getItem(6).copy());
            if (gained > 1) list.set(8, input.getItem(8).copy());
        }
        return list;
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return EnigmaticItems.ANNIHILATING_SWORD.toStack();
    }

    public boolean canCraftInDimensions(int w, int h) {
        return w == 3 &&  h == 3;
    }

    public RecipeSerializer<?> getSerializer() {
        return EnigmaticRecipes.ANNIHILATING.get();
    }
}
