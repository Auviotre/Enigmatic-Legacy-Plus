package auviotre.enigmatic.legacy.contents.block;

import auviotre.enigmatic.legacy.client.particles.StarDustParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class AstralDustSack extends Block {
    public AstralDustSack() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(MapColor.COLOR_PINK));
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0 && !level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos.above()))
            level.addParticle(StarDustParticle.get(random),
                    pos.getX() + 0.15F + 0.7F * random.nextFloat(), pos.getY() + 1.1F, pos.getZ() + 0.15F + 0.7F * random.nextFloat(),
                    0.0D, 0.0D, 0.0D
            );
    }
}
