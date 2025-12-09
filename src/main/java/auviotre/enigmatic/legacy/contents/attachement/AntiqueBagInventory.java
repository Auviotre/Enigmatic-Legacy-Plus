package auviotre.enigmatic.legacy.contents.attachement;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class AntiqueBagInventory implements INBTSerializable<CompoundTag> {
    public static final int SLOTS_COUNT = 12;
    ItemStackHandler handler = new ItemStackHandler(SLOTS_COUNT);

    public ItemStackHandler getStackHandler() {
        return this.handler;
    }

    public void init() {
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return this.handler.serializeNBT(provider);
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.handler.deserializeNBT(provider, tag);
    }
}
