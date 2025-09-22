package auviotre.enigmatic.legacy.client.screen;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.gui.LoreInscriberMenu;
import auviotre.enigmatic.legacy.packets.server.LoreInscriberRenamePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class LoreInscriberScreen extends AbstractContainerScreen<LoreInscriberMenu> implements ContainerListener {
    private static final ResourceLocation TEXTURE = EnigmaticLegacy.location("textures/gui/lore_inscriber.png");
    private final ResourceLocation texture;
    private EditBox name;

    public LoreInscriberScreen(LoreInscriberMenu container, Inventory Inventory, Component title) {
        this(container, Inventory, title, LoreInscriberScreen.TEXTURE);
        this.titleLabelX = 60;
    }

    private LoreInscriberScreen(LoreInscriberMenu container, Inventory Inventory, Component title, ResourceLocation location) {
        super(container, Inventory, title);
        this.texture = location;
    }

    protected void subInit() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, i + 55, j + 30, 95, 12, Component.translatable("container.repair"));
        this.name.setCanLoseFocus(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(128);
        this.name.setResponder(this::onNameChanged);
        this.addWidget(this.name);
        this.setInitialFocus(this.name);
    }

    protected void init() {
        super.init();
        this.subInit();
        this.menu.addSlotListener(this);
    }

    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this);
    }

    public void render(GuiGraphics graphics, int x, int y, float partialTick) {
        this.renderBackground(graphics, x, y, partialTick);
        super.render(graphics, x, y, partialTick);
        RenderSystem.disableBlend();
        this.renderNameField(graphics, x, y, partialTick);
        this.renderTooltip(graphics, x, y);
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.texture);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(this.texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
        graphics.blit(this.texture, i + 52, j + 26, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 102, 16);
        if (this.menu.getSlot(0).hasItem() && !this.menu.getSlot(1).hasItem()) {
            graphics.blit(this.texture, i + 71, j + 49, this.imageWidth, 0, 28, 21);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) this.minecraft.player.closeContainer();
        return this.name.keyPressed(keyCode, scanCode, modifiers) || this.name.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.name.getValue();
        this.init(minecraft, width, height);
        this.name.setValue(s);
    }

    protected void renderLabels(GuiGraphics graphics, int x, int y) {
        graphics.drawString(this.font, this.title, 52, 13, 4210752);
        RenderSystem.disableBlend();
    }

    public void renderNameField(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.name.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void onNameChanged(String input) {
        if (!input.isEmpty()) {
            Slot slot = this.menu.getSlot(0);
            if (slot.hasItem()) {
                String s = input;
                if (!slot.getItem().has(DataComponents.CUSTOM_NAME) && input.equals(slot.getItem().getHoverName().getString())) {
                    s = "";
                }

                if (this.menu.setItemName(s)) {
                    PacketDistributor.sendToServer(new LoreInscriberRenamePacket(s));
                }
            }
        }
    }

    public void slotChanged(AbstractContainerMenu menu, int id, ItemStack stack) {
        if (id == 0) {
            this.name.setValue(stack.isEmpty() ? "" : stack.getHoverName().getString());
            this.name.setEditable(!stack.isEmpty());
            this.setFocused(this.name);
        }
    }

    public void dataChanged(AbstractContainerMenu menu, int i, int i1) {
        // NO-OP
    }
}
