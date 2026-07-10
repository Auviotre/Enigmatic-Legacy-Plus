package auviotre.enigmatic.legacy.contents.item.potions;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.BaseDrinkableItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class RedemptionPotion extends BaseDrinkableItem {
    public RedemptionPotion() {
        super(IItemHelper.singleProperties().rarity(Rarity.RARE).craftRemainder(Items.GLASS_BOTTLE));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionPotion");
    }

    public void onConsumed(Level level, Player player, ItemStack stack) {
        EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        data.setForbiddenCursed(false);
        data.setThirstForbiddenCursed(false);
        player.playSound(SoundEvents.BEACON_ACTIVATE, 1.0F, 1.0F);
    }
}
