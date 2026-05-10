package auviotre.enigmatic.legacy.contents.item.spellstones.other;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import net.minecraft.world.item.Rarity;

public class Spellcore extends BaseItem {
    public Spellcore() {
        super(IItemHelper.singleProperties().rarity(Rarity.RARE).fireResistant());
    }
}
