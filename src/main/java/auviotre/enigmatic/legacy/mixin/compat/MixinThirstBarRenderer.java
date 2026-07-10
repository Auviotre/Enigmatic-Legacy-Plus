package auviotre.enigmatic.legacy.mixin.compat;

import auviotre.enigmatic.legacy.compat.thirst.ThirstCompatHandler;
import auviotre.enigmatic.legacy.contents.item.potions.ForbiddenJuice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.ghen.thirst.foundation.gui.ThirstBarRenderer")
public class MixinThirstBarRenderer {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void renderMix(int width, int height, GuiGraphics guiGraphics, CallbackInfo info) {
        if (ForbiddenJuice.isForbiddenCursed(Minecraft.getInstance().player)) {
            ThirstCompatHandler.renderBar(width, height, guiGraphics);
            info.cancel();
        }
    }
}
