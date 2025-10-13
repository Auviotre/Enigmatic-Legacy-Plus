package auviotre.enigmatic.legacy.compat.farmersdelight.contents.block;

import auviotre.enigmatic.legacy.compat.farmersdelight.FDCompat;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.FeastBlock;

public class DivineFruitPieBlock extends FeastBlock {
    protected static final VoxelShape PLATE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0);
    protected static final VoxelShape PIE_SHAPE = Shapes.joinUnoptimized(PLATE_SHAPE, Block.box(2.0, 2.0, 2.0, 14.0, 8.0, 14.0), BooleanOp.OR);


    public DivineFruitPieBlock() {
        super(Properties.ofFullCopy(Blocks.CAKE), FDCompat.Items.DIVINE_FRUIT_PIE::get, true);
    }


    public ItemInteractionResult useItemOn(ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (EnigmaticHandler.isTheOne(player)) {
            return super.useItemOn(heldStack, state, level, pos, player, hand, hit);
        } else {
            player.kill();
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(SERVINGS) == 0 ? PLATE_SHAPE : PIE_SHAPE;
    }

//    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
//        if (random.nextInt(3) == 0) {
//            world.addParticle(StarDustParticle.get(random), pos.getX() + F(random), pos.getY() + 0.6F + 0.1F * random.nextFloat(), pos.getZ() + F(random), 0.0D, 0.0D, 0.0D);
//        }
//    }

    private float F(RandomSource random) {
        return 0.05F + 0.9F * random.nextFloat();
    }
}
