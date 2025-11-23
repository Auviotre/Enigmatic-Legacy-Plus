package auviotre.enigmatic.legacy.packets;

import auviotre.enigmatic.legacy.packets.client.*;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    private static void handle(final IPayloadContext context, Runnable handler) {
        context.enqueueWork(handler).exceptionally(exception -> {
            context.disconnect(Component.translatable("message.enigmaticlegacy.networking.failed"));
            return null;
        });
    }

    public void handle(final EnderRingGrabItemPacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }

    public void handle(final EnigmaticDataSyncPacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }

    public void handle(final ForceProjectileRotationsPacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }

    public void handle(final PermanentDeathPacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }

    public void handle(final PlayerMotionPacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }

    public void handle(final TotemOfMalicePacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }

    public void handle(final SoulCompassUpdatePacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }

    public void handle(final IchorSpriteBeamPacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }

    public void handle(final ChaosDescendingPacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }

    public void handle(final TheCubeRevivePacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }

    public void handle(final SlotUnlockToastPacket data, final IPayloadContext ctx) {
        handle(ctx, () -> ClientPackets.handle(data));
    }
}
