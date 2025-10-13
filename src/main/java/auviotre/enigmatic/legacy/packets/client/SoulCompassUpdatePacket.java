package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SoulCompassUpdatePacket(boolean noValid, BlockPos pos) implements CustomPacketPayload {
    public static final Type<SoulCompassUpdatePacket> TYPE = new Type<>(EnigmaticLegacy.location("soul_compass_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulCompassUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SoulCompassUpdatePacket::noValid,
            BlockPos.STREAM_CODEC, SoulCompassUpdatePacket::pos,
            SoulCompassUpdatePacket::new);

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
