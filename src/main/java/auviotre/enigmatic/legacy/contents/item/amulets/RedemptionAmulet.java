package auviotre.enigmatic.legacy.contents.item.amulets;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IAmulet;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.contents.item.rings.RedemptionRing;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class RedemptionAmulet extends CursedCurioItem implements IAmulet {
    public RedemptionAmulet() {
        super(IItemHelper.singleProperties().rarity(Rarity.EPIC).fireResistant(), true);
    }

    public Color getColor() {
        return Color.ALL;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift4");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionAmulet1");
            if (RedemptionRing.Helper.canUseRelic(Minecraft.getInstance().player))
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionAmulet2");
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
        TooltipHandler.line(list);
        TooltipHandler.line(list, "curios.modifiers.amulet", ChatFormatting.GOLD);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantersPearl1", ChatFormatting.GOLD, 1);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionAmuletStat1", ChatFormatting.GOLD, 2);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionAmuletStat2", ChatFormatting.GOLD, 4);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionAmuletStat3", ChatFormatting.GOLD, 8);
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(Attributes.LUCK, new AttributeModifier(IItemHelper.getLocation(this), 2, AttributeModifier.Operation.ADD_VALUE));
        attributes.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(IItemHelper.getLocation(this), 8.0, AttributeModifier.Operation.ADD_VALUE));
        CuriosApi.addSlotModifier(attributes, "charm", IItemHelper.getLocation(this), 1.0, AttributeModifier.Operation.ADD_VALUE);
        return attributes;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGH)
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity victim = event.getEntity();
            if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.REDEMPTION_AMULET)) {
                event.setAmount(event.getAmount() - 4.0F);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onAttackLow(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity victim = event.getEntity();
            if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.REDEMPTION_AMULET) && RedemptionRing.Helper.canUseRelic(victim)) {
                DamageSource source = event.getSource();
                if (source.getEntity() == null || !source.getEntity().getPersistentData().getBoolean("RedemptionMark")) {
                    event.setAmount(0.0F);
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (event.getNewDamage() >= Float.MAX_VALUE) return;
            if (event.getEntity() instanceof Player player && EnigmaticHandler.hasCurio(player, EnigmaticItems.REDEMPTION_AMULET)) {
                float strengthScale = Math.clamp(1.0F - 0.8F * player.getAttackStrengthScale(0.0F), 0.0F, 1.0F);
                event.setNewDamage(event.getNewDamage() * strengthScale);
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker && EnigmaticHandler.hasCurio(attacker, EnigmaticItems.REDEMPTION_AMULET)) {
                event.getEntity().getPersistentData().putBoolean("RedemptionMark", true);
            }
        }
    }
}
