package auviotre.enigmatic.legacy;

import auviotre.enigmatic.legacy.client.ClientConfig;
import auviotre.enigmatic.legacy.client.handlers.QuoteHandler;
import auviotre.enigmatic.legacy.compat.CompatHandler;
import auviotre.enigmatic.legacy.contents.item.misc.SoulCrystal;
import auviotre.enigmatic.legacy.contents.item.rings.CursedRing;
import auviotre.enigmatic.legacy.contents.item.rings.DesolationRing;
import auviotre.enigmatic.legacy.contents.item.scrolls.NightScroll;
import auviotre.enigmatic.legacy.contents.item.spellstones.TheCube;
import auviotre.enigmatic.legacy.contents.item.tools.ChaosElytra;
import auviotre.enigmatic.legacy.contents.item.tools.SoulCompass;
import auviotre.enigmatic.legacy.handlers.SoulArchive;
import auviotre.enigmatic.legacy.packets.ClientPayloadHandler;
import auviotre.enigmatic.legacy.packets.client.*;
import auviotre.enigmatic.legacy.packets.server.*;
import auviotre.enigmatic.legacy.proxy.ClientProxy;
import auviotre.enigmatic.legacy.proxy.CommonProxy;
import auviotre.enigmatic.legacy.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(EnigmaticLegacy.MODID)
public class EnigmaticLegacy {
    public static final String MODID = "enigmaticlegacyplus";
    public static final Logger LOGGER = LoggerFactory.getLogger("EnigmaticLegacy");
    public static final CommonProxy PROXY = FMLEnvironment.dist.isClient() ? new ClientProxy() : new CommonProxy();

