package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.contents.block.DimensionalAnchor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public class MixinServerPlayer {
    @Inject(method = "findRespawnAndUseSpawnBlock", at = @At("HEAD"), cancellable = true)
    private static void findMix(@NotNull ServerLevel level, BlockPos pos, float angle, boolean forced, boolean keepInventory, CallbackInfoReturnable<Optional<ServerPlayer.RespawnPosAngle>> info) {
        BlockState blockstate = level.getBlockState(pos);
        if (blockstate.getBlock() instanceof DimensionalAnchor && (forced || blockstate.getValue(DimensionalAnchor.CHARGE) > 0) && DimensionalAnchor.canSetSpawn(level)) {
            Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, level, pos);
            if (!forced && !keepInventory && optional.isPresent()) {
                level.setBlock(pos, blockstate.setValue(DimensionalAnchor.CHARGE, blockstate.getValue(DimensionalAnchor.CHARGE) - 1), Block.UPDATE_ALL);
            }
            info.setReturnValue(optional.map((vec3) -> ServerPlayer.RespawnPosAngle.of(vec3, pos)));
        }
    }
}
