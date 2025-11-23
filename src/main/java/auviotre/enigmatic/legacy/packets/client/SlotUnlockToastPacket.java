package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SlotUnlockToastPacket(String identifier) implements CustomPacketPayload {
    public static final Type<SlotUnlockToastPacket> TYPE = new Type<>(EnigmaticLegacy.location("slot_unlock_toast"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlotUnlockToastPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SlotUnlockToastPacket::identifier,
            SlotUnlockToastPacket::new);

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
