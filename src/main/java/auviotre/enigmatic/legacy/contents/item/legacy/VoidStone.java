package auviotre.enigmatic.legacy.contents.item.legacy;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class VoidStone extends BaseItem {
    public VoidStone() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON).fireResistant());
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidStone1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidStone2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidStone3");
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.PRIMARY || !slot.mayPlace(stack) || !slot.mayPickup(player) || other.isEmpty())
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        other.setCount(0);
        if (player.level().isClientSide) {
            player.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.25F, 1.2F + player.getRandom().nextFloat() * 0.4F);
        }
        return true;
    }


    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (action == ClickAction.PRIMARY || !slot.mayPlace(stack) || !slot.mayPickup(player) || !slot.hasItem())
            return super.overrideStackedOnOther(stack, slot, action, player);
        slot.set(ItemStack.EMPTY);
        if (player.level().isClientSide) {
            player.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.25F, 1.2F + player.getRandom().nextFloat() * 0.4F);
        }
        return true;
    }

}
