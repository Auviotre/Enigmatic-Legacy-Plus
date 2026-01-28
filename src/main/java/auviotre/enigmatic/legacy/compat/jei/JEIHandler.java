package auviotre.enigmatic.legacy.compat.jei;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.client.screen.SpellstoneTableScreen;
import auviotre.enigmatic.legacy.compat.jei.category.SpellstoneCraftingCategory;
import auviotre.enigmatic.legacy.compat.jei.category.TaintingCategory;
import auviotre.enigmatic.legacy.compat.jei.extension.CursedRecipeExtension;
import auviotre.enigmatic.legacy.compat.jei.subtype.AmuletSubtypeInterpreter;
import auviotre.enigmatic.legacy.compat.jei.subtype.TaintableSubtypeInterpreter;
import auviotre.enigmatic.legacy.contents.crafting.CursedShapedRecipe;
import auviotre.enigmatic.legacy.contents.crafting.SpellstoneTableRecipe;
import auviotre.enigmatic.legacy.contents.gui.SpellstoneTableMenu;
import auviotre.enigmatic.legacy.contents.item.tools.TotemOfMalice;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticMenus;
import auviotre.enigmatic.legacy.registries.EnigmaticRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIHandler implements IModPlugin {
    private static final ResourceLocation ID = EnigmaticLegacy.location("jei_plugin");

    public static Level getLevel() {
        if (Minecraft.getInstance().level != null) return Minecraft.getInstance().level;
        else throw new NullPointerException("minecraft level must not be null.");
    }

    public ResourceLocation getPluginUid() {
        return ID;
    }

    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(EnigmaticItems.EARTH_HEART.get(), TaintableSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(EnigmaticItems.TWISTED_HEART.get(), TaintableSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(EnigmaticItems.PURE_HEART.get(), TaintableSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(EnigmaticItems.ABYSSAL_HEART.get(), TaintableSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(EnigmaticItems.ENIGMATIC_AMULET.get(), AmuletSubtypeInterpreter.INSTANCE);
    }

    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(EnigmaticBlocks.SPELLSTONE_TABLE.toStack(), EnigmaticRecipeTypes.SPELLSTONE_CRAFTING);
    }

    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(SpellstoneTableScreen.class, 85, 53, 6, 6, EnigmaticRecipeTypes.SPELLSTONE_CRAFTING);
    }

    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(SpellstoneTableMenu.class, EnigmaticMenus.SPELLSTONE_TABLE_MENU.get(), EnigmaticRecipeTypes.SPELLSTONE_CRAFTING, 0, 10, 11, 36);
    }

    public void registerVanillaCategoryExtensions(@NotNull IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(CursedShapedRecipe.class, new CursedRecipeExtension());
    }

    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new TaintingCategory(guiHelper));
        registration.addRecipeCategories(new SpellstoneCraftingCategory(guiHelper));
    }

    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
        RecipeManager manager = getLevel().getRecipeManager();
        List<RecipeHolder<SpellstoneTableRecipe>> holders = manager.getAllRecipesFor(EnigmaticRecipes.SPELLSTONE_CRAFTING.get());
        registration.addRecipes(EnigmaticRecipeTypes.SPELLSTONE_CRAFTING, holders);

        Ingredient cursed_ring = Ingredient.of(EnigmaticItems.CURSED_RING);
        Ingredient two_ring = Ingredient.of(EnigmaticItems.CURSED_RING, EnigmaticItems.REDEMPTION_RING);
        registration.addRecipes(EnigmaticRecipeTypes.TAINTING, List.of(
                TaintingCategory.create(EnigmaticItems.EARTH_HEART.toStack(), two_ring),
                TaintingCategory.create(EnigmaticItems.TWISTED_HEART.toStack(), cursed_ring),
                TaintingCategory.create(EnigmaticItems.PURE_HEART.toStack(), two_ring),
                TaintingCategory.create(EnigmaticItems.ABYSSAL_HEART.toStack(), cursed_ring)
        ));
        List<IJeiAnvilRecipe> recipes = new ArrayList<>();
        addRepairData(recipes, factory, EnigmaticItems.EXECUTION_AXE.toStack(), Ingredient.of(Items.NETHERITE_INGOT));
        addRepairData(recipes, factory, EnigmaticItems.ETHERIUM_SWORD.toStack(), Ingredient.of(EnigmaticItems.ETHERIUM_INGOT));
        addRepairData(recipes, factory, EnigmaticItems.ETHERIUM_HAMMER.toStack(), Ingredient.of(EnigmaticItems.ETHERIUM_INGOT));
        addRepairData(recipes, factory, EnigmaticItems.ETHERIUM_SCYTHE.toStack(), Ingredient.of(EnigmaticItems.ETHERIUM_INGOT));
        addRepairData(recipes, factory, EnigmaticItems.ETHERIUM_HELMET.toStack(), Ingredient.of(EnigmaticItems.ETHERIUM_INGOT));
        addRepairData(recipes, factory, EnigmaticItems.ETHERIUM_CHESTPLATE.toStack(), Ingredient.of(EnigmaticItems.ETHERIUM_INGOT));
        addRepairData(recipes, factory, EnigmaticItems.ETHERIUM_LEGGINGS.toStack(), Ingredient.of(EnigmaticItems.ETHERIUM_INGOT));
        addRepairData(recipes, factory, EnigmaticItems.ETHERIUM_BOOTS.toStack(), Ingredient.of(EnigmaticItems.ETHERIUM_INGOT));
        addRepairData(recipes, factory, EnigmaticItems.MAJESTIC_ELYTRA.toStack(), Ingredient.of(EnigmaticItems.ETHERIUM_INGOT));
        addRepairData(recipes, factory, EnigmaticItems.INFERNAL_SHIELD.toStack(), Ingredient.of(Blocks.OBSIDIAN.asItem()));
        addRepairData(recipes, factory, EnigmaticItems.ENDER_SLAYER.toStack(), Ingredient.of(Blocks.OBSIDIAN.asItem()));
        addCustomData(recipes, factory);
        registration.addRecipes(RecipeTypes.ANVIL, recipes);

        List<IJeiBrewingRecipe> brewingRecipes = new ArrayList<>();
        brewingRecipes.add(factory.createBrewingRecipe(
                List.of(Items.FERMENTED_SPIDER_EYE.getDefaultInstance()),
                EnigmaticItems.RECALL_POTION.toStack(),
                EnigmaticItems.WORMHOLE_POTION.toStack(),
                EnigmaticLegacy.location("recall_potion.to.wormhole_potion")
        ));
        brewingRecipes.add(factory.createBrewingRecipe(
                List.of(EnigmaticItems.ICHOR_DROPLET.toStack()),
                Items.OMINOUS_BOTTLE.getDefaultInstance(),
                EnigmaticItems.ICHOR_CURSE_BOTTLE.toStack(),
                EnigmaticLegacy.location("ominous_bottle.to.ichor_curse_bottle")
        ));
        registration.addRecipes(RecipeTypes.BREWING, brewingRecipes);
    }

    private void addRepairData(List<IJeiAnvilRecipe> list, IVanillaRecipeFactory factory, ItemStack stack, Ingredient repairIngredient) {
        String name = "";
        ItemStack damagedThreeQuarters = stack.copy();
        damagedThreeQuarters.setDamageValue(damagedThreeQuarters.getMaxDamage() * 3 / 4);
        ItemStack damagedHalf = stack.copy();
        damagedHalf.setDamageValue(damagedHalf.getMaxDamage() / 2);
        var damagedThreeQuartersSingletonList = List.of(damagedThreeQuarters);
        IJeiAnvilRecipe repairWithSame = factory.createAnvilRecipe(
                damagedThreeQuartersSingletonList,
                damagedThreeQuartersSingletonList,
                List.of(damagedHalf),
                EnigmaticLegacy.location("self_repair." + name)
        );
        list.add(repairWithSame);

        if (!repairIngredient.isEmpty()) {
            ItemStack damagedFully = stack.copy();
            damagedFully.setDamageValue(damagedFully.getMaxDamage());
            IJeiAnvilRecipe repairWithMaterial = factory.createAnvilRecipe(
                    List.of(damagedFully),
                    List.of(repairIngredient.getItems()),
                    damagedThreeQuartersSingletonList,
                    EnigmaticLegacy.location("materials_repair." + name)
            );
            list.add(repairWithMaterial);
        }
    }

    private void addCustomData(List<IJeiAnvilRecipe> list, IVanillaRecipeFactory factory) {
        ItemStack stack = EnigmaticItems.TOTEM_OF_MALICE.toStack();
        TotemOfMalice.setDurability(stack, TotemOfMalice.getMaxDurability(stack));
        list.add(factory.createAnvilRecipe(
                EnigmaticItems.TOTEM_OF_MALICE.toStack(),
                List.of(EnigmaticItems.EVIL_ESSENCE.toStack()),
                List.of(stack),
                EnigmaticLegacy.location("materials_repair.")
        ));
    }
}
