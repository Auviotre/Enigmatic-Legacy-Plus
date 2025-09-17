package auviotre.enigmatic.legacy.packets.toClient;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PermanentDeathPacket() implements CustomPacketPayload {
    public static final Type<PermanentDeathPacket> TYPE = new Type<>(EnigmaticLegacy.location("permanent_death_display"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PermanentDeathPacket> STREAM_CODEC = CustomPacketPayload.codec((packet, buf) -> {
    }, (buf) -> new PermanentDeathPacket());

    public static void handle(PermanentDeathPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> handle(packet)).exceptionally(exception -> {
            context.disconnect(Component.translatable("message.enigmaticlegacy.networking.failed"));
            return null;
        });
    }

    public static void handle(final PermanentDeathPacket packet) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            EnigmaticLegacy.PROXY.displayPermanentDeathScreen();
        }
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
