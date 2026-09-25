package auviotre.enigmatic.legacy.mixin.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.client.handlers.ClientEventHandler;
import auviotre.enigmatic.legacy.contents.item.materials.AbyssalHeart;
import auviotre.enigmatic.legacy.mixin.accessor.PostChainAccessor;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.IOException;

@OnlyIn(Dist.CLIENT)
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    @Final
    Minecraft minecraft;
    @Shadow
    @Final
    private Camera mainCamera;
    @Unique
    private static final ResourceLocation DARKEN_LOCATION = EnigmaticLegacy.location("shaders/post/darken.json");
    @Unique
    private @Nullable PostChain darkenEffect;

    @Inject(method = "resize", at = @At("RETURN"))
    public void resize(int width, int height, CallbackInfo info) {
        if (this.darkenEffect != null) this.darkenEffect.resize(width, height);
    }

    @Inject(method = "close", at = @At("RETURN"))
    public void close(CallbackInfo info) {
        if (this.darkenEffect != null) this.darkenEffect.close();
    }

    @Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;loadBlurEffect(Lnet/minecraft/server/packs/resources/ResourceProvider;)V"))
    public void reload(ResourceProvider resourceProvider, CallbackInfo info) {
        if (this.darkenEffect != null) this.darkenEffect.close();

        try {
            this.darkenEffect = new PostChain(this.minecraft.getTextureManager(), resourceProvider, this.minecraft.getMainRenderTarget(), DARKEN_LOCATION);
            this.darkenEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
        } catch (IOException ioException) {
            EnigmaticLegacy.LOGGER.warn("Failed to load shader: {}", DARKEN_LOCATION, ioException);
        } catch (JsonSyntaxException jsonSyntaxException) {
            EnigmaticLegacy.LOGGER.warn("Failed to parse shader: {}", DARKEN_LOCATION, jsonSyntaxException);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V"))
    public void renderMix(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo info) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.player == null || instance.level == null) return;

        float partialTick = instance.getTimer().getGameTimeDeltaPartialTick(true);
        float mix = Math.clamp(Mth.lerp(partialTick, ClientEventHandler.darkenLastTick, ClientEventHandler.darkenTick) / 800.0F, 0.0F, 1.0F);
        if (mix > 0 && this.darkenEffect != null && this.mainCamera.getEntity() instanceof LivingEntity && AbyssalHeart.specialVisualEffects != null && AbyssalHeart.specialVisualEffects.get()) {
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            float[] gray = {0.299f, 0.587f, 0.114f};
            for (PostPass postpass : ((PostChainAccessor) this.darkenEffect).getPasses()) {
                postpass.getEffect().safeGetUniform("Gray").set(0.3F * (1 - mix), 0.59F * (1 - mix), 0.11F * (1 - mix));
                mix = Mth.sqrt(mix);
                float[] rMatrix = uniform(Mth.lerp(mix, 1, gray[0]), gray[1] * mix, gray[2] * mix);
                float[] gMatrix = uniform(gray[0] * mix, Mth.lerp(mix, 1, gray[1]), gray[2] * mix);
                float[] bMatrix = uniform(gray[0] * mix, gray[1] * mix, Mth.lerp(mix, 1, gray[2]));
                postpass.getEffect().safeGetUniform("RedMatrix").set(rMatrix[0], rMatrix[1], rMatrix[2]);
                postpass.getEffect().safeGetUniform("GreenMatrix").set(gMatrix[0], gMatrix[1], gMatrix[2]);
                postpass.getEffect().safeGetUniform("BlueMatrix").set(bMatrix[0], bMatrix[1], bMatrix[2]);
            }
            this.darkenEffect.setUniform("Saturation", 1.0F - mix * 0.36F);
            this.darkenEffect.process(deltaTracker.getGameTimeDeltaTicks());
        }
    }

    private static float[] uniform(float r, float g, float b) {
        float v = r + g + b;
        return new float[]{r / v, g / v, b / v};
    }
}
