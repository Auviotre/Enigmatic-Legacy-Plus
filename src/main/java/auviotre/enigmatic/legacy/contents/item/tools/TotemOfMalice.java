package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TotemOfMalice extends BaseCursedItem {
    public static ModConfigSpec.IntValue specialDamageBoost;
    public static ModConfigSpec.IntValue specialDamageResistance;

    public TotemOfMalice() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant()
                .component(EnigmaticComponents.MALICE_DURABILITY, 0).component(EnigmaticComponents.MALICE_MAX_DURABILITY, 8));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.totem_of_malice").push("cursedItems.totemOfMalice");
        specialDamageBoost = builder.defineInRange("specialDamageBoost", 80, 0, 200);
        specialDamageResistance = builder.defineInRange("specialDamageResistance", 50, 0, 100);
        builder.pop(2);
    }

    public static void hurtAndBreak(ItemStack stack, LivingEntity entity) {
        if (!entity.level().isClientSide() && !entity.hasInfiniteMaterials()) {
            entity.invulnerableTime = 60;
            setDurability(stack, getDurability(stack) - 1);
            if (getDurability(stack) == 0 && entity.getRandom().nextInt(100) < 50) {
                stack.set(EnigmaticComponents.MALICE_MAX_DURABILITY, getMaxDurability(stack) - 1);
                if (getMaxDurability(stack) < 1) stack.shrink(1);
            }
        }
    }

    public static int getMaxDurability(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        return stack.getOrDefault(EnigmaticComponents.MALICE_MAX_DURABILITY, 8);
    }

    public static int getDurability(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (!stack.has(EnigmaticComponents.MALICE_DURABILITY)) return 0;
        int durability = stack.getOrDefault(EnigmaticComponents.MALICE_DURABILITY, 0);
        durability = Math.clamp(durability, 0, getMaxDurability(stack));
        stack.set(EnigmaticComponents.MALICE_DURABILITY, durability);
        return durability;
    }

    public static void setDurability(ItemStack stack, int damage) {
        stack.set(EnigmaticComponents.MALICE_DURABILITY, Mth.clamp(damage, 0, getMaxDurability(stack)));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.totemofMalice1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.totemofMalice2", ChatFormatting.GOLD, specialDamageBoost.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.totemofMalice3", ChatFormatting.GOLD, specialDamageResistance.get() + "%");
            TooltipHandler.line(list);
            if (getDurability(stack) > 0) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.totemofMalice4");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.totemofMalice5");
            } else {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.totemofMalice4_alt");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.totemofMalice5_alt");
            }
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(EnchantmentTags.CURSE);
    }

    public boolean isFoil(ItemStack stack) {
        return getDurability(stack) > 0;
    }

    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    public int getBarWidth(ItemStack stack) {
        return Math.round((float) getDurability(stack) * 13.0F / getMaxDurability(stack));
    }

    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) getDurability(stack) / getMaxDurability(stack));
        return Mth.hsvToRgb(f / 18 * 13, 1.0F, 0.25F + f * 0.5F);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAnvilUpdate(@NotNull AnvilUpdateEvent event) {
            ItemStack left = event.getLeft();
            ItemStack right = event.getRight();
            if (!left.isEmpty() && !right.isEmpty()) {
                if (left.is(EnigmaticItems.TOTEM_OF_MALICE) && right.is(EnigmaticItems.EVIL_ESSENCE)) {
                    ItemStack copy = left.copy();
                    int maxDurability = getMaxDurability(left);
                    if (TotemOfMalice.getDurability(left) < maxDurability) {
                        TotemOfMalice.setDurability(copy, maxDurability);
                        event.setOutput(copy);
                        event.setCost(maxDurability + copy.getOrDefault(DataComponents.REPAIR_COST, 0));
                        event.setMaterialCost(1);
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (event.getAmount() >= Float.MAX_VALUE) return;
            LivingEntity victim = event.getEntity();
            Entity entity = event.getSource().getEntity();
            if (!(entity instanceof LivingEntity attacker)) return;
            if (EnigmaticHandler.hasItem(attacker, EnigmaticItems.TOTEM_OF_MALICE) || EnigmaticHandler.hasCurio(attacker, EnigmaticItems.TOTEM_OF_MALICE)) {
                if (victim.getType().is(EntityTypeTags.ILLAGER)) {
                    event.setAmount(event.getAmount() * (1.0F + 0.01F * specialDamageBoost.get()));
                }
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            Entity entity = event.getSource().getEntity();
            if (!(entity instanceof LivingEntity attacker)) return;
            if (EnigmaticHandler.hasItem(victim, EnigmaticItems.TOTEM_OF_MALICE) || EnigmaticHandler.hasCurio(victim, EnigmaticItems.TOTEM_OF_MALICE)) {
                if (attacker.getType().is(EntityTypeTags.ILLAGER)) {
                    event.setNewDamage(event.getNewDamage() * (1.0F - 0.01F * specialDamageResistance.get()));
                }
            }
        }
    }
}
