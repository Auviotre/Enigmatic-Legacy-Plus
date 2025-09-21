package auviotre.enigmatic.legacy.handlers;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.amulets.EldritchAmulet;
import auviotre.enigmatic.legacy.contents.item.amulets.EnigmaticAmulet;
import auviotre.enigmatic.legacy.packets.toClient.EnigmaticDataSyncPacket;
import auviotre.enigmatic.legacy.packets.toClient.ForceProjectileRotationsPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

@Mod(value = EnigmaticLegacy.MODID)
@EventBusSubscriber(modid = EnigmaticLegacy.MODID)
public class EnigmaticEventHandler {
    @SubscribeEvent
    public static void onProjectileImpact(@NotNull ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult result) {
            if (result.getEntity() instanceof ServerPlayer player) {
                Entity projectile = event.getEntity();
                if (projectile instanceof Projectile arrow) {
                    if (arrow.getOwner() == player) {
                        for (String tag : arrow.getTags()) {
                            if (tag.startsWith("AB_DEFLECTED")) {
                                try {
                                    int time = Integer.parseInt(tag.split(":")[1]);
                                    if (arrow.tickCount - time < 10)
                                        // If we cancel the event here it gets stuck in the infinite loop
                                        return;
                                } catch (Exception ex) {
                                    ex.fillInStackTrace();
                                }
                            }
                        }
                    }
                }

                float chance = 0.0F;

                if (ISpellstone.get(player).is(EnigmaticItems.ANGEL_BLESSING)) {
                    chance += 0.01F * CONFIG.SPELLSTONES.deflectChance.get();
                }

//                    if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.THE_CUBE)) {
//                        trigger = true;
//                        chance += 0.35;
//                    }

                if (EnigmaticAmulet.hasColor(player, EnigmaticAmulet.AmuletColor.VIOLET)) {
                    chance += 0.15F;
                }

                if (chance > 0.0F && player.getRandom().nextFloat() <= chance) {
                    event.setCanceled(true);

                    projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0D));
                    projectile.yRotO = projectile.getYRot() + 180.0F;
                    projectile.setYRot(projectile.getYRot() + 180.0F);

                    if (projectile instanceof AbstractArrow arrow) {
                        if (!(arrow instanceof ThrownTrident)) arrow.setOwner(player);
                        arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }

                    projectile.getTags().removeIf(tag -> tag.startsWith("AB_DEFLECTED"));
                    projectile.addTag("AB_DEFLECTED:" + projectile.tickCount);

                    Vec3 movement = projectile.getDeltaMovement();
                    PacketDistributor.sendToPlayer(player, new ForceProjectileRotationsPacket(projectile.getId(), projectile.getYRot(), projectile.getXRot(), movement.x, movement.y, movement.z, projectile.getX(), projectile.getY(), projectile.getZ()));
                    player.level().playSound(null, player.blockPosition(), EnigmaticSounds.DEFLECT.get(), SoundSource.PLAYERS, 1.0F, 0.95F + player.getRandom().nextFloat() * 0.1F);
                }
            }
        }
    }

    @SubscribeEvent
    private static void entityJoinWorld(@NotNull EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CompoundTag tag = player.getData(EnigmaticAttachments.ENIGMATIC_DATA).save();
            PacketDistributor.sendToPlayer(player, new EnigmaticDataSyncPacket(tag));
        }
    }

    @SubscribeEvent
    private static void onClone(PlayerEvent.@NotNull Clone event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getOriginal() instanceof ServerPlayer original) {
            EnigmaticData data = original.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            data.setFireImmunityTimer(0);
            data.setFireImmunityTimer(0);
            PacketDistributor.sendToPlayer(player, new EnigmaticDataSyncPacket(data.save()));

            if (event.isWasDeath()) EldritchAmulet.reclaimInventory(original, player);
        }
    }

    @SubscribeEvent
    private static void onGrantAdvancement(@NotNull AdvancementEvent.AdvancementEarnEvent event) {
        String id = event.getAdvancement().id().toString();
        Player player = event.getEntity();
        if (id.equals(EnigmaticLegacy.MODID + ":main/discover_spellstone")) {
            if (EnigmaticHandler.unlockSpecialSlot("spellstone", player)) {
                player.displayClientMessage(Component.translatable("message.enigmaticlegacy.slot_unlocked", Component.translatable("curios.identifier.spellstone").withStyle(ChatFormatting.YELLOW)), true);
            }
        } else if (id.equals(EnigmaticLegacy.MODID + ":main/discover_scroll")) {
            if (EnigmaticHandler.unlockSpecialSlot("scroll", player)) {
                player.displayClientMessage(Component.translatable("message.enigmaticlegacy.slot_unlocked", Component.translatable("curios.identifier.scroll").withStyle(ChatFormatting.YELLOW)), true);
            }
        }
    }
}
