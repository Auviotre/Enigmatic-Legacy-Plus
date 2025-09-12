package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.function.Supplier;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class GolemHeart extends SpellstoneItem {
    public GolemHeart() {
        super(defaultSingleProperties().rarity(Rarity.RARE));

        this.immunityList.add(DamageTypes.CACTUS);
        this.immunityList.add(DamageTypes.CRAMMING);
        this.immunityList.add(DamageTypes.IN_WALL);
        this.immunityList.add(DamageTypes.FALLING_BLOCK);
        this.immunityList.add(DamageTypes.SWEET_BERRY_BUSH);

        Supplier<Float> meleeResistanceSupplier = () -> (1.0F - 0.01F * CONFIG.SPELLSTONES.meleeResistance.getAsInt());
        Supplier<Float> explosionResistanceSupplier = () -> (1.0F - 0.01F * CONFIG.SPELLSTONES.explosionResistance.getAsInt());
        Supplier<Float> magicVulnerabilitySupplier = () -> (float) CONFIG.SPELLSTONES.GHVulnerabilityModifier.getAsDouble();

        this.resistanceList.put(DamageTypes.GENERIC, meleeResistanceSupplier);
        this.resistanceList.put(DamageTypes.MOB_ATTACK, meleeResistanceSupplier);
        this.resistanceList.put(DamageTypes.PLAYER_ATTACK, meleeResistanceSupplier);
        this.resistanceList.put(DamageTypes.EXPLOSION, explosionResistanceSupplier);
        this.resistanceList.put(DamageTypes.PLAYER_EXPLOSION, explosionResistanceSupplier);

        this.resistanceList.put(DamageTypes.MAGIC, magicVulnerabilitySupplier);
        this.resistanceList.put(DamageTypes.WITHER, magicVulnerabilitySupplier);
        this.resistanceList.put(DamageTypes.DRAGON_BREATH, magicVulnerabilitySupplier);
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
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart1", ChatFormatting.GOLD, String.format("%.1f", CONFIG.SPELLSTONES.defaultArmorBonus.getAsDouble()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart3", ChatFormatting.GOLD, String.format("%.1f", CONFIG.SPELLSTONES.superArmorBonus.getAsDouble()), String.format("%.1f", CONFIG.SPELLSTONES.superArmorToughnessBonus.getAsDouble()));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart4", ChatFormatting.GOLD, CONFIG.SPELLSTONES.explosionResistance.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart5", ChatFormatting.GOLD, CONFIG.SPELLSTONES.meleeResistance.get() + "%");
            double resistance = CONFIG.SPELLSTONES.knockbackResistance.getAsDouble() * 100;
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart6", ChatFormatting.GOLD, String.format("%.0f%%", resistance));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart7");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart8");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
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
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getArmorDefaultModifiers() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ARMOR, new AttributeModifier(getLocation(this), CONFIG.SPELLSTONES.defaultArmorBonus.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(getLocation(this), CONFIG.SPELLSTONES.knockbackResistance.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getFullArmorModifiers() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        ResourceLocation noArmor = EnigmaticLegacy.location("golem_heart_without_armor");
        builder.put(Attributes.ARMOR, new AttributeModifier(noArmor, CONFIG.SPELLSTONES.superArmorBonus.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(noArmor, CONFIG.SPELLSTONES.superArmorToughnessBonus.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(noArmor, CONFIG.SPELLSTONES.knockbackResistance.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }
}
