package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.api.event.SummonedEntityEvent;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SummonedEntityTrigger.class)
public class MixinSummonedEntityTrigger {
    @Inject(method = "trigger", at = @At("HEAD"))
    private void onTrigger(ServerPlayer player, Entity entity, CallbackInfo info) {
        NeoForge.EVENT_BUS.post(new SummonedEntityEvent(player, entity));
    }
}