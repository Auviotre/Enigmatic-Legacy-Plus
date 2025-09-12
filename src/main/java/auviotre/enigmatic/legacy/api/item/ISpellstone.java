package auviotre.enigmatic.legacy.api.item;

import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.atomic.AtomicReference;

public interface ISpellstone {
    Lazy<KeyMapping> KEY_MAPPING = Lazy.of(() -> new KeyMapping(
            "key.spellstoneAbility",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.misc"
    ));

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
        if (getCooldown() > 0 && !cooldowns.isOnCooldown(stack.getItem())) {
            BuiltInRegistries.ITEM.forEach(item -> {
                if (item.getDefaultInstance().is(EnigmaticTags.Items.SPELLSTONES)) {
                    cooldowns.addCooldown(item, player.hasInfiniteMaterials() ? 15 : getCooldown());
                }
            });
        }
    }
}
