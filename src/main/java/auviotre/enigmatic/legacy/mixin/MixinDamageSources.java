package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.api.damage.FalseJusticeDamage;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DamageSources.class, priority = 2999)
public class MixinDamageSources {
    @Inject(method = "playerAttack", at = @At("RETURN"))
    private void falseJustice$playerAttack(Player attacker, CallbackInfoReturnable<DamageSource> cir) {
        if (attacker == null) return;
        ItemStack stack = EnigmaticHandler.getItem(attacker, EnigmaticItems.FALSE_JUSTICE);
        if (!stack.isEmpty() && EnigmaticHandler.canUse(attacker, stack)) {
            FalseJusticeDamage.from(cir.getReturnValue()).falseJustice$setBypassAll(true);
        }
    }
}
