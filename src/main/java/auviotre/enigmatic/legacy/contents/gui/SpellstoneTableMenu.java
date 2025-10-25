package auviotre.enigmatic.legacy.contents.gui;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.crafting.SpellstoneTableRecipe;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticMenus;
import auviotre.enigmatic.legacy.registries.EnigmaticRecipes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpellstoneTableMenu extends AbstractContainerMenu {
    protected final Container coreSlot = new InputContainer(1) {
        public int getMaxStackSize() {
            return 1;
        }
    };
    protected final Container debrisSlot = new InputContainer(1);
    protected final Container ingredientSlot = new InputContainer(7);
    protected final ResultContainer result = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    public SpellstoneTableMenu(int syncID, Inventory inventory) {
        this(syncID, inventory, ContainerLevelAccess.create(inventory.player.level(), inventory.player.blockPosition()));
    }

    public SpellstoneTableMenu(int syncID, Inventory inventory, FriendlyByteBuf buf) {
        this(syncID, inventory, ContainerLevelAccess.create(inventory.player.level(), inventory.player.blockPosition()));
    }

    public SpellstoneTableMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(EnigmaticMenus.SPELLSTONE_TABLE_MENU.get(), id);
        this.access = access;
        this.player = inventory.player;

        this.addSlot(new Slot(this.debrisSlot, 0, 27, 35) {
            public boolean mayPlace(ItemStack stack) {
                return stack.is(EnigmaticItems.SPELLSTONE_DEBRIS);
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EnigmaticLegacy.location("slot/empty_spellstone_debris_slot"));
            }
        });
        this.addSlot(new Slot(this.coreSlot, 0, 80, 35) {
            public boolean mayPlace(ItemStack stack) {
                return stack.is(EnigmaticItems.SPELLCORE);
            }
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EnigmaticLegacy.location("slot/empty_spellcore_slot"));
            }
        });
        this.addSlot(new Slot(this.ingredientSlot, 0, 60, 55));
        this.addSlot(new Slot(this.ingredientSlot, 1, 53, 35));
        this.addSlot(new Slot(this.ingredientSlot, 2, 60, 15));
        this.addSlot(new Slot(this.ingredientSlot, 3, 80, 8));
        this.addSlot(new Slot(this.ingredientSlot, 4, 100, 15));
        this.addSlot(new Slot(this.ingredientSlot, 5, 107, 35));
        this.addSlot(new Slot(this.ingredientSlot, 6, 100, 55));
        this.addSlot(new Slot(this.result, 0, 80, 62) {
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
            public boolean mayPickup(Player playerIn) {
                return this.hasItem();
            }
            public void onTake(Player thePlayer, ItemStack stack) {
                this.setChanged();
                SpellstoneTableRecipe.Input input = SpellstoneTableMenu.this.getInput();
                NonNullList<ItemStack> remains = player.level().getRecipeManager().getRemainingItemsFor(EnigmaticRecipes.SPELLSTONE_CRAFTING.get(), input, player.level());
                debrisSlot.setItem(0, remains.getFirst());
                coreSlot.clearContent();
                for (int i = 2; i < remains.size(); i++) {
                    ItemStack item = ingredientSlot.getItem(i - 2);
                    ItemStack remain = remains.get(i);
                    if (!item.isEmpty()) {
                        ingredientSlot.removeItem(i - 2, 1);
                        item = ingredientSlot.getItem(i - 2);
                    }
                    if (!remain.isEmpty()) {
                        if (item.isEmpty()) {
                            ingredientSlot.setItem(i - 2, remain);
                        } else if (ItemStack.isSameItemSameComponents(item, remain)) {
                            remain.grow(item.getCount());
                            ingredientSlot.setItem(i - 2, remain);
                        } else if (player.getInventory().add(remain)) {
                            player.drop(remain, false);
                        }
                    }
                }
            }
            public boolean isFake() {
                return true;
            }
        });

        for (int l = 0; l < 3; ++l) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(inventory, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l, 8 + l * 18, 142));
        }
    }

    public void slotsChanged(Container container) {
        this.access.execute((level, pos) -> {
            MinecraftServer server = level.getServer();
            ItemStack output = ItemStack.EMPTY;
            if (server != null) {
                SpellstoneTableRecipe.Input input = this.getInput();
                Optional<RecipeHolder<SpellstoneTableRecipe>> optional = server.getRecipeManager().getRecipeFor(EnigmaticRecipes.SPELLSTONE_CRAFTING.get(), input, level, (RecipeHolder<SpellstoneTableRecipe>) null);
                if (optional.isPresent()) {
                    SpellstoneTableRecipe recipe = optional.get().value();
                    ItemStack result = recipe.assemble(input, level.registryAccess());
                    if (result.isItemEnabled(level.enabledFeatures())) {
                        output = result;
                    }
                }
            }
            this.result.setItem(0, output);
        });
    }

    public ItemStack quickMoveStack(Player player, int id) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(id);
        if (slot.hasItem()) {
            ItemStack selected = slot.getItem();
            stack = selected.copy();
            if (id == 9) {

            } else if (id >= 10 && id < 46) {
                if (!this.moveItemStackTo(selected, 0, 9, false))
                    return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(selected, 10, 46, false))
                return ItemStack.EMPTY;

            if (selected.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();

            if (selected.getCount() == stack.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, selected);
        }

        return stack;
    }

    private SpellstoneTableRecipe.Input getInput() {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < this.ingredientSlot.getContainerSize(); i++)
            list.add(this.ingredientSlot.getItem(i));
        return new SpellstoneTableRecipe.Input(this.coreSlot.getItem(0), this.debrisSlot.getItem(0), list);
    }

    public boolean hasOutput() {
        return !this.result.isEmpty();
    }

    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.result && super.canTakeItemForPickAll(stack, slot);
    }

    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> {
            this.clearContainer(player, this.coreSlot);
            this.clearContainer(player, this.debrisSlot);
            this.clearContainer(player, this.ingredientSlot);
        });
    }

    public boolean stillValid(Player player) {
        return stillValid(this.access, player, EnigmaticBlocks.SPELLSTONE_TABLE.get());
    }

    public class InputContainer extends SimpleContainer {
        public InputContainer(int size) {
            super(size);
        }
        public void setChanged() {
            super.setChanged();
            SpellstoneTableMenu.this.slotsChanged(this);
        }
    }

    public static class Provider implements MenuProvider {
        private final Component name;

        public Provider(Component name) {
            this.name = name;
        }

        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
            return new SpellstoneTableMenu(id, inventory);
        }

        public Component getDisplayName() {
            return this.name;
        }
    }
}
