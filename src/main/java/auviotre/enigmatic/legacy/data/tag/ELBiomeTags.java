package auviotre.enigmatic.legacy.data.tag;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ELBiomeTags extends BiomeTagsProvider {
    public ELBiomeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnigmaticLegacy.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(EnigmaticTags.Biomes.HAS_SPELLSTONE_HUT)
                .addTag(BiomeTags.IS_FOREST)
                .addTag(BiomeTags.IS_MOUNTAIN)
                .addTag(BiomeTags.IS_JUNGLE)
                .addTag(BiomeTags.IS_OCEAN)
                .add(
                        Biomes.MUSHROOM_FIELDS,
                        Biomes.PLAINS,
                        Biomes.SNOWY_PLAINS,
                        Biomes.JUNGLE,
                        Biomes.SAVANNA
                );
    }
}
