package auviotre.enigmatic.legacy.contents.item.spellstones.other;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.contents.item.spellstones.*;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class Spelltuner extends BaseCurioItem {
    public Spelltuner() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant());
    }

    public static boolean hasTune(LivingEntity entity, ItemLike item) {
        ItemStack curio = EnigmaticHandler.getCurio(entity, EnigmaticItems.SPELLTUNER);
        if (curio.isEmpty() || EnigmaticHandler.hasCurio(entity, item)) return false;
        Context context = curio.get(EnigmaticComponents.SPELLTUNER_CONTEXT);
        return context != null && (context.spellstone().is(item.asItem()) || context.spellstone().is(EnigmaticItems.THE_CUBE));
    }

    public static int getColor(ItemStack stack) {
        Context context = stack.get(EnigmaticComponents.SPELLTUNER_CONTEXT);
        if (context == null) return 0xFF544DE8;
        if (context.spellstone().is(EnigmaticItems.THE_CUBE))
            return Mth.hsvToRgb((float) Math.random(), 1.0F, 1.0F) + 0xFF000000;
        return context.color();
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        Context spellContext = stack.get(EnigmaticComponents.SPELLTUNER_CONTEXT);
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spelltuner1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spelltuner2");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spelltuner3");
        } else {
            if (spellContext != null) {
                Item spellstone = spellContext.spellstone.getItem();
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.spelltunerContext", ChatFormatting.GOLD, spellContext.spellstone().getItem().getDescription());
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.spelltunerEffect");
                try {
                    assert spellstone instanceof SpellstoneItem;
                    ((SpellstoneItem) spellstone).addTuneTooltip(list);
                } catch (Exception ignore) {
                }
            } else {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.spelltunerContextAbsent");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.spelltunerEffect");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkillAbsent");
            }
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        List<Component> list = super.getAttributesTooltip(tooltips, context, stack);
        if (!list.isEmpty())
            list.add(Component.translatable("attribute.modifier.take.1", "10", Component.translatable("tooltip.enigmaticlegacy.spelltunerPassive")).withStyle(ChatFormatting.BLUE));
        return list;
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (hasTune(entity, EnigmaticItems.GOLEM_HEART)) {
            entity.getAttributes().addTransientAttributeModifiers(this.getGolemHeartModifiers());
        }
        if (hasTune(entity, EnigmaticItems.OCEAN_STONE)) {
            if (entity.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) entity.setAirSupply(entity.getMaxAirSupply());
            entity.getAttributes().addTransientAttributeModifiers(this.getOceanStoneModifiers());
        }
        if (hasTune(entity, EnigmaticItems.REVIVAL_LEAF)) {
            if (entity.tickCount % RevivalLeaf.naturalRegenerationSpeed.get() == 0 && entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(Math.max(0.4F, entity.getMaxHealth() / 125.0F));
            }
        }
    }

    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().removeAttributeModifiers(this.getGolemHeartModifiers());
        entity.getAttributes().removeAttributeModifiers(this.getOceanStoneModifiers());
        super.onUnequip(context, newStack, stack);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.LUCK, new AttributeModifier(getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getGolemHeartModifiers() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(getLocation(this), GolemHeart.knockbackResistance.getAsDouble() / 2, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getOceanStoneModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        map.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(getLocation(this), 1.0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return map;
    }

    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.PRIMARY && slot.mayPickup(player) && slot.hasItem()) {
            if (slot.getItem().getItem() instanceof SpellstoneItem spellstone) {
                player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 0.8F, 1.3F + player.getRandom().nextFloat() * 0.4F);
                stack.set(EnigmaticComponents.SPELLTUNER_CONTEXT, Context.of(spellstone.getDefaultInstance(), spellstone.getColor()));
                return true;
            }
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    public boolean isFoil(ItemStack stack) {
        return stack.get(EnigmaticComponents.SPELLTUNER_CONTEXT) != null;
    }

    public record Context(ItemStack spellstone, int color) {
        public static final MapCodec<Context> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                ItemStack.SINGLE_ITEM_CODEC.fieldOf("spellstone").forGetter(Context::spellstone),
                Codec.INT.fieldOf("color").forGetter(Context::color)
        ).apply(instance, Context::of));

        public static final Codec<Context> CODEC = MAP_CODEC.codec();


        public static final StreamCodec<RegistryFriendlyByteBuf, Context> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, Context::spellstone,
                ByteBufCodecs.INT, Context::color,
                Context::of);

        public static Context of(ItemStack spellstone, int color) {
            return new Context(spellstone, color);
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            DamageSource source = event.getSource();
            if (event.isCanceled()) return;
            if (!source.is(DamageTypes.LAVA) && source.type().effects().equals(DamageEffects.BURNING)) {
                if (hasTune(entity, EnigmaticItems.BLAZING_CORE)) event.setCanceled(true);
            }
            if (hasTune(entity, EnigmaticItems.ANGEL_BLESSING)) {
                if (source.is(EnigmaticTags.DamageTypes.ANGEL_BLESSING_IMMUNE_TO))
                    event.setCanceled(true);
            }
        }

        @SubscribeEvent
        private static void onCriticalHit(@NotNull CriticalHitEvent event) {
            if (hasTune(event.getEntity(), EnigmaticItems.LOST_ENGINE)) {
                event.setDamageMultiplier(event.getDamageMultiplier() + 0.3F);
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            if (hasTune(victim, EnigmaticItems.EYE_OF_NEBULA)) {
                if (event.getSource().is(Tags.DamageTypes.IS_MAGIC))
                    event.setNewDamage(event.getNewDamage() * (1.0F - 0.005F * (5 + EyeOfNebula.magicResistance.get())));
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onLivingDeath(@NotNull LivingDeathEvent event) {
            LivingEntity entity = event.getEntity();
            if (hasTune(entity, EnigmaticItems.VOID_PEARL) && entity.getRandom().nextFloat() < 0.005F * VoidPearl.undeadProbability.getAsInt()) {
                if (event.getSource().is(Tags.DamageTypes.IS_TECHNICAL)) return;
                event.setCanceled(true);
                entity.setHealth(1);
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            LivingEntity victim = event.getEntity();
            Entity entity = event.getSource().getEntity();
            if (entity instanceof LivingEntity attacker) {
                DamageSource source = event.getSource();
                if (hasTune(victim, EnigmaticItems.FORGOTTEN_ICE) && attacker.canFreeze()) {
                    if (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO) || source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                        attacker.hurt(EnigmaticDamageTypes.source(victim.level(), DamageTypes.FREEZE, victim), (float) ForgottenIce.damageFeedback.getAsDouble() / 3);
                        attacker.setTicksFrozen(attacker.getTicksFrozen() + attacker.getTicksRequiredToFreeze() / 3);
                    }
                }
                if (hasTune(attacker, EnigmaticItems.FORGOTTEN_ICE) && victim.canFreeze()) {
                    victim.setTicksFrozen(victim.getTicksFrozen() + victim.getTicksRequiredToFreeze() / 2);
                }
            }
        }
    }
}
