package auviotre.enigmatic.legacy.compat.appleskin;

import auviotre.enigmatic.legacy.client.handlers.ClientEventHandler;
import auviotre.enigmatic.legacy.contents.item.food.ForbiddenFruit;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.api.event.TooltipOverlayEvent;
import squeek.appleskin.helpers.TextureHelper;

public class AppleSkinCompatHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTooltipRender(TooltipOverlayEvent.@NotNull Render event) {
        if (!ForbiddenFruit.isForbiddenCursed(Minecraft.getInstance().player)) return;
        event.setCanceled(true);
        GuiGraphics graphics = event.guiGraphics;
        int x = event.x;
        int y = event.y;
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blitSprite(ClientEventHandler.FORBIDDEN_FOOD_FULL_SPRITE, x, y, 9, 9);
        graphics.blit(TextureHelper.MOD_ICONS, x, y + 10, 0, 21.0F, 34.0F, 7, 7, 256, 256);
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(x + 7, y + 10, 0.0F);
        poseStack.scale(0.75F, 0.75F, 0.75F);
        graphics.drawString(Minecraft.getInstance().font, "x0", 2, 1, -5592406);
        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHUDRender(HUDOverlayEvent.@NotNull Saturation event) {
        if (!ForbiddenFruit.isForbiddenCursed(Minecraft.getInstance().player)) return;
        event.setCanceled(true);
    }
}
