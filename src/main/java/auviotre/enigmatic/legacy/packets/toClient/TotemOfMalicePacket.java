package auviotre.enigmatic.legacy.packets.toClient;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TotemOfMalicePacket implements CustomPacketPayload {
    public static final Type<TotemOfMalicePacket> TYPE = new Type<>(EnigmaticLegacy.location("totem_of_malice_client"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TotemOfMalicePacket> STREAM_CODEC = CustomPacketPayload.codec(TotemOfMalicePacket::write, TotemOfMalicePacket::new);
    private final double x;
    private final double y;
    private final double z;
    private final ItemStack totem;

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
    public static void handle(TotemOfMalicePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> handle(packet)).exceptionally(exception -> {
            context.disconnect(Component.translatable("message.enigmaticlegacy.networking.failed"));
            return null;
        });
    }

    public static void handle(final TotemOfMalicePacket packet) {
        Minecraft instance = Minecraft.getInstance();
        LocalPlayer player = instance.player;
        if (player != null) {
            instance.particleEngine.createTrackingEmitter(player, ParticleTypes.WITCH, 40);
            instance.gameRenderer.displayItemActivation(packet.totem);
        }
        if (instance.level != null) {
            instance.level.playLocalSound(packet.x, packet.y, packet.z, SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 1.0F, 1.0F, false);
        }
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
