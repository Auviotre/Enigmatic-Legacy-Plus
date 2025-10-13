package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public class ChaosDescendingPacket implements CustomPacketPayload {
    public static final Type<ChaosDescendingPacket> TYPE = new Type<>(EnigmaticLegacy.location("chaos_descending"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChaosDescendingPacket> STREAM_CODEC = CustomPacketPayload.codec(ChaosDescendingPacket::write, ChaosDescendingPacket::new);
    public final double x, y, z;
    public final boolean targeted;

    public ChaosDescendingPacket(RegistryFriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.targeted = buf.readBoolean();
    }

    public ChaosDescendingPacket(Vec3 vec3, boolean targeted) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        this.targeted = targeted;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeBoolean(this.targeted);
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
