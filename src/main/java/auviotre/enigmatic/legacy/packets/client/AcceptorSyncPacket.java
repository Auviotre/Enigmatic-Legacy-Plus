package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AcceptorSyncPacket implements CustomPacketPayload {
    public static final Type<AcceptorSyncPacket> TYPE = new Type<>(EnigmaticLegacy.location("acceptor_client"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AcceptorSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(AcceptorSyncPacket::write, AcceptorSyncPacket::new);
    public final int entityID;
    public final int size;
    public final Set<UUID> set;

    public AcceptorSyncPacket(RegistryFriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.size = buf.readInt();
        this.set = new HashSet<>();
        for (int i = 0; i < this.size; i++) {
            this.set.add(buf.readUUID());
        }
    }

    public AcceptorSyncPacket(int entityID, Set<UUID> set) {
        this.entityID = entityID;
        this.set = set;
        this.size = set.size();
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeInt(this.size);
        for (UUID uuid : this.set) {
            buf.writeUUID(uuid);
        }
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
