package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class CosmicScroll extends BaseCurioItem {
    public CosmicScroll() {
        super(defaultSingleProperties().rarity(Rarity.EPIC));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cosmicScroll1", ChatFormatting.GOLD, "100%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cosmicScroll2");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cosmicScroll3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cosmicScroll4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cosmicScroll5", ChatFormatting.GOLD, 600);
            int cooldown = stack.getOrDefault(EnigmaticComponents.REVIVE_COOLDOWN, 0);
            if (cooldown > 0) {
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.cosmicScrollCooldown", ChatFormatting.GOLD, cooldown / 20);
            }
        } else TooltipHandler.holdShift(list);
    }

    @OnlyIn(Dist.CLIENT)
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        List<Component> list = super.getAttributesTooltip(tooltips, context, stack);
        if (!list.isEmpty())
            list.add(Component.translatable("attribute.modifier.take.1", "80", Component.translatable("tooltip.enigmaticlegacy.spelltunerPassive")).withStyle(ChatFormatting.BLUE));
        return list;
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        return entity instanceof Player player && player.hasInfiniteMaterials();
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        CuriosApi.addSlotModifier(attributes, "scroll", getLocation(this), 1.0, AttributeModifier.Operation.ADD_VALUE);
        attributes.put(Attributes.LUCK, new AttributeModifier(getLocation(this), 1.0, AttributeModifier.Operation.ADD_VALUE));
        attributes.put(EnigmaticAttributes.ETHERIUM_SHIELD, new AttributeModifier(getLocation(this), 0.2, AttributeModifier.Operation.ADD_VALUE));
        return attributes;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int id, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof LivingEntity living) {
            ItemStack scroll = EnigmaticHandler.getCurio(living, this);
            int cooldown = stack.getOrDefault(EnigmaticComponents.REVIVE_COOLDOWN, 0);
            if (stack != scroll && cooldown > 0) {
                stack.set(EnigmaticComponents.REVIVE_COOLDOWN, cooldown - 1);
            }
        }
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        int cooldown = stack.getOrDefault(EnigmaticComponents.REVIVE_COOLDOWN, 0);
        if (!context.entity().level().isClientSide && cooldown > 0) {
            stack.set(EnigmaticComponents.REVIVE_COOLDOWN, cooldown - 1);
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onLivingDeath(@NotNull LivingDeathEvent event) {
            LivingEntity entity = event.getEntity();
            ItemStack curio = EnigmaticHandler.getCurio(entity, EnigmaticItems.COSMIC_SCROLL);
            if (!curio.isEmpty() && curio.getOrDefault(EnigmaticComponents.REVIVE_COOLDOWN, 0) <= 0) {
                event.setCanceled(true);
                entity.setHealth(entity.getMaxHealth() / 2);
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 2));
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1));
                curio.set(EnigmaticComponents.REVIVE_COOLDOWN, 600 * 20);
            }
        }
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            DamageSource source = event.getSource();
            if (source.getEntity() instanceof LivingEntity entity) {
                ItemStack curio = EnigmaticHandler.getCurio(entity, EnigmaticItems.COSMIC_SCROLL);
                if (!curio.isEmpty() && !EnigmaticHandler.hasCurio(event.getEntity(), EnigmaticItems.COSMIC_SCROLL)) {
                    event.setAmount(event.getAmount() + event.getOriginalAmount());
                    if (source.is(DamageTypeTags.IS_PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK)) {
                        event.getEntity().igniteForSeconds(8);
                    }
                }
            }
        }
    }
}
