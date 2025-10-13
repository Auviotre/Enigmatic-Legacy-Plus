package auviotre.enigmatic.legacy.compat.farmersdelight.contents.item;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vectorwing.farmersdelight.common.item.ConsumableItem;
import vectorwing.farmersdelight.common.registry.ModEffects;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.List;

public class DivineFruitPie extends ConsumableItem {
    public static final FoodProperties.Builder DIVINE_FRUIT_PIE = new FoodProperties.Builder().alwaysEdible().nutrition(10).saturationModifier(1.6F)
            .effect(() -> new MobEffectInstance(ModEffects.COMFORT, 600, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2400, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, -1, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, -1, 4), 1.0F);

    public DivineFruitPie() {
        super(ModItems.bowlFoodItem(DIVINE_FRUIT_PIE.build()).fireResistant().rarity(Rarity.EPIC).component(EnigmaticComponents.CURSED, true), true);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.divineFruitPie");
            TooltipHandler.line(list);
            super.appendHoverText(stack, context, list, flag);
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand handIn) {
        if (EnigmaticHandler.isTheOne(player)) return super.use(level, player, handIn);
        player.kill();
        return InteractionResultHolder.consume(player.getItemInHand(handIn));
    }

    public void affectConsumer(ItemStack stack, Level level, LivingEntity consumer) {
        if (consumer instanceof ServerPlayer player) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 2.0F);
        }
    }

    public static class PieBlock extends BlockItem {
        public PieBlock(Block block) {
            super(block, BaseItem.defaultSingleProperties().rarity(Rarity.EPIC).component(EnigmaticComponents.CURSED, true));
        }

        public boolean isFoil(ItemStack stack) {
            return true;
        }

        @OnlyIn(Dist.CLIENT)
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
            if (Screen.hasShiftDown()) {
                super.appendHoverText(stack, context, list, flag);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.divineFruitPieBlock");
            } else TooltipHandler.holdShift(list);
            TooltipHandler.line(list);
            TooltipHandler.cursedOnly(list, stack);
        }
    }
}
