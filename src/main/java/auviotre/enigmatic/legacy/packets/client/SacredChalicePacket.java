package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SacredChalicePacket implements CustomPacketPayload {
    public static final Type<SacredChalicePacket> TYPE = new Type<>(EnigmaticLegacy.location("sacred_chalice_client"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SacredChalicePacket> STREAM_CODEC = CustomPacketPayload.codec(SacredChalicePacket::write, SacredChalicePacket::new);
    public final double x, y, z;
    public final Vec3 center;

    public SacredChalicePacket(RegistryFriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.center = buf.readVec3();
    }

    public SacredChalicePacket(AABB box) {
        this.x = box.getXsize();
        this.y = box.getYsize();
        this.z = box.getZsize();
        this.center = box.getCenter();
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeVec3(this.center);
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
