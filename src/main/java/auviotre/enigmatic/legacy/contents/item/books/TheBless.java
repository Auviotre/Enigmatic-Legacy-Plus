package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
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
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TheBless extends TheAcknowledgment {
    public static ModConfigSpec.DoubleValue damageBoostByFire;

    public TheBless() {
        super(defaultSingleProperties().rarity(Rarity.EPIC).component(EnigmaticComponents.CURSED, true).component(EnigmaticComponents.BLESSED, true), 6, -1.6F);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.the_bless").push("blessItems.theBless");
        damageBoostByFire = builder.defineInRange("specialDamageBoost", 0.15, 0, 1);
        builder.pop(2);
    }


    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            if (EnigmaticHandler.isTheCursedOne(Minecraft.getInstance().player)) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.fourthCurse"));
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.fourthCurseAlteration");
                TooltipHandler.line(list);
            }
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theBless1");
            if (EnigmaticHandler.isTheBlessedOne(Minecraft.getInstance().player))
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.theBless2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theBless3");
            if (EnigmaticHandler.isTheBlessedOne(Minecraft.getInstance().player))
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.theBless4");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theBlessLore1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theBlessLore2");
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.igniteForTicks(target.getRemainingFireTicks() + 80);
        if (attacker.getRandom().nextInt(5) == 0) {
            target.addEffect(new MobEffectInstance(EnigmaticEffects.ICHOR_CORROSION, 100), attacker);
            target.playSound(SoundEvents.TRIDENT_HIT, 0.5F, 0.1F);
        }
        List<MobEffectInstance> effects = target.getActiveEffects().stream().toList();
        for (MobEffectInstance effect : effects) {
            if (effect.getEffect().value().isBeneficial()) target.removeEffect(effect.getEffect());
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!EnigmaticHandler.canUse(player, stack)) return InteractionResultHolder.pass(stack);
        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack offhandStack = player.getOffhandItem();
            if (!offhandStack.isEmpty() && (offhandStack.getItem().getUseAnimation(offhandStack) == UseAnim.BLOCK))
                return InteractionResultHolder.pass(stack);
        }
        return super.use(world, player, hand);
    }


    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onTick(PlayerTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof LivingEntity entity && EnigmaticHandler.hasItem(entity, EnigmaticItems.THE_BLESS)) {
                entity.clearFire();
            }
        }

        @SubscribeEvent
        private static void onDamage(@NotNull LivingIncomingDamageEvent event) {
            DamageSource source = event.getSource();
            if (source.getDirectEntity() instanceof LivingEntity attacker && source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                ItemStack stack = attacker.getMainHandItem();
                if (stack.is(EnigmaticItems.THE_BLESS) && EnigmaticHandler.canUse(attacker, stack) && EnigmaticHandler.isTheBlessedOne(attacker)) {
                    event.setAmount(event.getAmount() * (float) (1 + Math.min(1.0F, event.getEntity().getRemainingFireTicks() * 0.01F * damageBoostByFire.get())));
                }
            }

            if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.CAMPFIRE) || source.is(DamageTypes.HOT_FLOOR)) {
                if (event.getEntity() instanceof LivingEntity entity && EnigmaticHandler.hasItem(entity, EnigmaticItems.THE_BLESS)) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (EnigmaticHandler.hasItem(event.getEntity(), EnigmaticItems.THE_BLESS) && EnigmaticHandler.isTheBlessedOne(event.getEntity())) {
                event.getContainer().setPostAttackInvulnerabilityTicks(40);
            }
        }
    }
}
