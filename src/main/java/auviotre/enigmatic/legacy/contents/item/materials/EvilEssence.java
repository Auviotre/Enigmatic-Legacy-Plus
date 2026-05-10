package auviotre.enigmatic.legacy.contents.item.materials;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import net.minecraft.world.item.Rarity;

public class EvilEssence extends BaseCursedItem {
    public EvilEssence() {
        super(IItemHelper.properties(16).rarity(Rarity.UNCOMMON));
    }
}
