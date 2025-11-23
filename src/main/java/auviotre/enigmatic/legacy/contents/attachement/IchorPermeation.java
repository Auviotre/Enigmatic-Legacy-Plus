package auviotre.enigmatic.legacy.contents.attachement;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class IchorPermeation implements INBTSerializable<CompoundTag> {
    private boolean infected = false;

    public boolean isInfected() {
        return this.infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("MagnetRingEffect", this.infected);
        return tag;
    }

    public void load(@NotNull CompoundTag tag) {
        this.infected = tag.getBoolean("infected");
    }

    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return save();
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        load(tag);
    }
}
