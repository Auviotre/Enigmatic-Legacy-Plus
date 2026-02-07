package auviotre.enigmatic.legacy;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ELConfig {

    public static final ModConfigSpec SPEC;
    public static final ELConfig CONFIG;

    static {
        final Pair<ELConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(ELConfig::new);
        SPEC = pair.getRight();
        CONFIG = pair.getLeft();
    }

    ELConfig(ModConfigSpec.Builder builder) {
        EnigmaticHandler.dispatchConfig(
                EnigmaticLegacy.MODID,
                builder,
                ModConfig.Type.COMMON
        );
    }
}
