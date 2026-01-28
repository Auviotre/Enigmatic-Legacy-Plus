package auviotre.enigmatic.legacy.contents.attribute;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.packets.client.ForceProjectileRotationsPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class ProjectileDeflectAttribute extends PercentageAttribute {
    public ProjectileDeflectAttribute() {
        super("attribute.name.projectile_deflect", 0.0, 0.0, 1.0);
        this.setSyncable(true);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onProjectileImpact(@NotNull ProjectileImpactEvent event) {
            if (event.getRayTraceResult() instanceof EntityHitResult result) {
                if (result.getEntity() instanceof ServerPlayer player) {
                    Entity projectile = event.getEntity();
                    if (projectile instanceof Projectile arrow) {
                        if (arrow.getOwner() == player) {
                            for (String tag : arrow.getTags()) {
                                if (tag.startsWith("ProjectileDeflected")) {
                                    try {
                                        int time = Integer.parseInt(tag.split(":")[1]);
                                        if (arrow.tickCount - time < 10) return;
                                    } catch (Exception ex) {
                                        ex.fillInStackTrace();
                                    }
                                }
                            }
                        }
                    }

                    AttributeInstance attribute = player.getAttribute(EnigmaticAttributes.PROJECTILE_DEFLECT);
                    float chance = attribute == null ? 0.0F : (float) attribute.getValue();
                    if (chance > 0.0F && player.getRandom().nextFloat() <= chance) {
                        event.setCanceled(true);

                        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0D));
                        projectile.yRotO = projectile.getYRot() + 180.0F;
                        projectile.setYRot(projectile.getYRot() + 180.0F);
                        if (projectile instanceof Projectile arrow) {
                            arrow.setOwner(player);
                        }

                        if (projectile instanceof AbstractArrow arrow) {
                            if (!(arrow instanceof ThrownTrident)) arrow.setOwner(player);
                            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        projectile.getTags().removeIf(tag -> tag.startsWith("ProjectileDeflected"));
                        projectile.addTag("ProjectileDeflected:" + projectile.tickCount);

                        Vec3 movement = projectile.getDeltaMovement();
                        PacketDistributor.sendToPlayer(player, new ForceProjectileRotationsPacket(projectile.getId(), projectile.getYRot(), projectile.getXRot(), movement.x, movement.y, movement.z, projectile.getX(), projectile.getY(), projectile.getZ()));
                        player.level().playSound(null, player.blockPosition(), EnigmaticSounds.DEFLECT.get(), SoundSource.PLAYERS, 1.0F, 0.95F + player.getRandom().nextFloat() * 0.1F);
                    }
                }
            }
        }
    }
}
