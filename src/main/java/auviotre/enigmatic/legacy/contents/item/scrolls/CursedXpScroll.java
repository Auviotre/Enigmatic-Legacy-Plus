package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
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
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class CursedXpScroll extends XpScroll {
    public static ModConfigSpec.DoubleValue magneticRange;
    public static ModConfigSpec.IntValue xpLevelUpperLimit;
    public static ModConfigSpec.DoubleValue damageBoostLimit;
    public static ModConfigSpec.DoubleValue healBoostLimit;
    public static ModConfigSpec.DoubleValue knockbackResistanceBoostLimit;

    public CursedXpScroll() {
        super(defaultSingleProperties().rarity(Rarity.RARE).component(EnigmaticComponents.CURSED, true));
    }

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.cursed_xp_scroll").push("cursedItems.cursedXpScroll");
        magneticRange = builder.defineInRange("magneticRange", 24.0, 1.0, 256.0);
        xpLevelUpperLimit = builder.defineInRange("xpLevelUpperLimit", 1000, 100, 32768);
        damageBoostLimit = builder.defineInRange("damageBoostLimit", 128.0, 0.0, 256.0);
        healBoostLimit = builder.defineInRange("healBoostLimit", 64.0, 0.0, 256.0);
        knockbackResistanceBoostLimit = builder.defineInRange("knockbackResistanceBoostLimit", 160.0, 0.0, 256.0);
        builder.pop(2);
    }

    public static double getLevelModifier(ItemStack stack) {
        return Mth.clamp((double) getExpLevel(stack.getOrDefault(EnigmaticComponents.XP_SCROLL_STORED, 0L)) / xpLevelUpperLimit.get(), 0.0D, 1.0D);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        MutableComponent modeComponent;
        if (!stack.getOrDefault(EnigmaticComponents.XP_SCROLL_ACTIVE, false))
            modeComponent = Component.translatable("tooltip.enigmaticlegacy.xpScrollDeactivated");
        else if (stack.getOrDefault(EnigmaticComponents.XP_SCROLL_MODE, false))
            modeComponent = Component.translatable("tooltip.enigmaticlegacy.xpScrollAbsorption");
        else modeComponent = Component.translatable("tooltip.enigmaticlegacy.xpScrollExtraction");

        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedXpScroll1");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedXpScroll2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedXpScroll3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedXpScroll4");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll11");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll12", ChatFormatting.GOLD, String.format("%.0f", magneticRange.getAsDouble()));
        } else TooltipHandler.holdShift(list);

        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScrollMode", modeComponent.withStyle(ChatFormatting.GOLD));
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScrollStoredXP");
        Long stored = stack.getOrDefault(EnigmaticComponents.XP_SCROLL_STORED, 0L);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScrollUnits", ChatFormatting.GOLD, stored, getExpLevel(stored));

        try {
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.currentKeybind", ChatFormatting.LIGHT_PURPLE, KeyMapping.createNameSupplier("key.scrollAbility").get().getString().toUpperCase());
        } catch (NullPointerException ignored) {
        }
        this.addAttributes(list, stack);
    }

    @OnlyIn(Dist.CLIENT)
    protected void addAttributes(List<Component> list, ItemStack stack) {
        double level = getLevelModifier(stack);
        if (level == 0) return;
        TooltipHandler.line(list);
        list.add(Component.translatable("curios.modifiers.scroll").withStyle(ChatFormatting.GOLD));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll1", ChatFormatting.GOLD, String.format("%.1f", level * damageBoostLimit.get()) + "%");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedXpScroll5", ChatFormatting.GOLD, String.format("%.1f", level * knockbackResistanceBoostLimit.get()) + "%");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll3", ChatFormatting.GOLD, String.format("%.1f", level * healBoostLimit.get()) + "%");
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack stack) {
        double level = getLevelModifier(stack);
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        if (level > 0)
            attributes.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(getLocation(this), level / 100.0 * knockbackResistanceBoostLimit.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return attributes;
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player) || context.entity().level().isClientSide() || !stack.getOrDefault(EnigmaticComponents.XP_SCROLL_ACTIVE, false))
            return;
        super.curioTick(context, stack);
        player.getAttributes().addTransientAttributeModifiers(getModifiers(stack));
        double range = magneticRange.get();
        List<ExperienceOrb> orbs = player.level().getEntitiesOfClass(ExperienceOrb.class, player.getBoundingBox().inflate(range), Entity::isAlive);
        for (ExperienceOrb orb : orbs) {
            if (orb.distanceTo(player) > range) continue;
            player.takeXpDelay = 0;
            orb.playerTouch(player);
        }
    }

    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        context.entity().getAttributes().removeAttributeModifiers(getModifiers(stack));
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onConfirmedDeath(@NotNull LivingDeathEvent event) {
            if (EnigmaticHandler.hasCurio(event.getEntity(), EnigmaticItems.CURSED_XP_SCROLL)) {
                ItemStack stack = EnigmaticHandler.getCurio(event.getEntity(), EnigmaticItems.CURSED_XP_SCROLL);
                stack.set(EnigmaticComponents.XP_SCROLL_STORED, 0L);
            }
        }

        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (EnigmaticHandler.hasCurio(attacker, EnigmaticItems.CURSED_XP_SCROLL)) {
                    ItemStack stack = EnigmaticHandler.getCurio(attacker, EnigmaticItems.CURSED_XP_SCROLL);
                    event.setAmount(event.getAmount() * (float) (1.0F + getLevelModifier(stack) / 100.0 * damageBoostLimit.get()));
                }
            }
        }

        @SubscribeEvent
        private static void onLivingHeal(@NotNull LivingHealEvent event) {
            if (EnigmaticHandler.hasCurio(event.getEntity(), EnigmaticItems.CURSED_XP_SCROLL)) {
                ItemStack stack = EnigmaticHandler.getCurio(event.getEntity(), EnigmaticItems.CURSED_XP_SCROLL);
                event.setAmount(event.getAmount() * (float) (1.0F + getLevelModifier(stack) / 100.0 * healBoostLimit.get()));
            }
        }

        @SubscribeEvent
        private static void onXPOrb(PlayerXpEvent.@NotNull PickupXp event) {
            Player player = event.getEntity();
            if (EnigmaticHandler.hasItem(player, EnigmaticItems.CURSED_XP_SCROLL) || EnigmaticHandler.hasCurio(player, EnigmaticItems.CURSED_XP_SCROLL)) {
                ExperienceOrb orb = event.getOrb();
                orb.value = orb.getValue() / 4;
            }
        }
    }
}
