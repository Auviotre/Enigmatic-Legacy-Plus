package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;

public class VoidPearl extends SpellstoneItem {
    public static ModConfigSpec.DoubleValue shadowRange;
    public static ModConfigSpec.IntValue darknessDamage;
    public static ModConfigSpec.IntValue witheringLevel;
    public static ModConfigSpec.DoubleValue witheringTime;
    public static ModConfigSpec.IntValue undeadProbability;

    public VoidPearl() {
        super(defaultSingleProperties().rarity(Rarity.RARE), 0xFF333333);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.void_pearl").push("spellstone.voidPearl");
        shadowRange = builder.defineInRange("shadowRange", 16.0, 0.0, 128.0);
        darknessDamage = builder.defineInRange("darknessDamage", 4, 0, 100);
        witheringLevel = builder.defineInRange("witheringLevel", 2, 0, 10);
        witheringTime = builder.defineInRange("witheringTime", 5.0, 0, 120.0);
        undeadProbability = builder.defineInRange("undeadProbability", 30, 0, 80);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkillAbsent");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidPearl1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidPearl2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidPearl3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidPearl4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidPearl5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidPearl6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidPearl7", ChatFormatting.GOLD, undeadProbability.get() + "%");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    @OnlyIn(Dist.CLIENT)
    public void addTuneTooltip(List<Component> list) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidPearl7", ChatFormatting.GOLD, undeadProbability.get() / 2 + "%");
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (entity.isOnFire()) entity.clearFire();
        if (entity.isFreezing()) entity.setTicksFrozen(0);
        if (entity.getAirSupply() < entity.getMaxAirSupply()) entity.setAirSupply(entity.getMaxAirSupply());

        for (MobEffectInstance effect : new ArrayList<>(entity.getActiveEffects())) {
            if (effect.getEffect().is(EnigmaticTags.Effects.ALWAYS_APPLY)) continue;
            entity.removeEffect(effect.getEffect());
        }

        if (entity.tickCount % 10 == 0) {
            List<LivingEntity> entities = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(shadowRange.get()));
            entities.removeIf(victim -> ISpellstone.get(victim).is(this));
            entities.removeIf(victim -> victim instanceof OwnableEntity ownable && entity == ownable.getOwner());
            entities.remove(entity);
            for (LivingEntity victim : entities) {
                if (victim.level().getMaxLocalRawBrightness(victim.blockPosition(), 0) < 3 || victim instanceof FlyingMob) {
                    if (!(entity instanceof Player player) || !(victim instanceof Player vPlayer) || player.canHarmPlayer(vPlayer)) {
                        if (victim.hurt(EnigmaticDamageTypes.source(victim.level(), EnigmaticDamageTypes.DARKNESS, entity), darknessDamage.getAsInt())) {
                            entity.level().playSound(null, victim.blockPosition(), SoundEvents.PHANTOM_BITE, SoundSource.PLAYERS, 1.0F, 0.3F + entity.getRandom().nextFloat() * 0.4F);

                            victim.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 1, false, true), entity);
                            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2, false, true), entity);
                            victim.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, true), entity);
                            victim.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, false, true), entity);
                            victim.addEffect(new MobEffectInstance(MobEffects.HUNGER, 160, 2, false, true), entity);
                            victim.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 3, false, true), entity);
                        }
                    }
                }
            }
        }
    }

    public int getCooldown() {
        return 0;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.VOID_PEARL)) {
                if (event.getSource().is(DamageTypes.DROWN) || event.getSource().is(DamageTypes.IN_WALL))
                    event.setCanceled(true);
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                if (ISpellstone.get(attacker).is(EnigmaticItems.VOID_PEARL)) {
                    event.getEntity().addEffect(new MobEffectInstance(MobEffects.WITHER, (int) (witheringTime.get() * 20), witheringLevel.get(), false, true), attacker);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onLivingDeath(@NotNull LivingDeathEvent event) {
            LivingEntity entity = event.getEntity();
            if (ISpellstone.get(entity).is(EnigmaticItems.VOID_PEARL) && entity.getRandom().nextFloat() < 0.01F * undeadProbability.getAsInt()) {
                if (event.getSource().is(Tags.DamageTypes.IS_TECHNICAL)) return;
                event.setCanceled(true);
                entity.setHealth(1);
            }
        }

        @SubscribeEvent
        private static void onApplyPotion(MobEffectEvent.@NotNull Applicable event) {
            MobEffectInstance instance = event.getEffectInstance();
            if (instance == null) return;
            if (instance.getEffect().is(EnigmaticTags.Effects.ALWAYS_APPLY)) return;
            ItemStack stack = ISpellstone.get(event.getEntity());
            MobEffectCategory category = instance.getEffect().value().getCategory();
            if (stack.is(EnigmaticItems.VOID_PEARL) || (stack.is(EnigmaticItems.THE_CUBE) && category.equals(MobEffectCategory.HARMFUL))) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
    }
}
