package auviotre.enigmatic.legacy.contents.item.potions;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MendingMixture extends BaseItem {
    public MendingMixture() {
        super(defaultSingleProperties().craftRemainder(Items.GLASS_BOTTLE));
        NeoForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.mendingMixture1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.mendingMixture2");
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @SubscribeEvent
    public void onMendingMixtureOn(@NotNull ItemStackedOnOtherEvent event) {
        Slot slot = event.getSlot();
        ItemStack carried = event.getCarriedItem();
        if (event.getClickAction() != ClickAction.PRIMARY && slot.mayPickup(event.getPlayer()) && slot.hasItem()) {
            ItemStack target = slot.getItem();
            if (target.isDamaged() && carried.is(EnigmaticItems.MENDING_MIXTURE.get())) {
                target.setDamageValue(0);
                event.getCarriedSlotAccess().set(Items.GLASS_BOTTLE.getDefaultInstance());
                event.setCanceled(true);
            }
        }
    }
}
