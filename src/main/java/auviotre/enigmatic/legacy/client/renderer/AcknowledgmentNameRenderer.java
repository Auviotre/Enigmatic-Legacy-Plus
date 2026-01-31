package auviotre.enigmatic.legacy.client.renderer;

import auviotre.enigmatic.legacy.contents.item.books.TheAcknowledgment;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class AcknowledgmentNameRenderer {
    private final GuiBook gui;
    private final Book book;
    private final Supplier<Font> font;
    private final Component name;

    public AcknowledgmentNameRenderer(GuiBook gui, Supplier<Font> font) {
        this.gui = gui;
        this.book = gui.book;
        this.font = font;

        Component customName = EnigmaticItems.THE_ACKNOWLEDGMENT.toStack().getHoverName();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            ItemStack stack = minecraft.player.getOffhandItem();
            if (stack.isEmpty() || !(stack.getItem() instanceof TheAcknowledgment)) {
                stack = minecraft.player.getMainHandItem();
            }
            if (!stack.isEmpty() && stack.getItem() instanceof TheAcknowledgment) {
                customName = stack.getItem().getName(stack);
            }
        }
        this.name = customName;
    }

    public void drawHeader(GuiGraphics graphics) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        GuiBook.drawFromTexture(graphics, this.gui.book, -8, 12, 0, 180, 140, 31);

        int color = this.book.nameplateColor;
        graphics.drawString(this.font.get(), this.name, 13, 16, color);
        Component toDraw = this.book.getSubtitle().withStyle(this.book.getFontStyle());
        graphics.drawString(this.font.get(), toDraw, 24, 24, color);
    }
}