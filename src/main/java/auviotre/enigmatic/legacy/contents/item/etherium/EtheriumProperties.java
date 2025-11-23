package auviotre.enigmatic.legacy.contents.item.etherium;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attributes.EtheriumShieldAttribute;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;

public interface EtheriumProperties {
    Tier TIER = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2794, 8.0F, 5F, 24, () -> Ingredient.of(EnigmaticItems.ETHERIUM_INGOT));

    Holder<ArmorMaterial> MATERIAL = Registry.registerForHolder(
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

    static ItemAttributeModifiers createAttributes(float attackDamage, float attackSpeed, float threshold) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage + TIER.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(EnigmaticAttributes.ETHERIUM_SHIELD, new AttributeModifier(EtheriumShieldAttribute.BASE_ID, threshold, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    static double getShieldThreshold(@NotNull LivingEntity entity) {
        AttributeInstance attribute = entity.getAttribute(EnigmaticAttributes.ETHERIUM_SHIELD);
        if (attribute == null) return 0;
        return attribute.getValue();
    }

    static boolean hasShield(@NotNull LivingEntity entity) {
        return entity.getMaxHealth() * getShieldThreshold(entity) > entity.getHealth();
    }
}