    public EnigmaticLegacy(IEventBus eventBus, @NotNull ModContainer container) {
        EnigmaticItems.ITEMS.register(eventBus);
        EnigmaticMenus.MENUS.register(eventBus);
        EnigmaticBlocks.BLOCKS.register(eventBus);
        EnigmaticSounds.SOUNDS.register(eventBus);
        EnigmaticEffects.EFFECTS.register(eventBus);
        EnigmaticPotions.POTIONS.register(eventBus);
        EnigmaticRecipes.RECIPE_TYPES.register(eventBus);
        EnigmaticMemories.MEMORY_TYPE.register(eventBus);
        EnigmaticAttributes.ATTRIBUTES.register(eventBus);
        EnigmaticEntities.ENTITY_TYPES.register(eventBus);
        EnigmaticComponents.COMPONENTS.register(eventBus);
        EnigmaticTriggers.TRIGGER_TYPES.register(eventBus);
        EnigmaticTabs.CREATIVE_MODE_TABS.register(eventBus);
        EnigmaticRecipes.RECIPE_SERIALIZERS.register(eventBus);
        EnigmaticParticles.PARTICLE_TYPES.register(eventBus);
        EnigmaticAttachments.ATTACHMENT_TYPES.register(eventBus);
        EnigmaticBlockEntities.BLOCK_ENTITIES.register(eventBus);
        EnigmaticLoots.LOOT_CONDITIONS.register(eventBus);
        EnigmaticLoots.LOOT_MODIFIERS.register(eventBus);
        EnigmaticStructureTypes.STRUCTURE_TYPES.register(eventBus);
        EnigmaticStructureTypes.STRUCTURE_PIECE_TYPES.register(eventBus);

        NeoForge.EVENT_BUS.register(this);
        eventBus.addListener(this::onCommonSetup);
        eventBus.addListener(this::interMod);
        eventBus.addListener(this::onPacketSetup);
        eventBus.addListener(this::attributeSetup);
        eventBus.addListener(this::onLoadComplete);
        eventBus.addListener(this::addCreative);

        container.registerConfig(ModConfig.Type.SERVER, ELConfig.SPEC);
        if (FMLEnvironment.dist.isClient()) {
            eventBus.addListener(this::onClientSetup);
            NeoForge.EVENT_BUS.register(QuoteHandler.INSTANCE);
            container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        CompatHandler.getInstance().register(eventBus);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        PROXY.clientInit();
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        PROXY.init();
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
    }

    private void interMod(InterModEnqueueEvent event) {

    }

    public void onPacketSetup(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID + ".1.0").optional();
        registrar.playToServer(EnderRingKeyPacket.TYPE, EnderRingKeyPacket.STREAM_CODEC, EnderRingKeyPacket::handle);
        registrar.playToServer(EmptyLeftClickPacket.TYPE, EmptyLeftClickPacket.STREAM_CODEC, EmptyLeftClickPacket::handle);
        registrar.playToServer(ToggleMagnetEffectKeyPacket.TYPE, ToggleMagnetEffectKeyPacket.STREAM_CODEC, ToggleMagnetEffectKeyPacket::handle);
        registrar.playToServer(SpellstoneKeyPacket.TYPE, SpellstoneKeyPacket.STREAM_CODEC, SpellstoneKeyPacket::handle);
        registrar.playToServer(ScrollKeyPacket.TYPE, ScrollKeyPacket.STREAM_CODEC, ScrollKeyPacket::handle);
        registrar.playToServer(LoreInscriberRenamePacket.TYPE, LoreInscriberRenamePacket.STREAM_CODEC, LoreInscriberRenamePacket::handle);
        registrar.playToServer(UpdateElytraBoostPacket.TYPE, UpdateElytraBoostPacket.STREAM_CODEC, UpdateElytraBoostPacket::handle);
        registrar.playToClient(EnderRingGrabItemPacket.TYPE, EnderRingGrabItemPacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(EnigmaticDataSyncPacket.TYPE, EnigmaticDataSyncPacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(ForceProjectileRotationsPacket.TYPE, ForceProjectileRotationsPacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(PlayerMotionPacket.TYPE, PlayerMotionPacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(PermanentDeathPacket.TYPE, PermanentDeathPacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(IchorSpriteBeamPacket.TYPE, IchorSpriteBeamPacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(TotemOfMalicePacket.TYPE, TotemOfMalicePacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(SoulCompassUpdatePacket.TYPE, SoulCompassUpdatePacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(ChaosDescendingPacket.TYPE, ChaosDescendingPacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(TheCubeRevivePacket.TYPE, TheCubeRevivePacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(SlotUnlockToastPacket.TYPE, SlotUnlockToastPacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
        registrar.playToClient(PlayQuotePacket.TYPE, PlayQuotePacket.STREAM_CODEC, ClientPayloadHandler.getInstance()::handle);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
            event.accept(EnigmaticItems.THE_JUDGEMENT.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(EnigmaticItems.LOOT_GENERATOR.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(EnigmaticItems.COSMIC_SCROLL.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        } else if (event.getTab() == EnigmaticTabs.MAIN_TAB.get()) {
            tabInsert(event, EnigmaticItems.SPELLTUNER.toStack(), EnigmaticBlocks.SPELLSTONE_TABLE.toStack());
            tabInsert(event, EnigmaticItems.ASTRAL_FRUIT.toStack(), EnigmaticBlocks.ASTRAL_DUST_SACK.toStack());
            tabInsert(event, EnigmaticItems.COSMIC_HEART.toStack(), EnigmaticBlocks.COSMIC_CAKE.toStack());
            tabInsert(event, EnigmaticItems.RAW_ETHERIUM.toStack(), EnigmaticBlocks.ETHERIUM_ORE.toStack(), false);
            tabInsert(event, EnigmaticItems.ETHERIUM_NUGGET.toStack(), EnigmaticBlocks.ETHERIUM_BLOCK.toStack());
            tabInsert(event, EnigmaticItems.ETHEREAL_FORGING_CHARM.toStack(), EnigmaticBlocks.ETHEREAL_LANTERN.toStack());
            tabInsert(event, EnigmaticBlocks.ETHEREAL_LANTERN.toStack(), EnigmaticBlocks.DIMENSIONAL_ANCHOR.toStack());
        } else if (event.getTab() == EnigmaticTabs.CURSE_TAB.get()) {
        }
    }

    private void tabInsert(BuildCreativeModeTabContentsEvent event, ItemStack existing, ItemStack newEntry) {
        tabInsert(event, existing, newEntry, true);
    }

    private void tabInsert(BuildCreativeModeTabContentsEvent event, ItemStack existing, ItemStack newEntry, boolean isAfter) {
        if (isAfter) event.insertAfter(existing, newEntry, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        else event.insertBefore(existing, newEntry, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    private void attributeSetup(final EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            event.add(type, EnigmaticAttributes.ETHERIUM_SHIELD);
            event.add(type, EnigmaticAttributes.PROJECTILE_DEFLECT);
            event.add(type, EnigmaticAttributes.LIFESTEAL);
        }
    }

    @SubscribeEvent
    public void onServerStarting(@NotNull ServerStartingEvent event) {
        SoulCrystal.ATTRIBUTE_DISPATCHER.clear();
        SoulArchive.initialize(event.getServer());
        CursedRing.POSSESSIONS.clear();
        NightScroll.Events.BOXES.clear();
        DesolationRing.Events.BOXES.clear();
        SoulCompass.Events.LAST_SOUL_COMPASS_UPDATE.clear();
        TheCube.clearLocationCache();
        TheCube.Events.LAST_HEALTH.clear();
        ChaosElytra.Events.TICK_MAP.clear();
        ChaosElytra.Events.MOVEMENT_MAP.clear();
    }
}
