package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.contents.item.spellstones.ForgottenIce;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntityExtension {
    @Shadow
    public abstract int getTicksRequiredToFreeze();

    @Shadow
    public abstract int getTicksFrozen();

    @Inject(method = "getTicksRequiredToFreeze", at = @At("RETURN"), cancellable = true)
    public void getTicksRequiredToFreezeMix(@NotNull CallbackInfoReturnable<Integer> info) {
        if (!ForgottenIce.freezingBoost.get()) return;
        info.setReturnValue(info.getReturnValue() + 400);
    }

    @Inject(method = "getPercentFrozen", at = @At("RETURN"), cancellable = true)
    public void getPercentFrozenMix(@NotNull CallbackInfoReturnable<Float> info) {
        if (!ForgottenIce.freezingBoost.get()) return;
        int i = this.getTicksRequiredToFreeze() - 400;
        info.setReturnValue((float) Math.min(this.getTicksFrozen(), i) / (float) i);
    }

    @Inject(method = "isFullyFrozen", at = @At("RETURN"), cancellable = true)
    public void isFullyFrozenMix(@NotNull CallbackInfoReturnable<Boolean> info) {
        if (!ForgottenIce.freezingBoost.get()) return;
        info.setReturnValue(this.getTicksFrozen() >= this.getTicksRequiredToFreeze() - 400);
    }
}
