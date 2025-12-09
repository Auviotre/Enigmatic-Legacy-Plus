package auviotre.enigmatic.legacy.data;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.crafting.CursedShapedRecipe;
import auviotre.enigmatic.legacy.contents.crafting.ShapelessNoRemainRecipe;
import auviotre.enigmatic.legacy.contents.crafting.SpellstoneTableRecipe;
import auviotre.enigmatic.legacy.data.helpers.RecipeProviderWithHelper;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ELRecipeProvider extends RecipeProviderWithHelper {

    public ELRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EnigmaticItems.SPELLTUNER)
                .pattern("SAA").pattern("AXA").pattern("AAS")
                .define('S', EnigmaticItems.SPELLSTONE_DEBRIS).define('A', Items.AMETHYST_SHARD)
                .define('X', EnigmaticItems.SPELLCORE)
                .unlockedBy("has_item", has(EnigmaticItems.SPELLCORE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EnigmaticBlocks.SPELLSTONE_TABLE)
                .pattern(" X ").pattern("AIA").pattern("SSS")
                .define('I', EnigmaticItems.ICHOR_DROPLET).define('A', Items.ENDER_PEARL)
                .define('X', EnigmaticItems.SPELLCORE).define('S', Blocks.CRYING_OBSIDIAN)
                .unlockedBy("has_item", has(EnigmaticItems.SPELLCORE))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EnigmaticItems.THE_ACKNOWLEDGMENT)
                .requires(Items.BOOK).requires(Items.LANTERN)
                .unlockedBy("has_item", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.IRON_RING)
                .pattern("NIN").pattern("I I").pattern("NIN")
                .define('I', Items.IRON_INGOT).define('N', Items.IRON_NUGGET)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.GOLDEN_RING)
                .pattern("NIN").pattern("IXI").pattern("NIN")
                .define('I', Items.GOLD_INGOT).define('N', Items.GOLD_NUGGET).define('X', EnigmaticItems.IRON_RING)
                .unlockedBy("has_item", has(Items.GOLD_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.MINER_RING)
                .pattern("CIC").pattern("BXB").pattern("CFC")
                .define('C', Items.COPPER_INGOT).define('I', Items.IRON_PICKAXE)
                .define('B', Items.COAL).define('F', Blocks.BLAST_FURNACE).define('X', EnigmaticItems.IRON_RING)
                .unlockedBy("has_item", has(EnigmaticItems.IRON_RING))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.MAGNET_RING)
                .pattern(" D ").pattern("IXG").pattern(" R ")
                .define('G', Items.GOLD_INGOT).define('I', Items.IRON_INGOT)
                .define('D', Items.DIAMOND).define('R', Items.REDSTONE).define('X', EnigmaticItems.IRON_RING)
                .unlockedBy("has_item", has(EnigmaticItems.IRON_RING))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.DISLOCATION_RING)
                .pattern("LEL").pattern("GXG").pattern("LGL")
                .define('G', Items.GOLD_INGOT).define('L', Items.LAPIS_LAZULI)
                .define('E', Items.ENDER_EYE).define('X', EnigmaticItems.MAGNET_RING)
                .unlockedBy("has_item", has(EnigmaticItems.MAGNET_RING))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.QUARTZ_RING)
                .pattern("QLQ").pattern("QXQ").pattern("LTL")
                .define('Q', Items.QUARTZ).define('L', Items.LAPIS_LAZULI)
                .define('T', Items.GHAST_TEAR).define('X', EnigmaticItems.GOLDEN_RING)
                .unlockedBy("has_item", has(EnigmaticItems.GOLDEN_RING))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.ENDER_RING)
                .pattern(" C ").pattern("GXG").pattern("NEN")
                .define('C', Items.ENDER_CHEST).define('G', Items.GOLD_INGOT)
                .define('N', Items.GOLD_NUGGET).define('E', Items.ENDER_PEARL).define('X', EnigmaticItems.IRON_RING)
                .unlockedBy("has_item", has(Items.ENDER_CHEST))
                .save(output);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(EnigmaticItems.IRON_RING), RecipeCategory.MISC, Items.IRON_INGOT, 0.1F, 200)
                .unlockedBy("has_item", has(EnigmaticItems.IRON_RING)).save(output, EnigmaticLegacy.MODID + ":" + getSmeltingRecipeName(Items.IRON_INGOT));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(EnigmaticItems.GOLDEN_RING), RecipeCategory.MISC, Items.GOLD_INGOT, 0.1F, 200)
                .unlockedBy("has_item", has(EnigmaticItems.GOLDEN_RING)).save(output, EnigmaticLegacy.MODID + ":" + getSmeltingRecipeName(Items.GOLD_INGOT));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(EnigmaticItems.IRON_RING), RecipeCategory.MISC, Items.IRON_INGOT, 0.1F, 100)
                .unlockedBy("has_item", has(EnigmaticItems.IRON_RING)).save(output, EnigmaticLegacy.MODID + ":" + getBlastingRecipeName(Items.IRON_INGOT));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(EnigmaticItems.GOLDEN_RING), RecipeCategory.MISC, Items.GOLD_INGOT, 0.1F, 100)
                .unlockedBy("has_item", has(EnigmaticItems.GOLDEN_RING)).save(output, EnigmaticLegacy.MODID + ":" + getBlastingRecipeName(Items.GOLD_INGOT));

        SimpleCookingRecipeBuilder.blasting(Ingredient.of(EnigmaticBlocks.ETHERIUM_ORE), RecipeCategory.MISC, EnigmaticItems.RAW_ETHERIUM, 0.8F, 200)
                .unlockedBy("has_item", has(EnigmaticBlocks.ETHERIUM_ORE)).save(output, EnigmaticLegacy.MODID + ":" + getBlastingRecipeName(EnigmaticItems.RAW_ETHERIUM));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(EnigmaticItems.RAW_ETHERIUM), RecipeCategory.MISC, EnigmaticItems.ETHERIUM_INGOT, 0.8F, 200)
                .unlockedBy("has_item", has(EnigmaticItems.RAW_ETHERIUM)).save(output, EnigmaticLegacy.MODID + ":" + getBlastingRecipeName(EnigmaticItems.ETHERIUM_INGOT));
        nineBlockStorageRecipes(
                output,
                RecipeCategory.MISC,
                EnigmaticItems.ETHERIUM_NUGGET,
                RecipeCategory.MISC,
                EnigmaticItems.ETHERIUM_INGOT,
                EnigmaticLegacy.MODID + ":etherium_ingot_from_etherium_nugget",
                "etherium_ingot",
                EnigmaticLegacy.MODID + ":etherium_nugget",
                null
        );
        nineBlockStorageRecipes(
                output,
                RecipeCategory.MISC,
                EnigmaticItems.ETHERIUM_INGOT,
                RecipeCategory.BUILDING_BLOCKS,
                EnigmaticBlocks.ETHERIUM_BLOCK,
                EnigmaticLegacy.MODID + ":etherium_block",
                null,
                EnigmaticLegacy.MODID + ":etherium_ingot_from_etherium_block",
                "etherium_ingot"
        );
        nineBlockStorageRecipes(
                output,
                RecipeCategory.MISC,
                EnigmaticItems.ASTRAL_DUST,
                RecipeCategory.BUILDING_BLOCKS,
                EnigmaticBlocks.ASTRAL_DUST_SACK,
                EnigmaticLegacy.MODID + ":astral_dust_sack",
                null,
                EnigmaticLegacy.MODID + ":astral_dust_from_astral_dust_sack",
                "astral_dust"
        );

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.BLANK_SCROLL)
                .pattern("SP ").pattern(" P ").pattern(" PS")
                .define('P', Items.PAPER).define('S', Items.STICK)
                .unlockedBy("has_item", has(Items.PAPER))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.XP_SCROLL)
                .pattern("EYE").pattern("BXI").pattern("EME")
                .define('E', Items.EXPERIENCE_BOTTLE).define('Y', Items.ENDER_EYE).define('M', Items.EMERALD)
                .define('B', Items.INK_SAC).define('I', Items.FEATHER).define('X', EnigmaticItems.BLANK_SCROLL)
                .unlockedBy("has_item", has(EnigmaticItems.BLANK_SCROLL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.HEAVEN_SCROLL)
                .pattern("GSG").pattern("BXI").pattern("LUL")
                .define('G', Items.GOLD_INGOT).define('S', Items.NETHER_STAR)
                .define('B', Items.INK_SAC).define('I', Items.FEATHER).define('X', EnigmaticItems.BLANK_SCROLL)
                .define('L', Items.LAPIS_LAZULI).define('U', EnigmaticItems.ANGEL_BLESSING)
                .unlockedBy("has_item", has(EnigmaticItems.ANGEL_BLESSING))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.FABULOUS_SCROLL)
                .pattern("IAI").pattern("AXA").pattern("DUD")
                .define('I', EnigmaticItems.ETHERIUM_INGOT).define('A', EnigmaticItems.ASTRAL_DUST)
                .define('D', Items.DRAGON_BREATH).define('U', Items.ELYTRA)
                .define('X', EnigmaticItems.HEAVEN_SCROLL)
                .unlockedBy("has_item", has(EnigmaticItems.HEAVEN_SCROLL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.UNWITNESSED_AMULET)
                .pattern("NGN").pattern("GXG").pattern("NGN")
                .define('N', Items.GOLD_NUGGET).define('G', EnigmaticItems.EARTH_HEART_FRAGMENT)
                .define('X', EnigmaticItems.ENIGMATIC_AMULET)
                .unlockedBy("has_item", has(EnigmaticItems.EARTH_HEART_FRAGMENT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.ANIMAL_GUIDEBOOK)
                .pattern(" G ").pattern("FXF").pattern(" A ")
                .define('A', Items.APPLE).define('G', Items.GOLD_NUGGET)
                .define('F', Items.DANDELION).define('X', Items.BOOK)
                .unlockedBy("has_item", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.HUNTER_GUIDEBOOK)
                .pattern("BE ").pattern("GXG").pattern(" LB")
                .define('E', Items.ENDER_PEARL).define('G', Items.GOLD_NUGGET)
                .define('L', Items.LEATHER).define('B', Items.BONE).define('X', Items.BOOK)
                .unlockedBy("has_item", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.ODE_TO_LIVING)
                .pattern("GXG").pattern("AYB").pattern("GEG")
                .define('E', Items.EXPERIENCE_BOTTLE).define('G', Items.GOLD_NUGGET)
                .define('Y', Items.GOLDEN_APPLE)
                .define('A', EnigmaticItems.ANIMAL_GUIDEBOOK)
                .define('B', EnigmaticItems.HUNTER_GUIDEBOOK)
                .define('X', EnigmaticItems.EARTH_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.EARTH_HEART))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.ENCHANTMENT_TRANSPOSER)
                .pattern("PGP").pattern("LXL").pattern("BRB")
                .define('P', Items.PRISMARINE_CRYSTALS).define('G', Items.GOLD_NUGGET).define('X', Items.BOOK)
                .define('L', Items.LAPIS_LAZULI).define('B', Items.BLAZE_POWDER).define('R', Items.REDSTONE)
                .unlockedBy("has_item", has(Items.BOOK))
                .save(output);
        ShapelessNoRemainRecipe.Builder.shapeless(RecipeCategory.MISC, EnigmaticItems.MENDING_MIXTURE)
                .requires(Items.DRAGON_BREATH).requires(Items.GLISTERING_MELON_SLICE).requires(Tags.Items.GEMS_PRISMARINE)
                .requires(Items.PHANTOM_MEMBRANE).requires(Items.BLAZE_POWDER).requires(Items.GHAST_TEAR)
                .unlockedBy("has_item", has(Items.DRAGON_BREATH))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.EXTRADIMENSIONAL_EYE)
                .pattern(" P ").pattern("GXG").pattern("NBN")
                .define('P', Items.PHANTOM_MEMBRANE).define('G', Items.GOLD_INGOT)
                .define('N', Items.GOLD_NUGGET).define('B', Items.BLAZE_POWDER).define('X', Items.ENDER_EYE)
                .unlockedBy("has_item", has(Items.ENDER_EYE))
                .save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EnigmaticItems.EARTH_HEART)
                .requires(EnigmaticItems.EARTH_HEART_FRAGMENT, 8)
                .unlockedBy("has_item", has(EnigmaticItems.EARTH_HEART_FRAGMENT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.MINING_CHARM)
                .pattern("IDI").pattern("GXG").pattern("ATA")
                .define('I', Items.IRON_NUGGET).define('D', Items.DIAMOND).define('G', Items.GOLD_INGOT)
                .define('A', Items.GLOWSTONE_DUST).define('T', Items.GHAST_TEAR).define('X', EnigmaticItems.EARTH_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.EARTH_HEART))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.MONSTER_CHARM)
                .pattern(" L ").pattern("GXG").pattern("ATA")
                .define('L', Items.SOUL_LANTERN).define('G', Items.BLAZE_POWDER).define('T', Items.NETHERITE_INGOT)
                .define('A', Items.EXPERIENCE_BOTTLE).define('X', Items.SKELETON_SKULL)
                .unlockedBy("has_item", has(Items.NETHERITE_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.FORGER_GEM)
                .pattern(" D ").pattern("IDI").pattern("NXN")
                .define('D', Items.DIAMOND).define('I', Items.IRON_INGOT)
                .define('N', Items.NETHERITE_SCRAP).define('X', EnigmaticItems.QUARTZ_RING)
                .unlockedBy("has_item", has(Items.NETHERITE_SCRAP))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.INFINIMEAL)
                .pattern("ABC").pattern("DXE").pattern("FGH")
                .define('A', Items.COCOA_BEANS).define('B', Items.VINE).define('C', Items.WARPED_FUNGUS)
                .define('D', Items.APPLE).define('E', Items.WHEAT).define('F', Items.POPPY)
                .define('G', Items.LILY_PAD).define('H', Items.NETHER_WART).define('X', EnigmaticItems.EARTH_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.EARTH_HEART))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EnigmaticItems.EXECUTION_AXE)
                .pattern("NVN").pattern("PXP").pattern(" R ")
                .define('X', Items.DIAMOND_AXE).define('N', Items.NETHERITE_INGOT)
                .define('P', Items.BLAZE_POWDER).define('R', Items.BLAZE_ROD).define('V', Items.WITHER_SKELETON_SKULL)
                .unlockedBy("has_item", has(Items.WITHER_SKELETON_SKULL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, EnigmaticItems.ICHOROOT)
                .pattern("AXA").pattern("AGA").pattern("AXA")
                .define('X', EnigmaticItems.ICHOR_DROPLET)
                .define('G', Items.GHAST_TEAR).define('A', Items.BEETROOT)
                .unlockedBy("has_item", has(EnigmaticItems.ICHOR_DROPLET))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EnigmaticItems.ICHOR_SPEAR, 2)
                .pattern(" IG").pattern(" BI").pattern("B  ")
                .define('I', EnigmaticItems.ICHOR_DROPLET).define('G', Items.GOLD_INGOT)
                .define('B', Items.BLAZE_ROD)
                .unlockedBy("has_item", has(EnigmaticItems.ICHOR_DROPLET))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, EnigmaticItems.EXTERMINATO)
                .pattern("BXB").pattern("XAX").pattern("BXB")
                .define('X', Items.BAKED_POTATO).define('A', EnigmaticItems.INFERNAL_CINDER)
                .define('B', Items.BLAZE_POWDER)
                .unlockedBy("has_item", has(EnigmaticItems.INFERNAL_CINDER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, EnigmaticItems.ASTRAL_FRUIT)
                .pattern("AAA").pattern("AXA").pattern("AAA")
                .define('X', Items.APPLE).define('A', EnigmaticItems.ASTRAL_DUST)
                .unlockedBy("has_item", has(EnigmaticItems.ASTRAL_DUST))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.ENDER_ROD)
                .pattern("  B").pattern("AXA").pattern("B  ")
                .define('X', Items.ENDER_EYE).define('B', Items.BLAZE_ROD).define('A', EnigmaticItems.ASTRAL_DUST)
                .unlockedBy("has_item", has(EnigmaticItems.ASTRAL_DUST))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.COSMIC_HEART)
                .pattern("ANA").pattern("PHP").pattern("AXA")
                .define('X', Items.ENDER_EYE).define('P', Items.BLAZE_POWDER).define('A', EnigmaticItems.ASTRAL_DUST)
                .define('N', Items.NETHER_STAR).define('H', Items.HEART_OF_THE_SEA)
                .unlockedBy("has_item", has(EnigmaticItems.ASTRAL_DUST))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticBlocks.COSMIC_CAKE)
                .pattern("GBG").pattern("ACA").pattern("FXF")
                .define('X', EnigmaticItems.COSMIC_HEART).define('B', Items.BLAZE_POWDER).define('A', EnigmaticItems.ASTRAL_DUST)
                .define('G', Items.GHAST_TEAR).define('C', Blocks.CAKE).define('F', Items.CHORUS_FRUIT)
                .unlockedBy("has_item", has(EnigmaticItems.COSMIC_HEART))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EnigmaticBlocks.DIMENSIONAL_ANCHOR)
                .pattern(" E ").pattern("IRI").pattern("SXS")
                .define('E', Items.ENDER_EYE).define('S', Blocks.END_STONE).define('X', EnigmaticItems.EYE_OF_NEBULA)
                .define('R', Blocks.RESPAWN_ANCHOR).define('I', EnigmaticItems.ETHERIUM_INGOT)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EnigmaticItems.ETHERIUM_SWORD)
                .pattern(" X ").pattern("DXD").pattern(" R ")
                .define('D', Items.DIAMOND)
                .define('X', EnigmaticItems.ETHERIUM_INGOT)
                .define('R', EnigmaticItems.ENDER_ROD)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EnigmaticItems.ETHERIUM_HAMMER)
                .pattern("XXX").pattern("XRX").pattern(" R ")
                .define('X', EnigmaticItems.ETHERIUM_INGOT)
                .define('R', EnigmaticItems.ENDER_ROD)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EnigmaticItems.ETHERIUM_SCYTHE)
                .pattern("XX").pattern(" R").pattern(" R")
                .define('X', EnigmaticItems.ETHERIUM_INGOT)
                .define('R', EnigmaticItems.ENDER_ROD)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EnigmaticItems.ETHERIUM_HELMET)
                .pattern("XXX").pattern("X X")
                .define('X', EnigmaticItems.ETHERIUM_INGOT)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EnigmaticItems.ETHERIUM_CHESTPLATE)
                .pattern("X X").pattern("XXX").pattern("XXX")
                .define('X', EnigmaticItems.ETHERIUM_INGOT)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EnigmaticItems.ETHERIUM_LEGGINGS)
                .pattern("XXX").pattern("X X").pattern("X X")
                .define('X', EnigmaticItems.ETHERIUM_INGOT)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EnigmaticItems.ETHERIUM_BOOTS)
                .pattern("X X").pattern("X X")
                .define('X', EnigmaticItems.ETHERIUM_INGOT)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.ETHEREAL_FORGING_CHARM)
                .pattern(" F ").pattern("RXR").pattern("EHE")
                .define('F', EnigmaticItems.FORGER_GEM)
                .define('R', EnigmaticItems.ENDER_ROD).define('X', EnigmaticItems.EARTH_HEART)
                .define('E', EnigmaticItems.ETHERIUM_INGOT).define('H', EnigmaticItems.ETHERIUM_HAMMER)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_HAMMER))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EnigmaticBlocks.ETHEREAL_LANTERN)
                .pattern("NAN").pattern("IXI").pattern("EIE")
                .define('A', EnigmaticItems.ASTRAL_DUST)
                .define('N', EnigmaticItems.ETHERIUM_NUGGET).define('X', Blocks.SEA_LANTERN)
                .define('I', EnigmaticItems.ETHERIUM_INGOT).define('E', EnigmaticItems.ENDER_ROD)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_NUGGET))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EnigmaticItems.DRAGON_BREATH_BOW)
                .pattern("DED").pattern("NXD").pattern("DED")
                .define('D', Items.DRAGON_BREATH).define('E', EnigmaticItems.ENDER_ROD)
                .define('X', Items.DRAGON_HEAD).define('N', Items.NETHERITE_INGOT)
                .unlockedBy("has_item", has(EnigmaticItems.ENDER_ROD))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EnigmaticItems.MAJESTIC_ELYTRA)
                .pattern("BAB").pattern("EXE").pattern("DVD")
                .define('A', EnigmaticItems.ANGEL_BLESSING).define('D', EnigmaticItems.ASTRAL_DUST)
                .define('E', EnigmaticItems.ETHERIUM_INGOT).define('X', Items.ELYTRA)
                .define('B', Items.DRAGON_BREATH).define('V', EnigmaticItems.EYE_OF_NEBULA)
                .unlockedBy("has_item", has(Items.ELYTRA))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.ASCENSION_AMULET)
                .pattern("ADA").pattern("EXE").pattern("BVB")
                .define('A', Items.AMETHYST_SHARD).define('D', EnigmaticItems.ASTRAL_DUST)
                .define('E', EnigmaticItems.ETHERIUM_INGOT).define('X', EnigmaticItems.ENIGMATIC_AMULET)
                .define('B', Items.DRAGON_BREATH).define('V', EnigmaticItems.COSMIC_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.COSMIC_HEART))
                .save(output);


        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.TWISTED_HEART)
                .pattern(" T ").pattern("BXB").pattern("RER")
                .define('T', Items.GHAST_TEAR).define('B', Items.BLAZE_POWDER).define('R', Items.REDSTONE)
                .define('E', Items.ENDER_EYE).define('X', EnigmaticItems.EARTH_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.EARTH_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.CURSED_STONE)
                .pattern("LTL").pattern("ESE").pattern("PXP")
                .define('T', EnigmaticItems.TWISTED_HEART).define('L', Items.LAVA_BUCKET)
                .define('S', Blocks.STONE).define('P', EnigmaticItems.INFERNAL_CINDER)
                .define('E', EnigmaticItems.EVIL_ESSENCE).define('X', Items.NETHER_STAR)
                .unlockedBy("has_item", has(EnigmaticItems.EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.EVIL_INGOT)
                .pattern("GEG").pattern("EXE").pattern("GEG")
                .define('G', Items.GHAST_TEAR).define('E', EnigmaticItems.EVIL_ESSENCE)
                .define('X', Items.NETHERITE_INGOT)
                .unlockedBy("has_item", has(EnigmaticItems.EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.TOOLS, EnigmaticItems.TWISTED_MIRROR)
                .pattern("IGI").pattern("PXP").pattern(" I ")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS_PANE)
                .define('P', EnigmaticItems.RECALL_POTION).define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.TWISTED_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.COMBAT, EnigmaticItems.INFERNAL_SHIELD)
                .pattern("PNP").pattern("RUR").pattern("OXO")
                .define('P', EnigmaticItems.INFERNAL_CINDER).define('R', Items.BLAZE_ROD)
                .define('N', Items.NETHERITE_INGOT).define('O', Blocks.OBSIDIAN)
                .define('U', Items.SHIELD).define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.TWISTED_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.BERSERK_EMBLEM)
                .pattern("PSP").pattern("CXC").pattern("GTG")
                .define('C', EnigmaticItems.INFERNAL_CINDER).define('S', Items.GOLDEN_SWORD)
                .define('G', Items.GHAST_TEAR).define('P', Items.BLAZE_POWDER)
                .define('T', Items.NETHERITE_INGOT).define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.TWISTED_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.SANGUINARY_HANDBOOK)
                .pattern("CSC").pattern("GBG").pattern("DXD")
                .define('C', EnigmaticItems.INFERNAL_CINDER).define('S', Items.NETHERITE_INGOT)
                .define('G', Items.GHAST_TEAR).define('D', Items.DRAGON_BREATH)
                .define('B', EnigmaticItems.HUNTER_GUIDEBOOK).define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.TWISTED_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.TOOLS, EnigmaticItems.SOUL_COMPASS)
                .pattern(" S ").pattern("PXP").pattern("GTN")
                .define('N', Items.NETHERITE_INGOT).define('S', Items.SOUL_LANTERN)
                .define('G', Items.GOLD_INGOT).define('P', Items.BLAZE_POWDER)
                .define('T', EnigmaticItems.TWISTED_HEART).define('X', Items.COMPASS)
                .unlockedBy("has_item", has(EnigmaticItems.TWISTED_HEART))
                .save(output);

        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.PURE_HEART)
                .pattern(" T ").pattern("BXB").pattern("RER")
                .define('T', Items.GHAST_TEAR).define('B', EnigmaticItems.ICHOR_DROPLET).define('R', Items.GLOWSTONE_DUST)
                .define('E', Items.ENDER_EYE).define('X', EnigmaticItems.EARTH_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.ICHOR_DROPLET))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.THE_BLESS)
                .pattern("AGA").pattern("IXI").pattern("APA")
                .define('A', EnigmaticItems.SACRED_CRYSTAL).define('I', Items.GOLD_INGOT)
                .define('P', EnigmaticItems.PURE_HEART).define('G', Items.GLOWSTONE_DUST)
                .define('X', EnigmaticItems.THE_ACKNOWLEDGMENT)
                .unlockedBy("has_item", has(EnigmaticItems.SACRED_CRYSTAL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.BLESS_AMPLIFIER)
                .pattern("GIG").pattern("QXQ").pattern("GAG")
                .define('A', EnigmaticItems.SACRED_CRYSTAL).define('I', EnigmaticItems.ICHOR_DROPLET)
                .define('Q', Items.QUARTZ).define('G', Items.GLOWSTONE_DUST)
                .define('X', EnigmaticItems.ENCHANTMENT_TRANSPOSER)
                .unlockedBy("has_item", has(EnigmaticItems.SACRED_CRYSTAL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.EARTH_PROMISE)
                .pattern("PGP").pattern("QXQ").pattern("EAE")
                .define('A', EnigmaticItems.MINING_CHARM).define('E', Items.ENCHANTED_GOLDEN_APPLE)
                .define('G', Items.GOLDEN_APPLE).define('P', EnigmaticItems.PURE_HEART)
                .define('X', EnigmaticItems.GOLDEN_RING).define('Q', EnigmaticItems.SACRED_CRYSTAL)
                .unlockedBy("has_item", has(EnigmaticItems.SACRED_CRYSTAL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.BLESS_STONE)
                .pattern("IAI").pattern("AXA").pattern("BPB")
                .define('A', EnigmaticItems.SACRED_CRYSTAL)
                .define('P', EnigmaticItems.PURE_HEART).define('I', EnigmaticItems.ICHOR_DROPLET)
                .define('X', EnigmaticItems.CURSED_STONE).define('B', Items.GLOWSTONE_DUST)
                .unlockedBy("has_item", has(EnigmaticItems.CURSED_STONE))
                .save(output);

        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.TOTEM_OF_MALICE)
                .pattern(" N ").pattern("EXE").pattern(" N ")
                .define('N', Items.NETHERITE_INGOT).define('X', Items.TOTEM_OF_UNDYING)
                .define('E', EnigmaticItems.EVIL_ESSENCE)
                .unlockedBy("has_item", has(EnigmaticItems.EVIL_ESSENCE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.DARKEST_SCROLL, 2)
                .pattern("BNB").pattern("WXW").pattern("BTB")
                .define('B', Items.BLACK_DYE).define('W', Items.WITHER_ROSE).define('N', Items.NETHERITE_SCRAP)
                .define('X', EnigmaticItems.BLANK_SCROLL).define('T', EnigmaticItems.DARKEST_SCROLL)
                .unlockedBy("has_item", has(EnigmaticItems.DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.NIGHT_SCROLL)
                .pattern("MXM").pattern("BTY").pattern("MEM")
                .define('M', Items.PHANTOM_MEMBRANE).define('B', Items.WITHER_ROSE)
                .define('T', EnigmaticItems.DARKEST_SCROLL).define('Y', Items.FEATHER)
                .define('E', Items.ENDER_EYE).define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.CURSED_SCROLL)
                .pattern("CXC").pattern("BTY").pattern("RER")
                .define('C', EnigmaticItems.INFERNAL_CINDER)
                .define('B', Items.INK_SAC).define('Y', Items.FEATHER)
                .define('T', EnigmaticItems.DARKEST_SCROLL).define('R', Items.REDSTONE)
                .define('E', Items.ENCHANTED_BOOK).define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.AVARICE_SCROLL)
                .pattern("GRG").pattern("BTY").pattern("GXG")
                .define('G', Items.GOLD_INGOT).define('B', Items.INK_SAC).define('Y', Items.FEATHER)
                .define('T', EnigmaticItems.DARKEST_SCROLL).define('R', EnigmaticItems.GOLDEN_RING)
                .define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.CURSED_XP_SCROLL)
                .pattern("GXG").pattern("BTB").pattern("GEG")
                .define('G', Items.EXPERIENCE_BOTTLE).define('B', EnigmaticItems.EVIL_ESSENCE)
                .define('T', EnigmaticItems.DARKEST_SCROLL).define('E', Blocks.EMERALD_BLOCK)
                .define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.THE_TWIST)
                .pattern("VRV").pattern("NXN").pattern("VTV")
                .define('R', Items.REDSTONE).define('N', Items.NETHERITE_INGOT)
                .define('V', EnigmaticItems.EVIL_ESSENCE).define('T', EnigmaticItems.TWISTED_HEART)
                .define('X', EnigmaticItems.THE_ACKNOWLEDGMENT)
                .unlockedBy("has_item", has(EnigmaticItems.EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.CURSE_TRANSPOSER)
                .pattern("VRV").pattern("PXP").pattern("VTV")
                .define('R', Items.GHAST_TEAR).define('P', Items.PHANTOM_MEMBRANE)
                .define('V', Items.REDSTONE).define('T', EnigmaticItems.EVIL_ESSENCE)
                .define('X', EnigmaticItems.ENCHANTMENT_TRANSPOSER)
                .unlockedBy("has_item", has(EnigmaticItems.EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.ENCHANTER_PEARL)
                .pattern(" E ").pattern("VXV").pattern("PTP")
                .define('E', Items.EMERALD).define('X', Items.ENDER_PEARL)
                .define('T', Blocks.CRYING_OBSIDIAN).define('P', Items.BLAZE_POWDER)
                .define('V', EnigmaticItems.EVIL_ESSENCE)
                .unlockedBy("has_item", has(EnigmaticItems.EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.ENDER_SLAYER)
                .pattern("VOV").pattern("EOE").pattern("GSG")
                .define('E', Items.ENDER_EYE).define('O', Blocks.OBSIDIAN)
                .define('G', Items.GHAST_TEAR).define('S', Items.STICK)
                .define('V', EnigmaticItems.EVIL_ESSENCE)
                .unlockedBy("has_item", has(EnigmaticItems.EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.THE_INFINITUM)
                .pattern("CPC").pattern("EXE").pattern("IAI")
                .define('C', EnigmaticItems.COSMIC_HEART).define('P', EnigmaticItems.ENCHANTER_PEARL)
                .define('E', EnigmaticItems.EVIL_ESSENCE).define('X', EnigmaticItems.THE_TWIST)
                .define('I', Items.NETHERITE_INGOT).define('A', EnigmaticItems.ABYSSAL_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.ABYSSAL_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.ELDRITCH_AMULET)
                .pattern("EAE").pattern("IXI").pattern("TST")
                .define('T', EnigmaticItems.TWISTED_HEART).define('S', Items.NETHER_STAR)
                .define('E', EnigmaticItems.EVIL_ESSENCE).define('X', EnigmaticItems.ASCENSION_AMULET)
                .define('I', Items.NETHERITE_INGOT).define('A', EnigmaticItems.ABYSSAL_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.ABYSSAL_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.DESOLATION_RING)
                .pattern("CAC").pattern("IXI").pattern("EVE")
                .define('C', EnigmaticItems.COSMIC_HEART).define('V', EnigmaticItems.VOID_PEARL)
                .define('E', EnigmaticItems.EVIL_ESSENCE).define('X', EnigmaticItems.GOLDEN_RING)
                .define('I', Items.NETHERITE_INGOT).define('A', EnigmaticItems.ABYSSAL_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.ABYSSAL_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.CHAOS_ELYTRA)
                .pattern("CAC").pattern("IXI").pattern("EVE")
                .define('C', EnigmaticItems.COSMIC_HEART).define('V', EnigmaticItems.VOID_PEARL)
                .define('E', EnigmaticItems.EVIL_ESSENCE).define('X', Items.ELYTRA)
                .define('I', EnigmaticItems.EVIL_INGOT).define('A', EnigmaticItems.ABYSSAL_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.ABYSSAL_HEART))
                .save(output);

        SpellstoneTableRecipe.Builder.spell(EnigmaticItems.GOLEM_HEART, 6)
                .requires(Blocks.OBSIDIAN)
                .requires(Items.IRON_INGOT)
                .requires(Blocks.REDSTONE_BLOCK)
                .requires(EnigmaticItems.IRON_RING)
                .requires(Blocks.REDSTONE_BLOCK)
                .requires(Items.IRON_INGOT)
                .requires(Blocks.OBSIDIAN)
                .unlockedBy("has_item", has(EnigmaticItems.SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(EnigmaticItems.BLAZING_CORE, 6)
                .requires(Blocks.MAGMA_BLOCK)
                .requires(Items.BLAZE_POWDER)
                .requires(Blocks.BASALT)
                .requires(Items.MAGMA_CREAM)
                .requires(Blocks.BASALT)
                .requires(Items.BLAZE_POWDER)
                .requires(Blocks.MAGMA_BLOCK)
                .unlockedBy("has_item", has(EnigmaticItems.SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(EnigmaticItems.OCEAN_STONE, 6)
                .requires(Blocks.DARK_PRISMARINE)
                .requires(Items.PRISMARINE_CRYSTALS)
                .requires(Blocks.LAPIS_BLOCK)
                .requires(Items.HEART_OF_THE_SEA)
                .requires(Blocks.LAPIS_BLOCK)
                .requires(Items.PRISMARINE_CRYSTALS)
                .requires(Blocks.DARK_PRISMARINE)
                .unlockedBy("has_item", has(EnigmaticItems.SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(EnigmaticItems.ANGEL_BLESSING, 8)
                .requires(Items.DRAGON_BREATH)
                .requires(Items.PHANTOM_MEMBRANE)
                .requires(Blocks.GLOWSTONE)
                .requires(EnigmaticItems.QUARTZ_RING)
                .requires(Blocks.GLOWSTONE)
                .requires(Items.PHANTOM_MEMBRANE)
                .requires(Items.DRAGON_BREATH)
                .unlockedBy("has_item", has(EnigmaticItems.SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(EnigmaticItems.EYE_OF_NEBULA, 7)
                .requires(EnigmaticItems.ENDER_ROD)
                .requires(Items.CHORUS_FRUIT)
                .requires(Blocks.END_STONE_BRICKS)
                .requires(Items.ENDER_EYE)
                .requires(Blocks.END_STONE_BRICKS)
                .requires(Items.CHORUS_FRUIT)
                .requires(EnigmaticItems.ENDER_ROD)
                .unlockedBy("has_item", has(EnigmaticItems.SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(EnigmaticItems.VOID_PEARL, 10)
                .requires(Blocks.GOLD_BLOCK)
                .requires(EnigmaticItems.ETHERIUM_NUGGET)
                .requires(Items.ENDER_PEARL)
                .requires(EnigmaticItems.VOID_STONE)
                .requires(Items.ENDER_PEARL)
                .requires(EnigmaticItems.ETHERIUM_NUGGET)
                .requires(Blocks.GOLD_BLOCK)
                .unlockedBy("has_item", has(EnigmaticItems.SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(EnigmaticItems.THE_CUBE, 24).allDifferent()
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .requires(EnigmaticItems.COSMIC_HEART)
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .unlockedBy("has_item", has(EnigmaticItems.SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(EnigmaticItems.ETHERIUM_CORE, 9)
                .requires(EnigmaticItems.ENDER_ROD)
                .requires(EnigmaticBlocks.ETHERIUM_BLOCK)
                .requires(EnigmaticItems.ENDER_ROD)
                .requires(EnigmaticItems.EARTH_HEART)
                .requires(EnigmaticItems.ENDER_ROD)
                .requires(EnigmaticBlocks.ETHERIUM_BLOCK)
                .requires(EnigmaticItems.ENDER_ROD)
                .unlockedBy("has_item", has(EnigmaticItems.SPELLCORE))
                .save(output);
    }
}