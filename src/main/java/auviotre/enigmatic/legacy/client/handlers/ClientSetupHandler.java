package auviotre.enigmatic.legacy.client.handlers;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.client.renderer.DimensionalAnchorRender;
import auviotre.enigmatic.legacy.client.renderer.PermanentItemRenderer;
import auviotre.enigmatic.legacy.client.renderer.layer.EtheriumShieldLayer;
import auviotre.enigmatic.legacy.client.renderer.layer.MajesticElytraLayer;
import auviotre.enigmatic.legacy.client.screen.LoreInscriberScreen;
import auviotre.enigmatic.legacy.contents.gui.button.EnderChestInventoryButton;
import auviotre.enigmatic.legacy.contents.gui.button.MagnetRingInventoryButton;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.contents.item.rings.EnderRing;
import auviotre.enigmatic.legacy.registries.EnigmaticBlockEntities;
import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import auviotre.enigmatic.legacy.registries.EnigmaticMenus;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.client.ICuriosScreen;

@Mod(value = EnigmaticLegacy.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EnigmaticLegacy.MODID, value = Dist.CLIENT)
public class ClientSetupHandler {
    @SubscribeEvent(priority = EventPriority.LOW)
    private static void onInventoryGuiInit(ScreenEvent.Init.@NotNull Post event) {
        Screen screen = event.getScreen();
        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen || screen instanceof ICuriosScreen) {
            AbstractContainerScreen<?> gui = (AbstractContainerScreen<?>) screen;
            boolean isCreative = screen instanceof CreativeModeInventoryScreen;
            event.addListener(MagnetRingInventoryButton.getInstance(gui, isCreative));
            event.addListener(EnderChestInventoryButton.getInstance(gui, isCreative));
        }
    }

    @SubscribeEvent
    private static void registerScreens(@NotNull RegisterMenuScreensEvent event) {
        event.register(EnigmaticMenus.LORE_INSCRIBER_MENU.get(), LoreInscriberScreen::new);
    }

    @SubscribeEvent
    private static void registerScreens(@NotNull RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(EnigmaticParticles.SPELL.get(), SpellParticle.MobEffectProvider::new);
    }

    @SubscribeEvent
    private static void registerEntityRenderers(EntityRenderersEvent.@NotNull RegisterRenderers event) {
        event.registerEntityRenderer(EnigmaticEntities.PERMANENT_ITEM_ENTITY.get(), PermanentItemRenderer::new);
        event.registerBlockEntityRenderer(EnigmaticBlockEntities.DIMENSIONAL_ANCHOR_ENTITY.get(), DimensionalAnchorRender::new);
    }

    @SubscribeEvent
    private static void registerKeybindings(@NotNull RegisterKeyMappingsEvent event) {
        event.register(ISpellstone.KEY_MAPPING.get());
        event.register(BaseCurioItem.KEY_MAPPING.get());
        event.register(EnderRing.KEY_MAPPING.get());
    }

    @SubscribeEvent
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addLayers(EntityRenderersEvent.AddLayers event) {
        addPlayerLayer(event, PlayerSkin.Model.WIDE);
        addPlayerLayer(event, PlayerSkin.Model.SLIM);
        if (event.getRenderer(EntityType.ARMOR_STAND) instanceof LivingEntityRenderer renderer) {
            renderer.addLayer(new MajesticElytraLayer<>(renderer, event.getEntityModels()));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addPlayerLayer(EntityRenderersEvent.AddLayers event, PlayerSkin.Model skin) {
        if (event.getSkin(skin) instanceof LivingEntityRenderer renderer) {
            renderer.addLayer(new MajesticElytraLayer<>(renderer, event.getEntityModels()));
            renderer.addLayer(new EtheriumShieldLayer(renderer, event.getEntityModels()));
        }
    }
}