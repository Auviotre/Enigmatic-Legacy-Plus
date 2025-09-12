package auviotre.enigmatic.legacy.compat.jei.subtype;

import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TaintableSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final TaintableSubtypeInterpreter INSTANCE = new TaintableSubtypeInterpreter();

    public @Nullable Boolean getSubtypeData(@NotNull ItemStack ingredient, UidContext context) {
        return ingredient.getOrDefault(EnigmaticComponents.TAINTABLE, false);
    }

    public String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, UidContext context) {
        String description = ingredient.getItem().getDescriptionId();
        if (ingredient.getOrDefault(EnigmaticComponents.TAINTABLE, false))
            return description + ".tainted";
        return description;
    }
}
