package auviotre.enigmatic.legacy.data.tag;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes.DARKNESS;
import static auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes.NEMESIS_CURSE;

public class ELDamageTypeTags extends DamageTypeTagsProvider {
    public ELDamageTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnigmaticLegacy.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(NEMESIS_CURSE, DARKNESS);
        this.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(DARKNESS);
        this.tag(DamageTypeTags.BYPASSES_RESISTANCE).add(DARKNESS);
        this.tag(DamageTypeTags.WITHER_IMMUNE_TO).add(DARKNESS);
    }
}
