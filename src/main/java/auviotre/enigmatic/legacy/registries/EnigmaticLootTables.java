package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public interface EnigmaticLootTables {
    private static ResourceKey<LootTable> key(String prefix, String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, EnigmaticLegacy.location(name + "_addon").withPrefix(prefix));
    }

    interface Chests {
        ResourceKey<LootTable> OVERWORLD_SIMPLE = key("overworld_simple");
        ResourceKey<LootTable> NETHER_SIMPLE = key("nether_simple");
        ResourceKey<LootTable> END_SIMPLE = key("end_simple");

        ResourceKey<LootTable> DARKEST_SCROLL = key("darkest_scroll");
        ResourceKey<LootTable> GOLEM_HEART = key("golem_heart");
        ResourceKey<LootTable> BLAZING_CORE = key("blazing_core");
        ResourceKey<LootTable> OCEAN_STONE = key("ocean_stone");
        ResourceKey<LootTable> ANGEL_BLESSING = key("angel_blessing");
        ResourceKey<LootTable> EYE_OF_NEBULA = key("eye_of_nebula");
        ResourceKey<LootTable> VOID_PEARL = key("void_pearl");
        ResourceKey<LootTable> FORGOTTEN_ICE = key("forgotten_ice");
        ResourceKey<LootTable> REVIVAL_LEAF = key("revival_leaf");
        ResourceKey<LootTable> LOST_ENGINE = key("lost_engine");

        private static ResourceKey<LootTable> key(String name) {
            return EnigmaticLootTables.key("chests/", name);
        }
    }

    interface Entities {
        ResourceKey<LootTable> BLAZE = key("blaze");
        ResourceKey<LootTable> CREEPER = key("creeper");
        ResourceKey<LootTable> DROWNED = key("drowned");
        ResourceKey<LootTable> ELDER_GUARDIAN = key("elder_guardian");
        ResourceKey<LootTable> ENDERMAN = key("enderman");
        ResourceKey<LootTable> EVOKER = key("evoker");
        ResourceKey<LootTable> GHAST = key("ghast");
        ResourceKey<LootTable> GUARDIAN = key("guardian");
        ResourceKey<LootTable> ILLAGER = key("illager");
        ResourceKey<LootTable> MAGMA_CUBE = key("magma_cube");
        ResourceKey<LootTable> PIGLIN = key("piglin");
        ResourceKey<LootTable> PIGLIN_BRUTE = key("piglin_brute");
        ResourceKey<LootTable> RAVAGER = key("ravager");
        ResourceKey<LootTable> SHULKER = key("shulker");
        ResourceKey<LootTable> SKELETON = key("skeleton");
        ResourceKey<LootTable> SPIDER = key("spider");
        ResourceKey<LootTable> VEX = key("vex");
        ResourceKey<LootTable> VILLAGER = key("villager");
        ResourceKey<LootTable> WITCH = key("witch");
        ResourceKey<LootTable> WITHER = key("wither");
        ResourceKey<LootTable> WITHER_SKELETON = key("wither_skeleton");
        ResourceKey<LootTable> ZOMBIE = key("zombie");
        ResourceKey<LootTable> ZOMBIFIED_PIGLIN = key("zombified_piglin");

        private static ResourceKey<LootTable> key(String name) {
            return EnigmaticLootTables.key("entities/", name);
        }
    }
}
