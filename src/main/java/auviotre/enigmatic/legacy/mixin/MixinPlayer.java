package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.contents.item.tools.InfernalShield;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        throw new IllegalStateException("Can't touch this");
    }

    @Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
    public void disableShieldMix(CallbackInfo info) {
        if (this.getUseItem().getItem() instanceof InfernalShield) info.cancel();
    }

    @Inject(method = "destroyVanishingCursedItems", at = @At("TAIL"))
    public void onDestroyVanishingCursedItems(CallbackInfo info) {
        CuriosApi.getCuriosInventory(this).ifPresent(handler -> {
            IItemHandlerModifiable curios = handler.getEquippedCurios();
            for (int i = 0; i < curios.getSlots(); i++) {
                ItemStack stack = curios.getStackInSlot(i);
                if (!stack.isEmpty() && EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                    curios.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        });
    }
}
