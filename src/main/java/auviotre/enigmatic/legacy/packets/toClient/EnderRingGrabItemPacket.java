package auviotre.enigmatic.legacy.packets.toClient;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EnderRingGrabItemPacket(ItemStack stack) implements CustomPacketPayload {
    public static final Type<EnderRingGrabItemPacket> TYPE = new Type<>(EnigmaticLegacy.location("ender_ring_grabbed_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnderRingGrabItemPacket> STREAM_CODEC = StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC, EnderRingGrabItemPacket::stack, EnderRingGrabItemPacket::new);

    public static void handle(EnderRingGrabItemPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> handle(packet)).exceptionally(exception -> {
            context.disconnect(Component.translatable("message.enigmaticlegacy.networking.failed"));
            return null;
        });
    }

    public static void handle(final EnderRingGrabItemPacket packet) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) player.containerMenu.setCarried(packet.stack().copy());
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
