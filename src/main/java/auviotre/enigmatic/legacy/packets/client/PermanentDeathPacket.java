package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PermanentDeathPacket() implements CustomPacketPayload {
    public static final Type<PermanentDeathPacket> TYPE = new Type<>(EnigmaticLegacy.location("permanent_death_display"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PermanentDeathPacket> STREAM_CODEC = CustomPacketPayload.codec((packet, buf) -> {
    }, (buf) -> new PermanentDeathPacket());

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
