package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.loot.modifiers.AddCurseLootModifier;
import auviotre.enigmatic.legacy.contents.loot.modifiers.SpecialLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EnigmaticLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EnigmaticLegacy.MODID);
    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_LOOT_TABLE = LOOT_MODIFIERS.register("add_curse_loot_table", AddCurseLootModifier.CODEC);
    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> SPECIAL_LOOT = LOOT_MODIFIERS.register("special_loot", SpecialLootModifier.CODEC);
}