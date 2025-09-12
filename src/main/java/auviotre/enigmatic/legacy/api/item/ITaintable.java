package auviotre.enigmatic.legacy.api.item;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ITaintable {
    ResourceLocation LOCATION = EnigmaticLegacy.location("tainted");

    static boolean isTainted(ItemStack stack) {
        return stack.getOrDefault(EnigmaticComponents.TAINTABLE.get(), false);
    }

    default void handleTaintable(ItemStack stack, Player player) {
        if (EnigmaticHandler.isTheOne(player)) {
            if (!stack.getOrDefault(EnigmaticComponents.TAINTABLE.get(), false)) {
                stack.set(EnigmaticComponents.TAINTABLE.get(), true);
            }
        } else {
            if (stack.getOrDefault(EnigmaticComponents.TAINTABLE.get(), false)) {
                stack.set(EnigmaticComponents.TAINTABLE.get(), false);
            }
        }
    }
}