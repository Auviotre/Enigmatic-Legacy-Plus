package auviotre.enigmatic.legacy.client;

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

    public final ModConfigSpec.BooleanValue etheriumShieldRenderLayer;

    ClientConfig(ModConfigSpec.Builder builder) {
        etheriumShieldRenderLayer = builder.define("etheriumShieldRenderLayer", true);
    }
}
