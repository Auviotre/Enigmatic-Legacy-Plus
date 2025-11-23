package auviotre.enigmatic.legacy.contents.entity;

import auviotre.enigmatic.legacy.packets.client.IchorSpriteBeamPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import auviotre.enigmatic.legacy.registries.EnigmaticMemories;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Optional;

public class IchorSprite extends PathfinderMob implements TraceableEntity {
    public static final int BEAM_RANGE = 8;
    protected static final ImmutableList<SensorType<? extends Sensor<? super IchorSprite>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.HURT_BY
    );
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            EnigmaticMemories.ICHOR_SPRITE_OWNER.get(),
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.PATH,
            MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
            MemoryModuleType.ANGRY_AT
    );
    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET = SynchedEntityData.defineId(IchorSprite.class, EntityDataSerializers.INT);
    @Nullable
    private LivingEntity clientSideCachedAttackTarget;
    private int clientSideAttackTime;

    public IchorSprite(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 32.0).add(Attributes.MOVEMENT_SPEED, 0.16F)
                .add(Attributes.FLYING_SPEED, 0.15F).add(Attributes.ATTACK_DAMAGE, 4.0).add(Attributes.FOLLOW_RANGE, 48.0);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_ATTACK_TARGET, 0);
    }

    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    protected Brain.Provider<IchorSprite> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return Ai.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    public Brain<IchorSprite> getBrain() {
        return (Brain<IchorSprite>) super.getBrain();
    }

    public void aiStep() {
        if (this.isAlive()) {
            if (this.level().isClientSide) {
                if (this.hasActiveAttackTarget()) {
                    if (this.clientSideAttackTime < 30) ++this.clientSideAttackTime;
                    LivingEntity target = this.getActiveAttackTarget();
                    if (target != null) {
                        this.getLookControl().setLookAt(target, 90.0F, 90.0F);
                        this.getLookControl().tick();
                    }
                }
                if (this.tickCount % 4 == 0) {
                    this.level().addParticle((ParticleOptions) EnigmaticParticles.ICHOR.get(), this.getRandomX(0.5), this.getY() + this.getRandom().nextFloat(), this.getRandomZ(0.5), 0.01, 0.01, 0.01);
                }
            }
            if ((this.getOwner() == null || !this.getOwner().isAlive()) && !this.level().isClientSide) {
                if (this.level() instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
                this.discard();
            }
        }
        super.aiStep();
    }

    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
            } else {
                this.moveRelative(this.getSpeed(), travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
            }
        }
        this.calculateEntityAnimation(false);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() == getOwner()) return false;
        return super.hurt(source, amount * 0.5F);
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
            if (this.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
                LivingEntity target = this.getTarget();
                if (target != null) {
                    target.addEffect(new MobEffectInstance(EnigmaticEffects.ICHOR_CORROSION, 600, 1), this);
                }
            }
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    public AABB getHitbox() {
        return this.getBoundingBox().inflate(BEAM_RANGE - 2);
    }

    protected void customServerAiStep() {
        this.level().getProfiler().push("ichorSpriteBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("ichorSpriteActivityUpdate");
        Ai.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof IchorSprite sprite) this.setOwner(sprite.getOwner());
    }

    public LivingEntity getOwner() {
        return this.getBrain().getMemory(EnigmaticMemories.ICHOR_SPRITE_OWNER.get()).orElse(null);
    }

    public void setOwner(LivingEntity owner) {
        this.getBrain().setMemory(EnigmaticMemories.ICHOR_SPRITE_OWNER.get(), owner);
    }

    @Nullable
    public LivingEntity getActiveAttackTarget() {
        if (!this.hasActiveAttackTarget()) return null;
        if (this.level().isClientSide) {
            if (this.clientSideCachedAttackTarget != null) {
                return this.clientSideCachedAttackTarget;
            } else {
                Entity entity = this.level().getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET));
                if (entity instanceof LivingEntity) {
                    this.clientSideCachedAttackTarget = (LivingEntity) entity;
                    return this.clientSideCachedAttackTarget;
                } else return null;
            }
        }
        return this.getTargetFromBrain();
    }

    void setActiveAttackTarget(int activeAttackTargetId) {
        if (this.clientSideCachedAttackTarget != this.level().getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET))) {
            this.clientSideCachedAttackTarget = null;
        }
        this.entityData.set(DATA_ID_ATTACK_TARGET, activeAttackTargetId);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_ID_ATTACK_TARGET.equals(key)) {
            this.clientSideAttackTime = 0;
            this.clientSideCachedAttackTarget = null;
        }

    }

    public float getAttackAnimationScale(float partialTick) {
        return (this.clientSideAttackTime + partialTick) / 30.0F;
    }

    public float getClientSideAttackTime() {
        return this.clientSideAttackTime;
    }

    public void setClientSideAttackTime(int time) {
        this.clientSideAttackTime = time;
    }

    public boolean hasActiveAttackTarget() {
        return this.entityData.get(DATA_ID_ATTACK_TARGET) != 0;
    }

    private static class FollowOwner extends Behavior<IchorSprite> {
        final int range;
        public FollowOwner(int range) {
            super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, EnigmaticMemories.ICHOR_SPRITE_OWNER.get(), MemoryStatus.VALUE_PRESENT), 1200);
            this.range = range;
        }

        protected boolean canStillUse(ServerLevel level, IchorSprite sprite, long gameTime) {
            return sprite.getOwner() != null && !sprite.getOwner().closerThan(sprite, range);
        }

        protected void tick(ServerLevel level, IchorSprite sprite, long gameTime) {
            LivingEntity owner = sprite.getOwner();
            if (owner != null && !owner.closerThan(sprite, range * 0.6)) {
                sprite.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(owner, true), 1.0F, range));
            }
        }
    }

    private static class HealOwner extends Behavior<IchorSprite> {
        public HealOwner() {
            super(ImmutableMap.of(EnigmaticMemories.ICHOR_SPRITE_OWNER.get(), MemoryStatus.VALUE_PRESENT), 20);
        }

        protected boolean checkExtraStartConditions(ServerLevel level, IchorSprite sprite) {
            if (sprite.getBrain().isActive(Activity.FIGHT)) return false;
            LivingEntity owner = sprite.getOwner();
            if (owner == null || !owner.closerThan(sprite, 5))
                return false;
            return super.checkExtraStartConditions(level, sprite);
        }

        protected void start(ServerLevel level, IchorSprite sprite, long gameTime) {
            LivingEntity owner = sprite.getOwner();
            if (owner != null && sprite.tickCount % 4 == 0) {
                float amount = Math.max(0.1F, (owner.getMaxHealth() - owner.getHealth()) * 0.1F);
                owner.heal(Math.min(amount, Math.max(sprite.getHealth() / 5.0F, 1.0F)));
                if (owner.getRandom().nextBoolean() && sprite.tickCount % 40 == 0) {
                    owner.addEffect(new MobEffectInstance(EnigmaticEffects.PURE_RESISTANCE, 100));
                }
            }
        }
    }

    private static class BeamAttack extends Behavior<IchorSprite> {
        private int attackTime = 0;

        public BeamAttack() {
            super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
        }

        private static LivingEntity getAttackTarget(LivingEntity entity) {
            return entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        }

        protected boolean checkExtraStartConditions(ServerLevel level, IchorSprite entity) {
            LivingEntity target = getAttackTarget(entity);
            return target != null && target.isAlive() && BehaviorUtils.canSee(entity, target);
        }

        protected boolean canStillUse(ServerLevel level, IchorSprite entity, long gameTime) {
            LivingEntity target = getAttackTarget(entity);
            return target != null && entity.isAlive() && entity.distanceToSqr(target) < BEAM_RANGE * BEAM_RANGE;
        }

        protected void start(ServerLevel level, IchorSprite entity, long gameTime) {
            this.attackTime = -10;
            LivingEntity target = getAttackTarget(entity);
            if (target != null)
                entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
            entity.hasImpulse = true;
        }

        protected void stop(ServerLevel level, IchorSprite sprite, long gameTime) {
            sprite.setActiveAttackTarget(0);
            sprite.setTarget(null);
        }

        protected void tick(ServerLevel level, IchorSprite sprite, long gameTime) {
            LivingEntity target = getAttackTarget(sprite);
            if (target != null) {
                sprite.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
                if (!sprite.hasLineOfSight(target)) {
                    this.doStop(level, sprite, gameTime);
                } else {
                    ++this.attackTime;
                    if (this.attackTime == 0) {
                        sprite.setActiveAttackTarget(target.getId());
                    } else if (this.attackTime >= 30) {
                        float f = 1.0F;
                        if (sprite.level().getDifficulty() == Difficulty.HARD) f += 2.0F;
                        PacketDistributor.sendToPlayersNear(level, null, sprite.getX(), sprite.getY(), sprite.getZ(), 16,
                                new IchorSpriteBeamPacket(sprite.position(), target.getEyePosition().add(0, target.getBbHeight() * 0.5, 0)));
                        if (target.hurt(sprite.damageSources().indirectMagic(sprite, sprite), f)) {
                            target.addEffect(new MobEffectInstance(EnigmaticEffects.ICHOR_CORROSION, 600), sprite);
                        }
                        attackTime = 0;
                        sprite.setClientSideAttackTime(0);
                        sprite.doHurtTarget(target);
                        this.doStop(level, sprite, gameTime);
                    }
                }
            }
        }
    }

    protected static class Ai {
        protected static Brain<?> makeBrain(IchorSprite sprite, Brain<IchorSprite> brain) {
            initCoreActivity(brain);
            initIdleActivity(brain);
            initFightActivity(sprite, brain);
            brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
            brain.setDefaultActivity(Activity.IDLE);
            brain.useDefaultActivity();
            return brain;
        }

        private static void initCoreActivity(Brain<IchorSprite> brain) {
            brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                    new Swim(0.8F),
                    new LookAtTargetSink(45, 90),
                    new MoveToTargetSink(),
                    new HealOwner()
            ));
        }

        private static void initIdleActivity(Brain<IchorSprite> brain) {
            brain.addActivity(Activity.IDLE, ImmutableList.of(
                    Pair.of(0, StartAttacking.create(Ai::getOwnerTarget)),
                    Pair.of(1, StartAttacking.create(Ai::findNearestValidAttackTarget)),
                    Pair.of(2, StartAttacking.create(Ai::getHurtBy)),
                    Pair.of(2, new FollowOwner(6)),
                    Pair.of(3, new RunOne<>(ImmutableList.of(
                            Pair.of(RandomStroll.fly(1.0F), 2),
                            Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 2),
                            Pair.of(new DoNothing(30, 60), 1)
                    ))),
                    Pair.of(4, createIdleLookBehaviors())
            ));
        }

        private static void initFightActivity(IchorSprite sprite, Brain<IchorSprite> brain) {
            brain.addActivityWithConditions(Activity.FIGHT, ImmutableList.of(
                    Pair.of(0, StopAttackingIfTargetInvalid.create(entity -> !isNearestValidAttackTarget(sprite, entity) && getOwnerTarget(sprite).isEmpty())),
                    Pair.of(1, BackUpIfTooClose.create(BEAM_RANGE, 0.8F)),
                    Pair.of(2, new FollowOwner(9)),
                    Pair.of(2, new BeamAttack()),
                    Pair.of(3, createOutOfReach(1.0F))
            ), ImmutableSet.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT)
            ));
        }

        private static RunOne<IchorSprite> createIdleLookBehaviors() {
            return new RunOne<>(ImmutableList.of(
                    Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 1),
                    Pair.of(SetEntityLookTarget.create(EnigmaticEntities.ICHOR_SPRITE.get(), 8.0F), 1),
                    Pair.of(SetEntityLookTarget.create(8.0F), 1),
                    Pair.of(new DoNothing(30, 60), 1)
            ));
        }

        private static boolean isNearestValidAttackTarget(Mob mob, LivingEntity target) {
            return findNearestValidAttackTarget(mob).filter(entity -> entity == target && entity.isAlive()).isPresent();
        }

        private static Optional<? extends LivingEntity> getOwnerTarget(Mob mob) {
            Optional<LivingEntity> memory = mob.getBrain().getMemory(EnigmaticMemories.ICHOR_SPRITE_OWNER.get());
            if (memory.isPresent()) {
                LivingEntity owner = memory.get();
                LivingEntity hurtByMob = owner.getLastHurtByMob();
                LivingEntity lastHurtMob = owner.getLastHurtMob();
                if (hurtByMob != null && hurtByMob != owner) return Optional.of(hurtByMob);
                if (owner instanceof Mob mobOwner && mobOwner.getTarget() != null) return Optional.of(mobOwner.getTarget());
                if (lastHurtMob != owner) return Optional.ofNullable(lastHurtMob);
            }
            return Optional.empty();
        }

        private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Mob mob) {
            Optional<LivingEntity> owner = mob.getBrain().getMemory(EnigmaticMemories.ICHOR_SPRITE_OWNER.get());
            Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(mob, MemoryModuleType.ANGRY_AT);
            if (optional.isPresent() && optional.get() != owner.orElse(null) && Sensor.isEntityAttackableIgnoringLineOfSight(mob, optional.get())) {
                return optional;
            } else {
                Optional<? extends LivingEntity> optional1 = getTargetIfWithinRange(mob);
                Optional<? extends LivingEntity> entity = optional1.isPresent() ? optional1 : mob.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
                return entity.orElse(null) != owner.orElse(null) ? entity : Optional.empty();
            }
        }

        public static Optional<? extends LivingEntity> getHurtBy(Mob mob) {
            Optional<LivingEntity> optional = mob.getBrain().getMemory(EnigmaticMemories.ICHOR_SPRITE_OWNER.get());
            return mob.getBrain().getMemory(MemoryModuleType.HURT_BY).map(DamageSource::getEntity).filter((entity) -> entity instanceof LivingEntity && entity != optional.orElse(null)).map((entity) -> (LivingEntity) entity);
        }

        private static Optional<? extends LivingEntity> getTargetIfWithinRange(Mob mob) {
            return mob.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER).filter(entity -> entity.closerThan(mob, BEAM_RANGE));
        }

        protected static void updateActivity(IchorSprite sprite) {
            Brain<IchorSprite> brain = sprite.getBrain();
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
            sprite.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
        }


        public static BehaviorControl<Mob> createOutOfReach(float speedModifier) {
            return BehaviorBuilder.create(instance -> instance.group(instance.registered(MemoryModuleType.WALK_TARGET), instance.registered(MemoryModuleType.LOOK_TARGET), instance.present(MemoryModuleType.ATTACK_TARGET), instance.registered(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES))
                    .apply(instance, (walkTarget, tracker, entityAccessor, nearAccessor) -> (level, entity, time) -> {
                        LivingEntity livingentity = instance.get(entityAccessor);
                        Optional<NearestVisibleLivingEntities> optional = instance.tryGet(nearAccessor);
                        if (optional.isPresent() && optional.get().contains(livingentity) && BehaviorUtils.isWithinAttackRange(entity, livingentity, 1)) {
                            walkTarget.erase();
                        } else {
                            tracker.set(new EntityTracker(livingentity, true));
                            walkTarget.set(new WalkTarget(new EntityTracker(livingentity, true), speedModifier, 0));
                        }

                        return true;
                    }));
        }
    }
}
