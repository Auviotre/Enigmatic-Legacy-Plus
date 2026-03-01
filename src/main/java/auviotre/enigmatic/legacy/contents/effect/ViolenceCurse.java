package auviotre.enigmatic.legacy.contents.effect;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;

public class ViolenceCurse extends MobEffect {
    public ViolenceCurse() {
        super(MobEffectCategory.NEUTRAL, 0x392e4d);
        ResourceLocation location = EnigmaticLegacy.location("effect.violence_curse");
        this.addAttributeModifier(Attributes.ATTACK_SPEED, location, 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        NeoForge.EVENT_BUS.register(this);
    }
    public ParticleOptions createParticleOptions(MobEffectInstance effect) {
        return EnigmaticParticles.VIOLENCE_CURSE.get();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEffectRemoveHigh(MobEffectEvent.@NotNull Remove event) {
        if (event.getEffect().equals(EnigmaticEffects.VIOLENCE_CURSE)) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEffectApply(MobEffectEvent.@NotNull Applicable event) {
        if (event.getEffectInstance().is(EnigmaticEffects.VIOLENCE_CURSE)) {
            if (!EnigmaticHandler.hasCurio(event.getEntity(), EnigmaticItems.VIOLENCE_SCROLL)) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            } else event.setResult(MobEffectEvent.Applicable.Result.APPLY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEffectRemoveLow(MobEffectEvent.@NotNull Remove event) {
        if (event.getEffect().equals(EnigmaticEffects.VIOLENCE_CURSE)) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent.@NotNull Pre event) {
        MobEffectInstance effect = event.getEntity().getEffect(EnigmaticEffects.VIOLENCE_CURSE);
        if (effect != null) event.setNewDamage(event.getNewDamage() * (1.01F + 0.01F * effect.getAmplifier()));
    }
}
