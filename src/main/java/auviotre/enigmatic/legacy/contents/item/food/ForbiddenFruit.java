package auviotre.enigmatic.legacy.contents.item.food;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ForbiddenFruit extends BaseItem {
    public static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .effect(() -> new MobEffectInstance(MobEffects.WITHER, 400, 3), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 400, 2), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 480, 3), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 640, 2), 1.0F)
            .nutrition(2).saturationModifier(0.5F).alwaysEdible().build();

    public ForbiddenFruit() {
        super(defaultSingleProperties().food(FOOD_PROPERTIES).rarity(Rarity.RARE).fireResistant());
    }

    public static boolean isForbiddenCursed(LivingEntity entity) {
        return entity != null && entity.getData(EnigmaticAttachments.ENIGMATIC_DATA).isForbiddenCursed();
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forbiddenFruit1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forbiddenFruit2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forbiddenFruit3", ChatFormatting.GOLD, "80%");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forbiddenFruitLore");
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (isForbiddenCursed(player)) return InteractionResultHolder.pass(player.getItemInHand(hand));
        return super.use(level, player, hand);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        EnigmaticData data = entity.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        data.setForbiddenCursed(true);
        return super.finishUsingItem(stack, level, entity);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onHeal(@NotNull LivingHealEvent event) {
            if (isForbiddenCursed(event.getEntity())) {
                event.setAmount(event.getAmount() * 0.2F);
            }
        }

        @SubscribeEvent
        private static void onEffectApply(MobEffectEvent.@NotNull Applicable event) {
            if (isForbiddenCursed(event.getEntity())) {
                MobEffectInstance effect = event.getEffectInstance();
                if (effect != null && effect.is(MobEffects.HUNGER)) {
                    event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                }
            }
        }

        @SubscribeEvent
        private static void onPlayerTick(PlayerTickEvent.@NotNull Pre event) {
            lockFoodData(event.getEntity());
        }

        @SubscribeEvent
        private static void onPlayerTicked(PlayerTickEvent.@NotNull Post event) {
            lockFoodData(event.getEntity());
        }

        private static void lockFoodData(Player player) {
            if (isForbiddenCursed(player)) {
                FoodData foodStats = player.getFoodData();
                foodStats.setFoodLevel(20);
                foodStats.saturationLevel = 0F;
                if (player.hasEffect(MobEffects.HUNGER)) player.removeEffect(MobEffects.HUNGER);
            }
        }
    }
}
