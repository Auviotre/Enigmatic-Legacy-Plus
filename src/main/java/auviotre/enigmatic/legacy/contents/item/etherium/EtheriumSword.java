package auviotre.enigmatic.legacy.contents.item.etherium;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticEnchantments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.AttributeUtil;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EtheriumSword extends SwordItem {
    public EtheriumSword() {
        super(EtheriumProperties.TIER, new Item.Properties().fireResistant().attributes(EtheriumProperties.createAttributes(6.0F, -2.6F, 0.04F)
                .withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(AttributeUtil.BASE_ENTITY_REACH_ID, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
        ));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        double power = 10;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            power += EtheriumProperties.getShieldThreshold(player) * 50;
            if (player.isUsingItem() && player.getUseItem().is(EnigmaticItems.ETHERIUM_SWORD))
                power *= 1.5;
        }
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumSword", ChatFormatting.GOLD, String.format("%.1f%%", power));
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        var holder = EnigmaticHandler.get(level, Registries.ENCHANTMENT, EnigmaticEnchantments.ETHERIC_RESONANCE);
        if (stack.getEnchantmentLevel(holder) > 0) {
            if (hand == InteractionHand.MAIN_HAND) {
                ItemStack offhandStack = player.getOffhandItem();
                if (!offhandStack.isEmpty() && offhandStack.getItem().getUseAnimation(offhandStack) == UseAnim.BLOCK)
                    return InteractionResultHolder.pass(stack);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return super.use(level, player, hand);
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32000;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttacked(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            double threshold = EtheriumProperties.getShieldThreshold(entity);
            if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
            if (entity.isUsingItem() && entity.getUseItem().is(EnigmaticItems.ETHERIUM_SWORD)) {
                threshold *= 1.5;
                entity.getUseItem().hurtAndBreak(1, entity, entity.getUsedItemHand().equals(InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                event.setAmount(event.getAmount() * 0.6F);
            }
            if (entity.getMainHandItem().is(EnigmaticItems.ETHERIUM_SWORD) && entity.getRandom().nextFloat() < 0.01 + threshold / 2) {
                if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
                    Vec3 vec = entity.position().subtract(attacker.position()).normalize().scale(0.25F);
                    attacker.knockback(0.4F, vec.x, vec.z);
                    entity.level().playSound(null, entity.blockPosition(), EnigmaticSounds.ETHERIUM_SHIELD_DEFLECT.get(), SoundSource.PLAYERS, 0.6F, 0.9F + entity.getRandom().nextFloat() * 0.1F);
                }
                entity.invulnerableTime += 20;
                event.setCanceled(true);
            }
        }
    }
}
