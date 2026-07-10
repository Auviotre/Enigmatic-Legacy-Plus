package auviotre.enigmatic.legacy.compat.thirst;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.potions.ForbiddenJuice;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.ghen.thirst.content.thirst.PlayerThirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import dev.ghen.thirst.foundation.common.event.RegisterThirstValueEvent;
import dev.ghen.thirst.foundation.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

public class ThirstCompatHandler {
    public static final ResourceLocation FORBIDDEN_DRINK_FULL_SPRITE = EnigmaticLegacy.location("hud/forbidden_drink_full");
    public static final ResourceLocation FORBIDDEN_DRINK_HALF_SPRITE = EnigmaticLegacy.location("hud/forbidden_drink_half");
    public static final ResourceLocation FORBIDDEN_DRINK_EMPTY_SPRITE = EnigmaticLegacy.location("hud/forbidden_drink_empty");

    public static void setHellBladeRecover(Player player) {
        PlayerThirst data = player.getData(ModAttachment.PLAYER_THIRST);
        data.setQuenched(data.getThirst());
    }

    @SubscribeEvent
    public void onRegisterThirst(@NotNull RegisterThirstValueEvent event) {
        event.addFood(EnigmaticItems.ICHOROOT.get(), 2, 3);
        event.addFood(EnigmaticItems.ASTRAL_FRUIT.get(), 3, 8);
        event.addFood(EnigmaticItems.FORBIDDEN_FRUIT.get(), 2, 1);
        event.addFood(EnigmaticItems.ENCHANTED_ASTRAL_FRUIT.get(), 3, 8);
        event.addDrink(EnigmaticItems.RECALL_POTION.get(), 6, 8);
        event.addDrink(EnigmaticItems.WORMHOLE_POTION.get(), 6, 8);
        event.addDrink(EnigmaticItems.ICHOR_BOTTLE.get(), 6, 10);
        event.addDrink(EnigmaticItems.ENCHANTED_ICHOR_BOTTLE.get(), 6, 10);
        event.addDrink(EnigmaticItems.REDEMPTION_POTION.get(), 6, 10);
        event.addDrink(EnigmaticItems.UNHOLY_GRAIL.get(), 1, 2);
        event.addDrink(EnigmaticItems.TWISTED_POTION.get(), 5, 3);
        event.addDrink(EnigmaticItems.ICHOR_CURSE_BOTTLE.get(), 4, 3);
        event.addDrink(EnigmaticItems.FORBIDDEN_JUICE.get(), 2, 2);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.@NotNull Pre event) {
        lockThirstData(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerTicked(PlayerTickEvent.@NotNull Post event) {
        lockThirstData(event.getEntity());
    }

    private void lockThirstData(@NotNull Player player) {
        PlayerThirst data = player.getData(ModAttachment.PLAYER_THIRST);
        if (ForbiddenJuice.isForbiddenCursed(player)) {
            data.setThirst(20);
            data.setQuenched(0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderBar(int width, int height, GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        minecraft.getProfiler().push("thirst");
        RenderSystem.enableBlend();
        int left = width / 2 + 91 + ClientConfig.THIRST_BAR_X_OFFSET.get();
        int top = height - minecraft.gui.rightHeight + ClientConfig.THIRST_BAR_Y_OFFSET.get();
        minecraft.gui.rightHeight += 10;
        int level = minecraft.player.getData(ModAttachment.PLAYER_THIRST).getThirst();

        for(int i = 0; i < 10; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            if (minecraft.gui.getGuiTicks() % (level * 3 + 1) == 0) {
                y = top + (minecraft.player.getRandom().nextInt(3) - 1);
            }
            guiGraphics.blitSprite(FORBIDDEN_DRINK_EMPTY_SPRITE, x, y, 9, 9);
            if (idx < level) {
                guiGraphics.blitSprite(FORBIDDEN_DRINK_FULL_SPRITE, x, y, 9, 9);
            } else if (idx == level) {
                guiGraphics.blitSprite(FORBIDDEN_DRINK_HALF_SPRITE, x, y, 9, 9);
            }
        }
        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }
}
