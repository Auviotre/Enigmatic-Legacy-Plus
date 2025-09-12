package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MixinMob implements ILivingEntityExtension {
    @Inject(method = "checkAndHandleImportantInteractions", at = @At("HEAD"), cancellable = true)
    public void checkMix(@NotNull Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(EnigmaticItems.EXTRADIMENSIONAL_EYE)) {
            InteractionResult result = stack.interactLivingEntity(player, this.self(), hand);
            if (result.consumesAction()) info.setReturnValue(result);
        }
    }
}
