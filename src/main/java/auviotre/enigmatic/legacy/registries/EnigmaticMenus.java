package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.gui.LoreInscriberMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, EnigmaticLegacy.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<LoreInscriberMenu>> LORE_INSCRIBER_MENU = MENUS.register("lore_inscriber", () -> IMenuTypeExtension.create(LoreInscriberMenu::new));
}
