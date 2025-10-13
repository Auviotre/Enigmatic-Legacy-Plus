package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
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
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class EyeOfNebula extends SpellstoneItem {
    public static ModConfigSpec.IntValue magicBoost;
    public static ModConfigSpec.IntValue magicResistance;
    public static ModConfigSpec.IntValue dodgeProbability;
    public static ModConfigSpec.IntValue attackEmpower;
    public static ModConfigSpec.DoubleValue vulnerabilityModifier;

    public EyeOfNebula() {
        super(defaultSingleProperties().rarity(Rarity.RARE), 0xFF0BDDB8);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.eye_of_nebula").push("spellstone.eyeOfNebula");
        magicBoost = builder.defineInRange("magicBoost", 40, 0, 100);
        magicResistance = builder.defineInRange("magicResistance", 65, 0, 100);
        dodgeProbability = builder.defineInRange("dodgeProbability", 15, 0, 100);
        attackEmpower = builder.defineInRange("attackEmpower", 150, 0, 1000);
        vulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 2.0, 1.0, 20.0);
        builder.pop(2);
    }

    private static void dodgeTeleport(ServerLevel level, Entity target, LivingEntity porter) {
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
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula1", ChatFormatting.GOLD, magicBoost.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula2", ChatFormatting.GOLD, magicResistance.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula3", ChatFormatting.GOLD, dodgeProbability.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula5", ChatFormatting.GOLD, attackEmpower.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula7");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    @OnlyIn(Dist.CLIENT)
    public void addTuneTooltip(List<Component> list) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.eyeOfNebula2", ChatFormatting.GOLD, (magicResistance.get() + 5) / 2 + "%");
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

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity victim = event.getEntity();
            if (ISpellstone.get(victim).is(EnigmaticItems.EYE_OF_NEBULA)) {
                if (victim.getRandom().nextFloat() < 0.01F * dodgeProbability.get() && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    Entity causer = event.getSource().getEntity();
                    if (causer != null && causer.level() instanceof ServerLevel level)
                        dodgeTeleport(level, causer, victim);
                    victim.invulnerableTime = 20;
                    event.setCanceled(true);
                }
            }

            if (event.getSource().getEntity() instanceof LivingEntity attacker && ISpellstone.get(attacker).is(EnigmaticItems.EYE_OF_NEBULA)) {
                if (attacker.getData(EnigmaticAttachments.ENIGMATIC_DATA).getNebulaPower()) {
                    event.setAmount(event.getAmount() * (1.0F + 0.01F * attackEmpower.get()));
                    if (attacker instanceof Player player) player.magicCrit(victim);
                    attacker.getData(EnigmaticAttachments.ENIGMATIC_DATA).setNebulaPower(false);
                }

                if (event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
                    event.setAmount(event.getAmount() * (1.0F + 0.01F * magicBoost.get()));
                }
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            if (ISpellstone.get(victim).is(EnigmaticItems.EYE_OF_NEBULA)) {
                if (event.getSource().is(Tags.DamageTypes.IS_MAGIC))
                    event.setNewDamage(event.getNewDamage() * (1.0F - 0.01F * magicResistance.get()));
                if (victim.isInWater())
                    event.setNewDamage(event.getNewDamage() * (float) vulnerabilityModifier.getAsDouble());
            }
        }

        @SubscribeEvent
        private static void onTeleport(EntityTeleportEvent.@NotNull EnderPearl event) {
            ItemStack stack = ISpellstone.get(event.getPlayer());
            if (stack.is(EnigmaticItems.EYE_OF_NEBULA) || stack.is(EnigmaticItems.THE_CUBE)) {
                event.setAttackDamage(0);
            }
        }
    }
}
