package auviotre.enigmatic.legacy.contents.item.etherium;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.EnumMap;
import java.util.List;

public class EtheriumProperties {
    public static final Tier TIER = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2794, 8.0F, 5F, 32, () -> Ingredient.of(EnigmaticItems.ETHERIUM_INGOT));

    public static final Holder<ArmorMaterial> MATERIAL = Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL, EnigmaticLegacy.location("etherium"),
            new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
                map.put(ArmorItem.Type.HELMET, 4);
                map.put(ArmorItem.Type.CHESTPLATE, 9);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.BOOTS, 4);
                map.put(ArmorItem.Type.BODY, 12);
            }), 24, EnigmaticSounds.ARMOR_EQUIP_ETHERIUM, () -> Ingredient.of(EnigmaticItems.ETHERIUM_INGOT),
                    List.of(new ArmorMaterial.Layer(EnigmaticLegacy.location("etherium"))), 4.0F, 0.0F)
    );
}
