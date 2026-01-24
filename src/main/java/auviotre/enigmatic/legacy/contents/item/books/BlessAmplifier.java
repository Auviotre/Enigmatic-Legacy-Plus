package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlessAmplifier extends BaseCursedItem {
    public BlessAmplifier() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON), true);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessAmplifier1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessAmplifier2");
        if (EnigmaticHandler.isTheBlessedOne(Minecraft.getInstance().player)) {
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessAmplifier3");
        }
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    protected boolean canAmplify(Player player, ItemStack amplifier, @NotNull ItemStack target) {
        if (amplifier.is(this) && !EnigmaticHandler.canUse(player, amplifier)) return false;
        ItemEnchantments enchantments = target.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (enchantments.isEmpty()) return false;
        return enchantments.keySet().stream().anyMatch(holder -> holder.value().getMaxLevel() > 0);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onEnchantmentTransOn(@NotNull ItemStackedOnOtherEvent event) {
            Slot slot = event.getSlot();
            Player player = event.getPlayer();
            ItemStack carried = event.getCarriedItem();
            if (event.getClickAction() != ClickAction.PRIMARY && slot.mayPickup(player) && slot.hasItem()) {
                ItemStack target = slot.getItem();
                if (carried.getItem() instanceof BlessAmplifier amplifier && amplifier.canAmplify(player, carried, target)) {
                    ItemEnchantments origin = EnchantmentHelper.getEnchantmentsForCrafting(target);
                    ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(origin);
                    List<Holder<Enchantment>> list = origin.keySet().stream().toList();
                    for (Holder<Enchantment> holder : list) {
                        int maxLevel = holder.value().getMaxLevel();
                        if (maxLevel > 1) {
                            double multiplier = EnigmaticHandler.isTheBlessedOne(player) ? 1.4 : 1;
                            int newLevel = (int) Math.min(maxLevel * multiplier, origin.getLevel(holder) * 1.4 + 0.8);
                            enchantments.set(holder, Math.max(newLevel, origin.getLevel(holder)));
                        }
                    }
                    EnchantmentHelper.setEnchantments(target, enchantments.toImmutable());
                    int cost = target.getOrDefault(DataComponents.REPAIR_COST, 0);
                    target.set(DataComponents.REPAIR_COST, (int) (cost * 1.1 + 0.9));
                    if (player.level().isClientSide)
                        player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 0.8F, 1.2F + player.getRandom().nextFloat() * 0.4F);
                    event.getCarriedSlotAccess().set(ItemStack.EMPTY);
                    event.setCanceled(true);
                }
            }
        }
    }
}
