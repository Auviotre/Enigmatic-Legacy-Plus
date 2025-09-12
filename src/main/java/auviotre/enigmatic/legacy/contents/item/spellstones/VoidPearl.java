package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class VoidPearl extends SpellstoneItem {
    public VoidPearl() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
        NeoForge.EVENT_BUS.register(this);

        this.immunityList.add(DamageTypes.DROWN);
        this.immunityList.add(DamageTypes.IN_WALL);
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
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidPearl7", ChatFormatting.GOLD, CONFIG.SPELLSTONES.undeadProbability.get() + "%");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.isOnFire()) entity.clearFire();
        if (entity.getAirSupply() < entity.getMaxAirSupply()) entity.setAirSupply(entity.getMaxAirSupply());

        for (MobEffectInstance effect : new ArrayList<>(entity.getActiveEffects())) {
            if (effect.getEffect().is(EnigmaticTags.Effects.ALWAYS_APPLY)) continue;
            entity.removeEffect(effect.getEffect());
        }

        if (entity.tickCount % 10 == 0) {
            List<LivingEntity> entities = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(CONFIG.SPELLSTONES.shadowRange.get()));
            entities.removeIf(victim -> ISpellstone.get(victim).is(this));
            entities.removeIf(victim -> victim instanceof OwnableEntity ownable && entity == ownable.getOwner());
            entities.remove(entity);
            for (LivingEntity victim : entities) {
                if (victim.level().getMaxLocalRawBrightness(victim.blockPosition(), 0) < 3 || victim instanceof FlyingMob) {
                    if (!(entity instanceof Player player) || !(victim instanceof Player vPlayer) || player.canHarmPlayer(vPlayer)) {
                        if (victim.hurt(EnigmaticDamageTypes.source(victim.level(), EnigmaticDamageTypes.DARKNESS, entity), CONFIG.SPELLSTONES.darknessDamage.getAsInt())) {
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

    @SubscribeEvent
    public void onAttack(LivingDamageEvent.Post event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK)) {
            if (ISpellstone.get(attacker).is(this)) {
                event.getEntity().addEffect(new MobEffectInstance(MobEffects.WITHER, (int) (CONFIG.SPELLSTONES.witheringTime.get() * 20), CONFIG.SPELLSTONES.witheringLevel.get(), false, true), attacker);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (ISpellstone.get(entity).is(this) && entity.getRandom().nextFloat() < 0.01F * CONFIG.SPELLSTONES.undeadProbability.getAsInt()) {
            event.setCanceled(true);
            entity.setHealth(1);
        }
    }

    @SubscribeEvent
    public void onApplyPotion(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance() == null) return;
        if (event.getEffectInstance().getEffect().is(EnigmaticTags.Effects.ALWAYS_APPLY)) return;
        if (ISpellstone.get(event.getEntity()).is(this)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }
}
