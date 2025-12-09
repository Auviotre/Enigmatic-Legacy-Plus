package auviotre.enigmatic.legacy.contents.entity.projectile;

import auviotre.enigmatic.legacy.contents.item.tools.IchorSpear;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownIchorSpear extends AbstractSpear {
    public ThrownIchorSpear(EntityType<? extends ThrownIchorSpear> type, Level world) {
        super(type, world, EnigmaticItems.ICHOR_SPEAR.toStack());
    }

    public ThrownIchorSpear(LivingEntity entity, Level world, ItemStack stack) {
        super(EnigmaticEntities.THROWN_ICHOR_SPEAR.get(), entity, world, stack);
    }

    public ThrownIchorSpear(Level world, double x, double y, double z, ItemStack stack) {
        super(EnigmaticEntities.THROWN_ICHOR_SPEAR.get(), x, y, z, world, stack);
    }

    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        Entity owner = this.getOwner();
        living.igniteForSeconds(5);
        living.addEffect(new MobEffectInstance(EnigmaticEffects.ICHOR_CORROSION, IchorSpear.duration.get(), IchorSpear.amplifier.get()), owner == null ? this : owner);
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide && !this.inGround) {
            Vec3 vec3 = this.getDeltaMovement();
            double dx = vec3.x;
            double dy = vec3.y;
            double dz = vec3.z;
            double length = vec3.length() * 1.25D;
            for (int i = 0; i < length; ++i) {
                this.level().addParticle(EnigmaticParticles.ICHOR.get(), this.getRandomX(0.0F) + dx * (double) i / length, this.getRandomY() + dy * (double) i / length, this.getRandomZ(0.0F) + dz * (double) i / length, -dx * 0.1, -dy * 0.1, -dz * 0.1);
            }
        }
    }

    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        Entity owner = this.getOwner();
        double damage = IchorSpear.baseDamage.get() * Math.sqrt(this.getDeltaMovement().length());
        DamageSource damageSource = this.damageSources().trident(this, owner == null ? this : owner);
        if (entity.hurt(damageSource, (float) damage)) {
            if (entity.getType() == EntityType.ENDERMAN) return;
            if (entity instanceof LivingEntity living) this.doPostHurtEffects(living);
        }
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 0.1F);
        this.discard();
    }

    protected ItemStack getPickupItem() {
        return this.item;
    }

    protected ItemStack getDefaultPickupItem() {
        return EnigmaticItems.ICHOR_SPEAR.toStack();
    }
}
