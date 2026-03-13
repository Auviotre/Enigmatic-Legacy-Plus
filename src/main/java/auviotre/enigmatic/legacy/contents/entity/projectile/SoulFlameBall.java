package auviotre.enigmatic.legacy.contents.entity.projectile;

import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SoulFlameBall extends Projectile {
    @Nullable
    private UUID targetUUID;
    @Nullable
    private Entity cachedTarget;

    public SoulFlameBall(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public SoulFlameBall(Level world, LivingEntity owner, LivingEntity target) {
        this(EnigmaticEntities.SOUL_FLAME_BALL.get(), world);
        this.setPos(owner.position().add(0, owner.getBbHeight() * 0.4F, 0));
        this.setOwner(owner);
        this.setTarget(target);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public void checkTarget() {
        Entity owner = this.getOwner();
        if (owner == null || this.tickCount < 10) return;
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(16.0), entity -> entity.isAlive() && entity instanceof Targeting);
        for (LivingEntity entity : entities) {
            LivingEntity target = ((Targeting) entity).getTarget();
            if (target != null && target.is(owner)) {
                this.setTarget(entity);
                break;
            }
        }
    }

    public void tick() {
        super.tick();
        Entity owner = this.getOwner();
        if (this.tickCount > 125 || owner == null) {
            this.discard();
            return;
        }
        if (!level().isClientSide()) {
            Vec3 movement = this.getDeltaMovement();
            Entity cachedTarget = this.getCachedTarget();
            if (cachedTarget != null && cachedTarget.isAlive()) {
                Vec3 target = new Vec3(cachedTarget.getX(), cachedTarget.getY(0.5F), cachedTarget.getZ());
                Vec3 delta = target.subtract(this.position());
                if (delta.length() < 16F) {
                    delta = delta.normalize().scale(0.56).add(movement);
                } else this.checkTarget();
                movement = delta.scale(0.7F);
                this.setDeltaMovement(movement);
                double d4 = movement.horizontalDistance();
                this.setYRot((float) (Mth.atan2(-movement.x, -movement.z) * 57.2957763671875));
                this.setXRot((float) (Mth.atan2(movement.y, d4) * 57.2957763671875));
            } else this.checkTarget();
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult))
                this.onHit(hitresult);
            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
        } else {
            if (this.random.nextInt(3) == 0)
                this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY(0.5), this.getZ(), 0, 0, 0);
            if (this.random.nextInt(3) == 0)
                this.level().addParticle(ParticleTypes.SOUL, this.getX(), this.getY(0.5), this.getZ(), 0, 0, 0);

            Vec3 move = this.getDeltaMovement().scale(0.3);
            for (int i = 1; i < 3; i++) {
                double x = this.getX() + move.x * i;
                double y = this.getY(0.5) + move.y * i;
                double z = this.getZ() + move.z * i;
                this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, move.x, move.y, move.z);
                this.level().addParticle(ParticleTypes.SOUL, x, y, z, move.x, move.y, move.z);
            }
        }
    }

    protected boolean canHitEntity(Entity entity) {
        return !this.ownedBy(entity) && super.canHitEntity(entity);
    }

    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity owner) {
            Entity target = hitResult.getEntity();
            if (target == this.getOwner()) return;
            int level = target.getPersistentData().getInt("IllusionSoulLevel");
            float damage = Math.min(2.0F + level * 0.8F, owner.getMaxHealth() * 0.75F);
            target.hurt(EnigmaticDamageTypes.source(level(), DamageTypes.MAGIC, this, this.getOwner()), damage);
            target.getPersistentData().putInt("IllusionSoulLevel", level + 1);
            Vec3 mv = this.getDeltaMovement().scale(0.5);
            if (level() instanceof ServerLevel serverLevel)
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX() + mv.x, this.getY() + mv.y, this.getZ() + mv.z, 1, 0, 0, 0, 0);
            level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT);
            this.discard();
        }
    }

    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!this.level().isClientSide) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
            this.discard();
        }
    }

    public void setTarget(@Nullable Entity entity) {
        if (entity != null) {
            this.targetUUID = entity.getUUID();
            this.cachedTarget = entity;
        }
    }

    public @Nullable Entity getCachedTarget() {
        if (this.cachedTarget != null && !this.cachedTarget.isRemoved()) {
            return this.cachedTarget;
        } else if (this.targetUUID != null && this.level() instanceof ServerLevel server) {
            this.cachedTarget = server.getEntity(this.targetUUID);
            return this.cachedTarget;
        } else {
            return null;
        }
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.targetUUID != null) tag.putUUID("TargetUUID", this.targetUUID);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("TargetUUID")) {
            this.targetUUID = tag.getUUID("TargetUUID");
            this.cachedTarget = null;
        }
    }
}
