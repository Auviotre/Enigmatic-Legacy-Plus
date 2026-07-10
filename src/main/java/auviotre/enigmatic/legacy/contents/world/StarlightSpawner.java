package auviotre.enigmatic.legacy.contents.world;

import auviotre.enigmatic.legacy.contents.entity.projectile.StarlightMeteor;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticEntities;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biomes;

public class StarlightSpawner implements CustomSpawner {
    private static final int RANGE = 36;
    private int nextTick;
    private float offsetX;
    private float offsetZ;

    public StarlightSpawner(ServerLevel level) {
        this.offsetX = (level.getRandom().nextFloat() - 0.5F) * 3.2F;
        this.offsetZ = (level.getRandom().nextFloat() - 0.5F) * 3.2F;
    }

    public int tick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
        if (!spawnFriendlies) return 0;
        if (!Level.END.equals(level.dimension())) return 0;
        RandomSource random = level.random;
        --this.nextTick;
        if (this.nextTick > 0) return 0;
        this.nextTick = this.nextTick + 500 + random.nextInt(250);
        this.offsetX += (level.getRandom().nextFloat() - 0.5F) * 2.4F;
        this.offsetZ += (level.getRandom().nextFloat() - 0.5F) * 2.4F;
        if (Math.abs(this.offsetX) > 2.0F) this.offsetX *= 0.9F;
        if (Math.abs(this.offsetZ) > 2.0F) this.offsetZ *= 0.9F;
        int count = 0;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) continue;
            BlockPos blockPos = player.blockPosition();
            if (Biomes.THE_END.equals(level.getBiome(blockPos))) continue;
            boolean flag = EnigmaticHandler.hasCurio(player, EnigmaticItems.STARLIGHT_RING);
            int range = flag ? RANGE * 3 / 5 : RANGE;
            if (flag) this.nextTick = Math.max(this.nextTick - random.nextInt(60), 200 + random.nextInt(100));
            for (int i = 0; i < random.nextInt(flag ? 4 : 3) ; i++) {
                BlockPos spawnPos;
                do {
                    spawnPos = blockPos.east(random.nextInt(-range, range + 1)).south(random.nextInt(-range, range + 1));
                } while (spawnPos.closerToCenterThan(blockPos.getBottomCenter(), range * 0.2F));
                spawnPos = spawnPos.atY(Math.max(blockPos.getY() + random.nextInt(40, 80), 128));

                var type = EnigmaticEntities.STARLIGHT_METEOR.get();
                StarlightMeteor starlightMeteor = type.create(level);
                if (NaturalSpawner.isValidEmptySpawnBlock(level, spawnPos, level.getBlockState(spawnPos), level.getFluidState(blockPos), type)) {
                    if (starlightMeteor != null) {
                        starlightMeteor.moveTo(spawnPos, 0.0F, 0.0F);
                        starlightMeteor.setDeltaMovement(offsetX, 1.6F, offsetZ);
                        level.addFreshEntityWithPassengers(starlightMeteor);
                        ++count;
                    }
                }
            }
        }
        return count;
    }
}
