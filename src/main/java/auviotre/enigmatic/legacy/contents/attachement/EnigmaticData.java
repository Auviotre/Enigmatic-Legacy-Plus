package auviotre.enigmatic.legacy.contents.attachement;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class EnigmaticData implements INBTSerializable<CompoundTag> {
    private boolean magnetRingEffect = false;
    private boolean unlockedNarrator = false;
    private boolean nebulaPower = false;
    private boolean isElytraBoosting = false;
    private boolean isForbiddenCursed = false;
    private long timeWithCurses, timeWithoutCurses;
    private int fireImmunityTimer = 0;
    private int fireImmunityTimerLast = 0;
    private int inBeaconRange = 0;
    private int witherKills = 0;
    private float etherealShield = 0;

    public boolean isMagnetRingEnable() {
        return magnetRingEffect;
    }

    public void setMagnetRingEnable(boolean enable) {
        this.magnetRingEffect = enable;
    }

    public void toggleMagnetRingEffect() {
        this.magnetRingEffect = !magnetRingEffect;
    }

    public long getTimeWithoutCurses() {
        return this.timeWithoutCurses;
    }

    public void setTimeWithoutCurses(long time) {
        this.timeWithoutCurses = time;
    }

    public boolean getUnlockedNarrator() {
        return this.unlockedNarrator;
    }

    public void setUnlockedNarrator(Boolean unlockedNarrator) {
        this.unlockedNarrator = unlockedNarrator;
    }

    public boolean getNebulaPower() {
        return this.nebulaPower;
    }

    public void setNebulaPower(boolean power) {
        this.nebulaPower = power;
    }

    public boolean isElytraBoosting() {
        return this.isElytraBoosting;
    }

    public void setElytraBoosting(boolean boosting) {
        this.isElytraBoosting = boosting;
    }

    public boolean isForbiddenCursed() {
        return this.isForbiddenCursed;
    }

    public void setForbiddenCursed(boolean forbidden) {
        this.isForbiddenCursed = forbidden;
    }

    public boolean isInBeaconRange() {
        return this.inBeaconRange > 0;
    }

    public void setInBeaconRangeTick(int tick) {
        this.inBeaconRange = tick;
    }

    public void InBeaconRangeTick() {
        this.inBeaconRange--;
    }

    public int getWitherKills() {
        return this.witherKills;
    }

    public void setWitherKills(int amount) {
        this.witherKills = amount;
    }

    public float getEtherealShield() {
        return this.etherealShield;
    }

    public void setEtherealShield(float amount) {
        this.etherealShield = amount;
    }

    public int getFireImmunityTimer() {
        return fireImmunityTimer;
    }

    public void setFireImmunityTimer(int timer) {
        this.fireImmunityTimerLast = this.fireImmunityTimer;
        this.fireImmunityTimer = Math.clamp(timer, 0, getFireImmunityCap());
    }

    public int getFireImmunityTimerLast() {
        return this.fireImmunityTimerLast;
    }

    public int getFireImmunityCap() {
        return 30000;
    }

    public long getTimeWithCurses() {
        return this.timeWithCurses;
    }

    public void setTimeWithCurses(long time) {
        this.timeWithCurses = time;
    }

    public void incrementTimeWithoutCurses() {
        ++this.timeWithoutCurses;
    }

    public void incrementTimeWithCurses() {
        ++this.timeWithCurses;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("MagnetRingEffect", this.magnetRingEffect);
        tag.putBoolean("UnlockedNarrator", this.unlockedNarrator);
        tag.putBoolean("NebulaPower", this.nebulaPower);
        tag.putBoolean("ElytraBoosting", this.isElytraBoosting);
        tag.putBoolean("isForbiddenCursed", this.isForbiddenCursed);
        tag.putInt("FireImmunityTimer", this.fireImmunityTimer);
        tag.putInt("FireImmunityTimerLast", this.fireImmunityTimerLast);
        tag.putInt("InBeaconRangeTick", this.inBeaconRange);
        tag.putInt("WitherKills", this.witherKills);
        tag.putFloat("EtherealShield", this.etherealShield);
        tag.putLong("timeWithCurses", this.timeWithCurses);
        tag.putLong("timeWithoutCurses", this.timeWithoutCurses);
        return tag;
    }

    public void load(@NotNull CompoundTag tag) {
        this.magnetRingEffect = tag.getBoolean("MagnetRingEffect");
        this.unlockedNarrator = tag.getBoolean("UnlockedNarrator");
        this.nebulaPower = tag.getBoolean("NebulaPower");
        this.isElytraBoosting = tag.getBoolean("ElytraBoosting");
        this.isForbiddenCursed = tag.getBoolean("isForbiddenCursed");
        this.fireImmunityTimer = tag.getInt("FireImmunityTimer");
        this.fireImmunityTimerLast = tag.getInt("FireImmunityTimerLast");
        this.inBeaconRange = tag.getInt("InBeaconRangeTick");
        this.witherKills = tag.getInt("WitherKills");
        this.etherealShield = tag.getFloat("EtherealShield");
        this.timeWithCurses = tag.getLong("timeWithCurses");
        this.timeWithoutCurses = tag.getLong("timeWithoutCurses");
    }

    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return save();
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        load(tag);
    }
}
