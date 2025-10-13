package auviotre.enigmatic.legacy.contents.item.spellstones.other;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import net.minecraft.world.item.Rarity;

public class Spellcore extends BaseItem {
    public Spellcore() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant().component(EnigmaticComponents.SPELLCORE_POWER, 0));
    }
}
