package auviotre.enigmatic.legacy.packets.server;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SpellstoneKeyPacket() implements CustomPacketPayload {
    public static final Type<SpellstoneKeyPacket> TYPE = new Type<>(EnigmaticLegacy.location("trigger_spellstone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellstoneKeyPacket> STREAM_CODEC = CustomPacketPayload.codec((packet, buf) -> {
    }, (buf) -> new SpellstoneKeyPacket());

    public static void handle(SpellstoneKeyPacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                final Player player = context.player();
                if (player instanceof ServerPlayer serverPlayer && player.level() instanceof ServerLevel level) {
                    ItemStack stack = ISpellstone.get(serverPlayer);
                    if (stack.isEmpty() || !(stack.getItem() instanceof ISpellstone spellstone)) return;
                    spellstone.triggerActiveAbility(level, serverPlayer, stack);
                }
            });
        }
    }


    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
