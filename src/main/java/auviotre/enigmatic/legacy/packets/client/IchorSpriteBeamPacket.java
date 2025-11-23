package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public class IchorSpriteBeamPacket implements CustomPacketPayload {
    public static final Type<IchorSpriteBeamPacket> TYPE = new Type<>(EnigmaticLegacy.location("ichor_sprite_beam"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IchorSpriteBeamPacket> STREAM_CODEC = CustomPacketPayload.codec(IchorSpriteBeamPacket::write, IchorSpriteBeamPacket::new);
    public final Vec3 self, tarPos;

    public IchorSpriteBeamPacket(RegistryFriendlyByteBuf buf) {
        this.self = buf.readVec3();
        this.tarPos = buf.readVec3();
    }

    public IchorSpriteBeamPacket(Vec3 pos, Vec3 tarPos) {
        this.self = pos;
        this.tarPos = tarPos;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVec3(this.self);
        buf.writeVec3(this.tarPos);
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
