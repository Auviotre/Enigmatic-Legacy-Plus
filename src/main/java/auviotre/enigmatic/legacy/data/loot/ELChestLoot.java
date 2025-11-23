package auviotre.enigmatic.legacy.data.loot;

import auviotre.enigmatic.legacy.contents.loot.conditions.SpellstoneLootCondition;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

import static auviotre.enigmatic.legacy.registries.EnigmaticLootTables.Chests;

public record ELChestLoot(HolderLookup.Provider registries) implements LootTableSubProvider {
    public ELChestLoot(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(Chests.OVERWORLD_SIMPLE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.EARTH_HEART).when(LootItemRandomChanceCondition.randomChance(0.3F)).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.EARTH_HEART_FRAGMENT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 3.0F))).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.IRON_RING).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.LORE_FRAGMENT).setWeight(5))
                        .add(LootItem.lootTableItem(EnigmaticItems.FORGER_GEM).when(LootItemRandomChanceCondition.randomChance(0.4F)).setWeight(6))
                        .add(LootItem.lootTableItem(EnigmaticItems.ICHOR_BOTTLE).when(LootItemRandomChanceCondition.randomChance(0.25F)).setWeight(5))
                        .add(LootItem.lootTableItem(EnigmaticItems.UNHOLY_GRAIL).when(LootItemRandomChanceCondition.randomChance(0.25F)).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(60))
                )
        );
        output.accept(Chests.NETHER_SIMPLE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.GOLDEN_RING).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.FORBIDDEN_FRUIT).when(LootItemRandomChanceCondition.randomChance(0.05F)).setWeight(5))
                        .add(LootItem.lootTableItem(Items.WITHER_ROSE).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 4.0F))).setWeight(25))
                        .add(LootItem.lootTableItem(Items.GHAST_TEAR).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.INFERNAL_CINDER).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).when(LootItemRandomChanceCondition.randomChance(0.25F)).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.ICHOR_DROPLET).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).when(LootItemRandomChanceCondition.randomChance(0.25F)).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.VOID_STONE).when(LootItemRandomChanceCondition.randomChance(0.25F)).setWeight(4))
                        .add(EmptyLootItem.emptyItem().setWeight(60))
                )
        );
        output.accept(Chests.END_SIMPLE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.MENDING_MIXTURE).setWeight(30))
                        .add(LootItem.lootTableItem(EnigmaticItems.RECALL_POTION).setWeight(15))
                        .add(LootItem.lootTableItem(EnigmaticItems.LORE_INSCRIBER).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.ETHERIUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.ETHERIUM_NUGGET).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))).setWeight(50))
                        .add(LootItem.lootTableItem(EnigmaticItems.ASTRAL_DUST).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))).setWeight(80))
                        .add(LootItem.lootTableItem(EnigmaticItems.ENDER_ROD).when(LootItemRandomChanceCondition.randomChance(0.4F)).setWeight(15))
                        .add(EmptyLootItem.emptyItem().setWeight(60))
                )
        );

        output.accept(Chests.DARKEST_SCROLL,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.DARKEST_SCROLL).when(LootItemRandomChanceCondition.randomChance(0.5F)))
                )
        );
        output.accept(Chests.GOLEM_HEART, spellstonePool(EnigmaticItems.GOLEM_HEART, 0.064F, 0.036F));
        output.accept(Chests.BLAZING_CORE, spellstonePool(EnigmaticItems.BLAZING_CORE, 0.046F, 0.018F));
        output.accept(Chests.OCEAN_STONE, spellstonePool(EnigmaticItems.OCEAN_STONE, 0.087F, 0.021F));
        output.accept(Chests.ANGEL_BLESSING, spellstonePool(EnigmaticItems.ANGEL_BLESSING, 0.105F, 0.025F));
        output.accept(Chests.EYE_OF_NEBULA, spellstonePool(EnigmaticItems.EYE_OF_NEBULA, 0.043F, 0.027F));
        output.accept(Chests.VOID_PEARL, spellstonePool(EnigmaticItems.VOID_PEARL, 0.018F, 0.01F));
        output.accept(Chests.FORGOTTEN_ICE, spellstonePool(EnigmaticItems.FORGOTTEN_ICE, 0.048F, 0.024F));
        output.accept(Chests.REVIVAL_LEAF, spellstonePool(EnigmaticItems.REVIVAL_LEAF, 0.048F, 0.024F));
        output.accept(Chests.LOST_ENGINE, spellstonePool(EnigmaticItems.LOST_ENGINE, 0.05F, 0.014F));

        output.accept(Chests.SPELLSTONE_HUT_TREASURE, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.BOOK).apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0F, 24.0F))))
                ).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).setBonusRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.REDSTONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 10.0F))))
                        .add(LootItem.lootTableItem(Items.LAPIS_LAZULI).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 10.0F))))
                        .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 10.0F))))
                        .add(LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 10.0F))))
                ).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.SPELLSTONE_DEBRIS).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                        .add(LootItem.lootTableItem(EnigmaticItems.EARTH_HEART).setWeight(20).when(LootItemRandomChanceCondition.randomChance(0.25F)))
                        .add(LootItem.lootTableItem(EnigmaticItems.EARTH_HEART_FRAGMENT).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))
                ).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.SPELLSTONE_DEBRIS).setWeight(80).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))).when(LootItemRandomChanceCondition.randomChance(0.75F)))
                        .add(LootItem.lootTableItem(EnigmaticItems.SPELLCORE).setWeight(20).when(LootItemRandomChanceCondition.randomChance(0.5F)))
                )
        );
    }

    private LootTable.Builder spellstonePool(ItemLike spellstone, float baseChance, float bonusChance) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(spellstone).setWeight(93).when(SpellstoneLootCondition.chance(baseChance, bonusChance)))
                        .add(LootItem.lootTableItem(EnigmaticItems.SPELLCORE).setWeight(7).when(SpellstoneLootCondition.chance(baseChance / 4, bonusChance / 2)))
                ).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.SPELLSTONE_DEBRIS).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).when(LootItemRandomChanceCondition.randomChance((baseChance + bonusChance) / 2)))
                );
    }
}
