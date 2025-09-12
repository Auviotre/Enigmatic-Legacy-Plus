package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.client.renderer.AcknowledgmentNameRenderer;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookLanding;

@Mixin(GuiBookLanding.class)
public abstract class MixinGuiBookLanding extends GuiBook {
    @Unique
    AcknowledgmentNameRenderer EL$renderer;

    public MixinGuiBookLanding() {
        super(null, null);
        throw new IllegalStateException("Can't touch this");
    }

    @Inject(method = "init", at = @At("RETURN"), require = 1)
    private void onInit(CallbackInfo info) {
        if (EnigmaticLegacy.MODID.equals(this.book.id.getNamespace())) {
            this.EL$renderer = new AcknowledgmentNameRenderer(this, () -> this.font);
        }
    }

    @Inject(method = "drawHeader", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void onDrawHeader(GuiGraphics graphics, CallbackInfo info) {
        if (EnigmaticLegacy.MODID.equals(this.book.id.getNamespace())) {
            this.EL$renderer.drawHeader(graphics);
            info.cancel();
        }
    }
}
