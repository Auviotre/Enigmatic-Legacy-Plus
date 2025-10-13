package auviotre.enigmatic.legacy.api;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This is used to annotate methods that should receive OmniconfigWrapper object
 * when {@link auviotre.enigmatic.legacy.handlers.EnigmaticHandler#dispatchConfig(String, ModConfigSpec.Builder, ModConfig.Type)}
 *
 * @author Integral
 */

@Retention(RUNTIME)
@Target(value = ElementType.METHOD)
public @interface SubscribeConfig {
    boolean defaultReceiveClient = false;

    /**
     * @return By default, only wrapper for common file is dispatched, by some handlers define themselves
     * to receive client wrapper through overriding return value of this method.
     */

    boolean receiveClient() default defaultReceiveClient;
}
