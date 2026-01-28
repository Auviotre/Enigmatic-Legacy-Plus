package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreationHeart extends SpellstoneItem {
    public static ModConfigSpec.IntValue cooldown;
    public CreationHeart() {
        super(defaultSingleProperties().rarity(Rarity.EPIC).fireResistant(), 0x0);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.creation_heart").push("spellstone.creationHeart");
        cooldown = builder.defineInRange("cooldown", 3, 1, 40);
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
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart7");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.creationHeart8");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    public int getCooldown() {
        return cooldown.get();
    }

    public void addTuneTooltip(List<Component> list) {

    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (entity.isOnFire()) entity.clearFire();
        if (entity.isFreezing()) entity.setTicksFrozen(0);

        List<MobEffectInstance> effects = new ArrayList<>(entity.getActiveEffects());

        for (MobEffectInstance effect : effects) {
            MobEffect value = effect.getEffect().value();
            if (Objects.equals(BuiltInRegistries.MOB_EFFECT.getKey(value), ResourceLocation.fromNamespaceAndPath("mana-and-artifice", "chrono-exhaustion"))) {
                continue;
            }

            if (!value.isBeneficial()) {
                entity.removeEffect(effect.getEffect());
            }
        }

    }
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(NeoForgeMod.CREATIVE_FLIGHT, new AttributeModifier(getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        return attributes;
    }

    public List<Component> getAttributesTooltip(@NotNull List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.CREATION_HEART)) {
                if (event.getSource().is(EnigmaticTags.DamageTypes.CREATION_HEART_IMMUNE_TO))
                    event.setCanceled(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onLivingDeath(@NotNull LivingDeathEvent event) {
            LivingEntity entity = event.getEntity();
            if (ISpellstone.get(entity).is(EnigmaticItems.CREATION_HEART) || EnigmaticHandler.hasItem(entity, EnigmaticItems.CREATION_HEART)) {
                if (event.getSource().is(Tags.DamageTypes.IS_TECHNICAL)) return;
                event.setCanceled(true);
                entity.setHealth(1);
            }
        }
    }
}