package auviotre.enigmatic.legacy.mixin;


import auviotre.enigmatic.legacy.api.event.EndPortalActivatedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public class MixinEnderEyeItem {
    private UseOnContext lastContext;

    @Inject(method = "useOn", at = @At("HEAD"))
    private void onUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        this.lastContext = context;
    }

    @Redirect(method = "useOn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;globalLevelEvent(ILnet/minecraft/core/BlockPos;I)V"))
    private void globalEvent(Level level, int id, BlockPos pos, int data) {
        if (this.lastContext != null) {
            NeoForge.EVENT_BUS.post(new EndPortalActivatedEvent(this.lastContext.getPlayer(), pos));
        }

        level.globalLevelEvent(id, pos, data);
    }
}
