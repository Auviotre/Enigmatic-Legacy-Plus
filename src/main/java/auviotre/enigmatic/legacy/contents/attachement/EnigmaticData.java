package auviotre.enigmatic.legacy.contents.attachement;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.UUID;

public class EnigmaticData implements INBTSerializable<CompoundTag> {
    private @Nullable UUID engineHook;
    private int spellstoneCooldown = 0;
    private boolean magnetRingEffect = false;
    private boolean unlockedNarrator = false;
    private boolean nebulaPower = false;
    private boolean isElytraBoosting = false;
    private boolean isForbiddenCursed = false;
    private boolean isThirstForbiddenCursed = false;
    private long timeWithCurses, timeWithoutCurses;
    private int fireImmunityTimer = 0;
    private int fireImmunityTimerLast = 0;
    private int inBeaconRange = 0;
    private int witherKills = 0;
    private float etherealShield = 0;
    private long etheriumShieldTick = 0;
    private int reviveCooldown = 0;

    public UUID getEngineHook() {
        return engineHook;
    }

    public void setEngineHook(UUID engineHook) {
        this.engineHook = engineHook;
    }

    public int getSpellstoneCooldown() {
        return spellstoneCooldown;
    }

    public void setSpellstoneCooldown(int spellstoneCooldown) {
        this.spellstoneCooldown = spellstoneCooldown;
    }

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

    public boolean isThirstForbiddenCursed() {
        return this.isThirstForbiddenCursed;
    }

    public void setThirstForbiddenCursed(boolean forbidden) {
        this.isThirstForbiddenCursed = forbidden;
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

    public long getEtheriumShieldTick() {
        return this.etheriumShieldTick;
    }

    public void setEtheriumShieldTick(long tick) {
        this.etheriumShieldTick = tick;
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

    public int getReviveCooldown() {
        return this.reviveCooldown;
    }

    public void setReviveCooldown(int reviveCooldown) {
        this.reviveCooldown = reviveCooldown;
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (this.engineHook != null) {
            tag.putUUID("EngineHook", engineHook);
        }
        tag.putBoolean("MagnetRingEffect", this.magnetRingEffect);
        tag.putBoolean("UnlockedNarrator", this.unlockedNarrator);
        tag.putBoolean("NebulaPower", this.nebulaPower);
        tag.putBoolean("ElytraBoosting", this.isElytraBoosting);
        tag.putBoolean("isForbiddenCursed", this.isForbiddenCursed);
        tag.putBoolean("isThirstForbiddenCursed", this.isThirstForbiddenCursed);
        tag.putInt("SpellstoneCooldown", this.spellstoneCooldown);
        tag.putInt("FireImmunityTimer", this.fireImmunityTimer);
        tag.putInt("FireImmunityTimerLast", this.fireImmunityTimerLast);
        tag.putInt("InBeaconRangeTick", this.inBeaconRange);
        tag.putInt("WitherKills", this.witherKills);
        tag.putFloat("EtherealShield", this.etherealShield);
        tag.putLong("EtheriumShieldTick", this.etheriumShieldTick);
        tag.putLong("timeWithCurses", this.timeWithCurses);
        tag.putLong("timeWithoutCurses", this.timeWithoutCurses);
        tag.putInt("reviveCooldown", this.reviveCooldown);
        return tag;
    }

    public void load(HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        if (tag.contains("EngineHook")) this.engineHook = tag.getUUID("EngineHook");
        this.magnetRingEffect = tag.getBoolean("MagnetRingEffect");
        this.unlockedNarrator = tag.getBoolean("UnlockedNarrator");
        this.nebulaPower = tag.getBoolean("NebulaPower");
        this.isElytraBoosting = tag.getBoolean("ElytraBoosting");
        this.isForbiddenCursed = tag.getBoolean("isForbiddenCursed");
        this.isThirstForbiddenCursed = tag.getBoolean("isThirstForbiddenCursed");
        this.spellstoneCooldown = tag.getInt("SpellstoneCooldown");
        this.fireImmunityTimer = tag.getInt("FireImmunityTimer");
        this.fireImmunityTimerLast = tag.getInt("FireImmunityTimerLast");
        this.inBeaconRange = tag.getInt("InBeaconRangeTick");
        this.witherKills = tag.getInt("WitherKills");
        this.etherealShield = tag.getFloat("EtherealShield");
        this.etheriumShieldTick = tag.getLong("EtheriumShieldTick");
        this.timeWithCurses = tag.getLong("timeWithCurses");
        this.timeWithoutCurses = tag.getLong("timeWithoutCurses");
        this.reviveCooldown = tag.getInt("reviveCooldown");
    }

    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return save(provider);
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        load(provider, tag);
    }
}
