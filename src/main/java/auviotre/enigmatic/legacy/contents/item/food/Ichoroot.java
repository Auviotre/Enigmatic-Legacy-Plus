package auviotre.enigmatic.legacy.contents.item.food;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class Ichoroot extends BaseItem {
    public static final FoodProperties PROPERTIES = new FoodProperties.Builder()
            .effect(() -> new MobEffectInstance(EnigmaticEffects.PURE_RESISTANCE, 360), 0.25F)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 360), 0.5F)
            .nutrition(3).saturationModifier(0.9F).alwaysEdible().build();

    public Ichoroot() {
        super(defaultSingleProperties().food(PROPERTIES));
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        List<MobEffectInstance> list = entity.getActiveEffects().stream().filter(effect -> effect.getEffect().value().getCategory().equals(MobEffectCategory.HARMFUL)).toList();
        if (!list.isEmpty()) entity.removeEffect(list.get(entity.getRandom().nextInt(list.size())).getEffect());
        return super.finishUsingItem(stack, level, entity);
    }
}