package auviotre.enigmatic.legacy.data.tag;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ELEntityTypeTags extends EntityTypeTagsProvider {
    public ELEntityTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnigmaticLegacy.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(EnigmaticTags.EntityTypes.NEUTRAL_ANGER_BLACKLIST).addOptional(ResourceLocation.fromNamespaceAndPath("the_bumblezone", "bee_queen"));
        this.tag(EnigmaticTags.EntityTypes.END_DWELLERS).add(
                EntityType.ENDERMAN,
                EntityType.ENDERMITE,
                EntityType.ENDER_DRAGON,
                EntityType.SHULKER
        );
    }
}
