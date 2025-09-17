package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.loot.conditions.IsMonsterCondition;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.event.CurioCanUnequipEvent;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class EnigmaticEnchantments {
    public static final ResourceKey<Enchantment> SLAYER = key("slayer");
    public static final ResourceKey<Enchantment> SHARPSHOOTER = key("sharpshooter");
    public static final ResourceKey<Enchantment> CEASELESS = key("ceaseless");
    public static final ResourceKey<Enchantment> WRATH = key("wrath");
    public static final ResourceKey<Enchantment> ETERNAL_BINDING_CURSE = key("eternal_binding_curse");
    public static final ResourceKey<Enchantment> NEMESIS_CURSE = key("nemesis_curse");
    public static final ResourceKey<Enchantment> SORROW_CURSE = key("sorrow_curse");

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchantmentGetter = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemGetter = context.lookup(Registries.ITEM);
        register(context, SLAYER, Enchantment.enchantment(
                                Enchantment.definition(
                                        itemGetter.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE), itemGetter.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        5, 5, Enchantment.dynamicCost(5, 8), Enchantment.dynamicCost(25, 8), 2, EquipmentSlotGroup.MAINHAND
                                )
                        ).exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.DAMAGE,
                                new AddValue(LevelBasedValue.perLevel(1.5F)),
                                IsMonsterCondition.target(LootContext.EntityTarget.THIS)
                        ).withEffect(
                                EnchantmentEffectComponents.POST_ATTACK,
                                EnchantmentTarget.ATTACKER,
                                EnchantmentTarget.VICTIM,
                                new ApplyMobEffect(
                                        HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                        LevelBasedValue.constant(1.0F), LevelBasedValue.perLevel(1.0F, 0.5F),
                                        LevelBasedValue.constant(3.0F), LevelBasedValue.constant(3.0F)
                                ),
                                IsMonsterCondition.target(LootContext.EntityTarget.THIS).and(DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)))
                        )
        );
        register(context, SHARPSHOOTER, Enchantment.enchantment(
                                Enchantment.definition(
                                        itemGetter.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                                        10, 5, Enchantment.dynamicCost(1, 10), Enchantment.dynamicCost(16, 10), 1, EquipmentSlotGroup.MAINHAND
                                )
                        ).exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.CROSSBOW_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.DAMAGE,
                                new AddValue(LevelBasedValue.perLevel(0.5F)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build()
                                )
                        )
        );
        register(context, CEASELESS, Enchantment.enchantment(
                                Enchantment.definition(
                                        itemGetter.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                                        1, 1, Enchantment.constantCost(20), Enchantment.constantCost(50), 8, EquipmentSlotGroup.MAINHAND
                                )
                        ).exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.BOW_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.AMMO_USE,
                                new SetValue(LevelBasedValue.constant(0.0F)),
                                MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.ARROW))
                        )
        );
        register(context, WRATH, Enchantment.enchantment(
                                Enchantment.definition(
                                        itemGetter.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                                        10,
                                        5,
                                        Enchantment.dynamicCost(1, 8),
                                        Enchantment.dynamicCost(25, 8),
                                        1,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                        .withEffect(EnchantmentEffectComponents.DAMAGE, new AddValue(LevelBasedValue.perLevel(1.25F)))
        );
        register(context, NEMESIS_CURSE, Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE), itemGetter.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                1, 1, Enchantment.constantCost(25), Enchantment.constantCost(50), 8, EquipmentSlotGroup.MAINHAND
                        )
                )
        );
        register(context, ETERNAL_BINDING_CURSE, Enchantment.enchantment(
                                Enchantment.definition(
                                        itemGetter.getOrThrow(EnigmaticTags.Items.ETERNAL_BINDING_ENCHANTABLE), itemGetter.getOrThrow(ItemTags.EQUIPPABLE_ENCHANTABLE),
                                        1, 1, Enchantment.constantCost(25), Enchantment.constantCost(50), 8,
                                        EquipmentSlotGroup.ANY
                                )
                        )
                        .exclusiveWith(enchantmentGetter.getOrThrow(EnigmaticTags.Enchantments.BINDING_CURSE_EXCLUSIVE))
                        .withEffect(EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)
        );
        register(context, SORROW_CURSE, Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                1, 1, Enchantment.constantCost(25), Enchantment.constantCost(50), 8, EquipmentSlotGroup.ARMOR
                        )
                )
        );
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, EnigmaticLegacy.location(name));
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onUnequip(@NotNull CurioCanUnequipEvent event) {
            if (event.getEntity() instanceof Player player && player.isCreative()) return;
            Holder<Enchantment> holder = event.getEntity().registryAccess().holderOrThrow(ETERNAL_BINDING_CURSE);
            if (EnchantmentHelper.getTagEnchantmentLevel(holder, event.getStack()) > 0)
                event.setUnequipResult(TriState.FALSE);
        }

        @SubscribeEvent
        private static void onDrop(@NotNull DropRulesEvent event) {
            event.addOverride(
                    stack -> {
                        Holder<Enchantment> holder = event.getEntity().registryAccess().holderOrThrow(ETERNAL_BINDING_CURSE);
                        return stack.is(EnigmaticItems.CURSED_RING) || stack.is(EnigmaticItems.ENIGMATIC_AMULET) || EnchantmentHelper.getTagEnchantmentLevel(holder, stack) > 0;
                    },
                    ICurio.DropRule.ALWAYS_KEEP
            );
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onDeath(@NotNull LivingDeathEvent event) {
            LivingEntity entity = event.getEntity();
            if (!entity.level().isClientSide()) {
                if (event.getSource().is(EnigmaticDamageTypes.NEMESIS_CURSE)) {
                    event.setCanceled(true);
                    entity.setHealth(1);
                }
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            DamageSource source = event.getSource();
            LivingEntity entity = event.getEntity();
            float damage = event.getNewDamage();
            if (source.getEntity() instanceof LivingEntity attacker) {
                var nemesis = EnigmaticHandler.get(attacker.level(), Registries.ENCHANTMENT, NEMESIS_CURSE);
                if (EnchantmentHelper.getEnchantmentLevel(nemesis, attacker) > 0) {
                    attacker.hurt(EnigmaticDamageTypes.source(attacker.level(), EnigmaticDamageTypes.NEMESIS_CURSE, event.getEntity()), damage * 0.35F);
                }
            }

            var sorrow = EnigmaticHandler.get(entity.level(), Registries.ENCHANTMENT, SORROW_CURSE);
            if (EnchantmentHelper.getEnchantmentLevel(sorrow, entity) > 0 && entity.getRandom().nextFloat() < 10.12F) {
                float severity = damage > 4 ? damage / 4 : 1;
                severity *= 0.5F + entity.getRandom().nextFloat();
                int amplifier = Math.min((int) (severity / 2), 3);
                MobEffectInstance instance = new MobEffectInstance(EnigmaticHandler.getRandomDebuff(entity), (int) (300 * severity), amplifier, false, true);
                entity.addEffect(instance);
            }
        }
    }
}
