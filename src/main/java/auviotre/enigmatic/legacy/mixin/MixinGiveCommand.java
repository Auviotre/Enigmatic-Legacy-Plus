package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(GiveCommand.class)
public abstract class MixinGiveCommand {
    @Inject(method = "giveItem", at = @At("HEAD"), cancellable = true)
    private static void mix(CommandSourceStack source, ItemInput item, Collection<ServerPlayer> targets, int count, CallbackInfoReturnable<Integer> cir) {
        if (IItemHelper.UNKNOWN_ITEMS.stream().anyMatch(itemLike -> item.getItem() == itemLike.asItem())) {
            source.sendFailure(Component.literal("Not Valid."));
            cir.setReturnValue(0);
        }
    }
}
