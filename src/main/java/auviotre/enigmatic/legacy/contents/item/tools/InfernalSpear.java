package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.effect.BlazingMight;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.AttributeUtil;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfernalSpear extends BaseItem {
    public InfernalSpear() {
        super(defaultSingleProperties().fireResistant().durability(Tiers.NETHERITE.getUses())
                .component(DataComponents.TOOL, SwordItem.createToolProperties())
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -1.6, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(AttributeUtil.BASE_ENTITY_REACH_ID, 1.8, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .build())
        );
    }

    public static Multimap<Holder<Attribute>, AttributeModifier> getBoost() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(EnigmaticLegacy.location("infernal_spear_ult"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        builder.put(Attributes.ARMOR, new AttributeModifier(EnigmaticLegacy.location("infernal_spear_ult"), 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(EnigmaticLegacy.location("infernal_spear_ult"), 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(EnigmaticLegacy.location("infernal_spear_ult"), 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return builder.build();
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        MobEffectInstance effect = player.getEffect(EnigmaticEffects.BLAZING_MIGHT);
        if (stack.is(this) && effect != null && effect.getAmplifier() == 9) {
            player.getAttributes().addTransientAttributeModifiers(getBoost());
            List<LivingEntity> entities = EnigmaticHandler.getObservedEntities(player, level, 2, 12, false);
            for (LivingEntity entity : entities) player.attack(entity);
            player.removeEffect(EnigmaticEffects.BLAZING_MIGHT);
            player.getAttributes().removeAttributeModifiers(getBoost());
            player.swing(hand);
            return InteractionResultHolder.consume(stack);
        }
        return super.use(level, player, hand);
    }

    public float getAttackDamageBonus(Entity target, float damage, DamageSource source) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            MobEffectInstance effect = attacker.getEffect(EnigmaticEffects.BLAZING_MIGHT);
            if (effect != null) return effect.getAmplifier() * 2.0F + 2.0F;
        }
        return super.getAttackDamageBonus(target, damage, source);
    }

    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return super.isValidRepairItem(stack, repairCandidate) || repairCandidate.is(Items.NETHERITE_INGOT);
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 24;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity victim = event.getEntity();
            if (event.getSource().getEntity() instanceof LivingEntity attacker && event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                if (!attacker.getMainHandItem().is(EnigmaticItems.INFERNAL_SPEAR)) return;
                int ticks = victim.getRemainingFireTicks();
                if (ticks >= 100) {
                    BlazingMight.addAmplifier(attacker, Math.min(3, ticks / 100), 500 + ticks);
                    victim.clearFire();
                }
            }
        }
    }
}
