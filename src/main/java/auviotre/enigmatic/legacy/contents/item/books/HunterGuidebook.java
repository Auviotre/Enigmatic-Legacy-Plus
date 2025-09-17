package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HunterGuidebook extends BaseItem {
    public HunterGuidebook() {
        super(defaultSingleProperties());
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.inInventory", ChatFormatting.GOLD);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.hunterGuidebook1", ChatFormatting.GOLD, 16);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.hunterGuidebook2");
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
            Entity attacker = event.getSource().getEntity();
            LivingEntity victim = event.getEntity();
            if (victim instanceof OwnableEntity ownable) {
                LivingEntity owner = ownable.getOwner();
                if (owner != null && owner != attacker) {
                    if (EnigmaticHandler.hasItem(owner, EnigmaticItems.ODE_TO_LIVING) && victim.distanceTo(owner) <= 24.0F) {
                        event.setCanceled(true);
                        owner.hurt(event.getSource(), event.getAmount() * 0.5F);
                    } else if (EnigmaticHandler.hasItem(owner, EnigmaticItems.HUNTER_GUIDEBOOK) && victim.distanceTo(owner) <= 16.0F) {
                        event.setCanceled(true);
                        owner.hurt(event.getSource(), event.getAmount());
                    }
                }
            }
        }
    }
}
