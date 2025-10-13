package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TheCubeRevivePacket implements CustomPacketPayload {
    public static final Type<TheCubeRevivePacket> TYPE = new Type<>(EnigmaticLegacy.location("the_cube_revive"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TheCubeRevivePacket> STREAM_CODEC = CustomPacketPayload.codec(TheCubeRevivePacket::write, TheCubeRevivePacket::new);
    public final double x, y, z;
    public final ItemStack cube;

    public TheCubeRevivePacket(RegistryFriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.cube = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
    }

    public TheCubeRevivePacket(Vec3 vec3, ItemStack stack) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        this.cube = stack;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, this.cube);
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
