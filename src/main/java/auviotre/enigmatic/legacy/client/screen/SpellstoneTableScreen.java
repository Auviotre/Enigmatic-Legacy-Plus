package auviotre.enigmatic.legacy.client.screen;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.gui.SpellstoneTableMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SpellstoneTableScreen extends AbstractContainerScreen<SpellstoneTableMenu> {
    private static final ResourceLocation TEXTURE = EnigmaticLegacy.location("textures/gui/spellstone_table.png");

    public SpellstoneTableScreen(SpellstoneTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    public void render(GuiGraphics graphics, int x, int y, float partialTick) {
        super.render(graphics, x, y, partialTick);
        RenderSystem.disableBlend();
        this.renderTooltip(graphics, x, y);
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.hasOutput()) {
            graphics.blit(TEXTURE, i + 85, j + 53, 176, 0, 6, 6);

        }
    }
}
