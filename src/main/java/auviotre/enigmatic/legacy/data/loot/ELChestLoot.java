package auviotre.enigmatic.legacy.data.loot;

import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
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
                        .add(LootItem.lootTableItem(EnigmaticItems.EARTH_HEART).when(LootItemRandomChanceCondition.randomChance(0.25F)).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.EARTH_HEART_FRAGMENT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.IRON_RING).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.LORE_FRAGMENT).setWeight(5))
                        .add(LootItem.lootTableItem(EnigmaticItems.ICHOR_BOTTLE).when(LootItemRandomChanceCondition.randomChance(0.25F)).setWeight(5))
                        .add(EmptyLootItem.emptyItem().setWeight(60))
                )
        );
        output.accept(Chests.END_SIMPLE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.MENDING_MIXTURE).setWeight(30))
                        .add(LootItem.lootTableItem(EnigmaticItems.RECALL_POTION).setWeight(15))
                        .add(LootItem.lootTableItem(EnigmaticItems.LORE_INSCRIBER).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.ETHERIUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.ASTRAL_DUST).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))).setWeight(80))
                        .add(LootItem.lootTableItem(EnigmaticItems.ASTRAL_FRUIT).when(LootItemRandomChanceCondition.randomChance(0.5F)).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.ENDER_ROD).when(LootItemRandomChanceCondition.randomChance(0.4F)).setWeight(15))
                        .add(EmptyLootItem.emptyItem().setWeight(60))
                )
        );

        output.accept(Chests.DARKEST_SCROLL,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.DARKEST_SCROLL).when(LootItemRandomChanceCondition.randomChance(0.5F)))
                )
        );
        output.accept(Chests.GOLEM_HEART,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.GOLEM_HEART).when(LootItemRandomChanceCondition.randomChance(0.064F)))
                )
        );
        output.accept(Chests.BLAZING_CORE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.BLAZING_CORE).when(LootItemRandomChanceCondition.randomChance(0.046F)))
                )
        );
        output.accept(Chests.OCEAN_STONE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.OCEAN_STONE).when(LootItemRandomChanceCondition.randomChance(0.087F)))
                )
        );
        output.accept(Chests.ANGEL_BLESSING,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.OCEAN_STONE).when(LootItemRandomChanceCondition.randomChance(0.135F)))
                )
        );
        output.accept(Chests.VOID_PEARL,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.VOID_PEARL).when(LootItemRandomChanceCondition.randomChance(0.018F)))
                )
        );
        output.accept(Chests.EYE_OF_NEBULA,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.EYE_OF_NEBULA).when(LootItemRandomChanceCondition.randomChance(0.053F)))
                )
        );
        output.accept(Chests.LOST_ENGINE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.LOST_ENGINE).when(LootItemRandomChanceCondition.randomChance(0.06F)))
                )
        );
    }
}
