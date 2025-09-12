package auviotre.enigmatic.legacy.contents.item.potions;

import auviotre.enigmatic.legacy.contents.item.generic.BaseDrinkableItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class IchorBottle extends BaseDrinkableItem {
    public static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1200, 4), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 2), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 2), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3200), 1.0F)
            .alwaysEdible().build();
    public IchorBottle() {
        super(defaultSingleProperties().craftRemainder(Items.GLASS_BOTTLE).food(FOOD_PROPERTIES).rarity(Rarity.RARE));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.ichorBottle1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.ichorBottle2");
    }

    public void onConsumed(Level level, Player player, ItemStack stack) {
        EnigmaticHandler.unlockSpecialSlot("charm", player, getLocation(this));
        player.eat(level, stack);
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
