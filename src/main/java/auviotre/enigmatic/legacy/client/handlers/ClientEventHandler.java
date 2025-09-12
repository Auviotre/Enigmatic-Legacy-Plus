package auviotre.enigmatic.legacy.client.handlers;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IBlessed;
import auviotre.enigmatic.legacy.api.item.ICursed;
import auviotre.enigmatic.legacy.api.item.IEldritch;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.contents.item.rings.EnderRing;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.packets.toServer.EnderRingKeyPacket;
import auviotre.enigmatic.legacy.packets.toServer.ScrollKeyPacket;
import auviotre.enigmatic.legacy.packets.toServer.SpellstoneKeyPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

@Mod(value = EnigmaticLegacy.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EnigmaticLegacy.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    public static final ResourceLocation ICONS = EnigmaticLegacy.location("textures/gui/icons.png");
    private static boolean spaceDown = false;

    @SubscribeEvent
    private static void onTooltipRendering(RenderTooltipEvent.@NotNull Color event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            LocalPlayer player = Minecraft.getInstance().player;
            if (item instanceof ICursed) {
                if (item instanceof IBlessed && player != null && EnigmaticHandler.isTheBlessedOne(player)) {
                    event.setBackground(0xF0201A10);
                    event.setBorderStart(0x90800C00);
                    event.setBorderEnd(0x80FFA632);
                } else if (item instanceof IEldritch) {
                    event.setBackground(0xF0201020);
                    event.setBorderStart(0xF08F609A);
                    event.setBorderEnd(0x805A3A7A);
                } else {
                    event.setBackground(0xF0201010);
                    event.setBorderStart(0xF0A01000);
                    event.setBorderEnd(0x70901000);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void onTooltip(@NotNull ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player != null && !player.hasInfiniteMaterials()) {
            if (event.getItemStack().getItem() instanceof ICursed) {
                if (!EnigmaticHandler.isTheCursedOne(player)) {
                    event.getToolTip().replaceAll(component -> {
                        if (component.getContents() instanceof TranslatableContents locations) {
                            if (locations.getKey().startsWith("tooltip.enigmaticlegacy.cursedOnesOnly")) return component;
                            if (locations.getKey().startsWith("tooltip.enigmaticlegacy.worthyOnesOnly")) return component;
                        }

                        return Component.literal(TooltipHandler.obscureString(component.getString(), player.getRandom())).withStyle(component.getStyle());
                    });
                }
            }
        }
    }

    @SubscribeEvent
    private static void onClientTick(ClientTickEvent.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (!ISpellstone.get(player).isEmpty() && ISpellstone.KEY_MAPPING.get().consumeClick())
            PacketDistributor.sendToServer(new SpellstoneKeyPacket());
        if (BaseCurioItem.KEY_MAPPING.get().consumeClick())
            PacketDistributor.sendToServer(new ScrollKeyPacket());
        if (EnderRing.KEY_MAPPING.get().consumeClick())
            PacketDistributor.sendToServer(new EnderRingKeyPacket(ItemStack.EMPTY));

        boolean down = Minecraft.getInstance().options.keyJump.isDown();
        boolean jumpClicked = false;
        if (spaceDown != down) {
            spaceDown = down;
            if (spaceDown) jumpClicked = true;
        }

        if (jumpClicked) {
            if (ISpellstone.get(player).is(EnigmaticItems.ANGEL_BLESSING) && player != null) {
                if (!player.isInWater() && !player.onGround() && !player.isSpectator()) {
                    PacketDistributor.sendToServer(new SpellstoneKeyPacket());
                }
            }
        }
    }

    @SubscribeEvent
    private static void onFogRender(ViewportEvent.RenderFog event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (event.getCamera().getFluidInCamera() == FogType.LAVA) {
            if (player.hasEffect(EnigmaticEffects.MOLTEN_HEART)) {
                RenderSystem.setShaderFogStart(0.0F);
                RenderSystem.setShaderFogEnd(6.0F);
            } else if (EnigmaticHandler.hasCurio(player, EnigmaticItems.BLAZING_CORE)) {
                RenderSystem.setShaderFogStart(0.0F);
                RenderSystem.setShaderFogEnd(10.0F);
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    private static void onOverlayRender(RenderGuiLayerEvent.@NotNull Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack spellstone = ISpellstone.get(player);
        GuiGraphics guiGraphics = event.getGuiGraphics();
        if (event.getName().equals(VanillaGuiLayers.AIR_LEVEL) && CONFIG.SPELLSTONES.preventOxygenBarRender.get()) {
            if (spellstone.is(EnigmaticItems.OCEAN_STONE) || spellstone.is(EnigmaticItems.VOID_PEARL)) {
                event.setCanceled(true);
            }
        }
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_LEVEL) && player != null && spellstone.is(EnigmaticItems.BLAZING_CORE)) {
            if (Minecraft.getInstance().gameMode != null && Minecraft.getInstance().gameMode.hasExperience()) {
                EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
                int timer = data.getFireImmunityTimer();
                int lastTimer = data.getFireImmunityTimerLast();
                if (timer == 0 && lastTimer == 0) return;
                event.setCanceled(true);
                String title = I18n.get("gui.enigmaticlegacy.blazing_core_bar_title");
                Font font = Minecraft.getInstance().font;
                int x = (guiGraphics.guiWidth() - font.width(title)) / 2 + 1;
                int y = guiGraphics.guiHeight() - 31 - 4;
                int boundaryColor = 5832704;
                guiGraphics.drawString(font, title, x + 1, y, boundaryColor);
                guiGraphics.drawString(font, title, x - 1, y, boundaryColor);
                guiGraphics.drawString(font, title, x, y + 1, boundaryColor);
                guiGraphics.drawString(font, title, x, y - 1, boundaryColor);
                guiGraphics.drawString(font, title, x, y, 16770638);
            }
        }
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR) && player != null && spellstone.is(EnigmaticItems.BLAZING_CORE)) {
            if (Minecraft.getInstance().gameMode != null && Minecraft.getInstance().gameMode.hasExperience()) {
                EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
                int timer = data.getFireImmunityTimer();
                int lastTimer = data.getFireImmunityTimerLast();
                if (timer == 0 && lastTimer == 0) return;
                event.setCanceled(true);
                int cap = data.getFireImmunityCap();
                DeltaTracker partialTick = event.getPartialTick();
                float barFiller = Mth.lerp(partialTick.getGameTimeDeltaTicks(), lastTimer, timer) / cap;
                barFiller = (float) Math.pow(barFiller, 2);
                int x = guiGraphics.guiWidth() / 2 - 91;
                int y = guiGraphics.guiHeight() - 32 + 3;

                RenderSystem.setShaderTexture(0, ICONS);
                int k = (int) (barFiller * 183.0F);
                guiGraphics.blit(ICONS, x, y, 0, 0, 182, 5);
                if (k > 0) guiGraphics.blit(ICONS, x, y, 0, 5, k, 5);
            }
        }
    }
}