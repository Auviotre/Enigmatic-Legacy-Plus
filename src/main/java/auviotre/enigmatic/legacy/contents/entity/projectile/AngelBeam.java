package auviotre.enigmatic.legacy.contents.entity.projectile;

import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class AngelBeam extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Vector3f> DATA_FROM_POS = SynchedEntityData.defineId(AngelBeam.class, EntityDataSerializers.VECTOR3);

    public AngelBeam(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public AngelBeam(Level level, LivingEntity owner, Vec3 beginning, float velocity) {
        this(EnigmaticEntities.ANGEL_BEAM.get(), level);
        this.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, velocity, 0.0F);
        this.setOwner(owner);
        this.setPos(beginning);
        this.entityData.set(DATA_FROM_POS, new Vector3f((float) beginning.x, (float) beginning.y, (float) beginning.z));
    }

    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.level().isClientSide() && vec3.length() > 0.5) {
            Vec3 pos = this.position();
            for (double i = 0; i < vec3.length();) {
                this.level().addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, vec3.x * 0.05, vec3.y * 0.05, vec3.z * 0.05);
                this.level().addParticle(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 0, 0, 0);
                double rand = 0.5 + random.nextDouble() * 0.3;
                i += rand;
                pos = pos.add(vec3.scale(-rand));
            }
        }
        if (this.tickCount >= 48) this.discard();
    }

    protected @Nullable ParticleOptions getTrailParticle() {
        return null;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FROM_POS, new Vector3f(0, 0, 0));
    }

    public Vector3f getBeginning() {
        return this.entityData.get(DATA_FROM_POS);
    }

    protected void onHitEntity(EntityHitResult result) {
        if (this.getOwner() instanceof LivingEntity owner) {
            Entity entity = result.getEntity();
            int bless = entity.getPersistentData().getInt("ResonanceAngelBless");
            entity.getPersistentData().putInt("ResonanceAngelBless", bless | 2);
            entity.hurt(this.damageSources().indirectMagic(this, owner), (float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE));
        }
    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.setDeltaMovement(this.getDeltaMovement().normalize().scale(result.distanceTo(this)));
    }
}