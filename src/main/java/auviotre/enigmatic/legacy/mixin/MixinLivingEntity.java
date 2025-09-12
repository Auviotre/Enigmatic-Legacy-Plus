package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.contents.item.tools.InfernalShield;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements ILivingEntityExtension {
    @Shadow protected ItemStack useItem;

    @Shadow public abstract boolean isUsingItem();

    @Inject(method = "isDamageSourceBlocked", at = @At("HEAD"), cancellable = true)
    private void onDamageSourceBlocking(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        if (EnigmaticHandler.onDamageSourceBlocking(this.self(), this.useItem, source)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void isBlockingMix(CallbackInfoReturnable<Boolean> info) {
        if (this.isUsingItem() && this.useItem.getItem() instanceof InfernalShield) {
            info.setReturnValue(true);
        }
    }
}
