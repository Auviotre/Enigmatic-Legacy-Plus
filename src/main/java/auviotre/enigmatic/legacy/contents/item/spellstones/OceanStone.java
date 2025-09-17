package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class OceanStone extends SpellstoneItem {
    public OceanStone() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.oceanStoneSkill");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.oceanStone1", ChatFormatting.GOLD, CONFIG.SPELLSTONES.underwaterCreaturesResistance.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.oceanStone2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.oceanStone3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.oceanStone4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.oceanStone5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.oceanStone6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.oceanStone7");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    public int getCooldown() {
        return 600;
    }

    public void triggerActiveAbility(ServerLevel level, @NotNull ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) return;

        if (player.level().dimensionType().natural())
            if (!level.getLevelData().isThundering()) {
                boolean paybackReceived = false;
                if (player.totalExperience >= 200) {
                    player.giveExperiencePoints((int) (-200 * CONFIG.SPELLSTONES.xpCostModifier.getAsDouble()));
                    paybackReceived = true;
                }

                if (paybackReceived) {
                    int thunderstormTime = (int) (10000 + (player.getRandom().nextFloat() * 20000));
                    level.setWeatherParameters(0, thunderstormTime, true, true);
                    level.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.NEUTRAL, 2.0F, 0.7F + player.getRandom().nextFloat() * 0.3F);
                    super.triggerActiveAbility(level, player, stack);
                }
            }
    }

    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().removeAttributeModifiers(this.getModifiers(entity));
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (EnigmaticHandler.hasCurio(entity, this)) {
            if (entity.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
                entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0, true, false));
                entity.setAirSupply(entity.getMaxAirSupply());
            }
            entity.getAttributes().addTransientAttributeModifiers(this.getModifiers(entity));
        }
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        Holder.Reference<Enchantment> holder = EnigmaticHandler.get(entity.level(), Registries.ENCHANTMENT, Enchantments.AQUA_AFFINITY);
        boolean flag = EnchantmentHelper.getEnchantmentLevel(holder, entity) > 0;
        map.put(Attributes.GRAVITY, new AttributeModifier(getLocation(this), entity.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) ? -1.0F : 0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        map.put(Attributes.SUBMERGED_MINING_SPEED, new AttributeModifier(getLocation(this), flag ? 4.0F : 0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        map.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(getLocation(this), 1.5F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return map;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.OCEAN_STONE)) {
                if (event.getSource().is(DamageTypes.DROWN)) event.setCanceled(true);
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.OCEAN_STONE)) {
                Entity entity = event.getSource().getEntity();
                if (entity == null) return;
                if (entity.getType().is(EntityTypeTags.CAN_BREATHE_UNDER_WATER)) {
                    event.setNewDamage(event.getNewDamage() * (1.0F - 0.01F * CONFIG.SPELLSTONES.underwaterCreaturesResistance.get()));
                } else if (event.getSource().type().effects().equals(DamageEffects.BURNING)) {
                    event.setNewDamage((float) (event.getNewDamage() * CONFIG.SPELLSTONES.OSVulnerabilityModifier.getAsDouble()));
                }
            }
        }
    }
}
