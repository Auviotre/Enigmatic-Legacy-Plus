package auviotre.enigmatic.legacy.packets.server;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.packets.client.EnderRingGrabItemPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EnderRingKeyPacket(ItemStack carried) implements CustomPacketPayload {
    public static final Type<EnderRingKeyPacket> TYPE = new Type<>(EnigmaticLegacy.location("press_ender_ring"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnderRingKeyPacket> STREAM_CODEC = StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC, EnderRingKeyPacket::carried, EnderRingKeyPacket::new);

    public static void handle(EnderRingKeyPacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                final Player player = context.player();
                if (EnigmaticHandler.isTheCursedOne(player) || EnigmaticHandler.hasCurio(player, EnigmaticItems.ENDER_RING)) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        ItemStack stack = serverPlayer.isCreative() ? packet.carried() : serverPlayer.containerMenu.getCarried();
                        serverPlayer.containerMenu.setCarried(ItemStack.EMPTY);
                        serverPlayer.openMenu(new SimpleMenuProvider((id, inventory, play) -> ChestMenu.threeRows(id, inventory, player.getEnderChestInventory()), Component.translatable("container.enderchest")));
                        if (!stack.isEmpty()) {
                            serverPlayer.containerMenu.setCarried(stack);
                            PacketDistributor.sendToPlayer(serverPlayer, new EnderRingGrabItemPacket(stack));
                        }
                    }
                    player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
                }
            });
        }
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
