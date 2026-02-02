package auviotre.enigmatic.legacy.packets;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.client.Quote;
import auviotre.enigmatic.legacy.client.screen.toast.SlotUnlockedToast;
import auviotre.enigmatic.legacy.packets.client.*;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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

    public static void handle(final SoulCompassUpdatePacket packet) {
        EnigmaticItems.SOUL_COMPASS.get().setNearestCrystal(packet.noValid() ? null : packet.pos());
    }

    public static void handle(final IchorSpriteBeamPacket packet) {
        Vec3 self = packet.self;
        Vec3 tarPos = packet.tarPos;
        double dist = self.distanceTo(tarPos);
        Vec3 delta = tarPos.subtract(self).normalize();
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            for (double i = 0.1; i < dist; i += 0.2 * level.random.nextDouble() + 0.2) {
                level.addParticle(EnigmaticParticles.ICHOR.get(),
                        self.x + delta.x * i, self.y + delta.y * i, self.z + delta.z * i,
                        delta.x * 0.05, delta.y * 0.05, delta.z * 0.05
                );
            }
        }
    }

    public static void handle(final TheCubeRevivePacket packet) {
        Minecraft instance = Minecraft.getInstance();
        Player player = instance.player;
        if (player != null) {
            instance.particleEngine.createTrackingEmitter(player, ParticleTypes.WITCH, 30);
            instance.gameRenderer.displayItemActivation(packet.cube);
        }
        if (instance.level != null) {
            instance.level.playLocalSound(packet.x, packet.y, packet.z, SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 1.0F, 1.0F, false);
        }
    }

    public static void handle(final SlotUnlockToastPacket packet) {
        ToastComponent toasts = Minecraft.getInstance().getToasts();
        ItemStack stack = switch (packet.identifier()) {
            case "ring" -> EnigmaticItems.IRON_RING.toStack();
            case "scroll" -> EnigmaticItems.BLANK_SCROLL.toStack();
            case "spellstone" -> EnigmaticItems.SPELLCORE.toStack();
            case "charm" -> EnigmaticItems.ENIGMATIC_AMULET.toStack();
            default -> ItemStack.EMPTY;
        };
        toasts.addToast(new SlotUnlockedToast(stack, packet.identifier()));
    }

    public static void handle(final ChaosDescendingPacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        int amount = 36;
        level.playLocalSound(packet.x, packet.y, packet.z, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.75F, 0.25F, true);
        level.playLocalSound(packet.x, packet.y, packet.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.2F, 1.25F, true);
        level.addParticle(ParticleTypes.EXPLOSION_EMITTER, packet.x, packet.y, packet.z, 0, 0, 0);
        if (packet.targeted) {
            for (int i = 0; i < amount; i++) {
                double theta = Math.random() * 2 * Math.PI;
                double phi = (Math.random() - 0.5D) * Math.PI;
                double dx = Math.cos(theta) * Math.cos(phi) * 0.2D;
                double dy = Math.sin(phi) * 0.2D - 0.05D;
                double dz = Math.sin(theta) * Math.cos(phi) * 0.2D;
                level.addParticle(ParticleTypes.SQUID_INK, packet.x, packet.y, packet.z, dx, dy, dz);
            }
            for (int i = 0; i < amount; i++) {
                double theta = Math.random() * 2 * Math.PI;
                double phi = (Math.random() - 0.5D) * Math.PI;
                double dx = Math.cos(theta) * Math.cos(phi) * 0.2D;
                double dy = Math.sin(phi) * 0.2D - 0.05D;
                double dz = Math.sin(theta) * Math.cos(phi) * 0.2D;
                level.addParticle(ParticleTypes.END_ROD, packet.x, packet.y, packet.z, dx, dy, dz);
            }
        } else {
            for (int i = 0; i < amount * 2; i++) {
                double range = Math.random() * 0.3 + 0.1;
                level.addParticle(ParticleTypes.SQUID_INK, packet.x, packet.y + 0.2 * Math.random() + 0.1, packet.z, range * Math.sin(Math.PI / amount * i), 0.0, range * Math.cos(Math.PI / amount * i));
            }
            for (int i = 0; i < amount * 2; i++) {
                double range = Math.random() * 0.3 + 0.1;
                level.addParticle(ParticleTypes.END_ROD, packet.x, packet.y + 0.2 * Math.random() + 0.1, packet.z, range * Math.sin(Math.PI / amount * i), 0.0, range * Math.cos(Math.PI / amount * i));
            }
            for (int i = 0; i < amount * 2; i++) {
                double range = Math.random() * 4 + 1;
                level.addParticle(ParticleTypes.SQUID_INK, packet.x + range * Math.sin(Math.PI / amount * i), packet.y + 0.1 * Math.random(), packet.z + range * Math.cos(Math.PI / amount * i), 0.0, Math.random(), 0.0);
            }
            for (int i = 0; i < amount * 2; i++) {
                double range = Math.random() * 4 + 1;
                level.addParticle(ParticleTypes.END_ROD, packet.x + range * Math.sin(Math.PI / amount * i), packet.y + 0.1 * Math.random(), packet.z + range * Math.cos(Math.PI / amount * i), 0.0, Math.random(), 0.0);
            }
//        for (int i = 0; i < amount * 2; i++) {
//            double range = Math.random() * 4 + 1;
//            level.addParticle(EnigmaticAddonParticles.ABYSS_CHAOS, packet.x + range * Math.sin(Math.PI / amount * i), packet.y + 0.1 * Math.random(), packet.z + range * Math.cos(Math.PI / amount * i), 0.0, Math.random(), 0.0);
//        }
        }
    }

    public static void handle(PlayQuotePacket packet) {
        Quote quote = Quote.getByID(packet.quoteId());
        EnigmaticLegacy.LOGGER.info("CLIENT: received PlayQuotePacket {}", packet.quoteId());

//        EnigmaticLegacy.LOGGER.info(String.valueOf(quote.getSubtitles()));
//        EnigmaticLegacy.LOGGER.info(String.valueOf(quote.getName()));
        quote.play(packet.delay());

        System.out.println("Quote SHOULD DISPLAY NOW");
        System.out.println("player = " + Minecraft.getInstance().player);
        System.out.println("level = " + Minecraft.getInstance().level);
    }
}
