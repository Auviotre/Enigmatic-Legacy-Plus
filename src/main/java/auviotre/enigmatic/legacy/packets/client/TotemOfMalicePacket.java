package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TotemOfMalicePacket implements CustomPacketPayload {
    public static final Type<TotemOfMalicePacket> TYPE = new Type<>(EnigmaticLegacy.location("totem_of_malice_client"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TotemOfMalicePacket> STREAM_CODEC = CustomPacketPayload.codec(TotemOfMalicePacket::write, TotemOfMalicePacket::new);
    public final double x, y, z;
    public final ItemStack totem;

    public TotemOfMalicePacket(RegistryFriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.totem = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
    }

    public TotemOfMalicePacket(Vec3 vec3, ItemStack stack) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        this.totem = stack;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, this.totem);
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
