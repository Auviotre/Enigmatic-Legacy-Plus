package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.scrolls.CursedScroll;
import auviotre.enigmatic.legacy.contents.item.spellstones.ForgottenIce;
import auviotre.enigmatic.legacy.contents.item.tools.InfernalShield;
import auviotre.enigmatic.legacy.contents.item.tools.TotemOfMalice;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.packets.client.TotemOfMalicePacket;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements ILivingEntityExtension {
    @Shadow
    protected ItemStack useItem;

    public MixinLivingEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand hand);

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public abstract boolean removeEffectsCuredBy(EffectCure cure);

    @Shadow
    public abstract float getMaxHealth();

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickMix(CallbackInfo info) {
        if (this.level().isClientSide && ForgottenIce.freezingBoost.get() && this.isFullyFrozen() && Math.random() > 0.25D) {
            this.level().addParticle(ParticleTypes.SNOWFLAKE, this.getRandomX(0.6D), this.getRandomY() + 0.1D, this.getRandomZ(0.6D), 0.0D, -0.05D, 0.0D);
        }
        if (!this.canFreeze() && this.getTicksFrozen() > 0) this.setTicksFrozen(0);
    }

    @Inject(method = "canFreeze", at = @At("RETURN"), cancellable = true)
    public void canFreezeMix(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(info.getReturnValue() && !ISpellstone.get(this.self()).is(EnigmaticItems.FORGOTTEN_ICE));
        // && EnchantmentHelper.getEnchantmentLevel(EnigmaticAddonEnchantments.FROST_PROTECTION, this.self()) < 16
        // !this.self().hasEffect(EnigmaticAddonEffects.FROZEN_HEART_EFFECT) &&
    }

    @Inject(method = "isDamageSourceBlocked", at = @At("HEAD"), cancellable = true)
    private void onDamageSourceBlocking(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        if (EnigmaticHandler.onDamageSourceBlocking(this.self(), this.useItem, source)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void isBlockingMix(CallbackInfoReturnable<Boolean> info) {
        if (this.isUsingItem() && this.useItem.getItem() instanceof InfernalShield) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
    private void checkMalice(@NotNull DamageSource source, CallbackInfoReturnable<Boolean> info) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        if (!EnigmaticHandler.isTheCursedOne(this.self())) return;
        ItemStack totem = null;
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = getItemInHand(hand);
            if (stack.is(EnigmaticItems.TOTEM_OF_MALICE) && CommonHooks.onLivingUseTotem(this.self(), source, stack, hand)) {
                totem = stack;
                break;
            }
        }
        ItemStack curio = EnigmaticHandler.getCurio(this.self(), EnigmaticItems.TOTEM_OF_MALICE);
        if (!curio.isEmpty()) totem = curio;
        if (totem != null && TotemOfMalice.getDurability(totem) > 0) {
            TotemOfMalice.hurtAndBreak(totem, this.self());
            totem = totem.copy();
            if (this.self() instanceof ServerPlayer player) {
                player.awardStat(Stats.ITEM_USED.get(EnigmaticItems.TOTEM_OF_MALICE.get()), 1);
                CriteriaTriggers.USED_TOTEM.trigger(player, totem);
                player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
            }

            float damage = this.getMaxHealth() * (0.8F + CursedScroll.getItemCurseLevel(totem) * 0.3F);
            List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8));

            for (LivingEntity entity : entities) {
                if (entity == this.self()) continue;
                entity.knockback(0.45F, entity.getX() - this.getX(), entity.getZ() - this.getZ());
                entity.hurt(EnigmaticDamageTypes.source(level(), EnigmaticDamageTypes.EVIL_CURSE, this), damage);
                entity.invulnerableTime = 0;
                if (entity.level() instanceof ServerLevel level) {
                    double hOffset = entity.getBbWidth() / 6;
                    double yOffset = entity.getBbHeight() / 4;
                    level.sendParticles(ParticleTypes.WITCH, entity.getX(), entity.getY(0.5), entity.getZ(), 12, hOffset, yOffset, hOffset, 0.01);
                }
            }

            this.setHealth(Math.max(1.0F, this.getMaxHealth() - 1.0F));
            this.removeEffectsCuredBy(EffectCures.PROTECTED_BY_TOTEM);
            if (this.isOnFire()) this.clearFire();
            if (this.isFreezing()) this.setTicksFrozen(0);
            if (level() instanceof ServerLevel level)
                PacketDistributor.sendToPlayersNear(level, null, this.getX(), this.getY(), this.getZ(), 32, new TotemOfMalicePacket(this.position(), totem));

            info.setReturnValue(true);
        }
    }
}
