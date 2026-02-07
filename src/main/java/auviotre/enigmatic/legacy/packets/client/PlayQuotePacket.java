package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PlayQuotePacket(int quoteId, int delay)
        implements CustomPacketPayload {

    public static final Type<PlayQuotePacket> TYPE =
            new Type<>(EnigmaticLegacy.location("play_quote"));

    public static final StreamCodec<FriendlyByteBuf, PlayQuotePacket> STREAM_CODEC =
            StreamCodec.of(
                    (FriendlyByteBuf buf, PlayQuotePacket pkt) -> {
                        buf.writeInt(pkt.quoteId());
                        buf.writeInt(pkt.delay());
                    },
                    buf -> new PlayQuotePacket(buf.readInt(), buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}