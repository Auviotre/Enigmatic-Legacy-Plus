package auviotre.enigmatic.legacy.client.screen.button;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.packets.server.EnderRingKeyPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.client.ICuriosScreen;

import javax.annotation.Nonnull;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

@OnlyIn(Dist.CLIENT)
public class EnderChestInventoryButton extends ImageButton {
    public static final WidgetSprites SPRITES = new WidgetSprites(
            EnigmaticLegacy.location("ender_chest_button"),
            EnigmaticLegacy.location("ender_chest_button_highlighted")
    );
    protected final AbstractContainerScreen<?> parentGui;
    protected boolean isRecipeBookVisible = false;

    public EnderChestInventoryButton(AbstractContainerScreen<?> container, int x, int y, int width, int height, Button.OnPress onPress) {
        super(x, y, width, height, SPRITES, onPress);
        this.parentGui = container;
    }

    public @NotNull
    static EnderChestInventoryButton getInstance(AbstractContainerScreen<?> gui, boolean isCreative) {
        EnderChestInventoryButton enderButton = new EnderChestInventoryButton(gui, 0, 0, 20, 18, (input) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                ItemStack stack = player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                if (gui instanceof InventoryScreen inventory) {
                    RecipeBookComponent recipeBookGui = inventory.getRecipeBookComponent();
                    if (recipeBookGui.isVisible()) recipeBookGui.toggleVisibility();
                }
                PacketDistributor.sendToServer(new EnderRingKeyPacket(stack));
            }
        });

        Tuple<Integer, Integer> enderOffsets = enderButton.getOffsets(isCreative);
        int x = enderOffsets.getA();
        int y = enderOffsets.getB();
        enderButton.setX(gui.getGuiLeft() + x);
        enderButton.setY(gui.getGuiTop() + y);
        return enderButton;
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
        if (EnigmaticHandler.isTheCursedOne(player) || EnigmaticHandler.hasCurio(player, EnigmaticItems.ENDER_RING)) {
            super.renderWidget(graphics, mouseX, mouseY, partialTicks);
        } else this.active = false;
    }

    public Tuple<Integer, Integer> getOffsets(boolean creative) {
        int x = creative ? 170 + CONFIG.ELSE.enderButtonOffsetXCreative.get() : 150 + CONFIG.ELSE.enderButtonOffsetX.get();
        int y = creative ? 5 + CONFIG.ELSE.enderButtonOffsetYCreative.get() : 61 + CONFIG.ELSE.enderButtonOffsetY.get();
        return new Tuple<>(x, y);
    }
}