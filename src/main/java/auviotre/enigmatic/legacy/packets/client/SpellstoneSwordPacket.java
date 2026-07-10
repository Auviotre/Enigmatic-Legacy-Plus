package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public class SpellstoneSwordPacket implements CustomPacketPayload {
    public static final Type<SpellstoneSwordPacket> TYPE = new Type<>(EnigmaticLegacy.location("spellstone_sword"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellstoneSwordPacket> STREAM_CODEC = CustomPacketPayload.codec(SpellstoneSwordPacket::write, SpellstoneSwordPacket::new);
    public final double x, y, z;
    public final double dx, dy, dz;
    public final int mode;

    public SpellstoneSwordPacket(RegistryFriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.dx = buf.readDouble();
        this.dy = buf.readDouble();
        this.dz = buf.readDouble();
        this.mode = buf.readInt();
    }

    public SpellstoneSwordPacket(Vec3 vec3, int mode) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        this.dx = 0;
        this.dy = 0;
        this.dz = 0;
        this.mode = mode;
    }

    public SpellstoneSwordPacket(Vec3 vec3, Vec3 delta, int mode) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        this.dx = delta.x;
        this.dy = delta.y;
        this.dz = delta.z;
        this.mode = mode;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeDouble(this.dx);
        buf.writeDouble(this.dy);
        buf.writeDouble(this.dz);
        buf.writeInt(this.mode);
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
