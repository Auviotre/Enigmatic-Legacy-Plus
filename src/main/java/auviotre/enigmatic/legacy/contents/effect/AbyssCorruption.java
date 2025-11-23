package auviotre.enigmatic.legacy.contents.effect;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class AbyssCorruption extends MobEffect {
    public AbyssCorruption() {
        super(MobEffectCategory.HARMFUL, 0x382c4d);
        ResourceLocation location = EnigmaticLegacy.location("effect.abyss_corruption");
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, location, -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, location, -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        NeoForge.EVENT_BUS.register(this);
    }

    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(EnigmaticDamageTypes.source(entity.level(), EnigmaticDamageTypes.ABYSS), 2.0F * (float) Math.pow(2, (double) amplifier / 2));
        entity.invulnerableTime = 0;
        return false;
    }

    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int i = 32 >> (amplifier / 2);
        return i == 0 || duration % i == 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEffectRemoveHigh(MobEffectEvent.@NotNull Remove event) {
        if (event.getEffect().equals(EnigmaticEffects.ABYSS_CORRUPTION)) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEffectApply(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance() != null && event.getEffectInstance().is(EnigmaticEffects.ABYSS_CORRUPTION)) {
            event.setResult(MobEffectEvent.Applicable.Result.APPLY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEffectRemoveLow(MobEffectEvent.@NotNull Remove event) {
        Holder<MobEffect> effect = event.getEffect();
        if (effect.equals(EnigmaticEffects.ABYSS_CORRUPTION)) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onDeath(@NotNull LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.hasEffect(EnigmaticEffects.ABYSS_CORRUPTION)) {
            List<LivingEntity> entities = victim.level().getEntitiesOfClass(LivingEntity.class, victim.getBoundingBox().inflate(4.2));
            for (LivingEntity entity : entities) {
                if (!entity.isAlive() || entity.is(victim)) continue;
                if (EnigmaticHandler.isTheWorthyOne(entity)) continue;
                entity.addEffect(Objects.requireNonNull(victim.getEffect(EnigmaticEffects.ABYSS_CORRUPTION)), victim);
                entity.hurt(EnigmaticDamageTypes.source(entity.level(), EnigmaticDamageTypes.ABYSS, victim), victim.getMaxHealth() * 0.8F);
            }
        }
    }
}
