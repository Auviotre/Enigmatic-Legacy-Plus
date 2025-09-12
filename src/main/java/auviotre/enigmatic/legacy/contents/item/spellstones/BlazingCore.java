package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class BlazingCore extends SpellstoneItem {

    public BlazingCore() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
        NeoForge.EVENT_BUS.register(this);

        this.immunityList.add(DamageTypes.IN_FIRE);
        this.immunityList.add(DamageTypes.ON_FIRE);
        this.immunityList.add(DamageTypes.CAMPFIRE);
        this.immunityList.add(DamageTypes.HOT_FLOOR);
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
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blazingCore1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blazingCore2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blazingCore3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blazingCore4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blazingCore5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blazingCore6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blazingCore7");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blazingCore8");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    public int getCooldown() {
        return 0;
    }

    public void curioTick(@NotNull SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.isOnFire()) entity.clearFire();
        if (!entity.getActiveEffects().isEmpty()) {
            Collection<MobEffectInstance> effects = new ArrayList<>(entity.getActiveEffects());
            for (MobEffectInstance effect : effects) {
                if (effect.is(EnigmaticEffects.MOLTEN_HEART)) {
                    effect.duration = effect.mapDuration(duration -> entity.tickCount % 2 == 0 ? duration : duration + 1);
                } else {
                    effect.tick(entity, () -> {
                    });
                }
            }
        }

        if (!entity.isInFluidType(NeoForgeMod.LAVA_TYPE.value())) {
            EnigmaticData data = entity.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            if (data.getFireImmunityTimer() > 0) {
                int down = entity.isInFluidType(NeoForgeMod.WATER_TYPE.value()) ? 200 : 25;
                data.setFireImmunityTimer(data.getFireImmunityTimer() - down);
            } else if (data.getFireImmunityTimerLast() > 0) {
                data.setFireImmunityTimer(0);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamage(@NotNull LivingIncomingDamageEvent event) {
        if (event.isCanceled()) return;
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player && player.hasInfiniteMaterials()) return;
        if (ISpellstone.get(entity).is(this) && !entity.hasEffect(MobEffects.FIRE_RESISTANCE) && !entity.hasEffect(EnigmaticEffects.MOLTEN_HEART)) {
            if (event.getSource().is(DamageTypes.LAVA) && entity.level() instanceof ServerLevel server) {
                EnigmaticData data = entity.getData(EnigmaticAttachments.ENIGMATIC_DATA);
                if (data.getFireImmunityTimer() < data.getFireImmunityCap()) {
                    if (data.getFireImmunityTimer() < (data.getFireImmunityCap() - 50))
                        event.setCanceled(true);

                    if (data.getFireImmunityTimer() == 0) {
                        server.playSound(null, entity.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 0.5F + entity.getRandom().nextFloat() * 0.5F);
                        double hOffset = entity.getBbWidth() / 6;
                        double yOffset = entity.getBbHeight() / 4;
                        server.sendParticles(ParticleTypes.SMOKE, entity.getX(), entity.getY(0.35), entity.getZ(), 12, hOffset, yOffset, hOffset, 0.01);
                    }

                    data.setFireImmunityTimer(data.getFireImmunityTimer() + 200);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingDamageEvent.@NotNull Pre event) {
        if (ISpellstone.get(event.getEntity()).is(this)) {
            Entity entity = event.getSource().getEntity();
            if (entity == null) return;
            if (entity.getType().is(EntityTypeTags.CAN_BREATHE_UNDER_WATER)) {
                event.setNewDamage(event.getNewDamage() * (float) CONFIG.SPELLSTONES.BCVulnerabilityModifier.getAsDouble());
            }
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingDamageEvent.@NotNull Post event) {
        LivingEntity victim = event.getEntity();
        Entity entity = event.getSource().getEntity();
        if (entity instanceof LivingEntity attacker && ISpellstone.get(victim).is(this) && !attacker.fireImmune()) {
            DamageSource source = event.getSource();
            if (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO) || source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                attacker.hurt(EnigmaticDamageTypes.source(victim.level(), DamageTypes.ON_FIRE, victim), (float) CONFIG.SPELLSTONES.damageFeedback.getAsDouble());
                attacker.igniteForSeconds(CONFIG.SPELLSTONES.ignitionFeedback.get());
            }
        }
    }
}
