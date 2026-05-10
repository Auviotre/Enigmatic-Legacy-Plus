package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static auviotre.enigmatic.legacy.registries.EnigmaticItems.*;

public class EnigmaticTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnigmaticLegacy.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.enigmaticlegacyplus")).withLabelColor(0xBC00BC)
            .icon(THE_ACKNOWLEDGMENT::toStack).displayItems(display(TAB_ACCEPT_LIST))
            .withTabsAfter(EnigmaticLegacy.location("curse_tab"))
            .withTabsBefore(CreativeModeTabs.INGREDIENTS)
            .build()
    );
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CURSE_TAB = CREATIVE_MODE_TABS.register("curse_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.enigmaticlegacy_curse")).withLabelColor(0xBF1200)
            .icon(CURSED_RING::toStack).displayItems(display(CTAB_ACCEPT_LIST))
            .withTabsBefore(EnigmaticLegacy.location("tab"))
            .build()
    );

    private static CreativeModeTab.DisplayItemsGenerator display(List<DeferredItem<? extends Item>> list) {
        return (parameters, output) -> list.forEach(item -> output.accept(item.get()));
    }
}
