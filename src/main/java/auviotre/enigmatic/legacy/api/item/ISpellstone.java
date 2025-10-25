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
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.atomic.AtomicReference;

public interface ISpellstone {

    static ItemStack get(LivingEntity entity) {
        AtomicReference<ItemStack> ret = new AtomicReference<>(ItemStack.EMPTY);
        CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
            IItemHandlerModifiable curios = handler.getEquippedCurios();
            for (int id = 0; id < curios.getSlots(); id++) {
                ItemStack stackInSlot = curios.getStackInSlot(id);
                if (stackInSlot.isEmpty()) continue;
                if (stackInSlot.getItem() instanceof ISpellstone) {
                    ret.set(stackInSlot);
                    break;
                }
            }
        });
        return ret.get();
    }

    int getCooldown();

    default void triggerActiveAbility(ServerLevel level, @NotNull ServerPlayer player, ItemStack stack) {
        ItemCooldowns cooldowns = player.getCooldowns();
        int cooldown = getCooldown();
        if (cooldown > 0 && !cooldowns.isOnCooldown(stack.getItem())) {
            cooldown = player.hasInfiniteMaterials() ? 15 : cooldown;
            if (EnigmaticHandler.hasCurio(player, EnigmaticItems.COSMIC_SCROLL)) cooldown = (int) (cooldown * 0.2F);
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
