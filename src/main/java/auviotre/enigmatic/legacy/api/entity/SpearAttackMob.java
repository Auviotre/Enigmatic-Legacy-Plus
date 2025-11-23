package auviotre.enigmatic.legacy.api.entity;

import net.minecraft.world.entity.monster.RangedAttackMob;

public interface SpearAttackMob extends RangedAttackMob {
    boolean isCharging();

    void setCharging(boolean isCharging);

    default void onSpearAttackPerform() {

    }
}
