package auviotre.enigmatic.legacy.proxy;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.client.screen.PermanentDeathScreen;
import auviotre.enigmatic.legacy.contents.item.rings.MinerRing;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClientProxy extends CommonProxy {
    public void clientInit() {
        try {
            ResourceLocation ELDRITCH_LOCATION = EnigmaticLegacy.location("eldritch_open");
            ClampedItemPropertyFunction taintedFunc = (stack, level, entity, i) -> ITaintable.isTainted(stack) ? 1.0F : 0.0F;
            ClampedItemPropertyFunction eldritchFunc = (stack, level, entity, i) -> entity == null ? 0.0F : stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            ItemProperties.register(EnigmaticItems.TWISTED_HEART.get(), ITaintable.LOCATION, taintedFunc);
            ItemProperties.register(EnigmaticItems.ABYSSAL_HEART.get(), ELDRITCH_LOCATION, (stack, level, entity, i) -> entity == null ? (ITaintable.isTainted(stack) ? 1.0F : 0.0F) : stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F));
            ItemProperties.register(EnigmaticItems.THE_INFINITUM.get(), ELDRITCH_LOCATION, eldritchFunc);
            ItemProperties.register(EnigmaticItems.ELDRITCH_AMULET.get(), ELDRITCH_LOCATION, eldritchFunc);
            ItemProperties.register(EnigmaticItems.DESOLATION_RING.get(), ELDRITCH_LOCATION, eldritchFunc);
            ItemProperties.register(EnigmaticItems.MINER_RING.get(), ResourceLocation.withDefaultNamespace("on"), (stack, level, entity, i) -> MinerRing.getPoint(stack) > 0 ? 1.0F : 0.0F);
            ItemProperties.register(EnigmaticItems.INFERNAL_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(EnigmaticItems.ENIGMATIC_AMULET.get(), EnigmaticLegacy.location("amulet_color"), (stack, level, entity, i) -> stack.getOrDefault(EnigmaticComponents.AMULET_COLOR, 0.0F));
        } catch (Exception exception) {
            EnigmaticLegacy.LOGGER.warn("Could not load item models.");
        }
    }

    public void displayPermanentDeathScreen() {
        if (Minecraft.getInstance().level != null) {
            boolean local = Minecraft.getInstance().isLocalServer();
            Minecraft.getInstance().level.disconnect();

            if (local) {
                Minecraft.getInstance().clearClientLevel(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
            } else {
                Minecraft.getInstance().clearClientLevel(new ProgressScreen(true));
            }
        }

        PermanentDeathScreen screen = new PermanentDeathScreen(new SelectWorldScreen(new TitleScreen()), Component.translatable("gui.enigmaticlegacy.permanent_death_screen_title"),
                Component.translatable("message.enigmaticlegacy.permanent_death_screen"));
        PermanentDeathScreen.active = screen;
        Minecraft.getInstance().setScreen(screen);
    }

    public String getClientUsername() {
        return Minecraft.getInstance().getUser().getName();
    }
}
