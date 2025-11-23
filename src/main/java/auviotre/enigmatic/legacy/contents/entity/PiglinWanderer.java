package auviotre.enigmatic.legacy.contents.entity;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.entity.SpearAttackMob;
import auviotre.enigmatic.legacy.contents.entity.behavior.SpearAttack;
import auviotre.enigmatic.legacy.contents.entity.projectile.ThrownIchorSpear;
import auviotre.enigmatic.legacy.contents.item.tools.IchorSpear;
import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class PiglinWanderer extends AbstractPiglin implements SpearAttackMob {
    protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinWanderer>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.HURT_BY,
            SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR
    );
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.DOORS_TO_CLOSE,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS,
            MemoryModuleType.NEARBY_ADULT_PIGLINS,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.ANGRY_AT,
            MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
            MemoryModuleType.HOME
    );
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(PiglinWanderer.class, EntityDataSerializers.BOOLEAN);

    public PiglinWanderer(EntityType<? extends AbstractPiglin> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 32.0).add(Attributes.MOVEMENT_SPEED, 0.34).add(Attributes.ATTACK_KNOCKBACK, 1.6).add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_CHARGING, false);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        Ai.initMemories(this);
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, EnigmaticItems.ICHOR_SPEAR.toStack());
    }

    protected void customServerAiStep() {
        this.timeInOverworld = 0;
        this.level().getProfiler().push("piglinWandererBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        Ai.updateActivity(this);
        super.customServerAiStep();
    }

    public boolean doHurtTarget(Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag) this.knockback(0.1, -Mth.sin(this.getYRot() * 0.017453292F), Mth.cos(this.getYRot() * 0.017453292F));
        return flag;
    }

    protected Brain.Provider<PiglinWanderer> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return Ai.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    public Brain<PiglinWanderer> getBrain() {
        return (Brain<PiglinWanderer>) super.getBrain();
    }

    public boolean wantsToPickUp(ItemStack stack) {
        return stack.is(EnigmaticItems.ICHOR_SPEAR) && super.wantsToPickUp(stack);
    }

    protected boolean canHunt() {
        return false;
    }

    protected void playConvertedSound() {
        this.makeSound(SoundEvents.PIGLIN_CONVERTED_TO_ZOMBIFIED);
    }

    protected void playAngrySound() {
        this.makeSound(SoundEvents.PIGLIN_ANGRY);
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (this.level().isClientSide) return false;
        if (flag && source.getEntity() instanceof LivingEntity)
            Ai.wasHurtBy(this, (LivingEntity) source.getEntity());
        return flag;
    }

    public PiglinArmPose getArmPose() {
        if (this.isAggressive() && this.isHoldingMeleeWeapon()) return PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON;
        if (this.isCharging()) return PiglinArmPose.CROSSBOW_HOLD;
        return PiglinArmPose.DEFAULT;
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_IS_CHARGING);
    }

    public void setCharging(boolean isCharging) {
        this.entityData.set(DATA_IS_CHARGING, isCharging);
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(this, (item) -> item instanceof IchorSpear);
        ItemStack stack = this.getItemInHand(hand);
        float velocity = 1.1F + this.level().getDifficulty().getId() * 0.02F;
        if (stack.getItem() instanceof IchorSpear && this.level() instanceof ServerLevel server) {
            ThrownIchorSpear spear = new ThrownIchorSpear(this, server, stack.copyWithCount(1));
            Vec3 pos = target.position().add(target.getDeltaMovement().scale(0.5));
            double dx = pos.x() - this.getX();
            double dy = pos.y() + target.getBbHeight() / 3 - spear.getY();
            double dz = pos.z() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            spear.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            spear.shoot(dx, dy + dist * 0.284, dz, velocity, 4 - this.level().getDifficulty().getId());
            server.addFreshEntity(spear);
            this.playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, 0.84F);
        }
    }

    private static class WeaponSwitch extends Behavior<PiglinWanderer> {
        public WeaponSwitch() {
            super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
        }

        private static LivingEntity getAttackTarget(LivingEntity shooter) {
            return shooter.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        }

        protected void start(ServerLevel level, PiglinWanderer piglin, long gameTime) {
            LivingEntity target = getAttackTarget(piglin);
            if (target == null) return;
            ItemStack backup = ItemStack.EMPTY;
            if (!piglin.getPersistentData().getCompound("BackupItem").isEmpty())
                backup = ItemStack.parse(piglin.registryAccess(), piglin.getPersistentData().getCompound("BackupItem")).orElse(ItemStack.EMPTY);
            if (target.closerThan(piglin, 5) && piglin.isHolding(is -> is.getItem() instanceof IchorSpear)) {
                InteractionHand hand = piglin.getMainHandItem().getItem() instanceof IchorSpear ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                ItemStack copy = piglin.getItemInHand(hand).copy();
                if (backup.isEmpty())
                    piglin.setItemSlot(EquipmentSlot.MAINHAND, Items.GOLDEN_SWORD.getDefaultInstance());
                else piglin.setItemSlot(EquipmentSlot.MAINHAND, backup.copy());
                piglin.getPersistentData().put("BackupItem", copy.save(piglin.registryAccess()));
                piglin.setCharging(false);
            }
            if (!target.closerThan(piglin, 9) && piglin.isHolding(is -> is.getItem() instanceof SwordItem)) {
                InteractionHand hand = piglin.getMainHandItem().getItem() instanceof SwordItem ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                ItemStack copy = piglin.getItemInHand(hand).copy();
                if (backup.isEmpty())
                    piglin.setItemSlot(EquipmentSlot.MAINHAND, EnigmaticItems.ICHOR_SPEAR.toStack());
                else piglin.setItemSlot(EquipmentSlot.MAINHAND, backup.copy());
                piglin.getPersistentData().put("BackupItem", copy.save(piglin.registryAccess()));
                piglin.setCharging(false);
            }
            doStop(level, piglin, gameTime);
        }
    }

    protected static class Ai {
        protected static Brain<?> makeBrain(PiglinWanderer wanderer, Brain<PiglinWanderer> brain) {
            initCoreActivity(brain);
            initIdleActivity(brain);
            initFightActivity(wanderer, brain);
            brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
            brain.setDefaultActivity(Activity.IDLE);
            brain.useDefaultActivity();
            return brain;
        }

        protected static void initMemories(PiglinWanderer wanderer) {
            GlobalPos globalpos = GlobalPos.of(wanderer.level().dimension(), wanderer.blockPosition());
            wanderer.getBrain().setMemory(MemoryModuleType.HOME, globalpos);
        }

        private static void initCoreActivity(Brain<PiglinWanderer> brain) {
            brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                    new LookAtTargetSink(45, 90),
                    new MoveToTargetSink(),
                    InteractWithDoor.create(),
                    StopBeingAngryIfTargetDead.create()
            ));
        }

        private static void initIdleActivity(Brain<PiglinWanderer> brain) {
            brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                    StartAttacking.create(Ai::findNearestValidAttackTarget),
                    createIdleLookBehaviors(),
                    createIdleMovementBehaviors(),
                    SetLookAndInteract.create(EntityType.PLAYER, 4)
            ));
        }

        private static void initFightActivity(PiglinWanderer wanderer, Brain<PiglinWanderer> brain) {
            brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                    StopAttackingIfTargetInvalid.create((entity) -> !isNearestValidAttackTarget(wanderer, entity)),
                    BehaviorBuilder.triggerIf(Ai::hasSpear, BackUpIfTooClose.create(10, 0.75F)),
                    SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(piglin -> hasSpear(piglin) ? (isCharging(piglin) ? 0.4F : 0.6F) : 1.0F),
                    new SpearAttack<>(),
                    new WeaponSwitch(),
                    MeleeAttack.create(20)
            ), MemoryModuleType.ATTACK_TARGET);
        }

        private static RunOne<PiglinWanderer> createIdleLookBehaviors() {
            return new RunOne<>(ImmutableList.of(
                    Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 1),
                    Pair.of(SetEntityLookTarget.create(EntityType.PIGLIN, 8.0F), 1),
                    Pair.of(SetEntityLookTarget.create(EntityType.PIGLIN_BRUTE, 8.0F), 1),
                    Pair.of(SetEntityLookTarget.create(8.0F), 1),
                    Pair.of(new DoNothing(30, 60), 1)
            ));
        }

        private static RunOne<PiglinWanderer> createIdleMovementBehaviors() {
            return new RunOne<>(ImmutableList.of(
                    Pair.of(RandomStroll.stroll(0.6F), 2),
                    Pair.of(InteractWith.of(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2),
                    Pair.of(InteractWith.of(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2),
                    Pair.of(StrollToPoi.create(MemoryModuleType.HOME, 0.6F, 2, 100), 2),
                    Pair.of(StrollAroundPoi.create(MemoryModuleType.HOME, 0.6F, 5), 2),
                    Pair.of(new DoNothing(30, 60), 1)
            ));
        }

        protected static void updateActivity(PiglinWanderer wanderer) {
            Brain<PiglinWanderer> brain = wanderer.getBrain();
            Activity activity = brain.getActiveNonCoreActivity().orElse(null);
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
            Activity activity1 = brain.getActiveNonCoreActivity().orElse(null);
            if (activity != activity1) playActivitySound(wanderer);
            wanderer.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
        }

        private static boolean isNearestValidAttackTarget(AbstractPiglin piglin, LivingEntity target) {
            return findNearestValidAttackTarget(piglin).filter((entity) -> entity == target).isPresent();
        }

        private static Optional<? extends LivingEntity> findNearestValidAttackTarget(AbstractPiglin piglin) {
            Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(piglin, MemoryModuleType.ANGRY_AT);
            if (optional.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight(piglin, optional.get())) {
                return optional;
            } else {
                Optional<? extends LivingEntity> optional1 = getTargetIfWithinRange(piglin);
                return optional1.isPresent() ? optional1 : piglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
            }
        }

        private static Optional<? extends LivingEntity> getTargetIfWithinRange(AbstractPiglin piglin) {
            return piglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER).filter(entity -> entity.closerThan(piglin, 16.0));
        }

        protected static void wasHurtBy(PiglinWanderer wanderer, LivingEntity target) {
            if (!(target instanceof AbstractPiglin)) {
                PiglinAi.maybeRetaliate(wanderer, target);
            }
        }

        private static boolean hasSpear(LivingEntity piglin) {
            return piglin.isHolding((is) -> is.getItem() instanceof IchorSpear);
        }


        private static boolean isCharging(LivingEntity entity) {
            return entity instanceof PiglinWanderer piglin && piglin.isCharging();
        }

        private static void playActivitySound(PiglinWanderer wanderer) {
            wanderer.getBrain().getActiveNonCoreActivity().ifPresent((activity) -> {
                if (activity == Activity.FIGHT) wanderer.playAngrySound();
            });
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onEntitySpawn(@NotNull FinalizeSpawnEvent event) {
            if (event.getEntity().getType().equals(EntityType.PIGLIN) && (event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION)) {
                ServerLevelAccessor level = event.getLevel();
                if (level.getRandom().nextFloat() > 0.1F) return;
                PiglinWanderer piglinWanderer = EnigmaticEntities.PIGLIN_WANDERER.get().create(level.getLevel());
                SpawnGroupData spawnData = event.getSpawnData();
                if (piglinWanderer != null) {
                    event.setSpawnCancelled(true);
                    event.setCanceled(true);
                    piglinWanderer.setPos(event.getX(), event.getY(), event.getZ());
                    piglinWanderer.finalizeSpawn(level, event.getDifficulty(), event.getSpawnType(), spawnData);
                    level.addFreshEntity(piglinWanderer);
                }
            }
        }
    }
}
