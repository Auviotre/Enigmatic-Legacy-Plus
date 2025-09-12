package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import top.theillusivec4.curios.api.CuriosApi;

public interface EnigmaticTags {
    interface Items {
        TagKey<Item> SCROLLS = register(CuriosApi.MODID, "scroll");
        TagKey<Item> SPELLSTONES = register(CuriosApi.MODID, "spellstone");
        TagKey<Item> ARMOR_CHECK_EXCLUSION = register("armor_check_exclusions");
        TagKey<Item> BYPASS_FOURTH_CURSE = register("bypass_fourth_curse");
        TagKey<Item> ETERNAL_BINDING_ENCHANTABLE = register("eternal_binding_enchantable");

        private static TagKey<Item> register(String name) {
            return register(EnigmaticLegacy.MODID, name);
        }

        private static TagKey<Item> register(String modid, String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(modid, name));
        }
    }

    interface Effects {
        TagKey<MobEffect> ALWAYS_APPLY = register("always_apply");

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
        TagKey<EntityType<?>> NEUTRAL_ANGER_BLACKLIST = register("neutral_anger_blacklist");
        TagKey<EntityType<?>> EXTRA_GOLEM = register("extra_golem");
        TagKey<EntityType<?>> END_DWELLERS = register("end_dwellers");

        private static TagKey<EntityType<?>> register(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, EnigmaticLegacy.location(name));
        }
    }
}
