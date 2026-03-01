package auviotre.enigmatic.legacy.packets.server;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EmptyLeftClickPacket(boolean clicked) implements CustomPacketPayload {
    public static final Type<EmptyLeftClickPacket> TYPE = new Type<>(EnigmaticLegacy.location("empty_left_click"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EmptyLeftClickPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, EmptyLeftClickPacket::clicked, EmptyLeftClickPacket::new);

    public static void handle(EmptyLeftClickPacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                final Player player = context.player();
                float base = (float) (0.5F * player.getAttributeValue(Attributes.ATTACK_DAMAGE));
                float damage = 1.0F + (float)player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * base;
                double delX = -Mth.sin(player.getYRot() * (float) Math.PI / 180F);
                double delY = Mth.cos(player.getYRot() * (float) Math.PI / 180F);
                DamageSource source = player.damageSources().playerAttack(player);

                if (player.level() instanceof ServerLevel server) {
                    for(LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, player.getWeaponItem().getSweepHitBox(player, player))) {
                        double entityReachSq = Mth.square(player.entityInteractionRange());
                        if (target != player && !player.isAlliedTo(target) && (!(target instanceof ArmorStand) || !((ArmorStand) target).isMarker()) && player.distanceToSqr(target) < entityReachSq) {
                            target.knockback(0.4, -delX, -delY);
                            target.hurt(source, EnchantmentHelper.modifyDamage(server, player.getWeaponItem(), player, source, damage));
                            EnchantmentHelper.doPostAttackEffects(server, target, source);
                        }
                    }
                    server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                    player.sweepAttack();
                    player.causeFoodExhaustion(0.1F);
                    player.getCooldowns().addCooldown(EnigmaticItems.THUNDER_SCROLL.get(), (int) (16 / player.getAttributeValue(Attributes.ATTACK_SPEED)));
                }
            });
        }
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
