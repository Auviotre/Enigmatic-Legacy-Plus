package auviotre.enigmatic.legacy.data;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
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
                    EnigmaticItems.UNWITNESSED_AMULET,
                    Component.translatable("advancement.enigmaticlegacy.root.title"),
                    Component.translatable("advancement.enigmaticlegacy.root.desc"),
                    ResourceLocation.withDefaultNamespace("textures/block/end_stone_bricks.png"),
                    AdvancementType.TASK, false, false, false
            ).requirements(AdvancementRequirements.Strategy.OR).addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(EnigmaticItems.UNWITNESSED_AMULET)).save(consumer, prefix("root"));
            AdvancementHolder recallPotion = Advancement.Builder.advancement().parent(root).display(
                    EnigmaticItems.RECALL_POTION,
                    Component.translatable("advancement.enigmaticlegacy.recall_potion.title"),
                    Component.translatable("advancement.enigmaticlegacy.recall_potion.desc"),
                    null, AdvancementType.TASK, true, true, false
            ).requirements(AdvancementRequirements.Strategy.OR).addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.RECALL_POTION).build())).save(consumer, prefix("recall_potion"));
            AdvancementHolder discoverSpellstone = Advancement.Builder.advancement().parent(root).display(
                    EnigmaticItems.GOLEM_HEART,
                    Component.translatable("advancement.enigmaticlegacy.discover_spellstone.title"),
                    Component.translatable("advancement.enigmaticlegacy.discover_spellstone.desc"),
                    null, AdvancementType.TASK, true, true, false
            ).requirements(AdvancementRequirements.Strategy.OR).addCriterion("has_spellstone", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticTags.Items.SPELLSTONES).build())).save(consumer, prefix("discover_spellstone"));
            AdvancementHolder allSpellstone = Advancement.Builder.advancement().parent(discoverSpellstone).display(
                            EnigmaticItems.VOID_PEARL,
                            Component.translatable("advancement.enigmaticlegacy.all_spellstone.title"),
                            Component.translatable("advancement.enigmaticlegacy.all_spellstone.desc"),
                            null, AdvancementType.TASK, true, true, false
                    ).requirements(AdvancementRequirements.Strategy.AND)
                    .addCriterion("has_spellstone1", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.GOLEM_HEART).build()))
                    .addCriterion("has_spellstone2", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.BLAZING_CORE).build()))
                    .addCriterion("has_spellstone3", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.OCEAN_STONE).build()))
                    .addCriterion("has_spellstone4", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.ANGEL_BLESSING).build()))
                    .addCriterion("has_spellstone5", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.EYE_OF_NEBULA).build()))
                    .addCriterion("has_spellstone6", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.VOID_PEARL).build()))
                    .save(consumer, prefix("all_spellstone"));
            AdvancementHolder discoverScroll = Advancement.Builder.advancement().parent(root).display(
                    EnigmaticItems.BLANK_SCROLL,
                    Component.translatable("advancement.enigmaticlegacy.discover_scroll.title"),
                    Component.translatable("advancement.enigmaticlegacy.discover_scroll.desc"),
                    null, AdvancementType.TASK, true, true, false
            ).requirements(AdvancementRequirements.Strategy.OR).addCriterion("has_scroll", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticTags.Items.SCROLLS).build())).save(consumer, prefix("discover_scroll"));
            AdvancementHolder cursedRing = Advancement.Builder.advancement().parent(root).display(
                    EnigmaticItems.CURSED_RING,
                    Component.translatable("advancement.enigmaticlegacy.cursed_ring.title"),
                    Component.translatable("advancement.enigmaticlegacy.cursed_ring.desc"),
                    null, AdvancementType.TASK, true, true, false
            ).requirements(AdvancementRequirements.Strategy.OR).addCriterion("equipped_cursed_ring", CuriosTriggers.equip().withItem(ItemPredicate.Builder.item().of(EnigmaticItems.CURSED_RING)).withLocation(LocationPredicate.Builder.location()).withSlot(SlotPredicate.Builder.slot().of("ring")).build()).save(consumer, prefix("cursed_ring"));
            ItemStack stack = EnigmaticItems.TWISTED_HEART.toStack();
            stack.set(EnigmaticComponents.TAINTABLE, true);
            AdvancementHolder twistedHeart = Advancement.Builder.advancement().parent(cursedRing).display(
                    stack,
                    Component.translatable("advancement.enigmaticlegacy.twisted_heart.title"),
                    Component.translatable("advancement.enigmaticlegacy.twisted_heart.desc"),
                    null, AdvancementType.TASK, true, true, false
            ).requirements(AdvancementRequirements.Strategy.OR).addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.TWISTED_HEART).build())).save(consumer, prefix("twisted_heart"));
            AdvancementHolder twistedMirror = Advancement.Builder.advancement().parent(twistedHeart).display(
                    EnigmaticItems.TWISTED_MIRROR,
                    Component.translatable("advancement.enigmaticlegacy.twisted_mirror.title"),
                    Component.translatable("advancement.enigmaticlegacy.twisted_mirror.desc"),
                    null, AdvancementType.TASK, true, true, false
            ).requirements(AdvancementRequirements.Strategy.OR).addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EnigmaticItems.TWISTED_MIRROR).build())).save(consumer, prefix("twisted_mirror"));
        }
    }
}
