package auviotre.enigmatic.legacy.contents.item.food;

import auviotre.enigmatic.legacy.contents.effect.BlazingMight;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Exterminato extends BaseItem {
    public static final FoodProperties PROPERTIES = new FoodProperties.Builder().nutrition(7).saturationModifier(0.7F).alwaysEdible().build();

    public Exterminato() {
        super(defaultSingleProperties().food(PROPERTIES));
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide()) BlazingMight.addAmplifier(entity, entity.getRandom().nextInt(3), 600);
        return super.finishUsingItem(stack, level, entity);
    }
}
