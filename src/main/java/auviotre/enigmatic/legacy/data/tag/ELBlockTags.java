package auviotre.enigmatic.legacy.data.tag;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
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
                EnigmaticBlocks.SPELLSTONE_TABLE.get(),
                EnigmaticBlocks.ETHERIUM_ORE.get(),
                EnigmaticBlocks.ETHERIUM_BLOCK.get()
        );
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                EnigmaticBlocks.DIMENSIONAL_ANCHOR.get(),
                EnigmaticBlocks.SPELLSTONE_TABLE.get(),
                EnigmaticBlocks.ETHERIUM_ORE.get(),
                EnigmaticBlocks.ETHERIUM_BLOCK.get(),
                EnigmaticBlocks.ETHEREAL_LANTERN.get()
        );
        this.tag(BlockTags.DRAGON_IMMUNE).add(
                EnigmaticBlocks.DIMENSIONAL_ANCHOR.get()
        );
        this.tag(BlockTags.MINEABLE_WITH_HOE).add(
                EnigmaticBlocks.ASTRAL_DUST_SACK.get()
        );
        this.tag(Tags.Blocks.ORES).add(
                EnigmaticBlocks.ETHERIUM_ORE.get()
        );
        this.tag(Tags.Blocks.STORAGE_BLOCKS).add(
                EnigmaticBlocks.ASTRAL_DUST_SACK.get(),
                EnigmaticBlocks.ETHERIUM_BLOCK.get()
        );
        this.tag(EnigmaticTags.Blocks.ALL_MINEABLE)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(BlockTags.MINEABLE_WITH_SHOVEL)
                .addTag(BlockTags.MINEABLE_WITH_HOE);
    }
}
