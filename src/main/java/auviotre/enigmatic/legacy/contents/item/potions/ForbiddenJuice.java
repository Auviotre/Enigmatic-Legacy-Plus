package auviotre.enigmatic.legacy.contents.item.potions;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.compat.CompatHandler;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.BaseDrinkableItem;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ForbiddenJuice extends BaseDrinkableItem {
    public static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .effect(() -> new MobEffectInstance(MobEffects.WITHER, 400, 3), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 400, 2), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 480, 3), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 640, 2), 1.0F)
            .alwaysEdible().build();

    public ForbiddenJuice() {
        super(IItemHelper.singleProperties().food(FOOD_PROPERTIES).craftRemainder(Items.GLASS_BOTTLE).rarity(Rarity.RARE).fireResistant());
    }

    public static boolean isForbiddenCursed(LivingEntity entity) {
        return entity != null && entity.getData(EnigmaticAttachments.ENIGMATIC_DATA).isThirstForbiddenCursed();
    }

    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return CompatHandler.isLoaded("thirst");
    }

    public boolean canDrink(Level level, Player player, ItemStack stack) {
        return !isForbiddenCursed(player);
    }

    public void onConsumed(Level level, @NotNull Player player, ItemStack stack) {
        player.eat(level, stack);
        EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        data.setThirstForbiddenCursed(true);
    }
}
