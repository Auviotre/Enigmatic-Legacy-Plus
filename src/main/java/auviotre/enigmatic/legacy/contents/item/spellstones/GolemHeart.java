package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class GolemHeart extends SpellstoneItem {
    public static ModConfigSpec.DoubleValue defaultArmorBonus;
    public static ModConfigSpec.DoubleValue superArmorBonus;
    public static ModConfigSpec.DoubleValue superArmorToughnessBonus;
    public static ModConfigSpec.DoubleValue knockbackResistance;
    public static ModConfigSpec.IntValue meleeResistance;
    public static ModConfigSpec.IntValue explosionResistance;
    public static ModConfigSpec.DoubleValue GHVulnerabilityModifier;

    public GolemHeart() {
        super(defaultSingleProperties().rarity(Rarity.RARE), 0xFFE40B0B);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.golem_heart").push("spellstone.golemHeart");
        defaultArmorBonus = builder.defineInRange("defaultArmorBonus", 4.0, 0, 20);
        superArmorBonus = builder.defineInRange("superArmorBonus", 16.0, 0, 100);
        superArmorToughnessBonus = builder.defineInRange("superArmorToughnessBonus", 4.0, 0.0, 20.0);
        knockbackResistance = builder.defineInRange("knockbackResistance", 0.9, 0.0, 1.0);
        meleeResistance = builder.defineInRange("meleeResistance", 25, 0, 100);
        explosionResistance = builder.defineInRange("explosionResistance", 40, 0, 100);
        GHVulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 2.0, 1.0, 20.0);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkillAbsent");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart1", ChatFormatting.GOLD, String.format("%.1f", defaultArmorBonus.getAsDouble()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart3", ChatFormatting.GOLD, String.format("%.1f", superArmorBonus.getAsDouble()), String.format("%.1f", superArmorToughnessBonus.getAsDouble()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart4", ChatFormatting.GOLD, explosionResistance.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart5", ChatFormatting.GOLD, meleeResistance.get() + "%");
            double resistance = knockbackResistance.getAsDouble() * 100;
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart6", ChatFormatting.GOLD, String.format("%.0f%%", resistance));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart7");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart8");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    @OnlyIn(Dist.CLIENT)
    public void addTuneTooltip(List<Component> list) {
        double resistance = knockbackResistance.getAsDouble() * 50;
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart6", ChatFormatting.GOLD, String.format("%.0f%%", resistance));
    }

    public int getCooldown() {
        return 0;
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (EnigmaticHandler.hasNoArmor(entity)) {
            entity.getAttributes().removeAttributeModifiers(this.getArmorDefaultModifiers());
            entity.getAttributes().addTransientAttributeModifiers(this.getFullArmorModifiers());
        } else {
            entity.getAttributes().removeAttributeModifiers(this.getFullArmorModifiers());
            entity.getAttributes().addTransientAttributeModifiers(this.getArmorDefaultModifiers());
        }
    }

    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().removeAttributeModifiers(this.getArmorDefaultModifiers());
        entity.getAttributes().removeAttributeModifiers(this.getFullArmorModifiers());
        super.onUnequip(context, newStack, stack);
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getArmorDefaultModifiers() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ARMOR, new AttributeModifier(getLocation(this), defaultArmorBonus.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(getLocation(this), knockbackResistance.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getFullArmorModifiers() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        ResourceLocation noArmor = EnigmaticLegacy.location("golem_heart_without_armor");
        builder.put(Attributes.ARMOR, new AttributeModifier(noArmor, superArmorBonus.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(noArmor, superArmorToughnessBonus.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(noArmor, knockbackResistance.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.GOLEM_HEART)) {
                if (event.getSource().is(EnigmaticTags.DamageTypes.GOLEM_HEART_IMMUNE_TO))
                    event.setCanceled(true);
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.GOLEM_HEART)) {
                DamageSource source = event.getSource();
                if (EnigmaticHandler.hasNoArmor(event.getEntity()) && source.is(DamageTypeTags.IS_EXPLOSION)) {
                    event.setNewDamage(event.getNewDamage() * (1.0F - 0.01F * explosionResistance.getAsInt()));
                } else if (source.is(Tags.DamageTypes.IS_MAGIC)) {
                    event.setNewDamage((float) (event.getNewDamage() * GHVulnerabilityModifier.getAsDouble()));
                } else if (source.is(EnigmaticTags.DamageTypes.GOLEM_HEART_IS_MELEE)) {
                    event.setNewDamage(event.getNewDamage() * (1.0F - 0.01F * meleeResistance.getAsInt()));
                }
            }
        }
    }
}
