package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentMenu.class)
public abstract class MixinEnchantmentMenu extends AbstractContainerMenu {
    @Shadow
    @Final
    public int[] costs;
    @Shadow
    @Final
    private Container enchantSlots;
    @Shadow
    @Final
    private ContainerLevelAccess access;
    @Shadow
    @Final
    private DataSlot enchantmentSeed;

    protected MixinEnchantmentMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Shadow
    protected abstract List<EnchantmentInstance> getEnchantmentList(RegistryAccess registryAccess, ItemStack stack, int slot, int cost);

    @Inject(at = @At("HEAD"), method = "clickMenuButton", cancellable = true)
    private void onEnchantedItem(Player player, int id, CallbackInfoReturnable<Boolean> info) {
        if (!this.getType().equals(MenuType.ENCHANTMENT)) return;
        if (EnigmaticHandler.hasCurio(player, EnigmaticItems.ENCHANTER_PEARL)) {
            ItemStack input = this.enchantSlots.getItem(0);
            int i = id + 1;
            if (this.costs[id] <= 0 || input.isEmpty() || (player.experienceLevel < i || player.experienceLevel < this.costs[id]) && !player.hasInfiniteMaterials()) {
                info.setReturnValue(false);
                return;
            }
            this.access.execute((level, pos) -> {
                List<EnchantmentInstance> list = this.getEnchantmentList(level.registryAccess(), input, id, this.costs[id]);
                if (!list.isEmpty()) {
                    RegistryAccess registryAccess = level.registryAccess();
                    Registry<Enchantment> enchantments = registryAccess.registryOrThrow(Registries.ENCHANTMENT);
                    ItemStack doubleEnchanted = EnchantmentHelper.enchantItem(player.getRandom(), input.copy(), Math.min(this.costs[id] + 7, 40), enchantments.holders().map(holders -> holders));
                    player.onEnchantmentPerformed(input, i);
                    ItemStack output = input.getItem().applyEnchantments(input, list);

                    output = EnigmaticHandler.mergeEnchantments(output, doubleEnchanted, false, false);
                    this.enchantSlots.setItem(0, output);
                    CommonHooks.onPlayerEnchantItem(player, output, list);

                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer serverPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger(serverPlayer, output, i);
                    }

                    this.enchantSlots.setChanged();
                    this.enchantmentSeed.set(player.getEnchantmentSeed());
                    this.slotsChanged(this.enchantSlots);
                    level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);
                }
            });
            info.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "getGoldCount", cancellable = true)
    public void onGetLapisCount(CallbackInfoReturnable<Integer> info) {
        if (!this.getType().equals(MenuType.ENCHANTMENT)) return;
        Player player = null;
        for (Slot slot : this.slots) {
            if (slot.container instanceof Inventory inventory) {
                player = inventory.player;
                break;
            }
        }
        if (EnigmaticHandler.hasCurio(player, EnigmaticItems.ENCHANTER_PEARL)) {
            info.setReturnValue(64);
        }
    }
}
