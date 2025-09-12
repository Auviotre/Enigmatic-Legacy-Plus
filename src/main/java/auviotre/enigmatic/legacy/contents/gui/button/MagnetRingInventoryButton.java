package auviotre.enigmatic.legacy.contents.gui.button;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.packets.toServer.ToggleMagnetEffectKeyPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.client.ICuriosScreen;

import javax.annotation.Nonnull;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class MagnetRingInventoryButton extends ImageButton {
    public static final WidgetSprites SPRITES = new WidgetSprites(
            EnigmaticLegacy.location("magnet_button"),
            EnigmaticLegacy.location("magnet_button_off"),
            EnigmaticLegacy.location("magnet_button_highlighted"),
            EnigmaticLegacy.location("magnet_button_off_highlighted")
    );
    protected final AbstractContainerScreen<?> parentGui;
    protected boolean isRecipeBookVisible = false;

    public MagnetRingInventoryButton(AbstractContainerScreen<?> container, int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, SPRITES, onPress);
        this.parentGui = container;
    }

    public @NotNull
    static MagnetRingInventoryButton getInstance(AbstractContainerScreen<?> gui, boolean isCreative) {
        MagnetRingInventoryButton magnetButton = new MagnetRingInventoryButton(gui, 0, 0, 20, 18, (input) -> {
            if (Minecraft.getInstance().player != null) {
                PacketDistributor.sendToServer(new ToggleMagnetEffectKeyPacket());
            }
        });

        Tuple<Integer, Integer> enderOffsets = magnetButton.getOffsets(isCreative);
        int x = enderOffsets.getA();
        int y = enderOffsets.getB();
        magnetButton.setX(gui.getGuiLeft() + x);
        magnetButton.setY(gui.getGuiTop() + y);
        return magnetButton;
    }

    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.active = true;
        if (this.parentGui instanceof InventoryScreen || this.parentGui instanceof ICuriosScreen) {
            boolean lastVisible = this.isRecipeBookVisible;
            this.isRecipeBookVisible = ((RecipeUpdateListener) this.parentGui).getRecipeBookComponent().isVisible();
            if (lastVisible != this.isRecipeBookVisible) {
                Tuple<Integer, Integer> offsets = this.getOffsets(false);
                this.setPosition(this.parentGui.getGuiLeft() + offsets.getA(), this.parentGui.getGuiTop() + offsets.getB());
            }
        } else if (this.parentGui instanceof CreativeModeInventoryScreen creative) {
            if (!creative.isInventoryOpen()) {
                this.active = false;
                return;
            }
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            if (EnigmaticHandler.hasCurio(player, EnigmaticItems.MAGNET_RING) || EnigmaticHandler.hasCurio(player, EnigmaticItems.DISLOCATION_RING)) {
                ResourceLocation location = SPRITES.get(data.isMagnetRingEnable(), isHoveredOrFocused());
                graphics.blitSprite(location, this.getX(), this.getY(), this.width, this.height);
            } else this.active = false;
        } else this.active = false;
    }

    public Tuple<Integer, Integer> getOffsets(boolean creative) {
        int x = creative ? 147 + CONFIG.ELSE.magnetButtonOffsetXCreative.get() : 127 + CONFIG.ELSE.magnetButtonOffsetX.get();
        int y = creative ? 5 + CONFIG.ELSE.magnetButtonOffsetYCreative.get() : 61 + CONFIG.ELSE.magnetButtonOffsetY.get();
        return new Tuple<>(x, y);
    }
}