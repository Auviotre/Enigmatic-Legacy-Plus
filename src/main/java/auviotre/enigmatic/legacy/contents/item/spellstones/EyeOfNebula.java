package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class EyeOfNebula extends SpellstoneItem {
    public EyeOfNebula() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
        NeoForge.EVENT_BUS.register(this);

    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebulaSkill");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula1", ChatFormatting.GOLD, CONFIG.SPELLSTONES.magicBoost.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula2", ChatFormatting.GOLD, CONFIG.SPELLSTONES.magicResistance.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula3", ChatFormatting.GOLD, CONFIG.SPELLSTONES.dodgeProbability.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula5", ChatFormatting.GOLD, CONFIG.SPELLSTONES.attackEmpower.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula7");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    public int getCooldown() {
        return 60;
    }

    public void triggerActiveAbility(ServerLevel level, @NotNull ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) return;
        LivingEntity target = EnigmaticHandler.getObservedEntity(player, player.level(), 3.0F, 32);
        if (target == null) return;
        double hOffset = player.getBbWidth() / 6;
        double yOffset = player.getBbHeight() / 4;
        float width = target.getBbWidth();
        Vec3 delta = target.position().subtract(player.position());
        Vec3 pos = target.position().add(delta.normalize().scale(1.2 * width + 1));
        pos = new Vec3(pos.x, target.getY() + 0.25, pos.z);
        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
        level.sendParticles(ParticleTypes.WITCH, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);

        delta = target.position().add(0, target.getBbHeight() / 2, 0).subtract(pos).normalize();
        double pitch = Math.asin(delta.y);
        double yaw = Math.atan2(delta.z, delta.x);
        pitch = Math.toDegrees(pitch) * 0.95;
        yaw = Math.toDegrees(yaw);
        yaw -= 90.0;
        player.teleportTo(level, pos.x, pos.y, pos.z, (float) yaw, (float) pitch);

        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
        level.sendParticles(ParticleTypes.WITCH, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
        super.triggerActiveAbility(level, player, stack);
        player.getData(EnigmaticAttachments.ENIGMATIC_DATA).setNebulaPower(true);
    }

    private void dodgeTeleport(ServerLevel level, Entity target, LivingEntity porter) {
        RandomSource random = porter.getRandom();
        double hOffset = porter.getBbWidth() / 6;
        double yOffset = porter.getBbHeight() / 4;
        level.playSound(null, porter.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + random.nextFloat() * 0.2F);
        level.sendParticles(ParticleTypes.WITCH, porter.getX(), porter.getY(0.5), porter.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
        float width = target.getBbWidth();
        Vec3 delta = target.position().subtract(porter.position());
        Vec3 pos = target.position().add(delta.normalize().scale(1.4 * width + 2.2));
        pos = new Vec3(pos.x, target.getY() + 0.25, pos.z);

        delta = target.position().add(0, target.getBbHeight() / 2, 0).subtract(pos).normalize();
        double pitch = Math.asin(delta.y);
        double yaw = Math.atan2(delta.z, delta.x);
        pitch = Math.toDegrees(pitch) * 0.95;
        yaw = Math.toDegrees(yaw);
        yaw -= 90.0;
        porter.teleportTo(level, pos.x, pos.y, pos.z, Collections.emptySet(), (float) yaw, (float) pitch);
        level.playSound(null, porter.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + random.nextFloat() * 0.2F);
        level.sendParticles(ParticleTypes.WITCH, porter.getX(), porter.getY(0.5), porter.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
    }

    @SubscribeEvent
    public void onDamage(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (ISpellstone.get(victim).is(this)) {
            if (victim.getRandom().nextFloat() < 0.01F * CONFIG.SPELLSTONES.dodgeProbability.get() && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                Entity causer = event.getSource().getEntity();
                if (causer != null && causer.level() instanceof ServerLevel level) dodgeTeleport(level, causer, victim);
                victim.invulnerableTime = 20;
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent.Pre event) {
        LivingEntity victim = event.getEntity();
        if (ISpellstone.get(victim).is(this)) {
            if (event.getSource().is(Tags.DamageTypes.IS_MAGIC))
                event.setNewDamage(event.getNewDamage() * (1.0F - 0.01F * CONFIG.SPELLSTONES.magicResistance.get()));
            if (victim.isInWater())
                event.setNewDamage(event.getNewDamage() * (float) CONFIG.SPELLSTONES.EONVulnerabilityModifier.getAsDouble());
        }

        if (event.getSource().getEntity() instanceof LivingEntity attacker && ISpellstone.get(attacker).is(this)) {
            if (attacker.getData(EnigmaticAttachments.ENIGMATIC_DATA).getNebulaPower()) {
                event.setNewDamage(event.getNewDamage() * (1.0F + 0.01F * CONFIG.SPELLSTONES.attackEmpower.get()));
                if (attacker instanceof Player player) player.magicCrit(victim);
                attacker.getData(EnigmaticAttachments.ENIGMATIC_DATA).setNebulaPower(false);
            }

            if (event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
                event.setNewDamage(event.getNewDamage() * (1.0F + 0.01F * CONFIG.SPELLSTONES.magicBoost.get()));
            }
        }
    }

    @SubscribeEvent
    public void onTeleport(EntityTeleportEvent.EnderPearl event) {
        if (ISpellstone.get(event.getPlayer()).is(this)) {
            event.setAttackDamage(0);
        }
    }
}
