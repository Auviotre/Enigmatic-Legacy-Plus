package auviotre.enigmatic.legacy.mixin.entity;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SwellGoal.class)
public abstract class MixinSwellGoal extends Goal {
    @Shadow
    @Final
    private Creeper creeper;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Creeper;setSwellDir(I)V"))
    public int setSwellDirMix(int rate) {
        if (EnigmaticHandler.isCurseBoosted(this.creeper)) {
            return rate * (1 + this.creeper.getRandom().nextInt(1));
        }
        return rate;
    }
}
