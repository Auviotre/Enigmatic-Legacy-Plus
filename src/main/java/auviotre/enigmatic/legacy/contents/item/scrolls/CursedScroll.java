package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CursedScroll extends CursedCurioItem {
    public CursedScroll() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
    }

    public static int getCurseAmount(LivingEntity entity) {
        AtomicInteger amount = new AtomicInteger();
        for (ItemStack armor : entity.getArmorSlots()) amount.addAndGet(getItemCurseLevel(armor));
        for (ItemStack hand : entity.getHandSlots()) amount.addAndGet(getItemCurseLevel(hand));
        CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
            IItemHandlerModifiable curios = handler.getEquippedCurios();
            for (int i = 0; i < curios.getSlots(); i++)
                amount.addAndGet(getItemCurseLevel(curios.getStackInSlot(i)));
        });
        return amount.get();
    }

    public static int getItemCurseLevel(@NotNull ItemStack stack) {
        ItemEnchantments enchantments = stack.getTagEnchantments();
        int level = 0;
        if (stack.is(EnigmaticItems.CURSED_RING)) level += 7;
        for (Holder<Enchantment> enchantment : enchantments.keySet()) {
            if (enchantment.is(EnchantmentTags.CURSE)) level += enchantments.getLevel(enchantment);
        }
        return level;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll1", ChatFormatting.GOLD, "4%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll2", ChatFormatting.GOLD, "4%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll3", ChatFormatting.GOLD, "4%");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll5");
        } else TooltipHandler.holdShift(list);
        Player player = Minecraft.getInstance().player;
        if (player != null && EnigmaticHandler.getCurio(player, this) == stack) {
            int curses = getCurseAmount(player);
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll1", ChatFormatting.GOLD, (4 * curses) + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll2", ChatFormatting.GOLD, (4 * curses) + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedScroll3", ChatFormatting.GOLD, (4 * curses) + "%");
        }
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void getBreakSpeed(PlayerEvent.@NotNull BreakSpeed event) {
            LivingEntity entity = event.getEntity();
            if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.CURSED_SCROLL)) {
                float multiplier = getCurseAmount(entity) * 0.04F;
                event.setNewSpeed(event.getOriginalSpeed() * multiplier + event.getNewSpeed());
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (event.getSource().getEntity() instanceof LivingEntity entity && EnigmaticHandler.hasCurio(entity, EnigmaticItems.CURSED_SCROLL)) {
                float multiplier = getCurseAmount(entity) * 0.04F + 1;
                event.setNewDamage(event.getNewDamage() * multiplier);
            }
        }

        @SubscribeEvent
        private static void onHeal(@NotNull LivingHealEvent event) {
            LivingEntity entity = event.getEntity();
            if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.CURSED_SCROLL)) {
                float multiplier = getCurseAmount(entity) * 0.04F + 1;
                event.setAmount(event.getAmount() * multiplier);
            }
        }
    }
}
