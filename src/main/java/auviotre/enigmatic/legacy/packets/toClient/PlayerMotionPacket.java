package auviotre.enigmatic.legacy.packets.toClient;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PlayerMotionPacket implements CustomPacketPayload {
    public static final Type<PlayerMotionPacket> TYPE = new Type<>(EnigmaticLegacy.location("player_motion"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerMotionPacket> STREAM_CODEC = CustomPacketPayload.codec(PlayerMotionPacket::write, PlayerMotionPacket::new);
    private final Vec3 movement;

    public PlayerMotionPacket(FriendlyByteBuf buf) {
        this.movement = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public PlayerMotionPacket(Vec3 movement) {
        this.movement = movement;
    }

    public static void handle(PlayerMotionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> handle(packet)).exceptionally(exception -> {
            context.disconnect(Component.translatable("message.enigmaticlegacy.networking.failed"));
            return null;
        });
    }

    public static void handle(final PlayerMotionPacket packet) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.setDeltaMovement(packet.movement);
        }
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(this.movement.x);
        buf.writeDouble(this.movement.y);
        buf.writeDouble(this.movement.z);
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
