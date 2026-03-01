package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class ViolenceScroll extends BaseCurioItem {
    public static ModConfigSpec.IntValue attackSpeed;
    public static ModConfigSpec.IntValue entityReach;
    public static ModConfigSpec.IntValue krBoost;

    public ViolenceScroll() {
        super(defaultSingleProperties().rarity(Rarity.EPIC).component(EnigmaticComponents.ELDRITCH, true).component(EnigmaticComponents.VIOLENCE_TIMER, 0)
                .component(EnigmaticComponents.ABSORBED_ENCHANTMENTS, AbsorbedEnchants.EMPTY));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.violence_scroll").push("abyssItems.violenceScroll");
        attackSpeed = builder.defineInRange("attackSpeed", 4, 0, 10);
        entityReach = builder.defineInRange("entityReach", 3, 0, 20);
        krBoost = builder.defineInRange("knockbackResistanceBoost", 2, 0, 10);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        int count = AbsorbedEnchants.getCount(stack);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScroll1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScroll2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScroll3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScroll4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScrollCount", ChatFormatting.GOLD, count);
            if (count > 0) {
                if (Minecraft.getInstance().player != null) {
                    MobEffectInstance effect = Minecraft.getInstance().player.getEffect(EnigmaticEffects.VIOLENCE_CURSE);
                    if (effect != null) count += (int) ((effect.getAmplifier() + 1) * (1 + count * 0.08));
                }
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScroll5", ChatFormatting.GOLD, String.format("+%d%%", count * attackSpeed.get()));
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScroll6", ChatFormatting.GOLD, String.format("+%d%%", count * entityReach.get()));
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScroll7", ChatFormatting.GOLD, String.format("+%d%%", count * krBoost.get()));
            }
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScrollLore1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScrollLore2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScrollLore3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScrollLore4");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.violenceScrollCount", ChatFormatting.GOLD, count);
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
        TooltipHandler.line(list);
        TooltipHandler.worthyOnly(list, stack);
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (!EnigmaticHandler.isTheWorthyOne(entity)) return;
        entity.getAttributes().addTransientAttributeModifiers(this.createAttributeMap(entity, stack));
        if (entity instanceof Player) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (timer < 1.0F) stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
        }
        ItemEnchantments.Mutable curses = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(stack));
        curses.removeIf(enchantment -> !enchantment.is(EnchantmentTags.CURSE));
        if (!curses.toImmutable().isEmpty()) {
            ItemEnchantments.Mutable leftover = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(stack));
            leftover.keySet().removeIf(enchantment -> enchantment.is(EnchantmentTags.CURSE));
            AbsorbedEnchants.addCurse(stack, curses.keySet());
            EnchantmentHelper.setEnchantments(stack, leftover.toImmutable());
        }

        int i = stack.getOrDefault(EnigmaticComponents.VIOLENCE_TIMER, 0);
        int timer = stack.getOrDefault(EnigmaticComponents.VIOLENCE_CURSE_TIMER, 0);
        if (i > 0) {
            timer++;
            stack.set(EnigmaticComponents.VIOLENCE_CURSE_TIMER, timer);
            int modifier = i / 80;
            i -= (1 + modifier);
            MobEffectInstance effect = entity.getEffect(EnigmaticEffects.VIOLENCE_CURSE);
            if (effect == null) entity.addEffect(new MobEffectInstance(EnigmaticEffects.VIOLENCE_CURSE, 210, Math.min(timer / 100, 9), true, true));
            else {
                effect.duration = 210;
                effect.amplifier = Math.min(timer / 100, 9);
            }
        } else if (timer > 0) stack.set(EnigmaticComponents.VIOLENCE_CURSE_TIMER, (int) (timer * 0.95F));
        stack.set(EnigmaticComponents.VIOLENCE_TIMER, Math.max(i, 0));
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (isSelected && EnigmaticHandler.isTheWorthyOne(livingEntity))
                stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
            else stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
        ItemEnchantments.Mutable curses = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(stack));
        curses.removeIf(enchantment -> !enchantment.is(EnchantmentTags.CURSE));
        if (!curses.toImmutable().isEmpty()) {
            ItemEnchantments.Mutable leftover = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(stack));
            leftover.keySet().removeIf(enchantment -> enchantment.is(EnchantmentTags.CURSE));
            AbsorbedEnchants.addCurse(stack, curses.keySet());
            EnchantmentHelper.setEnchantments(stack, leftover.toImmutable());
        }
    }

    private Multimap<Holder<Attribute>, AttributeModifier> createAttributeMap(LivingEntity entity, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> multimap = HashMultimap.create();
        int count = AbsorbedEnchants.getCount(stack);
        MobEffectInstance effect = entity.getEffect(EnigmaticEffects.VIOLENCE_CURSE);
        if (effect != null) count += (int) ((effect.getAmplifier() + 1) * (1 + count * 0.08));
        multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(getLocation(this), count * attackSpeed.get() * 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        multimap.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(getLocation(this), count * entityReach.get() * 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        multimap.put(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(getLocation(this), count * krBoost.get() * 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return multimap;
    }

    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().removeAttributeModifiers(this.createAttributeMap(entity, stack));
        entity.removeEffect(EnigmaticEffects.VIOLENCE_CURSE);
        stack.set(EnigmaticComponents.VIOLENCE_TIMER, 0);
        stack.set(EnigmaticComponents.VIOLENCE_CURSE_TIMER, 0);
    }

    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (!EnigmaticHandler.isTheWorthyOne(player)) return super.overrideStackedOnOther(stack, slot, action, player);
        if (action != ClickAction.PRIMARY && slot.mayPlace(stack) && slot.mayPickup(player) && slot.hasItem()) {
            ItemStack other = slot.getItem();
            if (AbsorbedEnchants.canDisenchant(stack, other)) {
                slot.set(AbsorbedEnchants.disenchant(stack, other));
                if (player.level().isClientSide)
                    player.level().playSound(player, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.8F, 1.2F + (float)Math.random() * 0.4F);
                return true;
            }
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (!EnigmaticHandler.isTheWorthyOne(player)) return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        if (action != ClickAction.PRIMARY && slot.mayPlace(stack) && slot.mayPickup(player) && !other.isEmpty()) {
            if (AbsorbedEnchants.canDisenchant(stack, other)) {
                access.set(AbsorbedEnchants.disenchant(stack, other));
                if (player.level().isClientSide)
                    player.level().playSound(player, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.8F, 1.2F + (float)Math.random() * 0.4F);
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    public record AbsorbedEnchants(int count, List<String> enchantments) {
        public static final MapCodec<AbsorbedEnchants> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.INT.fieldOf("count").forGetter(AbsorbedEnchants::count),
                Codec.list(Codec.STRING).fieldOf("enchantments").forGetter(AbsorbedEnchants::enchantments)
        ).apply(instance, AbsorbedEnchants::of));

        public static final Codec<AbsorbedEnchants> CODEC = MAP_CODEC.codec();

        public static final StreamCodec<RegistryFriendlyByteBuf, AbsorbedEnchants> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, AbsorbedEnchants::count,
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), AbsorbedEnchants::enchantments,
                AbsorbedEnchants::of);

        public static final AbsorbedEnchants EMPTY = of(0, List.of());

        @Contract("_, _ -> new")
        public static @NotNull AbsorbedEnchants of(int count, List<String> enchantments) {
            return new AbsorbedEnchants(count, enchantments);
        }

        public static int getCount(ItemStack stack) {
            AbsorbedEnchants enchants = stack.getOrDefault(EnigmaticComponents.ABSORBED_ENCHANTMENTS, EMPTY);
            return enchants.count();
        }

        private static void addCurse(ItemStack scroll, Set<Holder<Enchantment>> enchantments) {
            AbsorbedEnchants enchants = scroll.getOrDefault(EnigmaticComponents.ABSORBED_ENCHANTMENTS, EMPTY);
            ArrayList<String> list = new ArrayList<>(enchants.enchantments());
            for (Holder<Enchantment> enchantment : enchantments) {
                String name = enchantment.getRegisteredName();
                if (!list.contains(name)) list.add(name);
            }
            scroll.set(EnigmaticComponents.ABSORBED_ENCHANTMENTS, of(list.size(), list));
        }

        private static ItemStack disenchant(ItemStack scroll, ItemStack target) {
            ItemStack item = target.copy();
            ItemEnchantments.Mutable curses = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(target));
            ItemEnchantments.Mutable leftover = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(target));
            curses.removeIf(holder -> !holder.is(EnchantmentTags.CURSE));
            leftover.removeIf(holder -> holder.is(EnchantmentTags.CURSE));
            addCurse(scroll, curses.keySet());
            ItemEnchantments immutable = leftover.toImmutable();
            if (immutable.isEmpty() && item.is(Items.ENCHANTED_BOOK)) return Items.BOOK.getDefaultInstance();
            EnchantmentHelper.setEnchantments(item, immutable);
            return item;
        }

        private static boolean canDisenchant(ItemStack scroll, ItemStack target) {
            Objects.requireNonNull(scroll.getItem());
            ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(target);
            Stream<Holder<Enchantment>> stream = enchantments.keySet().stream();
            return stream.anyMatch(holder -> holder.is(EnchantmentTags.CURSE));
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
            DamageSource source = event.getSource();
            if (source.getEntity() instanceof LivingEntity entity && EnigmaticHandler.hasCurio(entity, EnigmaticItems.VIOLENCE_SCROLL)) {
                event.setInvulnerabilityTicks(5);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            if (EnigmaticHandler.hasCurio(event.getEntity(), EnigmaticItems.VIOLENCE_SCROLL)) {
                ItemStack curio = EnigmaticHandler.getCurio(event.getEntity(), EnigmaticItems.VIOLENCE_SCROLL);
                int i = curio.getOrDefault(EnigmaticComponents.VIOLENCE_TIMER, 0);
                curio.set(EnigmaticComponents.VIOLENCE_TIMER, i + 120);
            }
            DamageSource source = event.getSource();
            if (source.getEntity() instanceof LivingEntity entity && EnigmaticHandler.hasCurio(entity, EnigmaticItems.VIOLENCE_SCROLL)) {
                ItemStack curio = EnigmaticHandler.getCurio(entity, EnigmaticItems.VIOLENCE_SCROLL);
                int i = curio.getOrDefault(EnigmaticComponents.VIOLENCE_TIMER, 0);
                curio.set(EnigmaticComponents.VIOLENCE_TIMER, i + 100);
            }
        }
    }
}
