package auviotre.enigmatic.legacy.api.item;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public interface ISpellstone {

    static ItemStack get(LivingEntity entity) {
        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(entity);
        if (curios.isPresent()) {
            Optional<SlotResult> firstCurio = curios.get().findFirstCurio(stack -> !stack.isEmpty() && stack.getItem() instanceof ISpellstone, "enigmaticlegacy:spellstone");
            if (firstCurio.isPresent()) return firstCurio.get().stack();
        }
        return ItemStack.EMPTY;
    }

    int getCooldown();

    default void triggerActiveAbility(ServerLevel level, @NotNull ServerPlayer player, ItemStack stack) {
        ItemCooldowns cooldowns = player.getCooldowns();
        int cooldown = getCooldown();
        if (cooldown > 0 && !cooldowns.isOnCooldown(stack.getItem())) {
            cooldown = player.hasInfiniteMaterials() ? Math.min(15, cooldown) : cooldown;
            if (EnigmaticHandler.hasCurio(player, EnigmaticItems.COSMIC_SCROLL)) cooldown = (int) (cooldown * 0.4F);
            else if (EnigmaticHandler.hasCurio(player, EnigmaticItems.SPELLTUNER)) cooldown = (int) (cooldown * 0.9F);
            int finalCooldown = cooldown;
            BuiltInRegistries.ITEM.forEach(item -> {
                if (item.getDefaultInstance().is(EnigmaticTags.Items.SPELLSTONES)) {
                    cooldowns.addCooldown(item, finalCooldown);
                }
            });
        }
    }
}
