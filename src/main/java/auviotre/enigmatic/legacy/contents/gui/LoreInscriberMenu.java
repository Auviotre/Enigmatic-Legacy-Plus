package auviotre.enigmatic.legacy.contents.gui;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.tools.LoreInscriber;
import auviotre.enigmatic.legacy.registries.EnigmaticMenus;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

public class LoreInscriberMenu extends AbstractContainerMenu {
    public final Player player;
    protected final ResultContainer result = new ResultContainer();
    protected final ContainerLevelAccess access;
    private String unParsedInputField;    protected final Container loreSlot = new SimpleContainer(1) {
        public void setChanged() {
            super.setChanged();
            LoreInscriberMenu.this.slotsChanged(this);
        }
    };
    public LoreInscriberMenu(int syncID, Inventory inventory) {
        this(syncID, inventory, ContainerLevelAccess.create(inventory.player.level(), inventory.player.blockPosition()));
    }

    public LoreInscriberMenu(int syncID, Inventory inventory, FriendlyByteBuf buf) {
        this(syncID, inventory, ContainerLevelAccess.create(inventory.player.level(), inventory.player.blockPosition()));
    }

    private LoreInscriberMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(EnigmaticMenus.LORE_INSCRIBER_MENU.get(), id);

        this.access = access;
        this.player = inventory.player;
        this.addSlot(new Slot(this.loreSlot, 0, 40, 51) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof LoreInscriber.Fragment;
            }

            public int getMaxStackSize() {
                return 1;
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EnigmaticLegacy.location("slot/empty_lore_fragment_slot"));
            }
        });
        this.addSlot(new Slot(this.result, 1, 116, 51) {
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            public boolean mayPickup(Player playerIn) {
                return this.hasItem();
            }

            public void onTake(Player thePlayer, ItemStack stack) {
                this.setChanged();
                LoreInscriberMenu.this.loreSlot.setItem(0, ItemStack.EMPTY);
                if (!player.level().isClientSide)
                    player.level().playSound(null, player.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.1F);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
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
            } else this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.loreSlot) this.createResult();
    }

    public boolean createResult() {
        ItemStack input = this.loreSlot.getItem(0);
        if (input.isEmpty()) {
            this.result.setItem(0, ItemStack.EMPTY);
            return false;
        } else {
            ItemStack output = input.copy();

            if (StringUtils.isBlank(this.unParsedInputField)) {
                if (input.get(DataComponents.CUSTOM_NAME) != null) {
                    output.remove(DataComponents.CUSTOM_NAME);
                } else output = ItemStack.EMPTY;
            } else if (!this.unParsedInputField.equals(input.getHoverName().getString())) {
                output = this.unleashLoreParser(output.copy());
            } else {
                output = ItemStack.EMPTY;
            }

            this.result.setItem(0, output);
            this.broadcastChanges();
            return !output.isEmpty();
        }
    }

    public boolean setItemName(String name) {
        this.unParsedInputField = name;
        return this.createResult();
    }

    private ItemStack unleashLoreParser(ItemStack stack) {
        LoreInscriber.Parser parser = LoreInscriber.Parser.parseField(this.unParsedInputField);
        if (!parser.getFormattedString().isEmpty() || parser.shouldRemoveString()) {
            if (parser.isLoreString()) {
                if (parser.getLoreIndex() != -1) {
                    return LoreInscriber.Helper.setLoreString(stack, parser.getFormattedString(), parser.getLoreIndex());
                } else {
                    return LoreInscriber.Helper.addLoreString(stack, parser.getFormattedString());
                }
            } else if (parser.shouldRemoveString()) {
                return LoreInscriber.Helper.removeLoreString(stack, parser.getLoreIndex());
            } else {
                return LoreInscriber.Helper.setDisplayName(stack, parser.getFormattedString());
            }
        }
        return stack;
    }

    public ItemStack quickMoveStack(Player player, int id) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(id);
        if (slot.hasItem()) {
            ItemStack selected = slot.getItem();
            stack = selected.copy();
            if (id != 0 && id != 1) {
                if (id >= 2 && id < 38) {
                    if (!this.moveItemStackTo(selected, 0, 1, false))
                        return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(selected, 1, 37, false))
                return ItemStack.EMPTY;

            if (selected.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();

            if (selected.getCount() == stack.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, selected);
        }

        return stack;
    }

    public boolean stillValid(Player player) {
        return true;
    }

    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.loreSlot));
    }

    public static class Provider implements MenuProvider {
        private final Component name;

        public Provider(Component name) {
            this.name = name;
        }

        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
            return new LoreInscriberMenu(id, inventory);
        }

        public Component getDisplayName() {
            return this.name;
        }
    }




}