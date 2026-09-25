package auviotre.enigmatic.legacy.packets.client;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ForgerCrystalPacket implements CustomPacketPayload {
    public static final Type<ForgerCrystalPacket> TYPE = new Type<>(EnigmaticLegacy.location("forger_crystal_client"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ForgerCrystalPacket> STREAM_CODEC = CustomPacketPayload.codec(ForgerCrystalPacket::write, ForgerCrystalPacket::new);
    public final double x, y, z;
    public final ItemStack totem;

    public ForgerCrystalPacket(RegistryFriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.totem = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
    }

    public ForgerCrystalPacket(Vec3 vec3, ItemStack stack) {
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
