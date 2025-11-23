package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TheTwist extends TheAcknowledgment {
    public static ModConfigSpec.IntValue specialDamageBoost;
    public static ModConfigSpec.IntValue knockbackModifier;

    public TheTwist() {
        super(defaultSingleProperties().rarity(Rarity.EPIC).component(EnigmaticComponents.CURSED, true), 8, -1.8F);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.the_twist").push("cursedItems.theTwist");
        specialDamageBoost = builder.defineInRange("specialDamageBoost", 300, 0, 1000);
        knockbackModifier = builder.defineInRange("knockbackModifier", 300, 0, 1000);
        builder.pop(2);
    }

    public static Multimap<Holder<Attribute>, AttributeModifier> getKnockbackModifier() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        double modifier = 0.01 * knockbackModifier.getAsInt();
        builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(getLocation(EnigmaticItems.THE_TWIST.get()), modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return builder.build();
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.fourthCurse"));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.fourthCurseAlteration");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theTwist1", ChatFormatting.GOLD, specialDamageBoost.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theTwist2", ChatFormatting.GOLD, knockbackModifier.get() + "%");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theTwistLore1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theTwistLore2");
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!EnigmaticHandler.canUse(player, stack)) return InteractionResultHolder.pass(stack);
        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack offhandStack = player.getOffhandItem();
            if (!offhandStack.isEmpty() && (offhandStack.getItem().getUseAnimation(offhandStack) == UseAnim.BLOCK))
                return InteractionResultHolder.pass(stack);
        }
        return super.use(world, player, hand);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onTick(PlayerTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof LivingEntity entity && EnigmaticHandler.canUse(entity, entity.getMainHandItem())) {
                if (entity.getMainHandItem().is(EnigmaticItems.THE_TWIST))
                    entity.getAttributes().addTransientAttributeModifiers(getKnockbackModifier());
                else entity.getAttributes().removeAttributeModifiers(getKnockbackModifier());
            }
        }

        @SubscribeEvent
        private static void onDamage(@NotNull LivingIncomingDamageEvent event) {
            if (event.getEntity().getType().is(Tags.EntityTypes.BOSSES)) {
                DamageSource source = event.getSource();
                if (source.getDirectEntity() instanceof LivingEntity attacker && source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                    ItemStack stack = attacker.getMainHandItem();
                    if (stack.is(EnigmaticItems.THE_TWIST) && EnigmaticHandler.canUse(attacker, stack)) {
                        event.setAmount(event.getAmount() * (1 + 0.01F * specialDamageBoost.get()));
                    }
                }
            }
        }
    }
}
