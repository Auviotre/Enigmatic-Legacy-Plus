package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class EnigmaticPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ETHERIUM_ORE_PLACED = createKey("etherium_ore_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);
        register(context, features, ETHERIUM_ORE_PLACED, EnigmaticConfiguredFeatures.ETHERIUM_ORE, List.of(CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, EnigmaticLegacy.location(name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> holderGetter, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> configured, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(holderGetter.getOrThrow(configured), List.copyOf(modifiers)));
    }
}
