package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Optional;

public class ForgottenIce extends SpellstoneItem {
    public static final AttributeModifier HARD_FROZEN = new AttributeModifier(EnigmaticLegacy.location("hard_frozen"), -2.0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    static final ReplaceDisk EFFECT = new ReplaceDisk(
            new LevelBasedValue.Constant(5.0F),
            LevelBasedValue.constant(1.0F), new Vec3i(0, -1, 0),
            Optional.of(
                    BlockPredicate.allOf(
                            BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
                            BlockPredicate.matchesBlocks(Blocks.WATER),
                            BlockPredicate.matchesFluids(Fluids.WATER),
                            BlockPredicate.unobstructed()
                    )
            ),
            BlockStateProvider.simple(Blocks.FROSTED_ICE), Optional.of(GameEvent.BLOCK_PLACE)
    );
    public static ModConfigSpec.BooleanValue freezingBoost;
    public static ModConfigSpec.DoubleValue damageFeedback;
    public static ModConfigSpec.IntValue resistanceModifier;
    public static ModConfigSpec.DoubleValue vulnerabilityModifier;
    public static ModConfigSpec.IntValue cooldown;

    public ForgottenIce() {
        super(defaultSingleProperties().rarity(Rarity.RARE), 0xFF80E5FF);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.push("else");
        freezingBoost = builder.define("freezingBoost", true);
        builder.pop();
        builder.translation("item.enigmaticlegacyplus.forgotten_ice").push("spellstone.forgottenIce");
        resistanceModifier = builder.defineInRange("resistanceModifier", 30, 0, 100);
        damageFeedback = builder.defineInRange("damageFeedback", 4.0, 0.0, 64.0);
        vulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 2.5, 1.0, 20.0);
        cooldown = builder.defineInRange("cooldown", 240, 100, 400);
        builder.pop(2);
    }

    public static ImmutableMultimap<Holder<Attribute>, AttributeModifier> getFrozenAttributes() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.MOVEMENT_SPEED, HARD_FROZEN);
        return builder.build();
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgottenIceSkill");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgottenIce1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgottenIce2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgottenIce3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgottenIce4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgottenIce5", ChatFormatting.GOLD, "30%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgottenIce6");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    @OnlyIn(Dist.CLIENT)
    public void addTuneTooltip(List<Component> list) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgottenIce2");
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.setTicksFrozen(0);
        if (entity.level() instanceof ServerLevel level) {
            EFFECT.apply(level, 2, null, entity, entity.position());
        }
        List<LivingEntity> entities = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(5.0));
        for (LivingEntity target : entities) {
            if (target.isFullyFrozen()) {
                int tick = target.getPersistentData().getInt("ForgottenFrozenTick");
                if (target.getTicksFrozen() > 340) {
                    tick++;
                    target.getPersistentData().putBoolean("ForgottenFrozenHard", true);
                }
                target.getPersistentData().putInt("ForgottenFrozenTick", tick + 1);
            }
        }
    }

    public int getCooldown() {
        return cooldown.get();
    }

    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) return;
        ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Blocks.BLUE_ICE));
        SimpleParticleType snowflake = ParticleTypes.SNOWFLAKE;
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5.0));
        float damage = (float) (damageFeedback.getAsDouble() + player.getAttributeValue(Attributes.ATTACK_DAMAGE));
        for (LivingEntity entity : entities) {
            if (entity.is(player)) continue;
            int tick = entity.getPersistentData().getInt("ForgottenFrozenTick");
            float extra = 0;
            if (entity.isFullyFrozen()) extra = ((0.01F)) * Math.min(tick, 400) / 2;
            if (entity.canFreeze()) entity.setTicksFrozen(entity.getTicksFrozen() + 500);
            entity.hurt(player.damageSources().source(DamageTypes.FREEZE, player), damage * (1 + extra));
            if (tick <= 400) entity.getPersistentData().remove("ForgottenFrozenTick");
            else entity.getPersistentData().putInt("ForgottenFrozenTick", (tick - 400) / 2);
            float width = entity.getBbWidth() / 1.8F;
            level.sendParticles(particle, entity.getX(), entity.getY(0.8F), entity.getZ(), 16, width, 0.1D, width, 0.0D);
            level.sendParticles(snowflake, entity.getX(), entity.getY(0.8F), entity.getZ(), 10, width, 0.1D, width, 0.0D);
        }
        super.triggerActiveAbility(level, player, stack);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onTick(EntityTickEvent.Pre event) {
            if (event.getEntity() instanceof LivingEntity entity) {
                CompoundTag data = entity.getPersistentData();
                if (data.getBoolean("ForgottenFrozenHard")) {
                    Vec3 movement = entity.getDeltaMovement();
                    entity.setDeltaMovement(new Vec3(0, movement.y, 0));
                    entity.hasImpulse = true;
                    entity.getAttributes().addTransientAttributeModifiers(ForgottenIce.getFrozenAttributes());
                    if (!entity.isFullyFrozen()) {
                        data.remove("ForgottenFrozenHard");
                        entity.getAttributes().removeAttributeModifiers(ForgottenIce.getFrozenAttributes());
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            DamageSource source = event.getSource();
            if (!ISpellstone.get(entity).is(EnigmaticItems.FORGOTTEN_ICE) || event.isCanceled()) return;
            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
            if (source.is(DamageTypeTags.IS_FREEZING) || source.is(DamageTypeTags.BURN_FROM_STEPPING)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            if (ISpellstone.get(victim).is(EnigmaticItems.FORGOTTEN_ICE)) {
                if (event.getSource().is(DamageTypeTags.IS_FIRE))
                    event.setNewDamage(event.getNewDamage() * (float) vulnerabilityModifier.getAsDouble());
                if (event.getSource().is(EnigmaticTags.DamageTypes.FORGOTTEN_ICE_RESISTANT_TO))
                    event.setNewDamage(event.getNewDamage() * 0.01F * (100 - resistanceModifier.get()));
            }
            if (event.getSource().getEntity() instanceof LivingEntity attacker && EnigmaticHandler.hasCurio(attacker, EnigmaticItems.FORGOTTEN_ICE))
                if (victim.isFullyFrozen()) event.setNewDamage(event.getNewDamage() * 1.25F);
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            LivingEntity victim = event.getEntity();
            Entity entity = event.getSource().getEntity();
            if (entity instanceof LivingEntity attacker) {
                DamageSource source = event.getSource();
                if (ISpellstone.get(victim).is(EnigmaticItems.FORGOTTEN_ICE) && attacker.canFreeze()) {
                    if (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO) || source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                        attacker.hurt(EnigmaticDamageTypes.source(victim.level(), DamageTypes.FREEZE, victim), (float) damageFeedback.getAsDouble() / 2);
                        attacker.setTicksFrozen(attacker.getTicksFrozen() + attacker.getTicksRequiredToFreeze() / 2);
                    }
                }
                if (ISpellstone.get(attacker).is(EnigmaticItems.FORGOTTEN_ICE) && victim.canFreeze()) {
                    victim.setTicksFrozen(victim.getTicksFrozen() + victim.getTicksRequiredToFreeze());
                }
            }
        }
    }
}
