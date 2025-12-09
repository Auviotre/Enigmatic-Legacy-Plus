package auviotre.enigmatic.legacy.contents.gui;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.AntiqueBagInventory;
import auviotre.enigmatic.legacy.contents.capability.IAntiqueBagHandler;
import auviotre.enigmatic.legacy.contents.item.legacy.AntiqueBag;
import auviotre.enigmatic.legacy.registries.EnigmaticCapability;
import auviotre.enigmatic.legacy.registries.EnigmaticMenus;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AntiqueBagContainerMenu extends AbstractContainerMenu {
    protected final Player player;

    public AntiqueBagContainerMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, ContainerLevelAccess.create(inventory.player.level(), inventory.player.blockPosition()));
    }

    protected AntiqueBagContainerMenu(int id, @NotNull Inventory inventory, ContainerLevelAccess access) {
        super(EnigmaticMenus.ANTIQUE_BAG_MENU.get(), id);
        this.player = inventory.player;
        Optional<IAntiqueBagHandler> optional = EnigmaticCapability.get(player, EnigmaticCapability.ANTIQUE_BAG_INVENTORY);
        optional.ifPresent(bagHandler -> {
            IItemHandlerModifiable bagInventory = bagHandler.getInventory();
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 6; x++) {
                    this.addSlot(new BookSlot(bagInventory, x + y * 6, x * 18 + 35, y * 18 + 24));
                }
            }
        });

        for (int k = 0; k < 3; ++k) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            if (k == this.player.getInventory().selected) {
                this.addSlot(new Slot(inventory, k, 8 + k * 18, 142) {
                    public boolean mayPickup(Player playerIn) {
                        return false;
                    }

                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            } else {
                this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
            }
        }
    }

    public ItemStack quickMoveStack(Player player, int id) {
        ItemStack stack = ItemStack.EMPTY;
        int slotsCount = AntiqueBagInventory.SLOTS_COUNT;
        Slot slot = this.slots.get(id);
        if (slot != null && slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            stack = slotItem.copy();
            if (id < slotsCount) {
                if (!this.moveItemStackTo(slotItem, slotsCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 0, slotsCount, false)) {
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
            if (slotItem.getCount() == stack.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, slotItem);
        }

        return stack;
    }

    public boolean stillValid(Player player) {
        return true;
    }

    public static class Provider implements MenuProvider {
        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
            return new AntiqueBagContainerMenu(id, inventory, ContainerLevelAccess.create(inventory.player.level(), inventory.player.blockPosition()));
        }

        public Component getDisplayName() {
            return Component.translatable("gui.enigmaticlegacy.antique_bag");
        }
    }

    public static class BookSlot extends SlotItemHandler {
        public BookSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        public boolean mayPlace(@NotNull ItemStack stack) {
            return AntiqueBag.isBook(stack) && super.mayPlace(stack);
        }

        @Nullable
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, EnigmaticLegacy.location("slot/empty_book_slot"));
        }
    }
}
