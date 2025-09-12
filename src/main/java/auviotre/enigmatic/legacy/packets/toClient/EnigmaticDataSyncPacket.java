package auviotre.enigmatic.legacy.packets.toClient;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EnigmaticDataSyncPacket implements CustomPacketPayload {
    public static final Type<EnigmaticDataSyncPacket> TYPE = new Type<>(EnigmaticLegacy.location("enigmatic_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnigmaticDataSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(EnigmaticDataSyncPacket::write, EnigmaticDataSyncPacket::new);
    private final CompoundTag dataTag;

    public EnigmaticDataSyncPacket(FriendlyByteBuf buf) {
        this.dataTag = buf.readNbt();
    }

    public EnigmaticDataSyncPacket(CompoundTag dataTag) {
        this.dataTag = dataTag;
    }

    public static void handle(EnigmaticDataSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> handle(packet)).exceptionally(exception -> {
            context.disconnect(Component.translatable("message.enigmaticlegacy.networking.failed"));
            return null;
        });
    }

    public static void handle(final EnigmaticDataSyncPacket packet) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.getData(EnigmaticAttachments.ENIGMATIC_DATA).load(packet.dataTag);
        }
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(this.dataTag);
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
