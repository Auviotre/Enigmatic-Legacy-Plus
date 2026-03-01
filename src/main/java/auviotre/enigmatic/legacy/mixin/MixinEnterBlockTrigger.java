package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.api.event.EnterBlockEvent;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnterBlockTrigger.class)
public class MixinEnterBlockTrigger {
    @Inject(method = "trigger", at = @At("HEAD"))
    private void onTrigger(ServerPlayer player, BlockState state, CallbackInfo info) {
        NeoForge.EVENT_BUS.post(new EnterBlockEvent(player, state));
    }
}