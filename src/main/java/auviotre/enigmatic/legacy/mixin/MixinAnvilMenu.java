package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.contents.item.charms.ForgerGem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class MixinAnvilMenu extends ItemCombinerMenu {
    @Shadow
    @Final
    private DataSlot cost;

    public MixinAnvilMenu(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;broadcastChanges()V"))
    public void createResultMix(CallbackInfo ci) {
        if (EnigmaticHandler.hasCurio(player, EnigmaticItems.FORGER_GEM)) {
            int xp = 0;
            for (int i = 1; i < this.cost.get(); i++) xp += getXp(i);
            this.cost.set(Math.max(1, getExpLevel(xp / 2)));
        }
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    public void createResultLast(CallbackInfo ci) {
        if (EnigmaticHandler.hasCurio(player, EnigmaticItems.FORGER_GEM) && EnigmaticHandler.isTheOne(player)) {
            ItemStack result = this.resultSlots.getItem(0);
            if (!result.isEmpty() && result.has(DataComponents.MAX_DAMAGE)) {
                Integer i = result.get(DataComponents.MAX_DAMAGE);
                if (i != null) {
                    int modified = Mth.floor(i * (ForgerGem.extraDurabilityModifier.get() * 0.01 + 1));
                    if (!result.has(EnigmaticComponents.TOOL_DURABILITY_INFO)) {
                        result.set(EnigmaticComponents.TOOL_DURABILITY_INFO, ForgerGem.ToolInfo.of(i, modified - i));
                    }
                    ForgerGem.ToolInfo info = result.get(EnigmaticComponents.TOOL_DURABILITY_INFO);
                    if (info == null) return;
                    int max = Mth.floor(info.originDurability() * (1 + 0.01 * ForgerGem.extraMaxDurability.get()));
                    if (i >= max) return;
                    modified = Math.min(modified, max);
                    result.set(DataComponents.MAX_DAMAGE, modified);
                    result.set(EnigmaticComponents.TOOL_DURABILITY_INFO, ForgerGem.ToolInfo.of(info.originDurability(), modified - info.originDurability()));
                    this.resultSlots.setItem(0, result);
                }
            }
        }
    }

    private int getXp(int experienceLevel) {
        if (experienceLevel >= 30) {
            return 112 + (experienceLevel - 30) * 9;
        } else {
            return experienceLevel >= 15 ? 37 + (experienceLevel - 15) * 5 : 7 + experienceLevel * 2;
        }
    }

    private int getExpLevel(long experience) {
        int experienceLevel = 0;
        int neededForNext;
        while (true) {
            if (experienceLevel >= 30) neededForNext = 112 + (experienceLevel - 30) * 9;
            else neededForNext = experienceLevel >= 15 ? 37 + (experienceLevel - 15) * 5 : 7 + experienceLevel * 2;
            if (experience < neededForNext) return experienceLevel;
            experienceLevel++;
            experience -= neededForNext;
        }
    }
}
