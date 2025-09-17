package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

public class EnchantmentTransposer extends BaseItem {
    public EnchantmentTransposer() {
        super(defaultSingleProperties());
    }

    public EnchantmentTransposer(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantmentTransposer1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantmentTransposer2");
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantmentTransposer3");
    }

    protected boolean canDisenchant(Player player, ItemStack transposer, @NotNull ItemStack target) {
        ItemEnchantments enchantments = target.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (enchantments.isEmpty()) return false;
        if (!(transposer.getItem() instanceof EnchantmentTransposer item)) return false;
        return enchantments.keySet().stream().anyMatch(item::canTranspose);
    }

    public boolean canTranspose(Holder<Enchantment> enchantment) {
        return true;
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
                if (carried.getItem() instanceof EnchantmentTransposer transposer && transposer.canDisenchant(player, carried, target)) {
                    ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
                    ItemEnchantments.Mutable transposed = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(target));
                    ItemEnchantments.Mutable leftover = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(target));
                    transposed.removeIf(enchant -> !transposer.canTranspose(enchant));
                    leftover.removeIf(transposer::canTranspose);
                    EnchantmentHelper.setEnchantments(book, transposed.toImmutable());
                    EnchantmentHelper.setEnchantments(target, leftover.toImmutable());
                    if (player.level().isClientSide)
                        player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 0.8F, 1.2F + player.getRandom().nextFloat() * 0.4F);
                    event.getCarriedSlotAccess().set(book);
                    event.setCanceled(true);
                }
            }
        }
    }
}
