package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EnigmaticDataSyncPacket(CompoundTag dataTag) implements CustomPacketPayload {
    public static final Type<EnigmaticDataSyncPacket> TYPE = new Type<>(EnigmaticLegacy.location("enigmatic_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnigmaticDataSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(EnigmaticDataSyncPacket::write, EnigmaticDataSyncPacket::new);

    public EnigmaticDataSyncPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(this.dataTag);
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
