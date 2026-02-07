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
import net.minecraft.world.entity.player.Player;

public class ClientProxy extends CommonProxy {
    public void clientInit() {
        try {
            ResourceLocation eldritchLocation = EnigmaticLegacy.location("eldritch_open");
            ClampedItemPropertyFunction taintedFunc = (stack, level, entity, i) -> ITaintable.isTainted(stack) ? 1.0F : 0.0F;
            ClampedItemPropertyFunction eldritchFunc = (stack, level, entity, i) -> stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            ItemProperties.register(EnigmaticItems.TWISTED_HEART.get(), ITaintable.LOCATION, taintedFunc);
            ItemProperties.register(EnigmaticItems.PURE_HEART.get(), ITaintable.LOCATION, taintedFunc);
            ItemProperties.register(EnigmaticItems.ABYSSAL_HEART.get(), eldritchLocation, (stack, level, entity, i) -> entity == null ? (ITaintable.isTainted(stack) ? 1.0F : 0.0F) : stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F));
            ItemProperties.register(EnigmaticItems.THE_INFINITUM.get(), eldritchLocation, eldritchFunc);
            ItemProperties.register(EnigmaticItems.ELDRITCH_AMULET.get(), eldritchLocation, eldritchFunc);
            ItemProperties.register(EnigmaticItems.DESOLATION_RING.get(), eldritchLocation, eldritchFunc);
            ItemProperties.register(EnigmaticItems.CHAOS_ELYTRA.get(), eldritchLocation, eldritchFunc);
            ItemProperties.register(EnigmaticItems.MINER_RING.get(), ResourceLocation.withDefaultNamespace("on"), (stack, level, entity, i) -> MinerRing.getPoint(stack) > 0 ? 1.0F : 0.0F);
            ItemProperties.register(EnigmaticItems.ICHOR_SPEAR.get(), ResourceLocation.withDefaultNamespace("using"), (stack, level, entity, i) -> entity != null && entity.getUseItem().equals(stack) ? 1.0F : 0.0F);
            ItemProperties.register(EnigmaticItems.DRAGON_BREATH_BOW.get(), ResourceLocation.withDefaultNamespace("pulling"), (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem().equals(stack) ? 1.0F : 0.0F);
            ItemProperties.register(EnigmaticItems.DRAGON_BREATH_BOW.get(), ResourceLocation.withDefaultNamespace("pull"), (stack, level, entity, i) -> (entity == null || entity.getUseItem() != stack) ? 0.0F : (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F);
            ItemProperties.register(EnigmaticItems.EARTH_PROMISE.get(), ResourceLocation.withDefaultNamespace("broken"), (stack, level, entity, i) -> entity instanceof Player player && player.getCooldowns().isOnCooldown(stack.getItem()) ? 1.0F : 0.0F);
            ItemProperties.register(EnigmaticItems.ETHERIUM_SWORD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(EnigmaticItems.INFERNAL_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(EnigmaticItems.ENIGMATIC_AMULET.get(), EnigmaticLegacy.location("amulet_color"), (stack, level, entity, i) -> stack.getOrDefault(EnigmaticComponents.AMULET_COLOR, 0.0F));
            EnigmaticItems.SOUL_COMPASS.get().registerVariants();
            EnigmaticItems.ENIGMATIC_EYE.get().registerVariants();
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

        PermanentDeathScreen screen = new PermanentDeathScreen(new SelectWorldScreen(new TitleScreen()), Component.translatable("gui.enigmaticlegacy.permanent_death_screen_title"), Component.translatable("message.enigmaticlegacy.permanent_death_screen"));
        PermanentDeathScreen.active = screen;
        Minecraft.getInstance().setScreen(screen);
    }

    public String getClientUsername() {
        return Minecraft.getInstance().getUser().getName();
    }
}
