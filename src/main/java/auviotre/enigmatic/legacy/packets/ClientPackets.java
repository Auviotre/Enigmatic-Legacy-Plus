package auviotre.enigmatic.legacy.packets;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.packets.client.*;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClientPackets {
    public static void handle(final EnderRingGrabItemPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player != null) player.containerMenu.setCarried(packet.stack().copy());
    }

    public static void handle(final EnigmaticDataSyncPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.getData(EnigmaticAttachments.ENIGMATIC_DATA).load(packet.dataTag);
        }
    }

    public static void handle(final ForceProjectileRotationsPacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            Entity entity = level.getEntity(packet.entityID);
            if (entity != null) {
                entity.addTag("enigmaticlegacy.redirected");
                entity.moveTo(packet.posX, packet.posY, packet.posZ);
                entity.setDeltaMovement(packet.motionX, packet.motionY, packet.motionZ);
                entity.setYRot(packet.rotationYaw);
                entity.yRotO = packet.rotationYaw;
                entity.setXRot(packet.rotationPitch);
                entity.xRotO = packet.rotationPitch;
            }
        }
    }

    public static void handle(final PermanentDeathPacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            EnigmaticLegacy.PROXY.displayPermanentDeathScreen();
        }
    }


    public static void handle(final PlayerMotionPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.setDeltaMovement(packet.movement);
        }
    }

    public static void handle(final TotemOfMalicePacket packet) {
        Minecraft instance = Minecraft.getInstance();
        Player player = instance.player;
        if (player != null) {
            instance.particleEngine.createTrackingEmitter(player, ParticleTypes.WITCH, 40);
            instance.gameRenderer.displayItemActivation(packet.totem);
        }
        if (instance.level != null) {
            instance.level.playLocalSound(packet.x, packet.y, packet.z, SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 1.0F, 1.0F, false);
        }
    }
}
