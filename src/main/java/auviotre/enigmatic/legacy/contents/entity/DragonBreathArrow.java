package auviotre.enigmatic.legacy.contents.entity;

import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class DragonBreathArrow extends AbstractArrow {

    public DragonBreathArrow(EntityType<? extends AbstractArrow> type, Level world) {
        super(type, world);
        this.setBaseDamage(this.getBaseDamage() * 1.8);
    }

    public DragonBreathArrow(double x, double y, double z, Level level, @Nullable ItemStack bow) {
        super(EnigmaticEntities.DRAGON_BREATH_ARROW.get(), x, y, z, level, ItemStack.EMPTY, bow);
        this.setBaseDamage(this.getBaseDamage() * 1.8);
    }

    public void tick() {
        if (!this.isNoGravity() && !this.isNoPhysics()) this.setDeltaMovement(this.getDeltaMovement().scale(1.01));
        super.tick();
        Vec3 movement = this.getDeltaMovement();
        if (this.level().isClientSide) {
            double dx = movement.x;
            double dy = movement.y;
            double dz = movement.z;
            double length = movement.length() * 1.25D;
            for (int i = 0; i < length; ++i) {
                this.level().addParticle(ParticleTypes.DRAGON_BREATH, this.getRandomX(0.0F) + dx * (double) i / length, this.getRandomY() + dy * (double) i / length, this.getRandomZ(0.0F) + dz * (double) i / length, -dx * 0.1, -dy * 0.1, -dz * 0.1);
            }
        } else if (this.inGround) this.summonAreaEffect();
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide && hitResult.getType() == HitResult.Type.ENTITY && !this.ownedBy(((EntityHitResult) hitResult).getEntity())) {
            this.setPos(this.getPosition(0.0F).add(this.getDeltaMovement().scale(0.5F)));
            this.summonAreaEffect();
        }
    }

    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        hitResult.getEntity().invulnerableTime = 0;
    }

    private void summonAreaEffect() {
        float speed = (float) this.getDeltaMovement().length();
        float dmg = Mth.ceil(Mth.clamp(Math.sqrt(speed) * this.getBaseDamage(), 0.0, 2.147483647E9)) / 4.0F;
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0, 2.0, 4.0));
        AreaEffectCloud effectCloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
        if (this.getOwner() instanceof LivingEntity owner) effectCloud.setOwner(owner);
        effectCloud.setParticle(ParticleTypes.DRAGON_BREATH);
        effectCloud.setRadius(1.5F);
        effectCloud.setDuration(100);
        effectCloud.setRadiusOnUse(0.1F);
        effectCloud.setDurationOnUse(-1);
        effectCloud.setWaitTime(1);
        effectCloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, Mth.floor(dmg)));
        ItemStack dragonBow = this.getWeaponItem();
        if (dragonBow != null) {
            PotionContents contents = dragonBow.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            List<MobEffectInstance> instances = contents.customEffects();
            if (!instances.isEmpty()) {
                for (MobEffectInstance instance : instances) {
                    effectCloud.addEffect(instance);
                }
            }
        }
        if (!entities.isEmpty()) {
            for (LivingEntity entity : entities) {
                if (this.distanceToSqr(entity) < 12.0 && entity != this.getOwner()) {
                    effectCloud.setPos(entity.getX(), entity.getY(), entity.getZ());
                    break;
                }
            }
        }
        this.level().addFreshEntity(effectCloud);
        this.discard();
    }

    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }
}