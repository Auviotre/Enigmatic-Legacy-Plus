package auviotre.enigmatic.legacy.contents.entity.projectile;

import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class StarlightMeteor extends ThrowableItemProjectile {
    public StarlightMeteor(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    protected Item getDefaultItem() {
        return EnigmaticItems.STARLIGHT_PARTICLE.get();
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    public boolean isCurrentlyGlowing() {
        return true;
    }

    public void handleEntityEvent(byte id) {
        if (id == 3) {
            double d0 = 0.2;
            for(int i = 0; i < 6; ++i) {
                this.level().addParticle(EnigmaticParticles.BLUE_STAR_DUST.get(), this.getX(), this.getY(), this.getZ(), (this.random.nextFloat() - 0.5F) * d0, (this.random.nextFloat() - 0.5F) * d0, (this.random.nextFloat() - 0.5F) * d0);
                this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), (this.random.nextFloat() - 0.5F) * d0, (this.random.nextFloat() - 0.5F) * d0, (this.random.nextFloat() - 0.5F) * d0);
            }
            this.level().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    public void tick() {
        this.setDeltaMovement(this.getDeltaMovement().scale(0.985F));
        super.tick();
        double d0 = 0.08;
        Vec3 vec3 = this.getDeltaMovement().scale(0.2);
        if (this.tickCount % 2 == 0)
            this.level().addParticle(EnigmaticParticles.BLUE_STAR_DUST.get(), this.getX(), this.getY(), this.getZ(), (this.random.nextFloat() - 0.5F) * d0, (this.random.nextFloat() - 0.5F) * d0, (this.random.nextFloat() - 0.5F) * d0);
        this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), (this.random.nextFloat() - 0.5F) * d0, (this.random.nextFloat() - 0.5F) * d0, (this.random.nextFloat() - 0.5F) * d0);
        this.level().addParticle(EnigmaticParticles.STARLIGHT.get(), this.getX(), this.getY(), this.getZ(), vec3.x, vec3.y, vec3.z);
    }

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.hurt(livingEntity.damageSources().magic(), 8.0F)) {
                livingEntity.kill();
            }
        }
        this.dropItem();
        this.discard();
    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.dropItem();
        this.discard();
    }

    private void dropItem() {
        this.level().broadcastEntityEvent(this, (byte) 3);
        Entity owner = this.getOwner();
        boolean flag = owner instanceof LivingEntity livingEntity && livingEntity.hasInfiniteMaterials();
        if (!this.level().isClientSide && !flag) {
            ItemEntity item = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem(), 0, 0, 0);
            item.setDefaultPickUpDelay();
            item.lifespan = 400;
            item.setGlowingTag(true);
            this.level().addFreshEntity(item);
        }
    }
}
