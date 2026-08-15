package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.api.damage.FalseJusticeDamage;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DamageSource.class, priority = 3000)
public class MixinDamageSource implements FalseJusticeDamage {
    @Unique
    private boolean falseJustice$bypassAll = false;

    @Override
    public void falseJustice$setBypassAll(boolean z) {
        falseJustice$bypassAll = z;
    }

    @Override
    public boolean falseJustice$isBypassAll() {
        return falseJustice$bypassAll;
    }

    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void falseJustice$is(TagKey<DamageType> tagKey, CallbackInfoReturnable<Boolean> cir) {
        if (this.falseJustice$isBypassAll()) {
            if (tagKey == DamageTypeTags.BYPASSES_ARMOR
                    || tagKey == DamageTypeTags.BYPASSES_SHIELD
                    || tagKey == DamageTypeTags.BYPASSES_INVULNERABILITY
                    || tagKey == DamageTypeTags.BYPASSES_RESISTANCE
                    || tagKey == DamageTypeTags.BYPASSES_EFFECTS)
                cir.setReturnValue(true);
        }
    }
}
