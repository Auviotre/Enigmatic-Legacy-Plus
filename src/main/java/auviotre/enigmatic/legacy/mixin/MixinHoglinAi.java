package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HoglinAi.class)
public class MixinHoglinAi {
    @Inject(at = @At("RETURN"), method = "findNearestValidAttackTarget", cancellable = true)
    private static void onHoglinShallAttack(Hoglin hoglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> info) {
        Optional<? extends LivingEntity> target = info.getReturnValue();
        if (target.isPresent() && target.orElse(null) instanceof Player player) {
            if (EnigmaticHandler.hasItem(player, EnigmaticItems.ANIMAL_GUIDEBOOK) || EnigmaticHandler.hasItem(player, EnigmaticItems.ODE_TO_LIVING)) {
                info.setReturnValue(Optional.empty());
            }
        }
    }
}
