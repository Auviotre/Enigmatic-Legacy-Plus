package auviotre.enigmatic.legacy.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PermanentDeathScreen extends Screen {
    public static PermanentDeathScreen active = null;
    private final MutableComponent reason;
    private final Screen parent;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private int textHeight;

    public PermanentDeathScreen(Screen parent, Component title, MutableComponent reason) {
        super(title);
        this.parent = parent;
        this.reason = reason;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
        this.clearWidgets();
        this.message = MultiLineLabel.create(this.font, this.reason.withStyle(ChatFormatting.WHITE), this.width - 50);
        this.textHeight = this.message.getLineCount() * 9;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.toWorld"), button -> {
                    active = null;
                    this.minecraft.setScreen(this.parent);
                }).bounds(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + 9, this.height - 30), 200, 20)
                .build());
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
        this.message.renderCentered(guiGraphics, this.width / 2, this.height / 2 - this.textHeight / 2);
    }
}
