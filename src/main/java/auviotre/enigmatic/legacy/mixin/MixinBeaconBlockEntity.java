package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class MixinBeaconBlockEntity {

    @Inject(method = "applyEffects", at = @At("TAIL"))
    private static void applyEffectsMix(Level level, BlockPos pos, int beaconLevel, Holder<MobEffect> primaryEffect, Holder<MobEffect> secondaryEffect, CallbackInfo info) {
        if (!level.isClientSide()) {
            double range = beaconLevel * 10 + 10;
            AABB box = new AABB(pos).inflate(range).expandTowards(0.0, level.getHeight(), 0.0);
            List<Player> list = level.getEntitiesOfClass(Player.class, box);
            for (Player player : list) {
                if (EnigmaticHandler.hasCurio(player, EnigmaticItems.HEAVEN_SCROLL) || EnigmaticHandler.hasCurio(player, EnigmaticItems.FABULOUS_SCROLL)) {
                    player.getData(EnigmaticAttachments.ENIGMATIC_DATA).setInBeaconRangeTick(100);
                }
            }
        }
    }
}
