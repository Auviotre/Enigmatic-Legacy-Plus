package auviotre.enigmatic.legacy.contents.item.generic;

import auviotre.enigmatic.legacy.registries.EnigmaticComponents;

public class CursedCurioItem extends BaseCurioItem {
    public CursedCurioItem(Properties properties) {
        super(properties.component(EnigmaticComponents.CURSED, true));
    }
}
