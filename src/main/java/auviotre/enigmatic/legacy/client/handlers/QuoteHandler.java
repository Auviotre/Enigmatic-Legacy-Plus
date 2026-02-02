package auviotre.enigmatic.legacy.client.handlers;

import auviotre.enigmatic.legacy.client.Quote;
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
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

import static auviotre.enigmatic.legacy.contents.item.charms.EnigmaticEye.quoteSubtitles;

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

//    @SubscribeEvent
//    public void onPlayerTick(PlayerTickEvent event) {
//        if (event.getEntity() == Minecraft.getInstance().player) {
//            if (this.delayTicks > 0 && !(Minecraft.getInstance().screen instanceof LevelLoadingScreen)
//                    && !(Minecraft.getInstance().screen instanceof ReceivingLevelScreen)) {
//                this.delayTicks--;
//
//                if (this.delayTicks == 0) {
//                    SimpleSoundInstance instance = new SimpleSoundInstance(this.currentQuote.getSound().getLocation(),
//                            SoundSource.VOICE, 0.7F, 1, RANDOM, false, 0, SoundInstance.Attenuation.NONE, 0, 0, 0, true);
//
//                    Minecraft.getInstance().getSoundManager().play(instance);
//
//                    this.startedPlaying = System.currentTimeMillis();
//                }
//            }
//        }
//    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (delayTicks <= 0 || currentQuote == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        delayTicks--;

        if (delayTicks == 0) {
            playSound();
            startedPlaying = System.currentTimeMillis();
        }
    }

    private void playSound() {
        Minecraft mc = Minecraft.getInstance();

        SimpleSoundInstance instance = SimpleSoundInstance.forUI(
                currentQuote.getSound(),
                0.7F,
                1.0F
        );

        mc.getSoundManager().play(instance);
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
        Window window = Minecraft.getInstance().getWindow();

        double timeLeft = currentQuote.getSubtitles().getDuration() - getPlayTime();
        if (timeLeft <= 0.1) {
            currentQuote = null;
            startedPlaying = delayTicks = -1;
            return;
        }

        if (getPlayTime() < 0.05 || !quoteSubtitles.getAsBoolean())
            return;

        Font font = Minecraft.getInstance().font;
        Component textComponent =
                Component.literal(currentQuote.getSubtitles().getLine(getPlayTime()));

        List<FormattedCharSequence> lines = font.split(textComponent, 260);

        int alpha = 255;
        if (getPlayTime() < 0.5)
            alpha = (int) (255 * (getPlayTime() / 0.5));
        else if (timeLeft < 0.5)
            alpha = (int) (255 * (timeLeft / 0.5));

        alpha = Mth.clamp(alpha, 0, 255);

        int maxWidth = lines.stream().mapToInt(font::width).max().orElse(0);

        int x = window.getGuiScaledWidth() / 2 - maxWidth / 2;
        int y = window.getGuiScaledHeight() - 70 - (lines.size() * (font.lineHeight + 2));

        int bgColor = (alpha << 24); // black color with alpha
        int textColor = ChatFormatting.YELLOW.getColor() | (alpha << 24);

        // Background
        graphics.fill(
                x - 8, y - 6,
                x + maxWidth + 8, y + lines.size() * (font.lineHeight + 2) + 6,
                bgColor
        );

        // Text
        int lineY = y;
        for (FormattedCharSequence line : lines) {
            graphics.drawString(
                    font,
                    line,
                    window.getGuiScaledWidth() / 2 - font.width(line) / 2,
                    lineY,
                    textColor,
                    true
            );
            lineY += font.lineHeight + 2;
        }
    }
}
