package auviotre.enigmatic.legacy.contents.item.materials;

import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import net.minecraft.world.item.Rarity;

public class EvilEssence extends BaseCursedItem {
    public EvilEssence() {
        super(BaseItem.defaultProperties(16).rarity(Rarity.UNCOMMON));
    }
}
