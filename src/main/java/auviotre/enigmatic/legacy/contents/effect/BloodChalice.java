package auviotre.enigmatic.legacy.contents.effect;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BloodChalice extends MobEffect {
    public BloodChalice() {
        super(MobEffectCategory.BENEFICIAL, 0xBF0003);
    }

    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            AttributeInstance attribute = entity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);
            MobEffectInstance effect = entity.getEffect(EnigmaticEffects.BLOOD_CHALICE);
            if (attribute != null && effect != null) {
                ResourceLocation location = EnigmaticLegacy.location("effect.blood_chalice");
                attribute.removeModifier(location);
                attribute.addPermanentModifier(new AttributeModifier(location, 0.04 * (effect.getDuration() / (300 - amplifier * 40)), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
        return true;
    }

    public void removeAttributeModifiers(AttributeMap attributeMap) {
        AttributeInstance attribute = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
        if (attribute != null) {
            attribute.removeModifier(EnigmaticLegacy.location("effect.blood_chalice"));
        }
    }
}
