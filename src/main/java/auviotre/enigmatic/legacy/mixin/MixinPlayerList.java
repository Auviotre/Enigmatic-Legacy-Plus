package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class MixinPlayerList {
    @Inject(method = "respawn", at = @At("RETURN"))
    private void respawn(ServerPlayer player, boolean keepInventory, Entity.RemovalReason reason, CallbackInfoReturnable<ServerPlayer> info) {
        DimensionTransition dimensiontransition = player.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING);
        ServerLevel level = dimensiontransition.newLevel();
        if (!keepInventory) {
            BlockPos blockpos = BlockPos.containing(dimensiontransition.pos());
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(EnigmaticBlocks.DIMENSIONAL_ANCHOR.get())) {
                info.getReturnValue().connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 1.0F, 1.0F, level.getRandom().nextLong()));
            }
        }
    }
}
