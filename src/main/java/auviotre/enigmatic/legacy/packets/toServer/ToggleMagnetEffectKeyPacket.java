package auviotre.enigmatic.legacy.packets.toServer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.packets.toClient.EnigmaticDataSyncPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleMagnetEffectKeyPacket() implements CustomPacketPayload {
    public static final Type<ToggleMagnetEffectKeyPacket> TYPE = new Type<>(EnigmaticLegacy.location("toggle_magnet_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleMagnetEffectKeyPacket> STREAM_CODEC = CustomPacketPayload.codec((packet, o) -> {
    }, (buf) -> new ToggleMagnetEffectKeyPacket());

    public static void handle(ToggleMagnetEffectKeyPacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                final Player player = context.player();
                if (player instanceof ServerPlayer serverPlayer) {
                    EnigmaticData data = serverPlayer.getData(EnigmaticAttachments.ENIGMATIC_DATA);
                    data.toggleMagnetRingEffect();
                    PacketDistributor.sendToPlayer(serverPlayer, new EnigmaticDataSyncPacket(data.save()));
                }
            });
        }
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
