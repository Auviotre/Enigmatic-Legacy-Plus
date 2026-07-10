package auviotre.enigmatic.legacy.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class InfernalCinderSack extends Block {
    public InfernalCinderSack() {
        super(Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(MapColor.COLOR_GRAY));
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos.above())) return;
        level.addParticle(ParticleTypes.ASH,
                pos.getX() + 0.15F + 0.7F * random.nextFloat(), pos.getY() + 1.1F, pos.getZ() + 0.15F + 0.7F * random.nextFloat(),
                0.0D, 0.02D, 0.0D
        );
        if (random.nextInt(5) == 0)
            level.addParticle(ParticleTypes.CRIMSON_SPORE,
                    pos.getX() + 0.15F + 0.7F * random.nextFloat(), pos.getY() + 1.1F, pos.getZ() + 0.15F + 0.7F * random.nextFloat(),
                    0.0D, 0.0D, 0.0D
            );
    }
}
