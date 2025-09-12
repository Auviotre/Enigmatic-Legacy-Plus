package auviotre.enigmatic.legacy.contents.effect;

import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

public class MoltenHeart extends MobEffect {
    public MoltenHeart() {
        super(MobEffectCategory.BENEFICIAL, 0xE66410);
        NeoForge.EVENT_BUS.register(this);
    }

    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.isOnFire()) entity.clearFire();
        return super.applyEffectTick(entity, amplifier);
    }

    @SubscribeEvent
    public void onEntityHurt(@NotNull LivingIncomingDamageEvent event) {
        if (event.getEntity().hasEffect(EnigmaticEffects.MOLTEN_HEART)) {
            if (event.getSource().type().effects().equals(DamageEffects.BURNING)) {
                event.setCanceled(true);
            }
        }
    }
}
