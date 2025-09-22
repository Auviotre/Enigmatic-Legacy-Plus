package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Optional;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class ForgottenIce extends SpellstoneItem {
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

    public ForgottenIce() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
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

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.setTicksFrozen(0);
        if (entity.level() instanceof ServerLevel level) {
            EFFECT.apply(level, 2, null, entity, entity.position());
        }
    }

    public int getCooldown() {
        return 240;
    }

    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) return;


        super.triggerActiveAbility(level, player, stack);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            DamageSource source = event.getSource();
            if (!ISpellstone.get(entity).is(EnigmaticItems.FORGOTTEN_ICE) || event.isCanceled()) return;
            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
            if (source.type().effects().equals(DamageEffects.FREEZING) || source.is(DamageTypeTags.BURN_FROM_STEPPING)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            if (ISpellstone.get(victim).is(EnigmaticItems.FORGOTTEN_ICE)) {
                if (event.getSource().type().effects().equals(DamageEffects.BURNING)) {
                    event.setNewDamage(event.getNewDamage() * 2.5F);
                }
                if (event.getSource().is(EnigmaticTags.DamageTypes.FORGOTTEN_ICE_RESISTANT_TO)) {
                    event.setNewDamage(event.getNewDamage() * 0.7F);
                }
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
                        attacker.hurt(EnigmaticDamageTypes.source(victim.level(), DamageTypes.FREEZE, victim), (float) CONFIG.SPELLSTONES.damageFeedback.getAsDouble() / 2);
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
