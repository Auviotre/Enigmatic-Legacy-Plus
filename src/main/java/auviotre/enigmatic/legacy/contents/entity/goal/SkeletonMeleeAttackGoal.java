package auviotre.enigmatic.legacy.contents.entity.goal;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class SkeletonMeleeAttackGoal extends Goal {
    private final AbstractSkeleton mob;

    public SkeletonMeleeAttackGoal(AbstractSkeleton mob) {
        this.mob = mob;
    }

    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !EnigmaticHandler.isCurseBoosted(mob) || mob.isUsingItem()) return false;
        return target.distanceToSqr(this.mob) <= 1.4D && this.mob.getRandom().nextInt(reducedTickDelay(3)) == 0;
    }

    public void start() {
        LivingEntity target = this.mob.getTarget();
        super.start();
        if (target != null) {
            if (this.mob.isWithinMeleeAttackRange(target) && this.mob.getSensing().hasLineOfSight(target)) {
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(target);
                this.mob.push(target);
                target.push(this.mob);
                target.knockback(this.mob.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + 0.5, Mth.sin(this.mob.getYRot() * 0.017453292F), -Mth.cos(this.mob.getYRot() * 0.017453292F));
            }
        }
    }
}
