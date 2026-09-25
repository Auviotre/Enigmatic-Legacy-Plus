package auviotre.enigmatic.legacy.mixin.entity.dragon;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractDragonPhaseInstance.class)
public abstract class MixinAbstractPhase {
    @Shadow
    @Final
    protected EnderDragon dragon;

    @Inject(method = "getFlySpeed", at = @At("RETURN"), cancellable = true)
    public void getFlySpeed(CallbackInfoReturnable<Float> info) {
        if (EnigmaticHandler.isCurseBoosted(this.dragon))
            info.setReturnValue(info.getReturnValue() * 2.0F);
    }
}
