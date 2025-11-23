package auviotre.enigmatic.legacy.contents.effect;

import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import org.jetbrains.annotations.NotNull;

public class Poison extends MobEffect {
    public Poison() {
        super(MobEffectCategory.HARMFUL, 8889187);
        NeoForge.EVENT_BUS.register(this);
    }

    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getHealth() > 1.5F) {
            Registry<DamageType> types = entity.damageSources().damageTypes;
            Holder.Reference<DamageType> type = types.getHolder(NeoForgeMod.POISON_DAMAGE).orElse(types.getHolderOrThrow(DamageTypes.MAGIC));
            entity.hurt(new DamageSource(type), 1.5F);
        }
        return true;
    }

    public String getDescriptionId() {
        return "effect.minecraft.poison";
    }

    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int i = 25 >> amplifier;
        return i == 0 || duration % i == 0;
    }

    @SubscribeEvent
    public void onLivingHeal(@NotNull LivingHealEvent event) {
        if (event.getEntity().hasEffect(EnigmaticEffects.POISON)) {
            event.setAmount(event.getAmount() * 0.25F);
        }
    }

    @SubscribeEvent
    public void onDeath(@NotNull LivingDeathEvent event) {
        if (event.getSource().is(Tags.DamageTypes.IS_POISON)) {
            event.getEntity().setHealth(1.0F);
            event.setCanceled(true);
        }
    }
}
