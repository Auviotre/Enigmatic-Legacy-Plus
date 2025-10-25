package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.world.structure.SpellstoneHut;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public class EnigmaticStructures {
    public static final ResourceKey<Structure> SPELLSTONE_HUT = key("spellstone_hut");
    public static final ResourceKey<StructureSet> SPELLSTONE_HUT_STE = setKey("spellstone_hut");
    public static void bootstrapStructure(BootstrapContext<Structure> context) {
        HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
        context.register(SPELLSTONE_HUT, new SpellstoneHut(
                new Structure.StructureSettings.Builder(biome.getOrThrow(EnigmaticTags.Biomes.HAS_SPELLSTONE_HUT))
                        .generationStep(GenerationStep.Decoration.UNDERGROUND_STRUCTURES)
                        .terrainAdapation(TerrainAdjustment.BURY)
                        .build()
        ));
    }

    public static void bootstrapSet(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structure = context.lookup(Registries.STRUCTURE);
        HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
        context.register(SPELLSTONE_HUT_STE,
                new StructureSet(structure.getOrThrow(EnigmaticStructures.SPELLSTONE_HUT), new RandomSpreadStructurePlacement(32, 11, RandomSpreadType.LINEAR, 68431572))
        );
    }

    private static ResourceKey<Structure> key(String name) {
        return ResourceKey.create(Registries.STRUCTURE, EnigmaticLegacy.location(name));
    }
    private static ResourceKey<StructureSet> setKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, EnigmaticLegacy.location(name));
    }
}
