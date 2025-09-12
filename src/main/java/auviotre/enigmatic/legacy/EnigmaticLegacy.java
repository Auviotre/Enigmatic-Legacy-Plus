package auviotre.enigmatic.legacy;

import auviotre.enigmatic.legacy.api.item.IEldritch;
import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.contents.item.SoulCrystal;
import auviotre.enigmatic.legacy.contents.item.amulets.EnigmaticAmulet;
import auviotre.enigmatic.legacy.handlers.SoulArchive;
import auviotre.enigmatic.legacy.packets.toClient.EnderRingGrabItemPacket;
import auviotre.enigmatic.legacy.packets.toClient.EnigmaticDataSyncPacket;
import auviotre.enigmatic.legacy.packets.toClient.ForceProjectileRotationsPacket;
import auviotre.enigmatic.legacy.packets.toClient.PlayerMotionPacket;
import auviotre.enigmatic.legacy.packets.toServer.*;
import auviotre.enigmatic.legacy.registries.*;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static auviotre.enigmatic.legacy.contents.item.amulets.EnigmaticAmulet.AmuletColor;
import static auviotre.enigmatic.legacy.contents.item.amulets.EnigmaticAmulet.setColor;

@Mod(EnigmaticLegacy.MODID)
public class EnigmaticLegacy {
    public static final String MODID = "enigmaticlegacyplus";
    public static final Logger LOGGER = LoggerFactory.getLogger("EnigmaticLegacy");

    public EnigmaticLegacy(IEventBus modEventBus, @NotNull ModContainer container) {
        EnigmaticItems.ITEMS.register(modEventBus);
        EnigmaticMenus.MENUS.register(modEventBus);
        EnigmaticBlocks.BLOCKS.register(modEventBus);
        EnigmaticSounds.SOUNDS.register(modEventBus);
        EnigmaticEffects.EFFECTS.register(modEventBus);
        EnigmaticPotions.POTIONS.register(modEventBus);
        EnigmaticEntities.ENTITY_TYPES.register(modEventBus);
        EnigmaticComponents.COMPONENTS.register(modEventBus);
        EnigmaticTabs.CREATIVE_MODE_TABS.register(modEventBus);
        EnigmaticRecipes.RECIPE_SERIALIZER.register(modEventBus);
        EnigmaticParticles.PARTICLE_TYPES.register(modEventBus);
        EnigmaticAttachments.ATTACHMENT_TYPES.register(modEventBus);
        EnigmaticBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        EnigmaticLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        EnigmaticLootConditions.LOOT_CONDITIONS.register(modEventBus);

        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::interMod);
        modEventBus.addListener(this::onPacketSetup);
        modEventBus.addListener(this::onLoadComplete);
        container.registerConfig(ModConfig.Type.COMMON, ELConfig.SPEC);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::onClientSetup);
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        try {
            ClampedItemPropertyFunction taintedFunc = (stack, level, entity, i) -> ITaintable.isTainted(stack) ? 1.0F : 0.0F;
            ClampedItemPropertyFunction eldritchFunc = (stack, level, entity, i) -> entity == null ? 0.0F : stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            ItemProperties.register(EnigmaticItems.TWISTED_HEART.get(), ITaintable.LOCATION, taintedFunc);
            ItemProperties.register(EnigmaticItems.ABYSSAL_HEART.get(), IEldritch.LOCATION, (stack, level, entity, i) -> entity == null ? (ITaintable.isTainted(stack) ? 1.0F : 0.0F) : stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F));
            ItemProperties.register(EnigmaticItems.THE_INFINITUM.get(), IEldritch.LOCATION, eldritchFunc);
            ItemProperties.register(EnigmaticItems.ELDRITCH_AMULET.get(), IEldritch.LOCATION, eldritchFunc);
            ItemProperties.register(EnigmaticItems.INFERNAL_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(EnigmaticItems.ENIGMATIC_AMULET.get(), EnigmaticLegacy.location("amulet_color"), (stack, level, entity, i) -> stack.getOrDefault(EnigmaticComponents.AMULET_COLOR, 0.0F));
        } catch (Exception exception) {
            LOGGER.warn("Could not load item models.");
        }
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
    }

    private void interMod(InterModEnqueueEvent event) {

    }

    public void onPacketSetup(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0").optional();
        registrar.playToServer(EnderRingKeyPacket.TYPE, EnderRingKeyPacket.STREAM_CODEC, EnderRingKeyPacket::handle);
        registrar.playToServer(ToggleMagnetEffectKeyPacket.TYPE, ToggleMagnetEffectKeyPacket.STREAM_CODEC, ToggleMagnetEffectKeyPacket::handle);
        registrar.playToServer(SpellstoneKeyPacket.TYPE, SpellstoneKeyPacket.STREAM_CODEC, SpellstoneKeyPacket::handle);
        registrar.playToServer(ScrollKeyPacket.TYPE, ScrollKeyPacket.STREAM_CODEC, ScrollKeyPacket::handle);
        registrar.playToServer(LoreInscriberRenamePacket.TYPE, LoreInscriberRenamePacket.STREAM_CODEC, LoreInscriberRenamePacket::handle);
        registrar.playToServer(UpdateElytraBoostPacket.TYPE, UpdateElytraBoostPacket.STREAM_CODEC, UpdateElytraBoostPacket::handle);
        registrar.playToClient(EnderRingGrabItemPacket.TYPE, EnderRingGrabItemPacket.STREAM_CODEC, EnderRingGrabItemPacket::handle);
        registrar.playToClient(EnigmaticDataSyncPacket.TYPE, EnigmaticDataSyncPacket.STREAM_CODEC, EnigmaticDataSyncPacket::handle);
        registrar.playToClient(ForceProjectileRotationsPacket.TYPE, ForceProjectileRotationsPacket.STREAM_CODEC, ForceProjectileRotationsPacket::handle);
        registrar.playToClient(PlayerMotionPacket.TYPE, PlayerMotionPacket.STREAM_CODEC, PlayerMotionPacket::handle);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
            event.accept(EnigmaticItems.THE_JUDGEMENT.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(EnigmaticItems.LOOT_GENERATOR.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        } else if (event.getTab() == EnigmaticTabs.MAIN_TAB.get()) {
            event.insertAfter(EnigmaticItems.UNWITNESSED_AMULET.toStack(), setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.RED), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.RED), EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.AQUA), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.AQUA), EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.VIOLET), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.VIOLET), EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.MAGENTA), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.MAGENTA), EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.GREEN), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.GREEN), EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.BLACK), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.BLACK), EnigmaticAmulet.setColor(EnigmaticItems.ENIGMATIC_AMULET.toStack(), AmuletColor.BLUE), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(EnigmaticItems.ASTRAL_DUST.toStack(), EnigmaticBlocks.ASTRAL_DUST_SACK.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(EnigmaticItems.RAW_ETHERIUM.toStack(), EnigmaticBlocks.ETHERIUM_ORE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(EnigmaticItems.ETHERIUM_NUGGET.toStack(), EnigmaticBlocks.ETHERIUM_BLOCK.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(EnigmaticBlocks.ETHERIUM_BLOCK.toStack(), EnigmaticBlocks.DIMENSIONAL_ANCHOR.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        SoulCrystal.ATTRIBUTE_DISPATCHER.clear();
        SoulArchive.initialize(event.getServer());
    }
}
