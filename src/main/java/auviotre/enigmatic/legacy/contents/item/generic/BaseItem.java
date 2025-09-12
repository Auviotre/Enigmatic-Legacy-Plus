package auviotre.enigmatic.legacy.contents.item.generic;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

public class BaseItem extends Item {
    public BaseItem() {
        super(defaultProperties());
    }

    public BaseItem(Properties properties) {
        super(properties);
    }

    public static Properties defaultProperties() {
        return new Item.Properties();
    }

    public static Properties defaultProperties(int size) {
        return new Item.Properties().stacksTo(size);
    }

    public static Properties defaultSingleProperties() {
        return new Item.Properties().stacksTo(1);
    }

    public static @NotNull ItemAttributeModifiers createAttributes(float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }

    public static ResourceLocation getLocation(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
