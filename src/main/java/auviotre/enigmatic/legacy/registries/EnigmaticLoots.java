package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.loot.conditions.IsMonsterCondition;
import auviotre.enigmatic.legacy.contents.loot.conditions.SpellstoneLootCondition;
import auviotre.enigmatic.legacy.contents.loot.modifiers.AddCurseLootModifier;
import auviotre.enigmatic.legacy.contents.loot.modifiers.SpecialLootModifier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EnigmaticLoots {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, EnigmaticLegacy.MODID);
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> IS_MONSTER = LOOT_CONDITIONS.register("is_monster", () -> new LootItemConditionType(IsMonsterCondition.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> SPELLSTONE = LOOT_CONDITIONS.register("spellstone", () -> new LootItemConditionType(SpellstoneLootCondition.CODEC));

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EnigmaticLegacy.MODID);
    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_LOOT_TABLE = LOOT_MODIFIERS.register("add_curse_loot_table", AddCurseLootModifier.CODEC);
    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> SPECIAL_LOOT = LOOT_MODIFIERS.register("special_loot", SpecialLootModifier.CODEC);
}