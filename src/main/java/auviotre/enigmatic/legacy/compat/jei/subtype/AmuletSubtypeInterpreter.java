package auviotre.enigmatic.legacy.compat.jei.subtype;

import auviotre.enigmatic.legacy.contents.item.amulets.EnigmaticAmulet;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AmuletSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final AmuletSubtypeInterpreter INSTANCE = new AmuletSubtypeInterpreter();

    public @Nullable Float getSubtypeData(@NotNull ItemStack ingredient, UidContext context) {
        if (!ingredient.has(EnigmaticComponents.AMULET_COLOR)) return null;
        return EnigmaticAmulet.getColor(ingredient).getColorVar();
    }

    public String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, UidContext context) {
        String description = ingredient.getItem().getDescriptionId();
        String string = EnigmaticAmulet.getColor(ingredient).toString();
        return description + "." + string.toLowerCase();
    }
}
