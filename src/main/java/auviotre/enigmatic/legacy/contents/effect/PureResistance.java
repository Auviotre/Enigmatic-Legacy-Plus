package auviotre.enigmatic.legacy.contents.effect;

import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

public class PureResistance extends MobEffect {
    public PureResistance() {
        super(MobEffectCategory.BENEFICIAL, 0xFFBF4B);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
        if (event.getAmount() >= Float.MAX_VALUE) return;
        if (event.getEntity().hasEffect(EnigmaticEffects.PURE_RESISTANCE)) {
            MobEffectInstance effect = event.getEntity().getEffect(EnigmaticEffects.PURE_RESISTANCE);
            if (effect != null && effect.getAmplifier() >= 4) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent.@NotNull Pre event) {
        if (event.getNewDamage() >= Float.MAX_VALUE) return;
        if (event.getEntity().hasEffect(EnigmaticEffects.PURE_RESISTANCE)) {
            MobEffectInstance effect = event.getEntity().getEffect(EnigmaticEffects.PURE_RESISTANCE);
            if (effect != null) {
                float modifier = Math.max(0.0F, 0.2F * (4 - effect.getAmplifier()));
                event.setNewDamage(event.getNewDamage() * modifier);
            }
        }
    }
}
