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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class EtheriumCore extends SpellstoneItem {
    public static ModConfigSpec.IntValue damageConversion;
    public static ModConfigSpec.IntValue damageConversionLimit;
    public EtheriumCore() {
        super(defaultSingleProperties().rarity(Rarity.RARE), 0xFF55FFFF);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.etherium_core").push("spellstone.etheriumCore");
        damageConversion = builder.defineInRange("damageConversion", 40, 0, 100);
        damageConversionLimit = builder.defineInRange("damageConversionLimit", 25, 0, 100);
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
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumCore1", ChatFormatting.GOLD, "+10", "+8");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumCore1", ChatFormatting.GOLD, "+20%", "+40%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumCore2", ChatFormatting.GOLD, "+50%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumCore3", ChatFormatting.GOLD, "+40%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumCore4", ChatFormatting.GOLD, "40%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumCore5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumCore6");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ARMOR, new AttributeModifier(getLocation(this), 10, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(getLocation(this), 8, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR, new AttributeModifier(EnigmaticLegacy.location("etherium_core_buff"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(EnigmaticLegacy.location("etherium_core_buff"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(getLocation(this), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return builder.build();
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    public int getCooldown() {
        return 0;
    }

    public void addTuneTooltip(List<Component> list) {

    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity victim = event.getEntity();
            if (ISpellstone.get(victim).is(EnigmaticItems.ETHERIUM_CORE)) {
                if (event.getSource().is(EnigmaticTags.DamageTypes.ETHERIUM_CORE_IMMUNE_TO)) {
                    event.setCanceled(true);
                    return;
                }
                if (victim instanceof Player player) {
                    CompoundTag data = EnigmaticHandler.getPersistedData(player);
                    float counterattack = Math.min(event.getAmount() * damageConversion.get() * 0.01F + data.getFloat("EtheriumCounterattack"), damageConversionLimit.get());
                    data.putFloat("EtheriumCounterattack", counterattack);
                }
            }
            if (event.getSource().getEntity() instanceof Player attacker) {
                if (ISpellstone.get(attacker).is(EnigmaticItems.ETHERIUM_CORE)) {
                    CompoundTag data = EnigmaticHandler.getPersistedData(attacker);
                    event.setAmount(event.getAmount() + data.getFloat("EtheriumCounterattack"));
                    data.remove("EtheriumCounterattack");
                }
            }
        }
    }
}
