package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class EnigmaticBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EnigmaticLegacy.MODID);

    public static final DeferredBlock<SpellstoneTable> SPELLSTONE_TABLE = registerBlock("spellstone_table", SpellstoneTable::new, Rarity.UNCOMMON);
    public static final DeferredBlock<EtheriumOre> ETHERIUM_ORE = registerFireResistanceBlock("etherium_ore", EtheriumOre::new);
    public static final DeferredBlock<Block> ETHERIUM_BLOCK = registerFireResistanceBlock("etherium_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK).mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 10)));
    public static final DeferredBlock<EtherealLantern> ETHEREAL_LANTERN = registerBlock("ethereal_lantern", EtherealLantern::new, EtherealLantern.Item::new);
    public static final DeferredBlock<Block> ASTRAL_DUST_SACK = registerBlock("astral_dust_sack", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(MapColor.COLOR_PINK)), Rarity.COMMON);
    public static final DeferredBlock<DimensionalAnchor> DIMENSIONAL_ANCHOR = registerFireResistanceBlock("dimensional_anchor", DimensionalAnchor::new);
    public static final DeferredBlock<CosmicCake> COSMIC_CAKE = registerBlock("cosmic_cake", CosmicCake::new, CosmicCake.Item::new);

    private static <B extends Block> DeferredBlock<B> registerBlock(String name, Supplier<? extends B> supplier, Function<B, ? extends BlockItem> itemSupplier) {
        DeferredBlock<B> block = BLOCKS.register(name, supplier);
        EnigmaticItems.ITEMS.register(name, () -> itemSupplier.apply(block.get()));
        return block;
    }

    private static <B extends Block> DeferredBlock<B> registerBlock(String name, Supplier<? extends B> supplier, Rarity rarity) {
        DeferredBlock<B> block = BLOCKS.register(name, supplier);
        EnigmaticItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().rarity(rarity)));
        return block;
    }

    private static <B extends Block> DeferredBlock<B> registerFireResistanceBlock(String name, Supplier<? extends B> supplier) {
        DeferredBlock<B> block = BLOCKS.register(name, supplier);
        EnigmaticItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().fireResistant()));
        return block;
    }
}
