package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public class MixinWorldListEntry {

    @Shadow
    @Final
    LevelSummary summary;
    @Unique
    private Component EL$cachedInfo;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelSummary;getInfo()Lnet/minecraft/network/chat/Component;"))
    private Component getAltInfo(LevelSummary summary) {
        if (this.EL$cachedInfo == null) {
            this.EL$cachedInfo = EnigmaticHandler.getModdedInfo(summary);
        }
        return this.EL$cachedInfo;
    }

    @Inject(method = "joinWorld", at = @At("HEAD"), cancellable = true)
    private void onJoinWorld(CallbackInfo info) {
        if (EnigmaticHandler.isWorldFractured(EnigmaticHandler.getSaveFolder(this.summary))) {
            EnigmaticLegacy.PROXY.displayPermanentDeathScreen();
            info.cancel();
        }
    }
}
