package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.contents.item.charms.ForgerGem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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
        if (EnigmaticHandler.hasCurio(player, EnigmaticItems.FORGER_GEM) || EnigmaticHandler.hasCurio(player, EnigmaticItems.FORGER_CRYSTAL)) {
            int xp = 0;
            for (int i = 1; i < this.cost.get(); i++) xp += getXp(i);
            this.cost.set(Math.max(1, getExpLevel(xp / 2)));
        }
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    public void createResultLast(CallbackInfo ci) {
        if (EnigmaticHandler.hasCurio(player, EnigmaticItems.FORGER_CRYSTAL)) {

        } else if (EnigmaticHandler.hasCurio(player, EnigmaticItems.FORGER_GEM) && EnigmaticHandler.isTheOne(player)) {
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

        ItemStack curio = EnigmaticHandler.getCurio(player, EnigmaticItems.ETHEREAL_FORGING_CHARM);
        if (!curio.isEmpty()) {
            ItemStack result = this.resultSlots.getItem(0);
            if (!result.isEmpty() && !result.getOrDefault(EnigmaticComponents.ETHEREAL_FORGED, false)) {
                boolean active = curio.getOrDefault(EnigmaticComponents.BOOLEAN, false);
                if (result.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
                    ItemAttributeModifiers attributes = result.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                    ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
                    List<ItemAttributeModifiers.Entry> modifiers = attributes.modifiers();
                    for (ItemAttributeModifiers.Entry entry : modifiers) {
                        Holder<Attribute> attribute = entry.attribute();
                        AttributeModifier modifier = entry.modifier();
                        if (attribute == Attributes.ATTACK_DAMAGE) {
                            modifier = modify(modifier, active ? 1.0 : 0.5, active ? 0.08 : 0.05);
                        } else if (attribute == Attributes.ARMOR) {
                            modifier = modify(modifier, active ? 1.0 : 0.5, active ? 0.08 : 0.05);
                        } else if (attribute == Attributes.ARMOR_TOUGHNESS) {
                            modifier = modify(modifier, active ? 0.1 : 0.05, active ? 0.16 : 0.1);
                        }
                        builder.add(attribute, modifier, entry.slot());
                    }
                    result.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
                    result.set(EnigmaticComponents.ETHEREAL_FORGED, true);
                    this.resultSlots.setItem(0, result);
                }
            }
        }
    }

    private AttributeModifier modify(AttributeModifier modifier, double add, double mul) {
        double amount = modifier.amount();
        AttributeModifier.Operation operation = modifier.operation();
        if (operation.id() == 0 && amount > 0) amount += 0.5;
        if (operation.id() != 0 && amount > 0) amount += 0.05;
        modifier = new AttributeModifier(modifier.id(), amount, operation);
        return modifier;
    }

    private int getXp(int level) {
        if (level == 0) return 0;
        if (level > 30) return 112 + (level - 31) * 9;
        return level > 15 ? 37 + (level - 16) * 5 : 7 + (level - 1) * 2;
    }

    private int getExpLevel(long exp) {
        int level = 0;
        while (true) {
            int neededForNext = getXp(level + 1);
            if (exp < neededForNext) return level;
            level++;
            exp -= neededForNext;
        }
    }
}
