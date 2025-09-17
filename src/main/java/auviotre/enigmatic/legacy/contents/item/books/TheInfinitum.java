package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class TheInfinitum extends TheAcknowledgment {
    public TheInfinitum() {
        super(defaultSingleProperties().rarity(Rarity.EPIC).component(EnigmaticComponents.ELDRITCH, true), 15, -2.0F);
    }

    public static Multimap<Holder<Attribute>, AttributeModifier> getKnockbackModifier() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        double modifier = 0.01 * CONFIG.CURSED_ITEMS.knockbackModifier.getAsInt() / 1.5F;
        builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(getLocation(EnigmaticItems.THE_INFINITUM.get()), modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return builder.build();
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (isSelected && EnigmaticHandler.isTheWorthyOne(livingEntity)) stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
            else stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.fourthCurse"));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.fourthCurseAlteration");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theTwist1", ChatFormatting.GOLD, String.format("%d%%", CONFIG.CURSED_ITEMS.specialDamageBoost.get() * 2 / 3));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum1", ChatFormatting.GOLD, String.format("%d%%", CONFIG.CURSED_ITEMS.knockbackModifier.get() * 2 / 3));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum2", ChatFormatting.GOLD, "10%");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum5", ChatFormatting.GOLD, "85%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum6");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitumLore");
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
        TooltipHandler.line(list);
        TooltipHandler.worthyOnly(list, stack);
    }

    public float getAttackDamageBonus(@NotNull Entity target, float damage, DamageSource damageSource) {
        if (target.getType().is(Tags.EntityTypes.BOSSES))
            return damage * 0.01F * CONFIG.CURSED_ITEMS.specialDamageBoost.get() / 1.5F;
        return super.getAttackDamageBonus(target, damage, damageSource);
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!EnigmaticHandler.isTheWorthyOne(player)) return InteractionResultHolder.pass(player.getItemInHand(hand));
        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack offhandStack = player.getOffhandItem();
            if (!offhandStack.isEmpty() && (offhandStack.getItem().getUseAnimation(offhandStack) == UseAnim.BLOCK))
                return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        return super.use(world, player, hand);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onTick(PlayerTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof LivingEntity entity && EnigmaticHandler.isTheWorthyOne(entity)) {
                if (entity.getMainHandItem().is(EnigmaticItems.THE_INFINITUM))
                    entity.getAttributes().addTransientAttributeModifiers(getKnockbackModifier());
                else entity.getAttributes().removeAttributeModifiers(getKnockbackModifier());
            }
        }

        @SubscribeEvent
        private static void onDeath(@NotNull LivingDeathEvent event) {
            if (event.getEntity() instanceof LivingEntity entity && EnigmaticHandler.isTheWorthyOne(entity)) {
                if (entity.getMainHandItem().is(EnigmaticItems.THE_INFINITUM) || entity.getOffhandItem().is(EnigmaticItems.THE_INFINITUM)) {
                    if (entity.getRandom().nextFloat() < 0.85F) {
                        event.setCanceled(true);
                        entity.setHealth(1);
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            DamageSource source = event.getSource();
            if (source.getDirectEntity() instanceof LivingEntity attacker && source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                if (attacker.getMainHandItem().is(EnigmaticItems.THE_INFINITUM) && EnigmaticHandler.isTheWorthyOne(attacker)) {
                    attacker.heal(event.getNewDamage() * 0.1F);
                    Holder<MobEffect> debuff = EnigmaticHandler.getRandomDebuff(attacker);
                    MobEffectInstance instance = new MobEffectInstance(debuff, 200, 0, false, true);
                    event.getEntity().addEffect(instance);
                }
            }
        }
    }
}
