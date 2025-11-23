package auviotre.enigmatic.legacy.contents.item.generic;

import auviotre.enigmatic.legacy.registries.EnigmaticComponents;

public class CursedCurioItem extends BaseCurioItem {
    public CursedCurioItem(Properties properties) {
        super(properties.component(EnigmaticComponents.CURSED, true));
    }

    public CursedCurioItem(Properties properties, boolean betrayal) {
        super(properties.component(EnigmaticComponents.CURSED, true).component(EnigmaticComponents.BLESSED, betrayal));
    }
}
