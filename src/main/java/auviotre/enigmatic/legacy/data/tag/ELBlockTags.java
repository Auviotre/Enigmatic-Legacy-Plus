package auviotre.enigmatic.legacy.data.tag;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ELBlockTags extends BlockTagsProvider {
    public ELBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnigmaticLegacy.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
                EnigmaticBlocks.DIMENSIONAL_ANCHOR.get(),
                EnigmaticBlocks.ETHERIUM_ORE.get(),
                EnigmaticBlocks.ETHERIUM_BLOCK.get()
        );
        this.tag(Tags.Blocks.ORES).add(
                EnigmaticBlocks.ETHERIUM_ORE.get()
        );
        this.tag(Tags.Blocks.STORAGE_BLOCKS).add(
                EnigmaticBlocks.ASTRAL_DUST_SACK.get(),
                EnigmaticBlocks.ETHERIUM_BLOCK.get()
        );
    }
}
