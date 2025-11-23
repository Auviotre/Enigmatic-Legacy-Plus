package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
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
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TheInfinitum extends TheAcknowledgment {
    public static ModConfigSpec.IntValue specialDamageBoost;
    public static ModConfigSpec.IntValue knockbackModifier;
    public static ModConfigSpec.IntValue lifeSteal;
    public static ModConfigSpec.IntValue undeadProbability;

    public TheInfinitum() {
        super(defaultSingleProperties().rarity(Rarity.EPIC).component(EnigmaticComponents.ELDRITCH, true), 15, -2.0F);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.the_infinitum").push("abyssItems.theInfinitum");
        specialDamageBoost = builder.defineInRange("specialDamageBoost", 200, 0, 1000);
        knockbackModifier = builder.defineInRange("knockbackModifier", 200, 0, 1000);
        lifeSteal = builder.defineInRange("lifeSteal", 10, 0, 100);
        undeadProbability = builder.defineInRange("undeadProbability", 80, 0, 100);
        builder.pop(2);
    }

    public static Multimap<Holder<Attribute>, AttributeModifier> getKnockbackModifier() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        double modifier = 0.01 * knockbackModifier.getAsInt();
        builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(getLocation(EnigmaticItems.THE_INFINITUM.get()), modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return builder.build();
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (isSelected && EnigmaticHandler.isTheWorthyOne(livingEntity))
                stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
            else stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.fourthCurse"));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.fourthCurseAlteration");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theTwist1", ChatFormatting.GOLD, String.format("%d%%", specialDamageBoost.get()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum1", ChatFormatting.GOLD, String.format("%d%%", knockbackModifier.get()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum2", ChatFormatting.GOLD, lifeSteal.get() + "%");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theInfinitum5", ChatFormatting.GOLD, undeadProbability.get() + "%");
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
                    if (event.getSource().is(Tags.DamageTypes.IS_TECHNICAL)) return;
                    if (entity.getRandom().nextInt(100) < undeadProbability.get()) {
                        event.setCanceled(true);
                        entity.setHealth(1);
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onDamage(@NotNull LivingIncomingDamageEvent event) {
            if (event.getEntity().getType().is(Tags.EntityTypes.BOSSES)) {
                DamageSource source = event.getSource();
                if (source.getDirectEntity() instanceof LivingEntity attacker && source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                    if (attacker.getMainHandItem().is(EnigmaticItems.THE_INFINITUM) && EnigmaticHandler.isTheWorthyOne(attacker)) {
                        event.setAmount(event.getAmount() * (1 + 0.01F * specialDamageBoost.get()));
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            DamageSource source = event.getSource();
            if (source.getDirectEntity() instanceof LivingEntity attacker && source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                if (attacker.getMainHandItem().is(EnigmaticItems.THE_INFINITUM) && EnigmaticHandler.isTheWorthyOne(attacker)) {
                    attacker.heal(event.getNewDamage() * 0.01F * lifeSteal.get());
                    Holder<MobEffect> debuff = EnigmaticHandler.getRandomDebuff(attacker);
                    MobEffectInstance instance = new MobEffectInstance(debuff, 200, 0, false, true);
                    event.getEntity().addEffect(instance);
                }
            }
        }
    }
}
