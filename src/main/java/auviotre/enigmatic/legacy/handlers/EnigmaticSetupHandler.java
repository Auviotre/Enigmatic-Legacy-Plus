package auviotre.enigmatic.legacy.handlers;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.capability.AntiqueBagCapability;
import auviotre.enigmatic.legacy.contents.command.CurseTimeCommand;
import auviotre.enigmatic.legacy.registries.EnigmaticCapability;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticPotions;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Mod(value = EnigmaticLegacy.MODID)
@EventBusSubscriber(modid = EnigmaticLegacy.MODID)
public class EnigmaticSetupHandler {
    @SubscribeEvent
    private static void onRegisterCap(@NotNull RegisterCapabilitiesEvent event) {
        event.registerEntity(EnigmaticCapability.ANTIQUE_BAG_INVENTORY, EntityType.PLAYER, (entity, context) -> new AntiqueBagCapability(entity));
    }

    @SubscribeEvent
    private static void onWandererTradesEvent(@NotNull WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();
        rareTrades.add((trader, rand) -> new MerchantOffer(new ItemCost(EnigmaticItems.EARTH_HEART_FRAGMENT, 2), Optional.of(new ItemCost(Items.EMERALD, 20)), EnigmaticItems.EARTH_HEART.toStack(), 1, 5, 0.2F));
        genericTrades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), EnigmaticItems.EARTH_HEART_FRAGMENT.toStack(), 2, 5, 0.25F));
    }

    @SubscribeEvent
    private static void onRecipeCategoriesRegistry(@NotNull RegisterRecipeBookCategoriesEvent event) {
        // To Be Done.
    }

    @SubscribeEvent
    private static void onCommandRegistry(@NotNull RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CurseTimeCommand.register(dispatcher);
    }

    @SubscribeEvent
    private static void onPotionRegistry(@NotNull RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();
        ItemStack awkward = Items.POTION.getDefaultInstance();
        awkward.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
        builder.addRecipe(Ingredient.of(awkward), Ingredient.of(Items.ENDER_EYE), EnigmaticItems.RECALL_POTION.toStack());
        builder.addRecipe(Ingredient.of(EnigmaticItems.RECALL_POTION), Ingredient.of(Items.FERMENTED_SPIDER_EYE), EnigmaticItems.WORMHOLE_POTION.toStack());
        builder.addStartMix(Items.QUARTZ, EnigmaticPotions.HASTE);
        builder.addMix(Potions.FIRE_RESISTANCE, Items.BLAZE_ROD, EnigmaticPotions.MOLTEN_HEART);
        builder.addMix(Potions.LONG_FIRE_RESISTANCE, Items.BLAZE_ROD, EnigmaticPotions.LONG_MOLTEN_HEART);
        builder.addMix(EnigmaticPotions.HASTE, Items.REDSTONE, EnigmaticPotions.LONG_HASTE);
        builder.addMix(EnigmaticPotions.HASTE, Items.GLOWSTONE_DUST, EnigmaticPotions.STRONG_HASTE);
        builder.addMix(Potions.AWKWARD, EnigmaticItems.EARTH_HEART_FRAGMENT.get(), Potions.LUCK);
        builder.addRecipe(Ingredient.of(EnigmaticItems.RECALL_POTION), Ingredient.of(EnigmaticItems.TWISTED_HEART), EnigmaticItems.TWISTED_POTION.toStack());
        builder.addRecipe(Ingredient.of(Items.OMINOUS_BOTTLE), Ingredient.of(EnigmaticItems.ICHOR_DROPLET), EnigmaticItems.ICHOR_CURSE_BOTTLE.toStack());
        // Ultimate Potions
        builder.addMix(Potions.LONG_NIGHT_VISION, EnigmaticItems.ICHOR_DROPLET.get(), EnigmaticPotions.ULTIMATE_NIGHT_VISION);
        builder.addMix(Potions.LONG_INVISIBILITY, EnigmaticItems.ICHOR_DROPLET.get(), EnigmaticPotions.ULTIMATE_INVISIBILITY);
        builder.addMix(Potions.LONG_LEAPING, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_LEAPING);
        builder.addMix(Potions.STRONG_LEAPING, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_LEAPING);
        builder.addMix(Potions.LONG_FIRE_RESISTANCE, EnigmaticItems.ICHOR_DROPLET.get(), EnigmaticPotions.ULTIMATE_FIRE_RESISTANCE);
        builder.addMix(Potions.LONG_SWIFTNESS, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_SWIFTNESS);
        builder.addMix(Potions.STRONG_SWIFTNESS, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_SWIFTNESS);
        builder.addMix(Potions.LONG_SLOWNESS, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_SLOWNESS);
        builder.addMix(Potions.STRONG_SLOWNESS, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_SLOWNESS);
        builder.addMix(Potions.STRONG_TURTLE_MASTER, EnigmaticItems.ICHOR_DROPLET.get(), EnigmaticPotions.ULTIMATE_TURTLE_MASTER);
        builder.addMix(Potions.LONG_WATER_BREATHING, EnigmaticItems.ICHOR_DROPLET.get(), EnigmaticPotions.ULTIMATE_WATER_BREATHING);
        builder.addMix(Potions.STRONG_HEALING, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_HEALING);
        builder.addMix(Potions.STRONG_HARMING, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_HARMING);
        builder.addMix(Potions.LONG_POISON, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_POISON);
        builder.addMix(Potions.STRONG_POISON, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_POISON);
        builder.addMix(Potions.LONG_REGENERATION, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_REGENERATION);
        builder.addMix(Potions.STRONG_REGENERATION, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_REGENERATION);
        builder.addMix(Potions.LONG_STRENGTH, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_STRENGTH);
        builder.addMix(Potions.STRONG_STRENGTH, EnigmaticItems.ASTRAL_DUST.get(), EnigmaticPotions.ULTIMATE_STRENGTH);
        builder.addMix(Potions.LONG_WEAKNESS, EnigmaticItems.ICHOR_DROPLET.get(), EnigmaticPotions.ULTIMATE_WEAKNESS);
        builder.addMix(Potions.LUCK, EnigmaticItems.ICHOR_DROPLET.get(), EnigmaticPotions.ULTIMATE_LUCK);
        builder.addMix(Potions.LONG_SLOW_FALLING, EnigmaticItems.ICHOR_DROPLET.get(), EnigmaticPotions.ULTIMATE_SLOW_FALLING);

        builder.addMix(EnigmaticPotions.ULTIMATE_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, EnigmaticPotions.ULTIMATE_INVISIBILITY);
        builder.addMix(EnigmaticPotions.ULTIMATE_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, EnigmaticPotions.ULTIMATE_SLOWNESS);
        builder.addMix(EnigmaticPotions.ULTIMATE_HEALING, Items.FERMENTED_SPIDER_EYE, EnigmaticPotions.ULTIMATE_HARMING);
        builder.addMix(EnigmaticPotions.ULTIMATE_POISON, Items.FERMENTED_SPIDER_EYE, EnigmaticPotions.ULTIMATE_HARMING);
    }
}
