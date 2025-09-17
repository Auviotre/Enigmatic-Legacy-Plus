package auviotre.enigmatic.legacy.contents.item.amulets;

import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class EldritchAmulet extends BaseCurioItem {
    public EldritchAmulet() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.EPIC).component(EnigmaticComponents.ELDRITCH, true));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmulet1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmulet2");
        } else {
            TooltipHandler.holdShift(list);
            String name = stack.get(EnigmaticComponents.AMULET_NAME);
            if (name != null) {
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletInscription", ChatFormatting.RED, name);
            }
        }
        TooltipHandler.line(list);
        TooltipHandler.worthyOnly(list, stack);
        TooltipHandler.line(list);
        list.add(Component.translatable("curios.modifiers.charm").withStyle(ChatFormatting.GOLD));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmuletStat1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmuletStat2");
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (!EnigmaticHandler.isTheWorthyOne(entity)) return;
        if (entity instanceof Player) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (timer < 1.0F) stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
        }
        if (entity.tickCount % 5 == 0 && !entity.level().isClientSide()) {
            List<LivingEntity> entities = EnigmaticHandler.getObservedEntities(entity, entity.level(), 3, 128, false);
            for (LivingEntity target : entities) {
                if (EnigmaticHandler.hasCurio(target, this)) continue;
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1));
                target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 1));
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 1));
            }
        }
        entity.getAttributes().addTransientAttributeModifiers(getModifiers());
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (isSelected && EnigmaticHandler.isTheWorthyOne(livingEntity)) stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
            else stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
    }

    public void onUnequip(@NotNull SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().removeAttributeModifiers(getModifiers());
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(getLocation(this), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return map;
    }

    @SubscribeEvent
    public void onDamagePost(LivingDamageEvent.@NotNull Post event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && !attacker.level().isClientSide()) {
            if (EnigmaticHandler.hasCurio(attacker, this)) attacker.heal(event.getNewDamage() * 0.15F);
        }
    }
}
