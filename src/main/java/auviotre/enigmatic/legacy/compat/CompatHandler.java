package auviotre.enigmatic.legacy.compat;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.compat.appleskin.AppleSkinCompatHandler;
import auviotre.enigmatic.legacy.compat.thirst.ThirstCompatHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

public class CompatHandler {
    private static final CompatHandler INSTANCE = new CompatHandler();

    public static CompatHandler getInstance() {
        return INSTANCE;
    }

    public static boolean isLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    public void register(IEventBus modEventBus) {
        if (isLoaded("appleskin") && FMLEnvironment.dist.isClient()) {
            EnigmaticLegacy.LOGGER.info("Appleskin Compat.");
            NeoForge.EVENT_BUS.register(new AppleSkinCompatHandler());
        }
        if (isLoaded("thirst")) {
            EnigmaticLegacy.LOGGER.info("Thirst Compat.");
            NeoForge.EVENT_BUS.register(new ThirstCompatHandler());
        }
//        if (isLoaded("farmersdelight")) {
//            EnigmaticLegacy.LOGGER.info("Farmer's Delight Compat.");
//            FDCompat.Tabs.TABS.register(modEventBus);
//            FDCompat.Items.ITEMS.register(modEventBus);
//            FDCompat.Blocks.BLOCKS.register(modEventBus);
//            NeoForge.EVENT_BUS.register(new FDCompat.Handler());
//        }
    }
}
