package auviotre.enigmatic.legacy.data;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.trigger.UnholyGrailTrigger;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosTriggers;
import top.theillusivec4.curios.api.SlotPredicate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ELAdvancementProvider extends AdvancementProvider {
    public ELAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new Generator()));
    }

    public static class Generator implements AdvancementProvider.AdvancementGenerator {
        private static String prefix(String s) {
            return EnigmaticLegacy.MODID + ":main/" + s;
        }

        public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
            AdvancementHolder root = Advancement.Builder.advancement().display(
                            EnigmaticItems.THE_ACKNOWLEDGMENT,
                            Component.translatable("advancement.enigmaticlegacy.root.title"),
                            Component.translatable("advancement.enigmaticlegacy.root.desc"),
                            ResourceLocation.withDefaultNamespace("textures/block/end_stone_bricks.png"),
                            AdvancementType.TASK, false, false, false
                    ).requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("has_book", InventoryChangeTrigger.TriggerInstance.hasItems(EnigmaticItems.THE_ACKNOWLEDGMENT))
                    .addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(EnigmaticItems.UNWITNESSED_AMULET))
                    .save(consumer, "enigmaticlegacyplus:main/root");
            AdvancementHolder recallPotion = advancement(
                    root, consumer, "recall_potion", EnigmaticItems.RECALL_POTION.toStack(),
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.RECALL_POTION).build()))
            );
            AdvancementHolder discoverSpellstone = advancement(
                    root, consumer, "discover_spellstone", EnigmaticItems.GOLEM_HEART.toStack(),
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_spellstone", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticTags.Items.SPELLSTONES).build()))
            );
            AdvancementHolder spellstoneTable = advancement(
                    discoverSpellstone, consumer, "spellstone_table", EnigmaticBlocks.SPELLSTONE_TABLE.toStack(),
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticBlocks.SPELLSTONE_TABLE).build()))
            );
            advancement(
                    spellstoneTable, consumer, "etherium_core", EnigmaticItems.ETHERIUM_CORE.toStack(),
                    AdvancementType.TASK, true, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.ETHERIUM_CORE).build()))
            );
            AdvancementHolder allSpellstone = advancement(
                    discoverSpellstone, consumer, "all_spellstone", EnigmaticItems.SPELLCORE.toStack(),
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.AND,
                    Pair.of("has_spellstone1", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.GOLEM_HEART).build())),
                    Pair.of("has_spellstone2", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.BLAZING_CORE).build())),
                    Pair.of("has_spellstone3", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.OCEAN_STONE).build())),
                    Pair.of("has_spellstone4", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.ANGEL_BLESSING).build())),
                    Pair.of("has_spellstone5", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.EYE_OF_NEBULA).build())),
                    Pair.of("has_spellstone6", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.VOID_PEARL).build())),
                    Pair.of("has_spellstone7", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.FORGOTTEN_ICE).build())),
                    Pair.of("has_spellstone8", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.LOST_ENGINE).build())),
                    Pair.of("has_spellstone9", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.REVIVAL_LEAF).build()))
            );
            advancement(
                    allSpellstone, consumer, "the_cube", EnigmaticItems.THE_CUBE.toStack(),
                    AdvancementType.CHALLENGE, true, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.THE_CUBE).build()))
            );
            AdvancementHolder discoverScroll = advancement(
                    root, consumer, "discover_scroll", EnigmaticItems.BLANK_SCROLL.toStack(),
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_scroll", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticTags.Items.SCROLLS).build()))
            );
            AdvancementHolder darkestScroll = advancement(
                    discoverScroll, consumer, "darkest_scroll", EnigmaticItems.DARKEST_SCROLL.toStack(),
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.DARKEST_SCROLL).build()))
            );
            AdvancementHolder heavenScroll = advancement(
                    discoverScroll, consumer, "heaven_scroll", EnigmaticItems.HEAVEN_SCROLL.toStack(),
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.HEAVEN_SCROLL).build()))
            );
            advancement(
                    darkestScroll, consumer, "cursed_scroll", EnigmaticItems.CURSED_SCROLL.toStack(),
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.CURSED_SCROLL).build()))
            );
            advancement(
                    heavenScroll, consumer, "fabulous_scroll", EnigmaticItems.FABULOUS_SCROLL.toStack(),
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.FABULOUS_SCROLL).build()))
            );
            AdvancementHolder cursedRing = advancement(
                    root, consumer, "cursed_ring", EnigmaticItems.CURSED_RING.toStack(),
                    AdvancementType.GOAL, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("equipped_cursed_ring", CuriosTriggers.equip().withItem(ItemPredicate.Builder.item().of(EnigmaticItems.CURSED_RING)).withLocation(LocationPredicate.Builder.location()).withSlot(SlotPredicate.Builder.slot().of("ring")).build())
            );
            ItemStack stack = EnigmaticItems.TWISTED_HEART.toStack();
            stack.set(EnigmaticComponents.TAINTABLE, true);
            AdvancementHolder twistedHeart = advancement(
                    cursedRing, consumer, "twisted_heart", stack,
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.TWISTED_HEART).build()))
            );
            AdvancementHolder twistedMirror = advancement(
                    twistedHeart, consumer, "twisted_mirror", EnigmaticItems.TWISTED_MIRROR.toStack(),
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.TWISTED_MIRROR).build()))
            );
            AdvancementHolder theTwist = advancement(
                    twistedHeart, consumer, "the_twist", EnigmaticItems.THE_TWIST.toStack(),
                    AdvancementType.GOAL, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.THE_TWIST).build()))
            );
            stack = EnigmaticItems.ABYSSAL_HEART.toStack();
            stack.set(EnigmaticComponents.TAINTABLE, true);
            stack.set(EnigmaticComponents.ELDRITCH_TIMER, 1.0F);
            AdvancementHolder abyssalHeart = advancement(
                    theTwist, consumer, "abyssal_heart", stack,
                    AdvancementType.GOAL, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.ABYSSAL_HEART).build()))
            );
            stack = EnigmaticItems.THE_INFINITUM.toStack();
            stack.set(EnigmaticComponents.ELDRITCH_TIMER, 1.0F);
            advancement(
                    abyssalHeart, consumer, "the_infinitum", stack,
                    AdvancementType.GOAL, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.THE_INFINITUM).build()))
            );
            stack = EnigmaticItems.DESOLATION_RING.toStack();
            stack.set(EnigmaticComponents.ELDRITCH_TIMER, 1.0F);
            advancement(
                    abyssalHeart, consumer, "desolation_ring", stack,
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.DESOLATION_RING).build()))
            );
            stack = EnigmaticItems.PURE_HEART.toStack();
            stack.set(EnigmaticComponents.TAINTABLE, true);
            AdvancementHolder pureHeart = advancement(
                    cursedRing, consumer, "pure_heart", stack,
                    AdvancementType.TASK, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.PURE_HEART).build()))
            );
            AdvancementHolder theBless = advancement(
                    pureHeart, consumer, "the_bless", EnigmaticItems.THE_BLESS.toStack(),
                    AdvancementType.GOAL, false, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.THE_BLESS).build()))
            );
            advancement(
                    theBless, consumer, "redemption_ring", EnigmaticItems.REDEMPTION_RING.toStack(),
                    AdvancementType.CHALLENGE, true, AdvancementRequirements.Strategy.OR,
                    Pair.of("equipped_redemption_ring", CuriosTriggers.equip().withItem(ItemPredicate.Builder.item().of(EnigmaticItems.REDEMPTION_RING)).withLocation(LocationPredicate.Builder.location()).withSlot(SlotPredicate.Builder.slot().of("ring")).build())
            );
            AdvancementHolder unholyGrail = advancement(
                    root, consumer, "unholy_grail", EnigmaticItems.UNHOLY_GRAIL.toStack(),
                    AdvancementType.TASK, true, AdvancementRequirements.Strategy.OR,
                    Pair.of("use_item", UnholyGrailTrigger.TriggerInstance.use(false))
            );
            advancement(
                    unholyGrail, consumer, "unholy_grail_worthy", EnigmaticItems.UNHOLY_GRAIL.toStack(),
                    AdvancementType.GOAL, true, AdvancementRequirements.Strategy.OR,
                    Pair.of("use_item", UnholyGrailTrigger.TriggerInstance.use(true))
            );
            advancement(
                    unholyGrail, consumer, "lore_inscriber", EnigmaticItems.LORE_INSCRIBER.toStack(),
                    AdvancementType.TASK, true, AdvancementRequirements.Strategy.OR,
                    Pair.of("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.LORE_INSCRIBER).build()))
            );
        }

        @SafeVarargs
        private AdvancementHolder advancement(
                AdvancementHolder parent, Consumer<AdvancementHolder> consumer,
                String name, ItemStack item, AdvancementType type, boolean hidden,
                AdvancementRequirements.Strategy strategy, Pair<String, Criterion<?>>... pairs
        ) {
            Advancement.Builder builder = Advancement.Builder.advancement().parent(parent).display(item,
                    Component.translatable("advancement.enigmaticlegacy." + name + ".title"),
                    Component.translatable("advancement.enigmaticlegacy." + name + ".desc"),
                    null, type, true, true, hidden
            ).requirements(strategy);
            Arrays.stream(pairs).forEach(pair -> builder.addCriterion(pair.getFirst(), pair.getSecond()));
            return builder.save(consumer, "enigmaticlegacyplus:main/" + name);
        }
    }
}
