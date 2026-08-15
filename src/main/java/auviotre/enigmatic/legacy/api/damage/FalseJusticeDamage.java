package auviotre.enigmatic.legacy.api.damage;

import net.minecraft.world.damagesource.DamageSource;

public interface FalseJusticeDamage {
    void falseJustice$setBypassAll(boolean z);
    boolean falseJustice$isBypassAll();
    static FalseJusticeDamage from(DamageSource source) {
        return (FalseJusticeDamage) source;
    }
}
