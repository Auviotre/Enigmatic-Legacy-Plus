package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.damage.FalseJusticeDamage;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FalseJustice extends BaseCursedItem {
    public FalseJustice() {
        super(IItemHelper.singleProperties().rarity(Rarity.EPIC).component(EnigmaticComponents.ELDRITCH, true));
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (isSelected && EnigmaticHandler.isTheWorthyOne(livingEntity))
                stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
            else stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacyplus.falseJustice1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacyplus.falseJustice2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacyplus.falseJustice3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacyplus.falseJustice4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacyplus.falseJustice5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacyplus.falseJustice6");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacyplus.falseJusticeLore1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacyplus.falseJusticeLore2");
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
        TooltipHandler.line(list);
        TooltipHandler.worthyOnly(list, stack);
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        
        private static boolean hasActiveFalseJustice(LivingEntity entity) {
            if (entity == null) return false;
            ItemStack stack = EnigmaticHandler.getItem(entity, EnigmaticItems.FALSE_JUSTICE);
            return !stack.isEmpty() && EnigmaticHandler.canUse(entity, stack);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onDamage(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity victim = event.getEntity();
            LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;

            if (attacker == null) return;

            if (hasActiveFalseJustice(attacker)) {
                FalseJusticeDamage.from(event.getSource()).falseJustice$setBypassAll(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onDamagePre(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;

            if (hasActiveFalseJustice(attacker) || hasActiveFalseJustice(victim)) {
                event.setNewDamage(event.getOriginalDamage());
            }
        }
    }
}
