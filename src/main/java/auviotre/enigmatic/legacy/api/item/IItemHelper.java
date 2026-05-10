package auviotre.enigmatic.legacy.api.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface IItemHelper {
    List<ItemLike> UNKNOWN_ITEMS = new ArrayList<>();

    static Item.Properties properties() {
        return new Item.Properties();
    }

    static Item.Properties properties(int size) {
        return new Item.Properties().stacksTo(size);
    }

    static Item.Properties singleProperties() {
        return new Item.Properties().stacksTo(1);
    }

    static @NotNull ItemAttributeModifiers createAttributes(float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    static ResourceLocation getLocation(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }
}
