package auviotre.enigmatic.legacy.client.handlers;

import auviotre.enigmatic.legacy.client.Quote;
import auviotre.enigmatic.legacy.client.Subtitles;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class QuoteHandler {
    public static final QuoteHandler INSTANCE = new QuoteHandler();
    private static final RandomSource RANDOM = RandomSource.create();
    private Quote currentQuote = null;
    private long startedPlaying = -1;
    private int delayTicks = -1;

    private QuoteHandler() {}

    private double getPlayTime() {
        long millis = System.currentTimeMillis() - this.startedPlaying;
        return ((double)millis) / 1000;
    }

    public void playQuote(Quote quote, int delayTicks) {
        if (this.currentQuote == null) {
            this.currentQuote = quote;
            this.delayTicks = delayTicks;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.@NotNull Pre event) {
        if (currentQuote == null || delayTicks <= 0) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) return;
        if (mc.screen instanceof LevelLoadingScreen || mc.screen instanceof ReceivingLevelScreen) return;

        this.delayTicks--;

        if (this.delayTicks == 0) {
            SimpleSoundInstance instance = new SimpleSoundInstance(
                    this.currentQuote.getSound().getLocation(),
                    SoundSource.VOICE, 0.7F, 1, RANDOM, false, 0, SoundInstance.Attenuation.NONE, 0, 0, 0, true
            );

            mc.getSoundManager().play(instance);

            this.startedPlaying = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGuiEvent.Post event) {
        if (Minecraft.getInstance().screen != null || currentQuote == null || delayTicks > 0)
            return;

        drawQuote(event.getGuiGraphics());
    }

    @SubscribeEvent
    public void onScreenRender(ScreenEvent.Render.Post event) {
        if (currentQuote != null && delayTicks <= 0) {
            drawQuote(event.getGuiGraphics());
            Minecraft.getInstance().getSoundManager().resume();
        }
    }

    private void drawQuote(GuiGraphics graphics) {
        if (currentQuote == null) return;

        Window window = Minecraft.getInstance().getWindow();
        Subtitles subtitles = currentQuote.getSubtitles();
        Font font = Minecraft.getInstance().font;

        double playTime = getPlayTime();
        double timeLeft = subtitles.getDuration() - playTime;

        if (timeLeft <= 0.05) {
            currentQuote = null;
            startedPlaying = delayTicks = -1;
            return;
        }

        // Fade-in and Fade-out alpha
        int alpha;
        if (playTime < 0.5)
            alpha = (int) (255 * (playTime / 0.5));
        else if (timeLeft < 0.5)
            alpha = (int) (255 * (timeLeft / 0.5));
        else
            alpha = 255;
        alpha = Mth.clamp(alpha, 0, 255);

        // Lines
        Component textComponent = Component.literal(subtitles.getLine(playTime));
        List<FormattedCharSequence> lines = font.split(textComponent, 260);

        // Textbox size
        int maxWidth = lines.stream().mapToInt(font::width).max().orElse(0);
        int x = window.getGuiScaledWidth() / 2 - maxWidth / 2;
        int y = window.getGuiScaledHeight() - 70 - lines.size() * (font.lineHeight + 2);

        // Background
        for (int layer = 0; layer < 3; layer++) {
            int offset = 4 + layer * 4;
            float edgeFactor = 0.8f - (layer / 3.0f);

//            // Layers alpha
//            if (layer == 0) edgeFactor = 0.6f;
//            if (layer == 1) edgeFactor = 0.4f;
//            if (layer == 2) edgeFactor = 0.2f;


            int layerAlpha = (int) (alpha * edgeFactor);
            layerAlpha = Mth.clamp(layerAlpha, 0, 255);
            int bgColor = (layerAlpha << 24);

            graphics.fill(
                    x - offset, y - offset,
                    x + maxWidth + offset, y + lines.size() * (font.lineHeight + 2) + offset,
                    bgColor
            );
        }

        // Draw text
        int lineY = y;
        int textColor = ChatFormatting.YELLOW.getColor() | (alpha << 24);
        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, window.getGuiScaledWidth() / 2 - font.width(line) / 2, lineY, textColor, true);
            lineY += font.lineHeight + 2;
        }
    }
}
