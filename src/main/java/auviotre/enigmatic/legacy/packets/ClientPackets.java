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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class ClientPackets {
    public static void handle(final EnderRingGrabItemPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player != null) player.containerMenu.setCarried(packet.stack().copy());
    }

    public static void handle(final EnigmaticDataSyncPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.getData(EnigmaticAttachments.ENIGMATIC_DATA).load(player.registryAccess(), packet.dataTag());
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
        if (packet == null) return;
        if (level != null) {
            EnigmaticLegacy.PROXY.displayPermanentDeathScreen();
        }
    }


    public static void handle(final PlayerMotionPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.setDeltaMovement(packet.movement());
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
            case "charm" -> EnigmaticItems.UNWITNESSED_AMULET.toStack();
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
        }
    }

    public static void handle(PlayQuotePacket packet) {
        Quote quote = Quote.getByID(packet.quoteId());
        quote.playClient(packet.delay(), packet.isDeath());
    }

    public static void handle(SpellstoneSwordPacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        int amount;
        switch (packet.mode) {
            case 20:
                amount = 24;
                level.playSound(player, packet.x, packet.y, packet.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, (float) (1.2F + 0.2F * Math.random()));
                for (int i = 0; i < amount * 2; i++) {
                    double theta = Math.PI / amount * i;
                    level.addParticle(ParticleTypes.FLAME, packet.x + 3 * Math.sin(theta), packet.y + 0.1 * Math.random(), packet.z + 3 * Math.cos(theta), -0.4 * Math.sin(theta), Math.random() * 0.01, -0.4 * Math.cos(theta));
                }
                for (int i = 0; i < amount; i++) {
                    double theta = Math.PI / amount / 2 * i + Math.random() * 0.1;
                    level.addParticle(ParticleTypes.LAVA, packet.x, packet.y, packet.z, 0.1 * Math.sin(theta), Math.random() * 0.05, 0.1 * Math.cos(theta));
                }
                break;
            case 21:
                amount = 8;
                level.playSound(player, packet.x, packet.y, packet.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, (float) (0.7F + 0.2F * Math.random()));
                for (int i = 0; i < amount * 2; i++) {
                    double theta = Math.PI / amount * i + Math.random() * 0.1;
                    level.addParticle(ParticleTypes.LAVA, packet.x + Math.random() * 2 - 1, packet.y + Math.random() * 2 - 1, packet.z + Math.random() * 2 - 1, 0.1 * Math.sin(theta), Math.random() * 0.05, 0.1 * Math.cos(theta));
                }
                for (int i = 0; i < amount; i++) {
                    level.addParticle(ParticleTypes.EXPLOSION, packet.x + Math.random() * 3 - 1.5, packet.y + Math.random() * 3 - 1.5, packet.z + Math.random() * 3 - 1.5, 0, 0, 0);
                }
                for (int i = 0; i < amount * 2; i++) {
                    level.addParticle(ParticleTypes.SMOKE, packet.x + Math.random() * 3 - 1.5, packet.y + Math.random() * 3 - 1.5, packet.z + Math.random() * 3 - 1.5, 0, 0, 0);
                }
                break;
            case 22:
                Vec3 look = new Vec3(packet.dx, packet.dy, packet.dz).scale(2);
                if (player.tickCount % 10 == 0) level.playSound(player, packet.x, packet.y, packet.z, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, (float) (1.2F + 0.2F * Math.random()));
                for (int i = 0; i < 5; i++) {
                    Vec3 add = look.add(Math.random() * 1.8 - 0.9, Math.random() * 0.6 - 0.3, Math.random() * 1.8 - 0.9).normalize().scale(0.4);
                    level.addParticle(ParticleTypes.FLAME, packet.x, packet.y - 0.32, packet.z, add.x, add.y, add.z);
                    level.addParticle(ParticleTypes.SMOKE, packet.x, packet.y - 0.32, packet.z, add.x, add.y, add.z);
                }
                break;
            case 40:
                amount = 24;
                for (int i = 0; i < amount * 2; i++) {
                    double range = Math.random() * 0.2 + 0.2;
                    level.addParticle(ParticleTypes.SNOWFLAKE, packet.x, packet.y + 0.2 * Math.random(), packet.z, range * Math.sin(Math.PI / amount * i), 0.05, range * Math.cos(Math.PI / amount * i));
                }
                ItemParticleOption option = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Blocks.BLUE_ICE));
                for (int i = 0; i < amount * 3; i++) {
                    level.addParticle(option, packet.x, packet.y, packet.z, Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1);
                }
                break;
            case 50:
                amount = 12;
                for (int i = 0; i < amount * 2; i++) {
                    level.addParticle(ParticleTypes.HAPPY_VILLAGER, packet.x + Math.random() - 0.5, packet.y + 2 * Math.random(), packet.z + Math.random() - 0.5, 0.0, Math.random() * 0.25, 0.0);
                }
                break;
            case 80:
                amount = 10;
                for (int i = 0; i < amount * 2; i++) {
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, packet.x + Math.random() - 0.5, packet.y + 2 * Math.random(), packet.z + Math.random() - 0.5, 0.0, Math.random() * 0.05, 0.0);
                    level.addParticle(ParticleTypes.SOUL, packet.x + Math.random() - 0.5, packet.y + 2 * Math.random(), packet.z + Math.random() - 0.5, 0.0, Math.random() * 0.05, 0.0);
                }
                break;
            case 100:
                amount = 16;
                level.playLocalSound(packet.x, packet.y, packet.z, SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F, true);
                level.playLocalSound(packet.x, packet.y, packet.z, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1.2F, 1.25F, true);
                for (int i = 0; i < amount * 2; i++) {
                    double range = Math.random() * 0.3 + 0.1;
                    level.addParticle(ParticleTypes.SQUID_INK, packet.x, packet.y + 0.2 * Math.random(), packet.z, range * Math.sin(Math.PI / amount * i), 0.0, range * Math.cos(Math.PI / amount * i));
                }
                for (int i = 0; i < amount * 2; i++) {
                    level.addParticle(ParticleTypes.SQUID_INK, packet.x + 3 * Math.sin(Math.PI / amount * i), packet.y + 0.1 * Math.random(), packet.z + 3 * Math.cos(Math.PI / amount * i), 0.0, Math.random() * 0.3, 0.0);
                }
                break;
            case 101:
                amount = 12;
                for (int i = 0; i < amount * 3; i++) {
                    level.addParticle(ParticleTypes.SQUID_INK, packet.x + 2 * Math.random() - 1, packet.y, packet.z + 2 * Math.random() - 1, 0, Math.random(), 0);
                }
                for (int i = 0; i < amount; i++) {
                    level.addParticle(ParticleTypes.SQUID_INK, packet.x, packet.y + 0.2 * Math.random() + 0.5, packet.z, 0.4 * Math.sin(Math.PI / 2 / amount * i), 0.0, 0.4 * Math.cos(Math.PI / 2 / amount * i));
                }
        }
    }
}
