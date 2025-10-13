package auviotre.enigmatic.legacy.contents.item.potions;

import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.BaseDrinkableItem;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class RedemptionPotion extends BaseDrinkableItem {
    public RedemptionPotion() {
        super(defaultSingleProperties().rarity(Rarity.RARE).craftRemainder(Items.GLASS_BOTTLE));
    }

    public void onConsumed(Level level, Player player, ItemStack stack) {
        EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        data.setForbiddenCursed(false);
        player.playSound(SoundEvents.BEACON_ACTIVATE, 1.0F, 1.0F);
    }
}
