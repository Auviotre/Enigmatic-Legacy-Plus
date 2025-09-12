package auviotre.enigmatic.legacy.data;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.crafting.CursedShapedRecipe;
import auviotre.enigmatic.legacy.contents.crafting.ShapelessNoRemainRecipe;
import auviotre.enigmatic.legacy.data.helpers.RecipeProviderWithHelper;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
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
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EnigmaticBlocks.DIMENSIONAL_ANCHOR)
                .pattern(" E ").pattern("IRI").pattern("SXS")
                .define('E', Items.ENDER_EYE).define('S', Blocks.END_STONE).define('X', EnigmaticItems.EYE_OF_NEBULA)
                .define('R', Blocks.RESPAWN_ANCHOR).define('I', EnigmaticItems.ETHERIUM_INGOT)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_INGOT))
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
        CursedShapedRecipe.Builder.shaped(RecipeCategory.TOOLS, EnigmaticItems.TWISTED_MIRROR)
                .pattern("IGI").pattern("PXP").pattern(" I ")
                .define('I', Items.IRON_INGOT).define('G', Items.GLASS_PANE)
                .define('P', EnigmaticItems.RECALL_POTION).define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.TWISTED_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.COMBAT, EnigmaticItems.INFERNAL_SHIELD)
                .pattern("PNP").pattern("RUR").pattern("OXO")
                .define('P', Items.BLAZE_POWDER).define('R', Items.BLAZE_ROD)
                .define('N', Items.NETHERITE_INGOT).define('O', Blocks.OBSIDIAN)
                .define('U', Items.SHIELD).define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.TWISTED_HEART))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.BERSERK_EMBLEM)
                .pattern("CSC").pattern("PXP").pattern("GTG")
                .define('C', Items.CRIMSON_FUNGUS).define('S', Items.GOLDEN_SWORD)
                .define('G', Items.GHAST_TEAR).define('P', Items.BLAZE_POWDER)
                .define('T', Items.NETHERITE_INGOT).define('X', EnigmaticItems.TWISTED_HEART)
                .unlockedBy("has_item", has(EnigmaticItems.TWISTED_HEART))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnigmaticItems.DARKEST_SCROLL, 3)
                .pattern("BTB").pattern("WXW").pattern("BTB")
                .define('B', Items.BLACK_DYE).define('W', Items.WITHER_ROSE)
                .define('X', EnigmaticItems.BLANK_SCROLL).define('T', EnigmaticItems.DARKEST_SCROLL)
                .unlockedBy("has_item", has(EnigmaticItems.DARKEST_SCROLL))
                .save(output);
        CursedShapedRecipe.Builder.shaped(RecipeCategory.MISC, EnigmaticItems.CURSED_SCROLL)
                .pattern("MXM").pattern("BTY").pattern("RER")
                .define('M', Items.PHANTOM_MEMBRANE).define('B', Items.INK_SAC).define('Y', Items.FEATHER)
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
    }
}