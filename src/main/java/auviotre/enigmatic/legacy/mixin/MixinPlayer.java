package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.contents.item.tools.InfernalShield;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        throw new IllegalStateException("Can't touch this");
    }

    @Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
    public void disableShieldMix(CallbackInfo ci) {
        if (this.getUseItem().getItem() instanceof InfernalShield) ci.cancel();
    }
}
