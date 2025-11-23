package auviotre.enigmatic.legacy.contents.item.materials;

import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class TwistedHeart extends BaseCursedItem implements ITaintable {
    public TwistedHeart() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON));
    }

    public boolean canTaint(Player player) {
        return EnigmaticHandler.isTheCursedOne(player);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && !level.isClientSide())
            this.handleTaintable(stack, player);
    }
}
