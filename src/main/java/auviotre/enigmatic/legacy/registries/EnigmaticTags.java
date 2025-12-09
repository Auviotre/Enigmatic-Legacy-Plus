package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import top.theillusivec4.curios.api.CuriosApi;

public interface EnigmaticTags {
    interface Items {
        TagKey<Item> SCROLLS = register(CuriosApi.MODID, "scroll");
        TagKey<Item> SPELLSTONES = register(CuriosApi.MODID, "spellstone");
        TagKey<Item> AMULETS = register(CuriosApi.MODID, "amulets");
        TagKey<Item> THE_CUBE_MATERIAL = register("materials_of_the_cube");
        TagKey<Item> ARMOR_CHECK_EXCLUSION = register("armor_check_exclusions");
        TagKey<Item> BYPASS_FOURTH_CURSE = register("bypass_fourth_curse");
        TagKey<Item> ETERNAL_BINDING_ENCHANTABLE = register("enchantable/eternal_binding");
        TagKey<Item> ETHERIC_RESONANCE_ENCHANTABLE = register("enchantable/etheric_resonance");

        private static TagKey<Item> register(String name) {
            return register(EnigmaticLegacy.MODID, name);
        }

        private static TagKey<Item> register(String modid, String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(modid, name));
        }
    }

    interface Blocks {
        TagKey<Block> ALL_MINEABLE = register("all_mineable");

        private static TagKey<Block> register(String name) {
            return TagKey.create(Registries.BLOCK, EnigmaticLegacy.location(name));
        }
    }


    interface Effects {
        TagKey<MobEffect> ALWAYS_APPLY = register("always_apply");
        TagKey<MobEffect> SHOULD_NOT_RANDOM_OUT = register("should_not_random_out");

        private static TagKey<MobEffect> register(String name) {
            return TagKey.create(Registries.MOB_EFFECT, EnigmaticLegacy.location(name));
        }
    }

    interface Enchantments {
        TagKey<Enchantment> BINDING_CURSE_EXCLUSIVE = register("binding_curse_exclusive");

        private static TagKey<Enchantment> register(String name) {
            return TagKey.create(Registries.ENCHANTMENT, EnigmaticLegacy.location(name));
        }
    }

    interface EntityTypes {
        TagKey<EntityType<?>> EXTRA_GOLEM = register("extra_golem");
        TagKey<EntityType<?>> END_DWELLERS = register("end_dwellers");
        TagKey<EntityType<?>> GUARDIAN_HEART_EXCLUDED = register("guardian_heart_excluded");

        private static TagKey<EntityType<?>> register(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, EnigmaticLegacy.location(name));
        }
    }

    interface DamageTypes {
        TagKey<DamageType> GOLEM_HEART_IMMUNE_TO = registerSpellstone("golem_heart/immune_to");
        TagKey<DamageType> GOLEM_HEART_IS_MELEE = registerSpellstone("golem_heart/is_melee");
        TagKey<DamageType> ANGEL_BLESSING_IMMUNE_TO = registerSpellstone("angel_blessing/immune_to");
        TagKey<DamageType> ANGEL_BLESSING_VULNERABLE_TO = registerSpellstone("angel_blessing/vulnerable_to");
        TagKey<DamageType> LOST_ENGINE_IMMUNE_TO = registerSpellstone("lost_engine/immune_to");
        TagKey<DamageType> FORGOTTEN_ICE_RESISTANT_TO = registerSpellstone("forgotten_ice/resistant_to");
        TagKey<DamageType> THE_CUBE_IMMUNE_TO = registerSpellstone("the_cube/immune_to");
        TagKey<DamageType> ETHERIUM_CORE_IMMUNE_TO = registerSpellstone("etherium_core/immune_to");

        private static TagKey<DamageType> registerSpellstone(String name) {
            return TagKey.create(Registries.DAMAGE_TYPE, EnigmaticLegacy.location("spellstone/" + name));
        }
    }

    interface Biomes {
        TagKey<Biome> HAS_SPELLSTONE_HUT = register("has_structure/spellstone_hut");

        private static TagKey<Biome> register(String name) {
            return TagKey.create(Registries.BIOME, EnigmaticLegacy.location(name));
        }
    }
}
