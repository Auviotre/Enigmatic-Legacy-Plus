package auviotre.enigmatic.legacy.client.handlers;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.client.KeyHandler;
import auviotre.enigmatic.legacy.client.particles.IchorParticle;
import auviotre.enigmatic.legacy.client.particles.StarDustParticle;
import auviotre.enigmatic.legacy.client.renderer.*;
import auviotre.enigmatic.legacy.client.renderer.layer.EnigmaticElytraLayer;
import auviotre.enigmatic.legacy.client.renderer.layer.EtheriumShieldLayer;
import auviotre.enigmatic.legacy.client.renderer.model.IchorSpriteModel;
import auviotre.enigmatic.legacy.client.renderer.model.SpellstoneModel;
import auviotre.enigmatic.legacy.client.screen.AntiqueBagScreen;
import auviotre.enigmatic.legacy.client.screen.LoreInscriberScreen;
import auviotre.enigmatic.legacy.client.screen.SpellstoneTableScreen;
import auviotre.enigmatic.legacy.client.screen.button.EnderChestInventoryButton;
import auviotre.enigmatic.legacy.client.screen.button.MagnetRingInventoryButton;
import auviotre.enigmatic.legacy.contents.item.charms.HellBladeCharm;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.contents.item.spellstones.other.Spelltuner;
import auviotre.enigmatic.legacy.contents.item.tools.TotemOfMalice;
import auviotre.enigmatic.legacy.registries.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
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
    private static void registerItemDecorator(@NotNull RegisterItemDecorationsEvent event) {
        event.register(EnigmaticItems.HELL_BLADE_CHARM, new HellBladeCharm.Decorator());
        event.register(EnigmaticItems.TOTEM_OF_MALICE, new TotemOfMalice.Decorator());
        event.register(EnigmaticItems.THE_CUBE, new BaseItem.WIPDecorator());
        event.register(EnigmaticItems.CREATION_HEART, new BaseItem.WIPDecorator());
        event.register(EnigmaticItems.VOID_STONE, new BaseItem.WIPDecorator());
        event.register(EnigmaticItems.INFERNAL_SPEAR, new BaseItem.WIPDecorator());
        event.register(EnigmaticItems.COSMIC_SCROLL, new BaseItem.WIPDecorator());
    }


    @SubscribeEvent
    private static void registerItemColor(RegisterColorHandlersEvent.@NotNull Item event) {
        event.register((stack, index) -> index > 0 ? -1 : Spelltuner.getColor(stack), EnigmaticItems.SPELLTUNER);
    }

    @SubscribeEvent
    private static void registerScreens(@NotNull RegisterMenuScreensEvent event) {
        event.register(EnigmaticMenus.ANTIQUE_BAG_MENU.get(), AntiqueBagScreen::new);
        event.register(EnigmaticMenus.LORE_INSCRIBER_MENU.get(), LoreInscriberScreen::new);
        event.register(EnigmaticMenus.SPELLSTONE_TABLE_MENU.get(), SpellstoneTableScreen::new);
    }

    @SubscribeEvent
    private static void registerParticleProvider(@NotNull RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(EnigmaticParticles.SPELL.get(), SpellParticle.MobEffectProvider::new);
        event.registerSpriteSet(EnigmaticParticles.ICHOR.get(), IchorParticle.Provider::new);
        event.registerSpriteSet(EnigmaticParticles.ICHOR_CURSE.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(EnigmaticParticles.BLUE_STAR_DUST.get(), StarDustParticle.Provider::new);
        event.registerSpriteSet(EnigmaticParticles.RED_STAR_DUST.get(), StarDustParticle.Provider::new);
        event.registerSpriteSet(EnigmaticParticles.PURPLE_STAR_DUST.get(), StarDustParticle.Provider::new);
    }

    @SubscribeEvent
    private static void registerEntityRenderers(EntityRenderersEvent.@NotNull RegisterRenderers event) {
        event.registerEntityRenderer(EnigmaticEntities.PERMANENT_ITEM_ENTITY.get(), PermanentItemRenderer::new);
        event.registerEntityRenderer(EnigmaticEntities.COBWEB_BALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(EnigmaticEntities.DRAGON_BREATH_ARROW.get(), DragonBreathArrowRender::new);
        event.registerEntityRenderer(EnigmaticEntities.THROWN_ICHOR_SPEAR.get(), ThrownIchorSpearRenderer::new);
        event.registerEntityRenderer(EnigmaticEntities.ICHOR_SPRITE.get(), IchorSpriteRenderer::new);
        event.registerEntityRenderer(EnigmaticEntities.PIGLIN_WANDERER.get(), context -> new PiglinWandererRender(context, ModelLayers.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE_INNER_ARMOR, ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR));
        event.registerBlockEntityRenderer(EnigmaticBlockEntities.DIMENSIONAL_ANCHOR_ENTITY.get(), DimensionalAnchorRender::new);
        event.registerBlockEntityRenderer(EnigmaticBlockEntities.SPELLSTONE_TABLE_ENTITY.get(), SpellstoneTableRender::new);
    }

    @SubscribeEvent
    private static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpellstoneModel.LAYER, SpellstoneModel::createLayer);
        event.registerLayerDefinition(IchorSpriteModel.LAYER, IchorSpriteModel::createBodyLayer);
    }

    @SubscribeEvent
    private static void registerKeybindings(@NotNull RegisterKeyMappingsEvent event) {
        event.register(KeyHandler.SPELLSTONE.get());
        event.register(KeyHandler.SCROLL.get());
        event.register(KeyHandler.ENDER_RING.get());
    }

    @SubscribeEvent
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addLayers(EntityRenderersEvent.AddLayers event) {
        addPlayerLayer(event, PlayerSkin.Model.WIDE);
        addPlayerLayer(event, PlayerSkin.Model.SLIM);
        if (event.getRenderer(EntityType.ARMOR_STAND) instanceof LivingEntityRenderer renderer) {
            renderer.addLayer(new EnigmaticElytraLayer<>(renderer, event.getEntityModels()));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addPlayerLayer(EntityRenderersEvent.AddLayers event, PlayerSkin.Model skin) {
        if (event.getSkin(skin) instanceof LivingEntityRenderer renderer) {
            renderer.addLayer(new EnigmaticElytraLayer<>(renderer, event.getEntityModels()));
            renderer.addLayer(new EtheriumShieldLayer(renderer, event.getEntityModels()));
        }
    }
}