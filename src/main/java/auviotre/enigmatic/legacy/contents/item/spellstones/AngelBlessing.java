package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.packets.client.ForceProjectileRotationsPacket;
import auviotre.enigmatic.legacy.packets.client.PlayerMotionPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class AngelBlessing extends SpellstoneItem {
    public static ModConfigSpec.DoubleValue accelerationModifier;
    public static ModConfigSpec.DoubleValue accelerationModifierElytra;
    public static ModConfigSpec.IntValue deflectChance;
    public static ModConfigSpec.DoubleValue vulnerabilityModifier;
    public static ModConfigSpec.IntValue cooldown;

    public AngelBlessing() {
        super(defaultSingleProperties().rarity(Rarity.RARE), 0xFFB2DAFF);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.angel_blessing").push("spellstone.angelBlessing");
        accelerationModifier = builder.defineInRange("accelerationModifier", 1.0, 0, 256.0);
        accelerationModifierElytra = builder.defineInRange("accelerationModifierElytra", 0.6, 0, 256.0);
        deflectChance = builder.defineInRange("deflectChance", 40, 0, 100);
        vulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 2.0, 1.0, 20.0);
        cooldown = builder.defineInRange("cooldown", 30, 10, 100);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.angelBlessingSkill");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.angelBlessing1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.angelBlessing2", ChatFormatting.GOLD, deflectChance.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.angelBlessing3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.angelBlessing4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.angelBlessing5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.angelBlessing6");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    @OnlyIn(Dist.CLIENT)
    public void addTuneTooltip(List<Component> list) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.angelBlessing1");
    }

    public int getCooldown() {
        return cooldown.get();
    }

    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) return;
        if (player.getAbilities().flying) return;

        Vec3 finalMotion = getVec3(player);

        PacketDistributor.sendToPlayer(player, new PlayerMotionPacket(finalMotion));
        player.setDeltaMovement(finalMotion.x, finalMotion.y, finalMotion.z);
        level.playSound(null, player.blockPosition(), EnigmaticSounds.ACCELERATE.get(), SoundSource.PLAYERS, 1.0F, 0.6F + player.getRandom().nextFloat() * 0.1F);
        super.triggerActiveAbility(level, player, stack);
    }

    private static @NotNull Vec3 getVec3(ServerPlayer player) {
        Vec3 accelerationVec = player.getLookAngle();
        Vec3 motionVec = player.getDeltaMovement();

        if (player.isFallFlying()) {
            accelerationVec = accelerationVec.scale(accelerationModifierElytra.get());
            accelerationVec = accelerationVec.scale(1 / (Math.max(0.15D, motionVec.length()) * 2.25D));
        } else {
            accelerationVec = accelerationVec.scale(accelerationModifier.get());
        }

        return motionVec.add(motionVec.x + accelerationVec.x,motionVec.y + accelerationVec.y, motionVec.z + accelerationVec.z);
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        LivingEntity living = context.entity();

        if (!(living.level() instanceof ServerLevel level)) return;

        AABB box = new AABB(living.getX() - 4, living.getY() - 4, living.getZ() - 4, living.getX() + 4, living.getY() + 4, living.getZ() + 4);
        List<AbstractHurtingProjectile> projectileEntities = living.level().getEntitiesOfClass(AbstractHurtingProjectile.class, box);
        List<AbstractArrow> arrowEntities = living.level().getEntitiesOfClass(AbstractArrow.class, box);
        List<ThrowableItemProjectile> potionEntities = living.level().getEntitiesOfClass(ThrowableItemProjectile.class, box);

        for (AbstractHurtingProjectile entity : projectileEntities) this.redirect(level, living, entity);

        for (AbstractArrow entity : arrowEntities) this.redirect(level, living, entity);

        for (ThrowableItemProjectile entity : potionEntities) this.redirect(level, living, entity);
    }


    public void redirect(ServerLevel level, LivingEntity bearer, Entity redirected) {
        if (redirected instanceof WitherSkull) return;
        if ((redirected instanceof AbstractArrow arrow && arrow.getOwner() == bearer)
                || (redirected instanceof ThrowableItemProjectile projectile && projectile.getOwner() == bearer)) {
            if (redirected.getTags().contains("AB_ACCELERATED")) {
                return;
            }

            if (redirected.getTags().stream().anyMatch(tag -> tag.startsWith("AB_DEFLECTED")))
                return;

            if (redirected instanceof ThrownTrident trident) {
                if (trident.clientSideReturnTridentTickCount > 0) return;
            }

            if (redirected.addTag("AB_ACCELERATED")) {
                redirected.setDeltaMovement(redirected.getDeltaMovement().x * 1.75D, redirected.getDeltaMovement().y * 1.75D, redirected.getDeltaMovement().z * 1.75D);
                Vec3 movement = redirected.getDeltaMovement();
                List<ServerPlayer> players = level.getPlayers(player -> player.distanceToSqr(redirected) < 16.0);
                ForceProjectileRotationsPacket packet = new ForceProjectileRotationsPacket(redirected.getId(), redirected.getYRot(), redirected.getXRot(), movement.x, movement.y, movement.z, redirected.getX(), redirected.getY(), redirected.getZ());
                for (ServerPlayer player : players) {
                    PacketDistributor.sendToPlayer(player, packet);
                }
            }
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.ANGEL_BLESSING)) {
                if (event.getSource().is(EnigmaticTags.DamageTypes.ANGEL_BLESSING_IMMUNE_TO))
                    event.setCanceled(true);
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.ANGEL_BLESSING)) {
                DamageSource source = event.getSource();
                if (source.is(EnigmaticTags.DamageTypes.ANGEL_BLESSING_VULNERABLE_TO)) {
                    event.setNewDamage((float) (event.getNewDamage() * vulnerabilityModifier.getAsDouble()));
                }
            }
        }
    }
}
