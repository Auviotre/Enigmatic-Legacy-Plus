package auviotre.enigmatic.legacy.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ClientConfig CONFIG;

    static {
        final Pair<ClientConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        SPEC = pair.getRight();
        CONFIG = pair.getLeft();
    }

    ClientConfig(ModConfigSpec.Builder builder) {
        EnigmaticHandler.dispatchConfig(EnigmaticLegacy.MODID, builder, ModConfig.Type.CLIENT);
    }
}
