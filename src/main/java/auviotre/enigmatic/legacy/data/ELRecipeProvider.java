package auviotre.enigmatic.legacy.data;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.crafting.CursedShapedRecipe;
import auviotre.enigmatic.legacy.contents.crafting.ShapelessNoRemainRecipe;
import auviotre.enigmatic.legacy.contents.crafting.SpellstoneTableRecipe;
import auviotre.enigmatic.legacy.data.helpers.RecipeProviderWithHelper;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

import static auviotre.enigmatic.legacy.registries.EnigmaticItems.*;
import static auviotre.enigmatic.legacy.registries.EnigmaticBlocks.*;

public class ELRecipeProvider extends RecipeProviderWithHelper {

    public ELRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SPELLTUNER)
                .pattern("SAA").pattern("AXA").pattern("AAS")
                .define('S', SPELLSTONE_DEBRIS).define('A', Items.AMETHYST_SHARD)
                .define('X', SPELLCORE)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SPELLSTONE_SWORD)
                .pattern("ADA").pattern("DXD").pattern(" S ")
                .define('D', SPELLSTONE_DEBRIS).define('S', Items.STICK)
                .define('X', SPELLCORE).define('A', Items.AMETHYST_SHARD)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SPELLSTONE_TABLE)
                .pattern(" X ").pattern("AIA").pattern("SSS")
                .define('I', ICHOR_DROPLET).define('A', Items.ENDER_PEARL)
                .define('X', SPELLCORE).define('S', Blocks.CRYING_OBSIDIAN)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, THE_ACKNOWLEDGMENT)
                .requires(Items.BOOK).requires(Items.LANTERN)
                .unlockedBy("has_item", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, IRON_RING)
                .pattern("NIN").pattern("I I").pattern("NIN")
                .define('I', Items.IRON_INGOT).define('N', Items.IRON_NUGGET)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GOLDEN_RING)
                .pattern("NIN").pattern("IXI").pattern("NIN")
                .define('I', Items.GOLD_INGOT).define('N', Items.GOLD_NUGGET).define('X', IRON_RING)
                .unlockedBy("has_item", has(Items.GOLD_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MINER_RING)
                .pattern("CIC").pattern("BXB").pattern("CFC")
                .define('C', Items.COPPER_INGOT).define('I', Items.IRON_PICKAXE)
                .define('B', Items.COAL).define('F', Blocks.BLAST_FURNACE).define('X', IRON_RING)
                .unlockedBy("has_item", has(IRON_RING))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MAGNET_RING)
                .pattern(" D ").pattern("IXG").pattern(" R ")
                .define('G', Items.GOLD_INGOT).define('I', Items.IRON_INGOT)
                .define('D', Items.DIAMOND).define('R', Items.REDSTONE).define('X', IRON_RING)
                .unlockedBy("has_item", has(IRON_RING))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, DISLOCATION_RING)
                .pattern("LEL").pattern("GXG").pattern("LGL")
                .define('G', Items.GOLD_INGOT).define('L', Items.LAPIS_LAZULI)
                .define('E', Items.ENDER_EYE).define('X', MAGNET_RING)
                .unlockedBy("has_item", has(MAGNET_RING))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QUARTZ_RING)
                .pattern("QLQ").pattern("QXQ").pattern("LTL")
                .define('Q', Items.QUARTZ).define('L', Items.LAPIS_LAZULI)
                .define('T', Items.GHAST_TEAR).define('X', GOLDEN_RING)
                .unlockedBy("has_item", has(GOLDEN_RING))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ENDER_RING)
                .pattern(" C ").pattern("GXG").pattern("NEN")
                .define('C', Items.ENDER_CHEST).define('G', Items.GOLD_INGOT)
                .define('N', Items.GOLD_NUGGET).define('E', Items.ENDER_PEARL).define('X', IRON_RING)
                .unlockedBy("has_item", has(Items.ENDER_CHEST))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, INFERNAL_RING)
                .pattern("CNC").pattern("BXB").pattern("CGC")
                .define('C', INFERNAL_CINDER).define('G', Items.GHAST_TEAR)
                .define('N', Items.NETHERITE_SCRAP).define('B', Items.BLAZE_POWDER).define('X', IRON_RING)
                .unlockedBy("has_item", has(INFERNAL_CINDER))
                .save(output);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(IRON_RING), RecipeCategory.MISC, Items.IRON_INGOT, 0.1F, 200)
                .unlockedBy("has_item", has(IRON_RING)).save(output, EnigmaticLegacy.MODID + ":" + getSmeltingRecipeName(Items.IRON_INGOT));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(GOLDEN_RING), RecipeCategory.MISC, Items.GOLD_INGOT, 0.1F, 200)
                .unlockedBy("has_item", has(GOLDEN_RING)).save(output, EnigmaticLegacy.MODID + ":" + getSmeltingRecipeName(Items.GOLD_INGOT));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(IRON_RING), RecipeCategory.MISC, Items.IRON_INGOT, 0.1F, 100)
                .unlockedBy("has_item", has(IRON_RING)).save(output, EnigmaticLegacy.MODID + ":" + getBlastingRecipeName(Items.IRON_INGOT));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(GOLDEN_RING), RecipeCategory.MISC, Items.GOLD_INGOT, 0.1F, 100)
                .unlockedBy("has_item", has(GOLDEN_RING)).save(output, EnigmaticLegacy.MODID + ":" + getBlastingRecipeName(Items.GOLD_INGOT));

        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ETHERIUM_ORE), RecipeCategory.MISC, RAW_ETHERIUM, 0.8F, 200)
                .unlockedBy("has_item", has(ETHERIUM_ORE)).save(output, EnigmaticLegacy.MODID + ":" + getBlastingRecipeName(RAW_ETHERIUM));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(RAW_ETHERIUM), RecipeCategory.MISC, ETHERIUM_INGOT, 0.8F, 200)
                .unlockedBy("has_item", has(RAW_ETHERIUM)).save(output, EnigmaticLegacy.MODID + ":" + getBlastingRecipeName(ETHERIUM_INGOT));
        nineBlockStorageRecipes(
                output,
                RecipeCategory.MISC,
                ETHERIUM_NUGGET,
                RecipeCategory.MISC,
                ETHERIUM_INGOT,
                EnigmaticLegacy.MODID + ":etherium_ingot_from_etherium_nugget",
                "etherium_ingot",
                EnigmaticLegacy.MODID + ":etherium_nugget",
                null
        );
        nineBlockStorageRecipes(
                output,
                RecipeCategory.MISC,
                ETHERIUM_INGOT,
                RecipeCategory.BUILDING_BLOCKS,
                ETHERIUM_BLOCK,
                EnigmaticLegacy.MODID + ":etherium_block",
                null,
                EnigmaticLegacy.MODID + ":etherium_ingot_from_etherium_block",
                "etherium_ingot"
        );
        nineBlockStorageRecipes(
                output,
                RecipeCategory.MISC,
                STARLIGHT_INGOT,
                RecipeCategory.BUILDING_BLOCKS,
                STARLIGHT_BLOCK,
                EnigmaticLegacy.MODID + ":starlight_block",
                null,
                EnigmaticLegacy.MODID + ":starlight_ingot_from_starlight_block",
                "starlight_block"
        );
        nineBlockStorageRecipes(
                output,
                RecipeCategory.MISC,
                ASTRAL_DUST,
                RecipeCategory.BUILDING_BLOCKS,
                ASTRAL_DUST_SACK,
                EnigmaticLegacy.MODID + ":astral_dust_sack",
                null,
                EnigmaticLegacy.MODID + ":astral_dust_from_astral_dust_sack",
                "astral_dust"
        );
        nineBlockStorageRecipes(
                output,
                RecipeCategory.MISC,
                INFERNAL_CINDER,
                RecipeCategory.BUILDING_BLOCKS,
                INFERNAL_CINDER_SACK,
                EnigmaticLegacy.MODID + ":infernal_cinder_sack",
                null,
                EnigmaticLegacy.MODID + ":infernal_cinder_from_infernal_cinder_sack",
                "infernal_cinder"
        );
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ASTRAL_DUST_SACK), RecipeCategory.BUILDING_BLOCKS,
                        ASTRAL_GLASS.toStack(4), 0.4F, 200)
                .unlockedBy("has_item", has(ASTRAL_DUST_SACK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ASTRAL_GLASS_PANE, 16)
                .pattern("SS").pattern("SS")
                .define('S', ASTRAL_GLASS)
                .unlockedBy("has_item", has(ASTRAL_GLASS))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, STARLIGHT_INGOT)
                .pattern("SSS").pattern("NIN").pattern("SSS")
                .define('S', STARLIGHT_PARTICLE)
                .define('N', ETHERIUM_NUGGET).define('I', ETHERIUM_INGOT)
                .unlockedBy("has_item", has(STARLIGHT_PARTICLE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BLANK_SCROLL)
                .pattern("SP ").pattern(" P ").pattern(" PS")
                .define('P', Items.PAPER).define('S', Items.STICK)
                .unlockedBy("has_item", has(Items.PAPER))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SURVIVOR_SCROLL)
                .pattern("WLW").pattern("BXI").pattern("EME")
                .define('W', Items.WHEAT).define('L', Items.LEATHER)
                .define('E', Items.GOLD_INGOT).define('M', Blocks.CAMPFIRE)
                .define('B', Items.INK_SAC).define('I', Items.FEATHER).define('X', BLANK_SCROLL)
                .unlockedBy("has_item", has(BLANK_SCROLL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EXPLORER_SCROLL)
                .pattern("WLW").pattern("BXI").pattern("EME")
                .define('W', Items.LAPIS_LAZULI).define('L', Items.SUGAR)
                .define('E', Items.SUGAR_CANE).define('M', Items.RABBIT_FOOT)
                .define('B', Items.INK_SAC).define('I', Items.FEATHER).define('X', BLANK_SCROLL)
                .unlockedBy("has_item", has(BLANK_SCROLL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HUNTER_SCROLL)
                .pattern("EME").pattern("BXI").pattern("LCL")
                .define('C', Items.CROSSBOW).define('L', Items.LEATHER)
                .define('E', Items.ENDER_PEARL).define('M', Items.REDSTONE)
                .define('B', Items.INK_SAC).define('I', Items.FEATHER).define('X', BLANK_SCROLL)
                .unlockedBy("has_item", has(BLANK_SCROLL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, XP_SCROLL)
                .pattern("EYE").pattern("BXI").pattern("EME")
                .define('E', Items.EXPERIENCE_BOTTLE).define('Y', Items.ENDER_EYE).define('M', Items.EMERALD)
                .define('B', Items.INK_SAC).define('I', Items.FEATHER).define('X', BLANK_SCROLL)
                .unlockedBy("has_item", has(BLANK_SCROLL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ESCAPE_SCROLL)
                .pattern("SYS").pattern("BXI").pattern("ERE")
                .define('E', Items.PHANTOM_MEMBRANE).define('S', Items.SOUL_LANTERN)
                .define('R', RECALL_POTION).define('Y', Items.ENDER_EYE)
                .define('B', Items.INK_SAC).define('I', Items.FEATHER).define('X', BLANK_SCROLL)
                .unlockedBy("has_item", has(BLANK_SCROLL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HEAVEN_SCROLL)
                .pattern("GSG").pattern("BXI").pattern("LUL")
                .define('G', Items.GOLD_INGOT).define('S', Items.NETHER_STAR)
                .define('B', Items.INK_SAC).define('I', Items.FEATHER).define('X', BLANK_SCROLL)
                .define('L', Items.LAPIS_LAZULI).define('U', ANGEL_BLESSING)
                .unlockedBy("has_item", has(ANGEL_BLESSING))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FABULOUS_SCROLL)
                .pattern("IAI").pattern("AXA").pattern("DUD")
                .define('I', ETHERIUM_INGOT).define('A', ASTRAL_DUST)
                .define('D', Items.DRAGON_BREATH).define('U', Items.ELYTRA)
                .define('X', HEAVEN_SCROLL)
                .unlockedBy("has_item", has(HEAVEN_SCROLL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, UNWITNESSED_AMULET)
                .pattern(" N ").pattern("NXN").pattern(" G ")
                .define('N', Items.GOLD_NUGGET).define('G', EARTH_HEART_FRAGMENT)
                .define('X', EnigmaticTags.Items.ENIGMATIC_AMULETS)
                .unlockedBy("has_item", has(EARTH_HEART_FRAGMENT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ANIMAL_GUIDEBOOK)
                .pattern(" G ").pattern("FXF").pattern(" A ")
                .define('A', Items.APPLE).define('G', Items.GOLD_NUGGET)
                .define('F', Items.DANDELION).define('X', Items.BOOK)
                .unlockedBy("has_item", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HUNTER_GUIDEBOOK)
                .pattern("BE ").pattern("GXG").pattern(" LB")
                .define('E', Items.ENDER_PEARL).define('G', Items.GOLD_NUGGET)
                .define('L', Items.LEATHER).define('B', Items.BONE).define('X', Items.BOOK)
                .unlockedBy("has_item", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ODE_TO_LIVING)
                .pattern("GEG").pattern("AXB").pattern("YIY")
                .define('E', Items.EXPERIENCE_BOTTLE).define('G', Items.GOLD_INGOT)
                .define('Y', Items.GOLDEN_APPLE).define('I', ICHOR_DROPLET)
                .define('A', ANIMAL_GUIDEBOOK)
                .define('B', HUNTER_GUIDEBOOK)
                .define('X', EARTH_HEART)
                .unlockedBy("has_item", has(EARTH_HEART))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ENCHANTMENT_TRANSPOSER)
                .pattern("PGP").pattern("LXL").pattern("BRB")
                .define('P', Items.PRISMARINE_CRYSTALS).define('G', Items.GOLD_NUGGET).define('X', Items.BOOK)
                .define('L', Items.LAPIS_LAZULI).define('B', Items.BLAZE_POWDER).define('R', Items.REDSTONE)
                .unlockedBy("has_item", has(Items.BOOK))
                .save(output);
        ShapelessNoRemainRecipe.Builder.shapeless(RecipeCategory.MISC, MENDING_MIXTURE)
                .requires(Items.DRAGON_BREATH).requires(Items.GLISTERING_MELON_SLICE).requires(Tags.Items.GEMS_PRISMARINE)
                .requires(Items.PHANTOM_MEMBRANE).requires(Items.BLAZE_POWDER).requires(Items.GHAST_TEAR)
                .unlockedBy("has_item", has(Items.DRAGON_BREATH))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EXTRADIMENSIONAL_EYE)
                .pattern(" P ").pattern("GXG").pattern("NBN")
                .define('P', Items.PHANTOM_MEMBRANE).define('G', Items.GOLD_INGOT)
                .define('N', Items.GOLD_NUGGET).define('B', Items.BLAZE_POWDER).define('X', Items.ENDER_EYE)
                .unlockedBy("has_item", has(Items.ENDER_EYE))
                .save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EARTH_HEART)
                .requires(EARTH_HEART_FRAGMENT, 8)
                .unlockedBy("has_item", has(EARTH_HEART_FRAGMENT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MINING_CHARM)
                .pattern("IDI").pattern("GXG").pattern("ATA")
                .define('I', Items.IRON_NUGGET).define('D', Items.DIAMOND).define('G', Items.GOLD_INGOT)
                .define('A', Items.GLOWSTONE_DUST).define('T', Items.GHAST_TEAR).define('X', EARTH_HEART)
                .unlockedBy("has_item", has(EARTH_HEART))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MONSTER_CHARM)
                .pattern(" L ").pattern("GXG").pattern("ATA")
                .define('L', Items.SOUL_LANTERN).define('G', Items.BLAZE_POWDER).define('T', Items.NETHERITE_INGOT)
                .define('A', Items.EXPERIENCE_BOTTLE).define('X', Items.SKELETON_SKULL)
                .unlockedBy("has_item", has(Items.NETHERITE_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FORGER_GEM)
                .pattern(" D ").pattern("IDI").pattern("NXN")
                .define('D', Items.DIAMOND).define('I', Items.IRON_INGOT)
                .define('N', Items.NETHERITE_SCRAP).define('X', QUARTZ_RING)
                .unlockedBy("has_item", has(Items.NETHERITE_SCRAP))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, INFINIMEAL)
                .pattern("ABC").pattern("DXE").pattern("FGH")
                .define('A', Items.COCOA_BEANS).define('B', Items.VINE).define('C', Items.WARPED_FUNGUS)
                .define('D', Items.APPLE).define('E', Items.WHEAT).define('F', Items.POPPY)
                .define('G', Items.LILY_PAD).define('H', Items.NETHER_WART).define('X', EARTH_HEART)
                .unlockedBy("has_item", has(EARTH_HEART))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, INSIGNIA)
                .pattern(" R ").pattern("GEG").pattern("PNP")
                .define('R', Items.ENDER_EYE).define('G', Items.GOLD_INGOT)
                .define('E', Items.EMERALD).define('P', Items.PRISMARINE_CRYSTALS)
                .define('N', Items.NAME_TAG)
                .unlockedBy("has_item", has(Items.NAME_TAG))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EXECUTION_AXE)
                .pattern("AVN").pattern("PXP").pattern(" R ")
                .define('X', Items.DIAMOND_AXE).define('N', Items.NETHERITE_INGOT).define('A', Items.GOLD_INGOT)
                .define('P', INFERNAL_CINDER).define('R', Items.BLAZE_ROD).define('V', Items.WITHER_SKELETON_SKULL)
                .unlockedBy("has_item", has(Items.WITHER_SKELETON_SKULL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ICHOROOT)
                .pattern("AXA").pattern("AGA").pattern("AXA")
                .define('X', ICHOR_DROPLET)
                .define('G', Items.GHAST_TEAR).define('A', Items.BEETROOT)
                .unlockedBy("has_item", has(ICHOR_DROPLET))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ICHOR_SPEAR, 2)
                .pattern(" IG").pattern(" BI").pattern("B  ")
                .define('I', ICHOR_DROPLET).define('G', Items.GOLD_INGOT)
                .define('B', Items.BLAZE_ROD)
                .unlockedBy("has_item", has(ICHOR_DROPLET))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, INFERNAL_SPEAR)
                .pattern(" AS").pattern("BXA").pattern("AB ")
                .define('X', ICHOR_SPEAR).define('A', INFERNAL_CINDER)
                .define('B', Items.MAGMA_CREAM).define('S', Items.NETHERITE_SCRAP)
                .unlockedBy("has_item", has(INFERNAL_CINDER))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, EXTERMINATO)
                .pattern("BXB").pattern("XAX").pattern("BXB")
                .define('X', Items.BAKED_POTATO).define('A', INFERNAL_CINDER)
                .define('B', Items.BLAZE_POWDER)
                .unlockedBy("has_item", has(INFERNAL_CINDER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ASTRAL_FRUIT)
                .pattern("AAA").pattern("AXA").pattern("AAA")
                .define('X', Items.APPLE).define('A', ASTRAL_DUST)
                .unlockedBy("has_item", has(ASTRAL_DUST))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ENDER_ROD)
                .pattern("  B").pattern("AXA").pattern("B  ")
                .define('X', Items.ENDER_EYE).define('B', Items.BLAZE_ROD).define('A', ASTRAL_DUST)
                .unlockedBy("has_item", has(ASTRAL_DUST))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, COSMIC_HEART)
                .pattern("ANA").pattern("PHP").pattern("AXA")
                .define('X', Items.ENDER_EYE).define('P', Items.BLAZE_POWDER).define('A', ASTRAL_DUST)
                .define('N', Items.NETHER_STAR).define('H', Items.HEART_OF_THE_SEA)
                .unlockedBy("has_item", has(ASTRAL_DUST))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, COSMIC_CAKE)
                .pattern("GBG").pattern("ACA").pattern("FXF")
                .define('X', COSMIC_HEART).define('B', Items.BLAZE_POWDER).define('A', ASTRAL_DUST)
                .define('G', Items.GHAST_TEAR).define('C', Blocks.CAKE).define('F', Items.CHORUS_FRUIT)
                .unlockedBy("has_item", has(COSMIC_HEART))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DIMENSIONAL_ANCHOR)
                .pattern(" E ").pattern("IRI").pattern("SXS")
                .define('E', Items.ENDER_EYE).define('S', Blocks.END_STONE).define('X', EYE_OF_NEBULA)
                .define('R', Blocks.RESPAWN_ANCHOR).define('I', ETHERIUM_INGOT)
                .unlockedBy("has_item", has(ETHERIUM_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ETHERIUM_SWORD)
                .pattern(" X ").pattern("DXD").pattern(" R ")
                .define('D', Items.DIAMOND)
                .define('X', ETHERIUM_INGOT)
                .define('R', ENDER_ROD)
                .unlockedBy("has_item", has(ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ETHERIUM_HAMMER)
                .pattern("XXX").pattern("XRX").pattern(" R ")
                .define('X', ETHERIUM_INGOT)
                .define('R', ENDER_ROD)
                .unlockedBy("has_item", has(ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ETHERIUM_SCYTHE)
                .pattern("XX").pattern(" R").pattern(" R")
                .define('X', ETHERIUM_INGOT)
                .define('R', ENDER_ROD)
                .unlockedBy("has_item", has(ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ETHERIUM_HELMET)
                .pattern("XXX").pattern("X X")
                .define('X', ETHERIUM_INGOT)
                .unlockedBy("has_item", has(ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ETHERIUM_CHESTPLATE)
                .pattern("X X").pattern("XXX").pattern("XXX")
                .define('X', ETHERIUM_INGOT)
                .unlockedBy("has_item", has(ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ETHERIUM_LEGGINGS)
                .pattern("XXX").pattern("X X").pattern("X X")
                .define('X', ETHERIUM_INGOT)
                .unlockedBy("has_item", has(ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ETHERIUM_BOOTS)
                .pattern("X X").pattern("X X")
                .define('X', ETHERIUM_INGOT)
                .unlockedBy("has_item", has(ETHERIUM_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ETHEREAL_FORGING_CHARM)
                .pattern(" F ").pattern("RXR").pattern("EHE")
                .define('F', FORGER_GEM)
                .define('R', ENDER_ROD).define('X', EARTH_HEART)
                .define('E', ETHERIUM_INGOT).define('H', ETHERIUM_HAMMER)
                .unlockedBy("has_item", has(ETHERIUM_HAMMER))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ETHEREAL_LANTERN)
                .pattern("NAN").pattern("IXI").pattern("EIE")
                .define('A', ASTRAL_DUST)
                .define('N', ETHERIUM_NUGGET).define('X', Blocks.SEA_LANTERN)
                .define('I', ETHERIUM_INGOT).define('E', ENDER_ROD)
                .unlockedBy("has_item", has(ETHERIUM_NUGGET))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, DRAGON_BREATH_BOW)
                .pattern("DED").pattern("NXD").pattern("DED")
                .define('D', Items.DRAGON_BREATH).define('E', ENDER_ROD)
                .define('X', Items.DRAGON_HEAD).define('N', ETHERIUM_INGOT)
                .unlockedBy("has_item", has(ENDER_ROD))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, MAJESTIC_ELYTRA)
                .pattern("BAB").pattern("EXE").pattern("DVD")
                .define('A', ANGEL_BLESSING).define('D', ASTRAL_DUST)
                .define('E', ETHERIUM_INGOT).define('X', Items.ELYTRA)
                .define('B', Items.DRAGON_BREATH).define('V', EYE_OF_NEBULA)
                .unlockedBy("has_item", has(Items.ELYTRA))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, STARLIGHT_BUCKET)
                .pattern("   ").pattern("PAP").pattern(" I ")
                .define('P', STARLIGHT_PARTICLE)
                .define('I', STARLIGHT_INGOT).define('A', ASTRAL_DUST)
                .unlockedBy("has_item", has(STARLIGHT_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, STARLIGHT_RING)
                .pattern("PPP").pattern("AXA").pattern("SBS")
                .define('B', Blocks.BEACON).define('S', STARLIGHT_INGOT)
                .define('A', ASTRAL_DUST).define('X', QUARTZ_RING)
                .define('P', STARLIGHT_PARTICLE)
                .unlockedBy("has_item", has(STARLIGHT_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, STARLIGHT_PEARL)
                .pattern("PAP").pattern("SXS").pattern("PAP")
                .define('S', STARLIGHT_INGOT).define('A', ASTRAL_DUST)
                .define('X', Items.ENDER_PEARL).define('P', STARLIGHT_PARTICLE)
                .unlockedBy("has_item", has(STARLIGHT_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ASCENSION_AMULET)
                .pattern("ADA").pattern("EXE").pattern("BVB")
                .define('A', Items.AMETHYST_SHARD).define('D', ASTRAL_DUST)
                .define('E', STARLIGHT_INGOT).define('X', EnigmaticTags.Items.ENIGMATIC_AMULETS)
                .define('B', Items.DRAGON_BREATH).define('V', COSMIC_HEART)
                .unlockedBy("has_item", has(COSMIC_HEART))
                .save(output);

        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, TWISTED_HEART)
                .pattern(" T ").pattern("BXB").pattern("RER")
                .define('T', Items.GHAST_TEAR).define('B', Items.BLAZE_POWDER).define('R', Items.REDSTONE)
                .define('E', Items.ENDER_EYE).define('X', EARTH_HEART)
                .unlockedBy("has_item", has(EARTH_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, CURSED_STONE)
                .pattern("LTL").pattern("ESE").pattern("PXP")
                .define('T', TWISTED_HEART).define('L', Items.LAVA_BUCKET)
                .define('S', Blocks.STONE).define('P', INFERNAL_CINDER)
                .define('E', EVIL_ESSENCE).define('X', Items.NETHER_STAR)
                .unlockedBy("has_item", has(EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EVIL_INGOT)
                .pattern("GEG").pattern("EXE").pattern("GEG")
                .define('G', Items.GHAST_TEAR).define('E', EVIL_ESSENCE)
                .define('X', Items.NETHERITE_INGOT)
                .unlockedBy("has_item", has(EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.TOOLS, TWISTED_MIRROR)
                .pattern("IGI").pattern("PXP").pattern(" I ")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS_PANE)
                .define('P', RECALL_POTION).define('X', TWISTED_HEART)
                .unlockedBy("has_item", has(TWISTED_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.COMBAT, INFERNAL_SHIELD)
                .pattern("PNP").pattern("RUR").pattern("OXO")
                .define('P', INFERNAL_CINDER).define('R', Items.BLAZE_ROD)
                .define('N', Items.NETHERITE_INGOT).define('O', Blocks.OBSIDIAN)
                .define('U', Items.SHIELD).define('X', TWISTED_HEART)
                .unlockedBy("has_item", has(TWISTED_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, BERSERK_EMBLEM)
                .pattern("PSP").pattern("CXC").pattern("GTG")
                .define('C', INFERNAL_CINDER).define('S', Items.GOLDEN_SWORD)
                .define('G', Items.GHAST_TEAR).define('P', Items.BLAZE_POWDER)
                .define('T', Items.NETHERITE_INGOT).define('X', TWISTED_HEART)
                .unlockedBy("has_item", has(TWISTED_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, SANGUINARY_HANDBOOK)
                .pattern("CSC").pattern("GBG").pattern("DXD")
                .define('C', INFERNAL_CINDER).define('S', Items.NETHERITE_INGOT)
                .define('G', Items.GHAST_TEAR).define('D', Items.DRAGON_BREATH)
                .define('B', HUNTER_GUIDEBOOK).define('X', TWISTED_HEART)
                .unlockedBy("has_item", has(TWISTED_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.TOOLS, SOUL_COMPASS)
                .pattern(" S ").pattern("PXP").pattern("GTN")
                .define('N', Items.NETHERITE_INGOT).define('S', Items.SOUL_LANTERN)
                .define('G', Items.GOLD_INGOT).define('P', Items.BLAZE_POWDER)
                .define('T', TWISTED_HEART).define('X', Items.COMPASS)
                .unlockedBy("has_item", has(TWISTED_HEART))
                .save(output);

        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, PURE_HEART)
                .pattern(" T ").pattern("BXB").pattern("RER")
                .define('T', Items.GHAST_TEAR).define('B', ICHOR_DROPLET).define('R', Items.GLOWSTONE_DUST)
                .define('E', Items.ENDER_EYE).define('X', EARTH_HEART)
                .unlockedBy("has_item", has(ICHOR_DROPLET))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, THE_BLESS)
                .pattern("AGA").pattern("IXI").pattern("APA")
                .define('A', SACRED_CRYSTAL).define('I', Items.GOLD_INGOT)
                .define('P', PURE_HEART).define('G', Items.GLOWSTONE_DUST)
                .define('X', THE_ACKNOWLEDGMENT)
                .unlockedBy("has_item", has(SACRED_CRYSTAL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, BLESS_AMPLIFIER)
                .pattern("GIG").pattern("QXQ").pattern("GAG")
                .define('A', SACRED_CRYSTAL).define('I', ICHOR_DROPLET)
                .define('Q', Items.QUARTZ).define('G', Items.GLOWSTONE_DUST)
                .define('X', ENCHANTMENT_TRANSPOSER)
                .unlockedBy("has_item", has(SACRED_CRYSTAL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, SACRED_CHALICE)
                .pattern("XIX").pattern("XXX").pattern(" X ")
                .define('I', ICHOR_DROPLET).define('X', SACRED_CRYSTAL)
                .unlockedBy("has_item", has(SACRED_CRYSTAL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, FORGER_CRYSTAL)
                .pattern(" Q ").pattern("QXQ").pattern("EPE")
                .define('E', Items.GOLD_INGOT).define('P', Items.TOTEM_OF_UNDYING)
                .define('X', FORGER_GEM).define('Q', SACRED_CRYSTAL)
                .unlockedBy("has_item", has(SACRED_CRYSTAL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, SCORCHED_CHARM)
                .pattern("N N").pattern("QXQ").pattern("EPE")
                .define('N', Items.NETHERITE_SCRAP)
                .define('E', Blocks.BASALT).define('P', PURE_HEART)
                .define('X', BLAZING_CORE).define('Q', SACRED_CRYSTAL)
                .unlockedBy("has_item", has(SACRED_CRYSTAL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, REDEMPTION_AMULET)
                .pattern("CDC").pattern("AXA").pattern("PNP")
                .define('D', Items.GLOWSTONE_DUST).define('A', Items.GOLDEN_APPLE)
                .define('C', SACRED_CRYSTAL).define('P', PURE_HEART)
                .define('X', EnigmaticTags.Items.ENIGMATIC_AMULETS).define('N', Items.NETHER_STAR)
                .unlockedBy("has_item", has(SACRED_CRYSTAL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EARTH_PROMISE)
                .pattern("PGP").pattern("QXQ").pattern("EAE")
                .define('A', MINING_CHARM).define('E', Items.ENCHANTED_GOLDEN_APPLE)
                .define('G', Items.GOLDEN_APPLE).define('P', PURE_HEART)
                .define('X', GOLDEN_RING).define('Q', SACRED_CRYSTAL)
                .unlockedBy("has_item", has(SACRED_CRYSTAL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, BLESS_STONE)
                .pattern("IAI").pattern("AXA").pattern("BPB")
                .define('A', SACRED_CRYSTAL)
                .define('P', PURE_HEART).define('I', ICHOR_DROPLET)
                .define('X', CURSED_STONE).define('B', Items.GLOWSTONE_DUST)
                .unlockedBy("has_item", has(CURSED_STONE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, THUNDER_SCROLL)
                .pattern("ISI").pattern("BXY").pattern("EPE")
                .define('B', Items.INK_SAC).define('Y', Items.FEATHER)
                .define('I', ICHOR_DROPLET).define('S', OCEAN_STONE)
                .define('E', Items.LAPIS_LAZULI).define('P', SACRED_CRYSTAL)
                .define('X', DARKEST_SCROLL)
                .unlockedBy("has_item", has(SACRED_CRYSTAL))
                .save(output);

        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, TOTEM_OF_MALICE)
                .pattern(" N ").pattern("EXE").pattern(" N ")
                .define('N', Items.NETHERITE_INGOT).define('X', Items.TOTEM_OF_UNDYING)
                .define('E', EVIL_ESSENCE)
                .unlockedBy("has_item", has(EVIL_ESSENCE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, DARKEST_SCROLL, 2)
                .pattern("BNB").pattern("WXW").pattern("BTB")
                .define('B', Items.BLACK_DYE).define('W', Items.WITHER_ROSE).define('N', Items.NETHERITE_SCRAP)
                .define('X', BLANK_SCROLL).define('T', DARKEST_SCROLL)
                .unlockedBy("has_item", has(DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, NIGHT_SCROLL)
                .pattern("MXM").pattern("BTY").pattern("MEM")
                .define('M', Items.PHANTOM_MEMBRANE).define('B', Items.WITHER_ROSE)
                .define('T', DARKEST_SCROLL).define('Y', Items.FEATHER)
                .define('E', Items.ENDER_EYE).define('X', TWISTED_HEART)
                .unlockedBy("has_item", has(DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, CURSED_SCROLL)
                .pattern("CXC").pattern("BTY").pattern("RER")
                .define('C', INFERNAL_CINDER)
                .define('B', Items.INK_SAC).define('Y', Items.FEATHER)
                .define('T', DARKEST_SCROLL).define('R', Items.REDSTONE)
                .define('E', Items.ENCHANTED_BOOK).define('X', TWISTED_HEART)
                .unlockedBy("has_item", has(DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, AVARICE_SCROLL)
                .pattern("GRG").pattern("BTY").pattern("GXG")
                .define('G', Items.GOLD_INGOT).define('B', Items.INK_SAC).define('Y', Items.FEATHER)
                .define('T', DARKEST_SCROLL).define('R', GOLDEN_RING)
                .define('X', TWISTED_HEART)
                .unlockedBy("has_item", has(DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, CURSED_XP_SCROLL)
                .pattern("GXG").pattern("BTB").pattern("GEG")
                .define('G', Items.EXPERIENCE_BOTTLE).define('B', EVIL_ESSENCE)
                .define('T', DARKEST_SCROLL).define('E', Blocks.EMERALD_BLOCK)
                .define('X', TWISTED_HEART)
                .unlockedBy("has_item", has(DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, THE_TWIST)
                .pattern("VRV").pattern("NXN").pattern("VTV")
                .define('R', Items.REDSTONE).define('N', Items.NETHERITE_INGOT)
                .define('V', EVIL_ESSENCE).define('T', TWISTED_HEART)
                .define('X', THE_ACKNOWLEDGMENT)
                .unlockedBy("has_item", has(EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, CURSE_TRANSPOSER)
                .pattern("VRV").pattern("PXP").pattern("VTV")
                .define('R', Items.GHAST_TEAR).define('P', Items.PHANTOM_MEMBRANE)
                .define('V', Items.REDSTONE).define('T', EVIL_ESSENCE)
                .define('X', ENCHANTMENT_TRANSPOSER)
                .unlockedBy("has_item", has(EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, ENCHANTER_PEARL)
                .pattern(" E ").pattern("VXV").pattern("PTP")
                .define('E', Items.EMERALD).define('X', Items.ENDER_PEARL)
                .define('T', Blocks.CRYING_OBSIDIAN).define('P', Items.BLAZE_POWDER)
                .define('V', EVIL_ESSENCE)
                .unlockedBy("has_item", has(EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, ENDER_SLAYER)
                .pattern("VOV").pattern("EOE").pattern("GSG")
                .define('E', Items.ENDER_EYE).define('O', Blocks.OBSIDIAN)
                .define('G', Items.GHAST_TEAR).define('S', Items.STICK)
                .define('V', EVIL_ESSENCE)
                .unlockedBy("has_item", has(EVIL_ESSENCE))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, THE_INFINITUM)
                .pattern("CPC").pattern("EXE").pattern("IAI")
                .define('C', COSMIC_HEART).define('P', ENCHANTER_PEARL)
                .define('E', EVIL_ESSENCE).define('X', THE_TWIST)
                .define('I', Items.NETHERITE_INGOT).define('A', ABYSSAL_HEART)
                .unlockedBy("has_item", has(ABYSSAL_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, ELDRITCH_AMULET)
                .pattern("EAE").pattern("IXI").pattern("TST")
                .define('T', TWISTED_HEART).define('S', Items.NETHER_STAR)
                .define('E', EVIL_ESSENCE).define('X', ASCENSION_AMULET)
                .define('I', Items.NETHERITE_INGOT).define('A', ABYSSAL_HEART)
                .unlockedBy("has_item", has(ABYSSAL_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, DESOLATION_RING)
                .pattern("CAC").pattern("IXI").pattern("EVE")
                .define('C', COSMIC_HEART).define('V', VOID_PEARL)
                .define('E', EVIL_ESSENCE).define('X', GOLDEN_RING)
                .define('I', Items.NETHERITE_INGOT).define('A', ABYSSAL_HEART)
                .unlockedBy("has_item", has(ABYSSAL_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, DIMNESS_CHARM)
                .pattern("NTN").pattern("IXI").pattern("EVE")
                .define('N', Items.NETHERITE_INGOT).define('T', TWISTED_HEART)
                .define('E', EVIL_ESSENCE).define('X', ABYSSAL_HEART)
                .define('I', Items.IRON_INGOT).define('V', ENIGMATIC_EYE)
                .unlockedBy("has_item", has(ABYSSAL_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, VIOLENCE_SCROLL)
                .pattern("CAC").pattern("EXE").pattern("TVT")
                .define('C', COSMIC_HEART).define('T', TWISTED_HEART)
                .define('E', EVIL_ESSENCE).define('V', CURSE_TRANSPOSER)
                .define('X', CURSED_SCROLL).define('A', ABYSSAL_HEART)
                .unlockedBy("has_item", has(ABYSSAL_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, CHAOS_ELYTRA)
                .pattern("CAC").pattern("IXI").pattern("EVE")
                .define('C', COSMIC_HEART).define('V', VOID_PEARL)
                .define('E', EVIL_ESSENCE).define('X', Items.ELYTRA)
                .define('I', EVIL_INGOT).define('A', ABYSSAL_HEART)
                .unlockedBy("has_item", has(ABYSSAL_HEART))
                .save(output);


        SpellstoneTableRecipe.Builder.spell(GOLEM_HEART, 6)
                .requires(Blocks.OBSIDIAN)
                .requires(Items.IRON_INGOT)
                .requires(Blocks.REDSTONE_BLOCK)
                .requires(IRON_RING)
                .requires(Blocks.REDSTONE_BLOCK)
                .requires(Items.IRON_INGOT)
                .requires(Blocks.OBSIDIAN)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(BLAZING_CORE, 6)
                .requires(Blocks.MAGMA_BLOCK)
                .requires(Items.BLAZE_POWDER)
                .requires(Blocks.BASALT)
                .requires(Items.MAGMA_CREAM)
                .requires(Blocks.BASALT)
                .requires(Items.BLAZE_POWDER)
                .requires(Blocks.MAGMA_BLOCK)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(FORGOTTEN_ICE, 6)
                .requires(Items.IRON_INGOT)
                .requires(Blocks.SNOW_BLOCK)
                .requires(Blocks.BLUE_ICE)
                .requires(Blocks.IRON_BLOCK)
                .requires(Blocks.BLUE_ICE)
                .requires(Blocks.SNOW_BLOCK)
                .requires(Items.IRON_INGOT)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(OCEAN_STONE, 6)
                .requires(Blocks.DARK_PRISMARINE)
                .requires(Items.PRISMARINE_CRYSTALS)
                .requires(Blocks.LAPIS_BLOCK)
                .requires(Items.HEART_OF_THE_SEA)
                .requires(Blocks.LAPIS_BLOCK)
                .requires(Items.PRISMARINE_CRYSTALS)
                .requires(Blocks.DARK_PRISMARINE)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(REVIVAL_LEAF, 6)
                .requires(Ingredient.of(ItemTags.LEAVES))
                .requires(Items.EMERALD)
                .requires(Ingredient.of(Tags.Items.SEEDS))
                .requires(INFINIMEAL)
                .requires(Ingredient.of(Tags.Items.SEEDS))
                .requires(Items.EMERALD)
                .requires(Ingredient.of(ItemTags.LEAVES))
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(LOST_ENGINE, 13)
                .requires(Blocks.COPPER_BLOCK)
                .requires(Blocks.REDSTONE_BLOCK)
                .requires(Blocks.PISTON)
                .requires(Blocks.LIGHTNING_ROD)
                .requires(Blocks.PISTON)
                .requires(Blocks.REDSTONE_BLOCK)
                .requires(Blocks.COPPER_BLOCK)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(ANGEL_BLESSING, 8)
                .requires(STARLIGHT_PARTICLE)
                .requires(Items.PHANTOM_MEMBRANE)
                .requires(Blocks.GLOWSTONE)
                .requires(QUARTZ_RING)
                .requires(Blocks.GLOWSTONE)
                .requires(Items.PHANTOM_MEMBRANE)
                .requires(STARLIGHT_PARTICLE)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(ILLUSION_LANTERN, 10)
                .requires(Blocks.SOUL_SOIL)
                .requires(Blocks.CYAN_STAINED_GLASS)
                .requires(Items.NETHERITE_SCRAP)
                .requires(Items.BLAZE_POWDER)
                .requires(Items.NETHERITE_SCRAP)
                .requires(Blocks.CYAN_STAINED_GLASS)
                .requires(Blocks.SOUL_SOIL)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(EYE_OF_NEBULA, 7)
                .requires(ENDER_ROD)
                .requires(Items.CHORUS_FRUIT)
                .requires(Blocks.END_STONE_BRICKS)
                .requires(Items.ENDER_EYE)
                .requires(Blocks.END_STONE_BRICKS)
                .requires(Items.CHORUS_FRUIT)
                .requires(ENDER_ROD)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(VOID_PEARL, 10)
                .requires(Blocks.GOLD_BLOCK)
                .requires(ETHERIUM_NUGGET)
                .requires(Items.ENDER_PEARL)
                .requires(VOID_STONE)
                .requires(Items.ENDER_PEARL)
                .requires(ETHERIUM_NUGGET)
                .requires(Blocks.GOLD_BLOCK)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(THE_CUBE, 24).allDifferent()
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .requires(COSMIC_HEART)
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .requires(EnigmaticTags.Items.THE_CUBE_MATERIAL)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);
        SpellstoneTableRecipe.Builder.spell(ETHERIUM_CORE, 9)
                .requires(ENDER_ROD)
                .requires(ETHERIUM_BLOCK)
                .requires(ENDER_ROD)
                .requires(EARTH_HEART)
                .requires(ENDER_ROD)
                .requires(ETHERIUM_BLOCK)
                .requires(ENDER_ROD)
                .unlockedBy("has_item", has(SPELLCORE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, COSMIC_SCROLL)
                .pattern("DAD").pattern("IXI").pattern("DCD")
                .define('D', ASTRAL_DUST).define('A', Items.ENCHANTED_GOLDEN_APPLE)
                .define('X', DARKEST_SCROLL).define('I', STARLIGHT_INGOT)
                .define('C', COSMIC_HEART)
                .unlockedBy("has_scroll", has(COSMIC_SCROLL))
                .save(output);

        ShapelessNoRemainRecipe.Builder.shapeless(RecipeCategory.MISC, FORBIDDEN_JUICE)
                .requires(FORBIDDEN_FRUIT).requires(Items.HONEY_BOTTLE)
                .unlockedBy("has_item", has(FORBIDDEN_FRUIT))
                .save(output);
    }
}