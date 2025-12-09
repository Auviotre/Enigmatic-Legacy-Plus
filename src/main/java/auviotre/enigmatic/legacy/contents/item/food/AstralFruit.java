package auviotre.enigmatic.legacy.contents.item.food;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class AstralFruit extends BaseItem {
    public static final FoodProperties DEFAULT_PROPERTIES = new FoodProperties.Builder()
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 400, 2), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3200, 2), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3200, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 4800), 1.0F)
            .nutrition(6).saturationModifier(1.3F).alwaysEdible().build();
    public static final FoodProperties ENCHANTED_PROPERTIES = new FoodProperties.Builder()
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 1200, 2), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3200, 3), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3200, 2), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 4800), 1.0F)
            .nutrition(6).saturationModifier(1.3F).alwaysEdible().build();
    private final boolean enchanted;

    public AstralFruit(boolean enchanted) {
        super(defaultProperties().rarity(enchanted ? Rarity.EPIC : Rarity.RARE).fireResistant().component(EnigmaticComponents.CURSED, enchanted)
                .food(enchanted ? ENCHANTED_PROPERTIES : DEFAULT_PROPERTIES));
        this.enchanted = enchanted;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (this.enchanted) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.astralFruit");
            TooltipHandler.line(list);
            TooltipHandler.cursedOnly(list, stack);
        }
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && this.enchanted) {
            EnigmaticHandler.unlockSpecialSlot("ring", player, getLocation(this));
        }
        return super.finishUsingItem(stack, level, entity);
    }

    public boolean isFoil(ItemStack stack) {
        return this.enchanted;
    }
}
