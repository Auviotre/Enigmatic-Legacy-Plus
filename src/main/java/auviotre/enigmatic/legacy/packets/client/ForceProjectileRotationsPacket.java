package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ForceProjectileRotationsPacket implements CustomPacketPayload {
    public static final Type<ForceProjectileRotationsPacket> TYPE = new Type<>(EnigmaticLegacy.location("force_projectile_rotation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ForceProjectileRotationsPacket> STREAM_CODEC = CustomPacketPayload.codec(ForceProjectileRotationsPacket::write, ForceProjectileRotationsPacket::new);

    public final int entityID;
    public final double motionX, motionY, motionZ, posX, posY, posZ;
    public final float rotationYaw;
    public final float rotationPitch;

    public ForceProjectileRotationsPacket(FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.rotationYaw = buf.readFloat();
        this.rotationPitch = buf.readFloat();
        this.motionX = buf.readDouble();
        this.motionY = buf.readDouble();
        this.motionZ = buf.readDouble();
        this.posX = buf.readDouble();
        this.posY = buf.readDouble();
        this.posZ = buf.readDouble();
    }

    public ForceProjectileRotationsPacket(int entityID, float rotationYaw, float rotationPitch, double vecX, double vecY, double vecZ, double posX, double posY, double posZ) {
        this.entityID = entityID;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.motionX = vecX;
        this.motionY = vecY;
        this.motionZ = vecZ;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeFloat(this.rotationYaw);
        buf.writeFloat(this.rotationPitch);
        buf.writeDouble(this.motionX);
        buf.writeDouble(this.motionY);
        buf.writeDouble(this.motionZ);
        buf.writeDouble(this.posX);
        buf.writeDouble(this.posY);
        buf.writeDouble(this.posZ);
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
