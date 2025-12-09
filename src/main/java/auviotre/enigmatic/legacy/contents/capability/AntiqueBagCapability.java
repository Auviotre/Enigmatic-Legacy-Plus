package auviotre.enigmatic.legacy.contents.capability;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.AntiqueBagInventory;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class AntiqueBagCapability implements IAntiqueBagHandler {
    public static final ResourceLocation ID = EnigmaticLegacy.location("antique_bag_inventory");
    AntiqueBagInventory inventory;
    LivingEntity owner;

    public AntiqueBagCapability(final LivingEntity entity) {
        this.owner = entity;
        this.inventory = entity.getData(EnigmaticAttachments.ANTIQUE_BAG_INVENTORY);
    }

    public void reset() {
        this.inventory.init();
    }

    public int getSlots() {
        return AntiqueBagInventory.SLOTS_COUNT;
    }

    public ItemStack getBook(int index) {
        return this.inventory.getStackHandler().getStackInSlot(index);
    }

    public void setBook(int index, ItemStack book) {
        this.inventory.getStackHandler().setStackInSlot(index, book);
    }

    public ItemStack findBook(Item book) {
        IItemHandlerModifiable handler = this.getInventory();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.is(book)) return stack;
        }
        return ItemStack.EMPTY;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public IItemHandlerModifiable getInventory() {
        return this.inventory.getStackHandler();
    }

    public Tag writeTag() {
        return this.inventory.serializeNBT(this.owner.registryAccess());
    }

    public void readTag(Tag nbt) {
        this.inventory.deserializeNBT(this.owner.registryAccess(), (CompoundTag) nbt);
    }
}
