package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.api.item.ICursed;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class TheTwist extends TheAcknowledgment implements ICursed {
    public TheTwist() {
        super(defaultSingleProperties().rarity(Rarity.EPIC), 8, -1.8F);
        NeoForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.fourthCurse"));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.fourthCurseAlteration");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theTwist1", ChatFormatting.GOLD, CONFIG.CURSED_ITEMS.specialDamageBoost.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theTwist2", ChatFormatting.GOLD, CONFIG.CURSED_ITEMS.knockbackModifier.get() + "%");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theTwistLore1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theTwistLore2");
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
        if (target.getType().is(Tags.EntityTypes.BOSSES))
            return damage * 0.01F * CONFIG.CURSED_ITEMS.specialDamageBoost.get();
        return super.getAttackDamageBonus(target, damage, damageSource);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getKnockbackModifier() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        double modifier = 0.01 * CONFIG.CURSED_ITEMS.knockbackModifier.getAsInt();
        builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(getLocation(this), modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return builder.build();
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!EnigmaticHandler.isTheCursedOne(player)) return InteractionResultHolder.pass(player.getItemInHand(hand));
        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack offhandStack = player.getOffhandItem();
            if (!offhandStack.isEmpty() && (offhandStack.getItem().getUseAnimation(offhandStack) == UseAnim.BLOCK))
                return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        return super.use(world, player, hand);
    }

    @SubscribeEvent
    public void onTick(PlayerTickEvent.@NotNull Pre event) {
        if (event.getEntity() instanceof LivingEntity entity && EnigmaticHandler.isTheCursedOne(entity)) {
            if (entity.getMainHandItem().is(this))
                entity.getAttributes().addTransientAttributeModifiers(this.getKnockbackModifier());
            else entity.getAttributes().removeAttributeModifiers(this.getKnockbackModifier());
        }
    }
}
