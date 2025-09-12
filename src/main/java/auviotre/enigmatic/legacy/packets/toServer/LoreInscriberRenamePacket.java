package auviotre.enigmatic.legacy.packets.toServer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.gui.LoreInscriberMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LoreInscriberRenamePacket(String name) implements CustomPacketPayload {
    public static final Type<LoreInscriberRenamePacket> TYPE = new Type<>(EnigmaticLegacy.location("lore_inscriber_rename"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LoreInscriberRenamePacket> STREAM_CODEC = CustomPacketPayload.codec((packet, buf) -> {
        buf.writeUtf(packet.name);
    }, (buf) -> new LoreInscriberRenamePacket(buf.readUtf()));

    public static void handle(LoreInscriberRenamePacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                final Player player = context.player();
                if (player.containerMenu instanceof LoreInscriberMenu menu)
                    menu.setItemName(packet.name());
            });
        }
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
