package auviotre.enigmatic.legacy.mixin.client;

import auviotre.enigmatic.legacy.contents.item.charms.Insignia;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {
    @Shadow
    @Final
    protected EntityRenderDispatcher entityRenderDispatcher;

    @Shadow
    public abstract Font getFont();

    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    private void onRenderNameTag(T entity, Component displayName, PoseStack stack, MultiBufferSource buffer, int packedLight, float partialTick, CallbackInfo info) {
        if (entity instanceof Player player) {
            ItemStack insignia = EnigmaticHandler.getCurio(player, EnigmaticItems.INSIGNIA);
            if (!insignia.isEmpty()) {
                Optional<Component> customName = EnigmaticItems.INSIGNIA.get().getCustomName(insignia);
                if (customName.isPresent()) {
                    info.cancel();
                    Insignia.renderInsigniaNameplate(entity, customName.get(), stack, buffer, packedLight, this.entityRenderDispatcher, partialTick, this.getFont());
                }
            }
        }
    }
}
