package auviotre.enigmatic.legacy.mixin.entity;

import auviotre.enigmatic.legacy.contents.item.rings.CursedRing;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PhantomSpawner.class)
public class MixinPhantomSpawner {
    private int ticksUntilSpawn = 0;

    @Inject(at = @At("RETURN"), method = "tick", cancellable = true)
    private void onTick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies, CallbackInfoReturnable<Integer> info) {
        if (spawnEnemies && level.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA)) {
            var random = level.random;
            --this.ticksUntilSpawn;
            if (this.ticksUntilSpawn <= 0) {
                this.ticksUntilSpawn += (60 + random.nextInt(60)) * 20;
                if (level.getSkyDarken() < 5 && level.dimensionType().hasSkyLight()) return;
                int i = 0;
                for (ServerPlayer player : level.players()) {
                    if (!player.isSpectator() && !player.isCreative()) {
                        BlockPos pos = player.blockPosition();
                        if (!level.dimensionType().hasSkyLight() || pos.getY() >= level.getSeaLevel() && level.canSeeSky(pos)) {
                            DifficultyInstance difficulty = level.getCurrentDifficultyAt(pos);
                            if (difficulty.isHarderThan(random.nextFloat() * 3.0F)) {
                                ServerStatsCounter stats = player.getStats();
                                int ticksSinceRest = Mth.clamp(stats.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);

                                if (EnigmaticHandler.isTheWorthyOne(player) && CursedRing.forTheWorthyMode.get() && CursedRing.enableInsomnia.get()) {
                                    if (random.nextInt(ticksSinceRest) <= 72000) {
                                        BlockPos blockPos = pos.above(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
                                        BlockState blockstate = level.getBlockState(blockPos);
                                        FluidState fluidstate = level.getFluidState(blockPos);
                                        if (NaturalSpawner.isValidEmptySpawnBlock(level, blockPos, blockstate, fluidstate, EntityType.PHANTOM)) {
                                            SpawnGroupData ilivingentitydata = null;
                                            int l = 1 + random.nextInt(difficulty.getDifficulty().getId() + 1);
                                            for (int i1 = 0; i1 < l; ++i1) {
                                                Phantom phantom = EntityType.PHANTOM.create(level);
                                                if (phantom != null) {
                                                    phantom.moveTo(blockPos, 0.0F, 0.0F);
                                                    EnigmaticHandler.setCurseBoosted(phantom, true, player);
                                                    ilivingentitydata = phantom.finalizeSpawn(level, difficulty, MobSpawnType.NATURAL, ilivingentitydata);
                                                    level.addFreshEntityWithPassengers(phantom);
                                                }
                                            }
                                            i += l;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                info.setReturnValue(info.getReturnValue() + i);
            }
        }
    }
}
