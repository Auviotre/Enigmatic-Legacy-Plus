package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
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
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class BerserkEmblem extends CursedCurioItem {
    public static ModConfigSpec.DoubleValue attackDamage;
    public static ModConfigSpec.DoubleValue attackSpeed;
    public static ModConfigSpec.DoubleValue movementSpeed;
    public static ModConfigSpec.DoubleValue damageResistance;

    public BerserkEmblem() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant());
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.berserk_emblem").push("cursedItems.berserkEmblem");
        attackDamage = builder.defineInRange("attackDamage", 1.0, 0, 10.0);
        attackSpeed = builder.defineInRange("attackSpeed", 1.0, 0, 10.0);
        movementSpeed = builder.defineInRange("movementSpeed", 0.5, 0, 10.0);
        damageResistance = builder.defineInRange("damageResistance", 0.5, 0, 1.0);
        builder.pop(2);
    }

    public static float getMissingHealthPool(@NotNull LivingEntity entity) {
        return (entity.getMaxHealth() - Math.min(entity.getHealth(), entity.getMaxHealth())) / entity.getMaxHealth();
    }

    private Multimap<Holder<Attribute>, AttributeModifier> createAttributeMap(LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        float missingHealthPool = getMissingHealthPool(entity);
        attributes.put(Attributes.ATTACK_SPEED, new AttributeModifier(getLocation(this), missingHealthPool * attackSpeed.get(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        attributes.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(getLocation(this), missingHealthPool * movementSpeed.get(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return attributes;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm1", ChatFormatting.GOLD, String.format("%.1f%%", attackDamage.get()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm2", ChatFormatting.GOLD, String.format("%.1f%%", attackSpeed.get()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm3", ChatFormatting.GOLD, String.format("%.1f%%", movementSpeed.get()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm4", ChatFormatting.GOLD, String.format("%.1f%%", damageResistance.get()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm6");
        } else TooltipHandler.holdShift(list);
        LocalPlayer player = Minecraft.getInstance().player;
        if (EnigmaticHandler.getCurio(player, this) == stack) {
            float missingPool = getMissingHealthPool(player);
            int percentage = (int) (missingPool * 100F);
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm7");
            double damageBoost = percentage * attackDamage.get() * (EnigmaticHandler.hasCurio(player, EnigmaticItems.HELL_BLADE_CHARM) ? 1.25 : 1.0);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm1", ChatFormatting.GOLD, String.format("%.1f%%", damageBoost));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm2", ChatFormatting.GOLD, String.format("%.1f%%", percentage * attackSpeed.get()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm3", ChatFormatting.GOLD, String.format("%.1f%%", percentage * movementSpeed.get()));
            double damageResist = percentage * damageResistance.get() * (EnigmaticHandler.hasCurio(player, EnigmaticItems.HELL_BLADE_CHARM) ? 0.6 : 1.0);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.berserkCharm4", ChatFormatting.GOLD, String.format("%.1f%%", damageResist));
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

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            Entity entity = event.getSource().getEntity();
            if (entity instanceof LivingEntity attacker && EnigmaticHandler.hasCurio(attacker, EnigmaticItems.BERSERK_EMBLEM)) {
                float boost = getMissingHealthPool(attacker) * (float) attackDamage.getAsDouble();
                if (EnigmaticHandler.hasCurio(attacker, EnigmaticItems.HELL_BLADE_CHARM)) boost *= 1.25F;
                event.setAmount(event.getAmount() * (1.0F + boost));
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.BERSERK_EMBLEM)) {
                float resistance = getMissingHealthPool(victim) * (float) damageResistance.getAsDouble();
                if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.HELL_BLADE_CHARM)) resistance *= 0.6F;
                event.setNewDamage(event.getNewDamage() * (1.0F - resistance));
            }
        }
    }
}