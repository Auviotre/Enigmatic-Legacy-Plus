package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static auviotre.enigmatic.legacy.registries.EnigmaticItems.TAB_ACCEPT_LIST;
import static auviotre.enigmatic.legacy.registries.EnigmaticItems.THE_ACKNOWLEDGMENT;

public class EnigmaticTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnigmaticLegacy.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.enigmaticlegacyplus").withStyle(ChatFormatting.DARK_PURPLE))
            .icon(THE_ACKNOWLEDGMENT::toStack)
            .displayItems((parameters, output) -> TAB_ACCEPT_LIST.forEach(item -> output.accept(item.get()))).build());
}
