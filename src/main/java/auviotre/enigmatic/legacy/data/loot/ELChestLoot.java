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

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(Chests.OVERWORLD_SIMPLE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.EARTH_HEART).when(LootItemRandomChanceCondition.randomChance(0.9F)).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.EARTH_HEART_FRAGMENT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 3.0F))).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.IRON_RING).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.LORE_FRAGMENT).setWeight(5))
                        .add(EmptyLootItem.emptyItem().setWeight(60))
                ).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.ICHOR_BOTTLE).when(LootItemRandomChanceCondition.randomChance(0.75F)).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.FORGER_GEM).when(LootItemRandomChanceCondition.randomChance(0.5F)).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.VOID_TOME).when(LootItemRandomChanceCondition.randomChance(0.36F)).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.UNHOLY_GRAIL).when(LootItemRandomChanceCondition.randomChance(0.4F)).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.ANTIQUE_BAG).when(LootItemRandomChanceCondition.randomChance(0.32F)).setWeight(5))
                        .add(EmptyLootItem.emptyItem().setWeight(120))
                )
        );
        output.accept(Chests.NETHER_SIMPLE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.GOLDEN_RING).setWeight(20))
                        .add(LootItem.lootTableItem(Items.WITHER_ROSE).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 4.0F))).setWeight(15))
                        .add(LootItem.lootTableItem(Items.GHAST_TEAR).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).setWeight(15))
                        .add(LootItem.lootTableItem(EnigmaticItems.INFERNAL_CINDER).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).when(LootItemRandomChanceCondition.randomChance(0.5F)).setWeight(15))
                        .add(LootItem.lootTableItem(EnigmaticItems.ICHOR_DROPLET).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).when(LootItemRandomChanceCondition.randomChance(0.5F)).setWeight(15))
                        .add(EmptyLootItem.emptyItem().setWeight(40))
                ).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.ICHOR_CURSE_BOTTLE).when(LootItemRandomChanceCondition.randomChance(0.8F)).setWeight(25))
                        .add(LootItem.lootTableItem(EnigmaticItems.FORBIDDEN_FRUIT).when(LootItemRandomChanceCondition.randomChance(0.7F)).setWeight(15))
                        .add(LootItem.lootTableItem(EnigmaticItems.VOID_STONE).when(LootItemRandomChanceCondition.randomChance(0.5F)).setWeight(10))
                        .add(EmptyLootItem.emptyItem().setWeight(125))
                )
        );
        output.accept(Chests.END_SIMPLE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.MENDING_MIXTURE).setWeight(15))
                        .add(LootItem.lootTableItem(EnigmaticItems.RECALL_POTION).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.ETHERIUM_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).setWeight(15))
                        .add(LootItem.lootTableItem(EnigmaticItems.ETHERIUM_NUGGET).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))).setWeight(25))
                        .add(LootItem.lootTableItem(EnigmaticItems.ASTRAL_DUST).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.ENDER_ROD).when(LootItemRandomChanceCondition.randomChance(0.4F)).setWeight(15))
                        .add(EmptyLootItem.emptyItem().setWeight(50))
                ).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.ASTRAL_FRUIT).when(LootItemRandomChanceCondition.randomChance(0.75F)).setWeight(20))
                        .add(LootItem.lootTableItem(EnigmaticItems.ANTIQUE_BAG).when(LootItemRandomChanceCondition.randomChance(0.5F)).setWeight(10))
                        .add(LootItem.lootTableItem(EnigmaticItems.LORE_INSCRIBER).setWeight(15))
                        .add(EmptyLootItem.emptyItem().setWeight(120))
                )
        );

        output.accept(Chests.DARKEST_SCROLL,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.DARKEST_SCROLL).when(LootItemRandomChanceCondition.randomChance(0.8F)))
                )
        );
        output.accept(Chests.GOLEM_HEART, spellstonePool(EnigmaticItems.GOLEM_HEART, 0.067F, 0.028F));          //  9.5%
        output.accept(Chests.BLAZING_CORE, spellstonePool(EnigmaticItems.BLAZING_CORE, 0.045F, 0.019F));        //  6.4%
        output.accept(Chests.OCEAN_STONE, spellstonePool(EnigmaticItems.OCEAN_STONE, 0.077F, 0.027F));          // 10.4%
        output.accept(Chests.ANGEL_BLESSING, spellstonePool(EnigmaticItems.ANGEL_BLESSING, 0.132F, 0.044F));    // 17.6%
        output.accept(Chests.EYE_OF_NEBULA, spellstonePool(EnigmaticItems.EYE_OF_NEBULA, 0.046F, 0.027F));      //  7.3%
        output.accept(Chests.VOID_PEARL, spellstonePool(EnigmaticItems.VOID_PEARL, 0.039F, 0.022F));            //  6.1%
        output.accept(Chests.FORGOTTEN_ICE, spellstonePool(EnigmaticItems.FORGOTTEN_ICE, 0.164F, 0.067F));      // 23.1%
        output.accept(Chests.REVIVAL_LEAF, spellstonePool(EnigmaticItems.REVIVAL_LEAF, 0.084F, 0.033F));        // 11.7%
        output.accept(Chests.LOST_ENGINE, spellstonePool(EnigmaticItems.LOST_ENGINE, 0.057F, 0.021F));          //  7.8%

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
                        .add(LootItem.lootTableItem(EnigmaticItems.SPELLSTONE_DEBRIS).setWeight(80).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 5.0F))).when(LootItemRandomChanceCondition.randomChance(0.75F)))
                        .add(LootItem.lootTableItem(EnigmaticItems.SPELLCORE).setWeight(20).when(LootItemRandomChanceCondition.randomChance(0.5F)))
                )
        );
    }

    private LootTable.Builder spellstonePool(ItemLike spellstone, float baseChance, float bonusChance) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(spellstone).setWeight(90).when(SpellstoneLootCondition.chance(baseChance, bonusChance)))
                        .add(LootItem.lootTableItem(EnigmaticItems.SPELLCORE).setWeight(10).when(SpellstoneLootCondition.chance(baseChance / 4, bonusChance / 2)))
                ).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(EnigmaticItems.SPELLSTONE_DEBRIS).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 3.0F))).when(LootItemRandomChanceCondition.randomChance((baseChance + bonusChance) / 2)))
                );
    }
}
