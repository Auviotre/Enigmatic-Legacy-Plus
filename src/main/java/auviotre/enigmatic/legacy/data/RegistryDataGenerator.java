package auviotre.enigmatic.legacy.data;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RegistryDataGenerator extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            // Vanilla
            .add(Registries.CONFIGURED_FEATURE, EnigmaticConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, EnigmaticPlacedFeatures::bootstrap)
            .add(Registries.ENCHANTMENT, EnigmaticEnchantments::bootstrap)
            .add(Registries.DAMAGE_TYPE, EnigmaticDamageTypes::bootstrap)
            .add(Registries.STRUCTURE_SET, EnigmaticStructures::bootstrapSet)
            .add(Registries.STRUCTURE, EnigmaticStructures::bootstrapStructure)
            // NeoForge
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, EnigmaticBiomeModifiers::bootstrap);

    public RegistryDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of(EnigmaticLegacy.MODID, "minecraft"));
    }
}
