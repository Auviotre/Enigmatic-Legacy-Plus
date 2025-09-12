package auviotre.enigmatic.legacy.packets.toClient;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ForceProjectileRotationsPacket implements CustomPacketPayload {
    public static final Type<ForceProjectileRotationsPacket> TYPE = new Type<>(EnigmaticLegacy.location("force_projectile_rotation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ForceProjectileRotationsPacket> STREAM_CODEC = CustomPacketPayload.codec(ForceProjectileRotationsPacket::write, ForceProjectileRotationsPacket::new);

    private final int entityID;
    private final double motionX, motionY, motionZ, posX, posY, posZ;
    private final float rotationYaw;
    private final float rotationPitch;

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

    public static void handle(ForceProjectileRotationsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> handle(packet)).exceptionally(exception -> {
            context.disconnect(Component.translatable("message.enigmaticlegacy.networking.failed"));
            return null;
        });
    }

    public static void handle(final ForceProjectileRotationsPacket packet) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            Entity entity = level.getEntity(packet.entityID);
            if (entity != null) {
                entity.addTag("enigmaticlegacy.redirected");
                entity.moveTo(packet.posX, packet.posY, packet.posZ);
                entity.setDeltaMovement(packet.motionX, packet.motionY, packet.motionZ);
                entity.setYRot(packet.rotationYaw);
                entity.yRotO = packet.rotationYaw;
                entity.setXRot(packet.rotationPitch);
                entity.xRotO = packet.rotationPitch;
            }
        }
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
