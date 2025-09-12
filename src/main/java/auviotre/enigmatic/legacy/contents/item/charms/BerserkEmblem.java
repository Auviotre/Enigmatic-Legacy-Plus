package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class BerserkEmblem extends CursedCurioItem {
    public BerserkEmblem() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant());
        NeoForge.EVENT_BUS.register(this);
    }

    public static float getMissingHealthPool(LivingEntity entity) {
        return (entity.getMaxHealth() - Math.min(entity.getHealth(), entity.getMaxHealth())) / entity.getMaxHealth();
    }

    private Multimap<Holder<Attribute>, AttributeModifier> createAttributeMap(LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        float missingHealthPool = getMissingHealthPool(entity);
        attributes.put(Attributes.ATTACK_SPEED, new AttributeModifier(getLocation(this), missingHealthPool * CONFIG.CURSED_ITEMS.BEAttackSpeed.get(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        attributes.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(getLocation(this), missingHealthPool * CONFIG.CURSED_ITEMS.BEMovementSpeed.get(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return attributes;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm1", ChatFormatting.GOLD, String.format("%.1f%%", CONFIG.CURSED_ITEMS.BEAttackDamage.get()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm2", ChatFormatting.GOLD, String.format("%.1f%%", CONFIG.CURSED_ITEMS.BEAttackSpeed.get()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm3", ChatFormatting.GOLD, String.format("%.1f%%", CONFIG.CURSED_ITEMS.BEMovementSpeed.get()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm4", ChatFormatting.GOLD, String.format("%.1f%%", CONFIG.CURSED_ITEMS.BEDamageResistance.get()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm6");
        } else TooltipHandler.holdShift(list);

        if (Minecraft.getInstance().player != null)
            if (EnigmaticHandler.getCurio(Minecraft.getInstance().player, this) == stack) {
                float missingPool = getMissingHealthPool(Minecraft.getInstance().player);
                int percentage = (int) (missingPool * 100F);
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm7");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm1", ChatFormatting.GOLD, String.format("%.1f%%", percentage * CONFIG.CURSED_ITEMS.BEAttackDamage.get()));
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm2", ChatFormatting.GOLD, String.format("%.1f%%", percentage * CONFIG.CURSED_ITEMS.BEAttackSpeed.get()));
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm3", ChatFormatting.GOLD, String.format("%.1f%%", percentage * CONFIG.CURSED_ITEMS.BEMovementSpeed.get()));
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm4", ChatFormatting.GOLD, String.format("%.1f%%", percentage * CONFIG.CURSED_ITEMS.BEDamageResistance.get()));
            }
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (entity.level().isClientSide) return;
        entity.getAttributes().addTransientAttributeModifiers(this.createAttributeMap(entity));
    }

    public void onUnequip(@NotNull SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().removeAttributeModifiers(this.createAttributeMap(entity));
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && EnigmaticHandler.isTheCursedOne(context.entity());
    }


    @SubscribeEvent
    public void onEntityHurt(LivingDamageEvent.@NotNull Pre event) {
        LivingEntity victim = event.getEntity();
        if (EnigmaticHandler.hasCurio(victim, this)) {
            event.setNewDamage(event.getNewDamage() * (1.0F - (getMissingHealthPool(victim) * (float) CONFIG.CURSED_ITEMS.BEDamageResistance.getAsDouble())));
        }
        Entity entity = event.getSource().getEntity();
        if (entity instanceof LivingEntity attacker) {
            event.setNewDamage(event.getNewDamage() * (1.0F + (getMissingHealthPool(attacker) * (float) CONFIG.CURSED_ITEMS.BEAttackDamage.getAsDouble())));
        }
    }
}