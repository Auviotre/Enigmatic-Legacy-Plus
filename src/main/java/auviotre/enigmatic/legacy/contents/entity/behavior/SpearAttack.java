package auviotre.enigmatic.legacy.contents.entity.behavior;

import auviotre.enigmatic.legacy.api.entity.SpearAttackMob;
import auviotre.enigmatic.legacy.contents.item.tools.IchorSpear;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.projectile.ProjectileUtil;

public class SpearAttack<E extends Mob & SpearAttackMob> extends Behavior<E> {
    private int attackDelay = 0;
    private int cooldown = 0;

    public SpearAttack() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
    }

    private static LivingEntity getAttackTarget(LivingEntity shooter) {
        return shooter.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    protected boolean checkExtraStartConditions(ServerLevel level, E owner) {
        LivingEntity target = getAttackTarget(owner);
        return owner.isHolding(is -> is.getItem() instanceof IchorSpear) && BehaviorUtils.canSee(owner, target) && owner.closerThan(target, 16);
    }

    protected boolean canStillUse(ServerLevel level, E entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(level, entity);
    }

    protected void tick(ServerLevel level, E owner, long gameTime) {
        this.lookAtTarget(owner, getAttackTarget(owner));
        this.attack(owner);
    }

    protected void stop(ServerLevel level, E entity, long gameTime) {
        if (entity.isUsingItem()) entity.stopUsingItem();

        if (entity.isHolding((is) -> is.getItem() instanceof IchorSpear)) {
            entity.setCharging(false);
        }
    }

    private void attack(E shooter) {
        if (shooter.isCharging()) {
            if (!shooter.isUsingItem()) shooter.setCharging(false);
            if (shooter.getTicksUsingItem() >= this.attackDelay) {
                shooter.performRangedAttack(getAttackTarget(shooter), 1.2F);
                shooter.stopUsingItem();
                shooter.setCharging(false);
                this.cooldown = 10 + shooter.getRandom().nextInt(30);
            }
        } else {
            if (cooldown <= 0) {
                shooter.startUsingItem(ProjectileUtil.getWeaponHoldingHand(shooter, item -> item instanceof IchorSpear));
                shooter.setCharging(true);
                this.attackDelay = 15 + shooter.getRandom().nextInt(30);
            } else this.cooldown--;
        }
    }

    private void lookAtTarget(Mob shooter, LivingEntity target) {
        shooter.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
    }
}
