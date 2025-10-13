package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.loot.conditions.IsMonsterCondition;
import auviotre.enigmatic.legacy.contents.loot.conditions.SpellstoneLootCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticLootConditions {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, EnigmaticLegacy.MODID);
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> IS_MONSTER = LOOT_CONDITIONS.register("is_monster", () -> new LootItemConditionType(IsMonsterCondition.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> SPELLSTONE = LOOT_CONDITIONS.register("spellstone", () -> new LootItemConditionType(SpellstoneLootCondition.CODEC));
}