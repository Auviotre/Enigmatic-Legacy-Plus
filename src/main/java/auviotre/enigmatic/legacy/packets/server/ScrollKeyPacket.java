package auviotre.enigmatic.legacy.packets.server;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.scrolls.XpScroll;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ScrollKeyPacket() implements CustomPacketPayload {
    public static final Type<ScrollKeyPacket> TYPE = new Type<>(EnigmaticLegacy.location("trigger_scroll"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ScrollKeyPacket> STREAM_CODEC = CustomPacketPayload.codec((packet, buf) -> {
    }, (buf) -> new ScrollKeyPacket());

    public static void handle(ScrollKeyPacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                final Player player = context.player();
                if (player instanceof ServerPlayer serverPlayer) {
                    if (EnigmaticHandler.hasCurio(serverPlayer, EnigmaticItems.XP_SCROLL)) {
                        ItemStack curio = EnigmaticHandler.getCurio(serverPlayer, EnigmaticItems.XP_SCROLL);
                        XpScroll.trigger(player.level(), curio, serverPlayer, InteractionHand.MAIN_HAND, false);
                    } else if (EnigmaticHandler.hasCurio(serverPlayer, EnigmaticItems.CURSED_XP_SCROLL)) {
                        ItemStack curio = EnigmaticHandler.getCurio(serverPlayer, EnigmaticItems.CURSED_XP_SCROLL);
                        XpScroll.trigger(player.level(), curio, serverPlayer, InteractionHand.MAIN_HAND, false);
                    }
                }
            });
        }
    }


    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
