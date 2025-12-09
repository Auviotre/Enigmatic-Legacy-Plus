package auviotre.enigmatic.legacy.data.tag;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ELEntityTypeTags extends EntityTypeTagsProvider {
    public ELEntityTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnigmaticLegacy.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(EnigmaticTags.EntityTypes.END_DWELLERS).add(
                EntityType.ENDERMAN,
                EntityType.ENDERMITE,
                EntityType.ENDER_DRAGON,
                EntityType.SHULKER
        );
        this.tag(EnigmaticTags.EntityTypes.GUARDIAN_HEART_EXCLUDED).add(
                EntityType.GUARDIAN,
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EnigmaticEntities.PIGLIN_WANDERER.get()
        );
    }
}
