package auviotre.enigmatic.legacy.contents.effect;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

public class BlazingMight extends MobEffect {
    public BlazingMight() {
        super(MobEffectCategory.BENEFICIAL, 0xEA6363);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, EnigmaticLegacy.location("effect.blazing_might"), 3, AttributeModifier.Operation.ADD_VALUE);
        NeoForge.EVENT_BUS.register(this);
    }

    public static void addAmplifier(LivingEntity entity, int add, int duration) {
        if (add < 1) return;
        int strength = add - 1;
        if (entity.hasEffect(EnigmaticEffects.BLAZING_MIGHT)) {
            MobEffectInstance instance = entity.getEffect(EnigmaticEffects.BLAZING_MIGHT);
            strength = instance == null ? 0 : instance.getAmplifier();
            entity.removeEffect(EnigmaticEffects.BLAZING_MIGHT);
            boolean flag = entity.getMainHandItem().is(EnigmaticItems.INFERNAL_SPEAR);
            strength = Math.max(strength, Math.min(strength + add, flag ? 9 : 4));
            duration = Math.max(instance == null ? 0 : instance.getDuration() / 2, duration);
        }
        entity.addEffect(new MobEffectInstance(EnigmaticEffects.BLAZING_MIGHT, duration, strength, true, true));
    }

    @SubscribeEvent
    public void onEntityHurt(LivingDamageEvent.@NotNull Pre event) {
        LivingEntity victim = event.getEntity();
        if (victim.hasEffect(EnigmaticEffects.BLAZING_MIGHT) && event.getNewDamage() > 0.0F) {
            MobEffectInstance effect = victim.getEffect(EnigmaticEffects.BLAZING_MIGHT);
            assert effect != null;
            victim.removeEffect(EnigmaticEffects.BLAZING_MIGHT);
            if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.BERSERK_EMBLEM)) {
                event.setNewDamage(Math.max(0.0F, event.getNewDamage() - effect.getAmplifier() * 2.0F - 1.0F));
            }
        }
    }
}
