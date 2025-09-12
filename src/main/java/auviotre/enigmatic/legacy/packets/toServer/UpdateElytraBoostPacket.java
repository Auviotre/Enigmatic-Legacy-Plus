package auviotre.enigmatic.legacy.packets.toServer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.packets.toClient.EnigmaticDataSyncPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateElytraBoostPacket(boolean boosting) implements CustomPacketPayload {
    public static final Type<UpdateElytraBoostPacket> TYPE = new Type<>(EnigmaticLegacy.location("update_elytra_boost"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateElytraBoostPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, UpdateElytraBoostPacket::boosting, UpdateElytraBoostPacket::new);

    public static void handle(UpdateElytraBoostPacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                final Player player = context.player();
                if (player instanceof ServerPlayer serverPlayer) {
                    if (packet.boosting) player.level().playSound(null, player.getX(), player.getY(), player.getZ(), EnigmaticSounds.ACCELERATE, SoundSource.AMBIENT, 3.0F, 1.0F);
                    EnigmaticData data = serverPlayer.getData(EnigmaticAttachments.ENIGMATIC_DATA);
                    boolean wasBoosting = data.isElytraBoosting();
                    data.setElytraBoosting(packet.boosting);
                    if (wasBoosting != packet.boosting) PacketDistributor.sendToPlayer(serverPlayer, new EnigmaticDataSyncPacket(data.save()));
                }
            });
        }
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
