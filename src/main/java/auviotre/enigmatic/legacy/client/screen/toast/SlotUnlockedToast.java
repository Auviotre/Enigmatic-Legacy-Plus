package auviotre.enigmatic.legacy.client.screen.toast;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlotUnlockedToast implements Toast {
    private static final ResourceLocation BACKGROUND = EnigmaticLegacy.location("toast/slot_unlock");
    private static final Component DESCRIPTION_TEXT = Component.translatable("toast.enigmaticlegacy.slot_unlock.description");
    private final ItemStack drawnStack;
    private final String identifier;

    public SlotUnlockedToast(ItemStack stack, String id) {
        this.drawnStack = stack;
        this.identifier = id;
    }

    public Visibility render(GuiGraphics graphics, ToastComponent toast, long timeLastVisible) {
        if (drawnStack.isEmpty()) return Visibility.HIDE;
        Font font = toast.getMinecraft().font;
        Component slot_id = Component.translatable("curios.identifier." + this.identifier);
        graphics.blitSprite(BACKGROUND, 0, 0, this.width(), this.height());
        graphics.drawString(font, Component.translatable("toast.enigmaticlegacy.slot_unlock.title", slot_id), 30, 7, -11534256, false);
        graphics.drawString(font, DESCRIPTION_TEXT, 30, 18, -16777216, false);
        graphics.renderFakeItem(this.drawnStack, 8, 8);
        return timeLastVisible >= 5000.0 * toast.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }
}