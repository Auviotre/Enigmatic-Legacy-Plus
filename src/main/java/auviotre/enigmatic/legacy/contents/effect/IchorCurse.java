package auviotre.enigmatic.legacy.contents.effect;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.IchorPermeation;
import auviotre.enigmatic.legacy.contents.entity.IchorSprite;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

public class IchorCurse extends MobEffect {
    private static final ResourceLocation EFFECT_ID = EnigmaticLegacy.location("ichor_curse_boost");

    public IchorCurse() {
        super(MobEffectCategory.NEUTRAL, 0xFFBF4B);
        NeoForge.EVENT_BUS.register(this);
    }

    public ParticleOptions createParticleOptions(MobEffectInstance effect) {
        return EnigmaticParticles.ICHOR_CURSE.get();
    }

    @SubscribeEvent
    public void onTick(EntityTickEvent.@NotNull Post event) {
        if (event.getEntity() instanceof Monster monster && monster.level() instanceof ServerLevel server) {
            IchorPermeation data = monster.getData(EnigmaticAttachments.ICHOR_PERMEATION);
            if (data.isInfected() && monster.tickCount % 2 == 0) {
                double hOffset = monster.getBbWidth() / 3;
                double yOffset = monster.getBbHeight() / 4;
                ParticleOptions particle = EnigmaticParticles.ICHOR_CURSE.get();
                server.sendParticles(particle, monster.getX(), monster.getY(0.5), monster.getZ(), 1, hOffset, yOffset, hOffset, 0);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEffectApply(MobEffectEvent.@NotNull Applicable event) {
        if (!EnigmaticHandler.isTheOne(event.getEntity()) && event.getEffectInstance() != null && event.getEffectInstance().is(EnigmaticEffects.ICHOR_CURSE)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }


    @SubscribeEvent
    public void onEffectApply(MobEffectEvent.@NotNull Added event) {
        if (event.getEntity().hasEffect(EnigmaticEffects.ICHOR_CURSE)) {
            MobEffectInstance instance = event.getEffectInstance();
            MobEffectInstance old = event.getOldEffectInstance();
            if (instance != null && old != null && instance.is(EnigmaticEffects.ICHOR_CORROSION)) {
                if (instance.getDuration() < old.getDuration()) instance.duration = old.getDuration();
                int amplifier = instance.getAmplifier();
                instance.amplifier = Math.max(Math.min(4, 1 + amplifier + old.getAmplifier()), amplifier);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDeath(@NotNull LivingDeathEvent event) {
        if (event.isCanceled()) return;
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (!attacker.hasEffect(EnigmaticEffects.ICHOR_CURSE) || event.getEntity().getType().is(Tags.EntityTypes.BOSSES))
                return;
            if (event.getEntity() instanceof Monster monster && monster.getTarget() == attacker && monster.level() instanceof ServerLevel level) {
                if (!monster.level().dimension().equals(Level.NETHER)) return;
                if (monster.getSpawnType() == null || !monster.getSpawnType().equals(MobSpawnType.NATURAL)) return;
                IchorPermeation data = monster.getData(EnigmaticAttachments.ICHOR_PERMEATION);
                if (!data.isInfected() && monster.hasLineOfSight(attacker)) {
                    data.setInfected(true);
                    for (int i = 0; i < monster.getRandom().nextInt(1, 3); i++) {
                        IchorSprite sprite = EnigmaticEntities.ICHOR_SPRITE.get().create(level);
                        if (sprite != null) {
                            sprite.setPos(monster.getEyePosition());
                            sprite.setOwner(monster);
                            level.addFreshEntity(sprite);
                            level.sendParticles(ParticleTypes.EXPLOSION, sprite.getX(), sprite.getY(), sprite.getZ(), 1, 0, 0, 0, 0);
                        }
                    }
                    monster.getAttributes().addTransientAttributeModifiers(ImmutableMultimap.of(Attributes.MAX_HEALTH, new AttributeModifier(EFFECT_ID, 0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
                    event.setCanceled(true);
                    monster.setHealth(monster.getMaxHealth() * 0.75F);
                    attacker.knockback(0.2F, Mth.sin((float) Math.toRadians(monster.getYRot())), -Mth.cos((float) Math.toRadians(monster.getYRot())));
                    monster.addEffect(new MobEffectInstance(EnigmaticEffects.PURE_RESISTANCE, 200, 3));
                }
            }
        }
    }
}
