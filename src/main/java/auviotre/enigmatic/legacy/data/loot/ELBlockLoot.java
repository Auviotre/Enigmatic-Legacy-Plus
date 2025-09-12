package auviotre.enigmatic.legacy.data.loot;

import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

import static auviotre.enigmatic.legacy.registries.EnigmaticBlocks.*;

public class ELBlockLoot extends BlockLootSubProvider {
    private final Set<Block> generatedLootTables = new HashSet<>();

    public ELBlockLoot(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    protected void generate() {
        this.dropSelf(DIMENSIONAL_ANCHOR.get());
        this.dropSelf(ETHERIUM_BLOCK.get());
        this.add(ETHERIUM_ORE.get(), block -> createOreDrop(block, EnigmaticItems.RAW_ETHERIUM.get()));
    }

    protected void add(Block block, LootTable.Builder builder) {
        this.generatedLootTables.add(block);
        this.map.put(block.getLootTable(), builder);
    }

    protected Iterable<Block> getKnownBlocks() {
        return this.generatedLootTables;
    }
}
