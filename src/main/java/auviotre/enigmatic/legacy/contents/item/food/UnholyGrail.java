package auviotre.enigmatic.legacy.contents.item.food;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class UnholyGrail extends BaseItem {
    public UnholyGrail() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON).fireResistant());
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.unholyGrail");
        } else TooltipHandler.holdShift(list);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!level.isClientSide()) {
            if (EnigmaticHandler.isTheCursedOne(user) && ForbiddenFruit.isForbiddenCursed(user)) {
                user.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 2, false, true));
                user.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 800, 1, false, true));
                user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1, false, true));
                user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000, 1, false, true));
                user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0, false, true));
            } else {
                user.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 2, false, true));
                user.addEffect(new MobEffectInstance(MobEffects.POISON, 160, 1, false, true));
                user.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 240, 0, false, true));
                user.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1, false, true));
                user.addEffect(new MobEffectInstance(MobEffects.HUNGER, 160, 2, false, true));
                user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 240, 0, false, true));
            }
        }
        if (user instanceof Player player) player.awardStat(Stats.ITEM_USED.get(this));
        return stack;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }
}
