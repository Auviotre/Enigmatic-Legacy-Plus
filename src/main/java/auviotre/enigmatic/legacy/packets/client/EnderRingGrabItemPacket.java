package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record EnderRingGrabItemPacket(ItemStack stack) implements CustomPacketPayload {
    public static final Type<EnderRingGrabItemPacket> TYPE = new Type<>(EnigmaticLegacy.location("ender_ring_grabbed_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnderRingGrabItemPacket> STREAM_CODEC = StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC, EnderRingGrabItemPacket::stack, EnderRingGrabItemPacket::new);

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
