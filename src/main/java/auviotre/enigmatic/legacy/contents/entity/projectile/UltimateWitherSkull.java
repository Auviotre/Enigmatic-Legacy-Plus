package auviotre.enigmatic.legacy.contents.entity.projectile;

import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class UltimateWitherSkull extends AbstractHurtingProjectile {
	private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(UltimateWitherSkull.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(UltimateWitherSkull.class, EntityDataSerializers.INT);

	int targetID;
	Entity target;

	public UltimateWitherSkull(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
		super(type, level);
		this.targetID = -1;
		this.target = null;
	}

	public UltimateWitherSkull(Level level, LivingEntity owner, LivingEntity target) {
		super(EnigmaticEntities.ULTIMATE_WITHER_SKULL.get(), owner, Vec3.ZERO, level);
		this.accelerationPower = 0.0;
		this.target = target;
		this.targetID = target == null ? -1 : target.getId();
		this.entityData.set(UltimateWitherSkull.DATA_TARGET_ID, this.targetID);
		this.setDeltaMovement(Vec3.ZERO);
	}

	public boolean isOnFire() {
		return false;
	}

	public float getBlockExplosionResistance(Explosion explosionIn, BlockGetter worldIn, BlockPos pos, BlockState blockStateIn, FluidState fluidState, float explosionPower) {
		return this.isDangerous() && !blockStateIn.is(BlockTags.WITHER_IMMUNE) ? Math.min(0.8F, explosionPower) : explosionPower;
	}

	public void tick() {
		super.tick();
		if (this.tickCount > 10 && this.tickCount < 400) {
			if (this.target == null) {
				int targetId = this.entityData.get(UltimateWitherSkull.DATA_TARGET_ID);
				if (targetId != -1) {
					try {
						this.target = this.level().getEntity(targetId);
					} catch (Exception exception) {
						exception.printStackTrace(System.err);
					}
				}
			}

			if (this.target != null && this.target.isAlive()) {
				final Vec3 thisVec = this.position().add(0, this.getBbHeight() * 0.5 ,0);
				final Vec3 targetVec = this.target.position().add(0, this.target.getBbHeight() * 0.5 ,0);
				final Vec3 diffVec = targetVec.subtract(thisVec);
				final Vec3 motionVec = diffVec.normalize().scale(0.6);

				double distance = this.distanceTo(this.target);
				Vec3 formerMotion = this.getDeltaMovement().scale(0.6);

				if (distance < 6D && distance != 0) {
					this.setDeltaMovement(new Vec3(formerMotion.x + (motionVec.x * 2D / distance), formerMotion.y + (motionVec.y * 2D / distance), formerMotion.z + (motionVec.z * 2D / distance)).normalize().multiply(1.4D, 1.4D, 1.4D));
				}
				this.accelerationPower = motionVec.length() / 6.0D;
			}
		}

		if (this.level().isClientSide || this.getOwner() == null) return;

		if (this.tickCount <= 10) {
			this.setPos(this.getX(), this.getY(), this.getZ());
			this.accelerationPower = 0.1F;
		} else if (this.tickCount >= 400) {
			this.onHit(BlockHitResult.miss(this.position(), Direction.DOWN, this.blockPosition()));
		}
	}

	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		Entity entity = result.getEntity();
		if (this.level() instanceof ServerLevel level) {
			if (entity == this.getOwner()) return;
			boolean flag;
			if (this.getOwner() instanceof LivingEntity owner) {
				DamageSource source = this.damageSources().indirectMagic(this, owner);
				flag = entity.hurt(source,  this.isDangerous() ? 24.0F : 8.0F);
				if (flag) {
					if (entity.isAlive()) {
						EnchantmentHelper.doPostAttackEffects(level, entity, source);
					}
				}
			} else flag = entity.hurt(this.damageSources().magic(), this.isDangerous() ? 16.0F : 5.0F);


			if (flag && entity instanceof LivingEntity living) {
				int i = 20;
				if (this.level().getDifficulty() == Difficulty.HARD) i = 50;
				living.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * i, 1), this.getEffectSource());
			}
		}
	}

	protected void onHit(HitResult result) {
		super.onHit(result);
		if (this.level() instanceof ServerLevel level) {
			List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3));
			for (LivingEntity entity : entities) entity.invulnerableTime = 0;
			level.explode(this, this.getX(), this.getY(), this.getZ(), this.isDangerous() ? 1.5F : 1.0F, false, Level.ExplosionInteraction.MOB);
			level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 8, 0.05, 0.05, 0.05, 0.1);
			this.discard();
		}
	}

	public boolean isPickable() {
		return false;
	}

	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(UltimateWitherSkull.DATA_DANGEROUS, false);
		builder.define(UltimateWitherSkull.DATA_TARGET_ID, -1);
	}

	public boolean isDangerous() {
		return this.entityData.get(UltimateWitherSkull.DATA_DANGEROUS);
	}

	public void setDangerous(boolean invulnerable) {
		this.entityData.set(DATA_DANGEROUS, invulnerable);
	}

	protected boolean shouldBurn() {
		return false;
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("dangerous", this.isDangerous());
		compound.putInt("boundTargetID", this.entityData.get(UltimateWitherSkull.DATA_TARGET_ID));
	}

	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setDangerous(compound.getBoolean("dangerous"));
		this.entityData.set(UltimateWitherSkull.DATA_TARGET_ID, compound.getInt("boundTargetID"));
	}
}