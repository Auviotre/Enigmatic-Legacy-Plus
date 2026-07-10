package auviotre.enigmatic.legacy.contents.block;

import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AstralGlass extends TransparentBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty COLORFUL = BooleanProperty.create("colorful");
    public static final IntegerProperty STYLE = IntegerProperty.create("style", 0, 3);

    public AstralGlass() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(1.6F, 6.0F).requiresCorrectToolForDrops().lightLevel(state -> 10));
        this.registerDefaultState(this.defaultBlockState().setValue(STYLE, 0).setValue(FACING, Direction.NORTH).setValue(COLORFUL, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STYLE, FACING, COLORFUL);
    }

    public @Nullable Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        if (state.getValue(COLORFUL)) return null;
        return switch (state.getValue(STYLE)) {
            case 0 -> 0xE39B3F;
            case 1 -> 0xB24CD8;
            case 2 -> 0x6699D8;
            case 3 -> 0xF27FA5;
            default -> null;
        };
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide && level.hasNeighborSignal(pos))
            level.setBlock(pos, state.setValue(STYLE, level.getBestNeighborSignal(pos) / 4 % 4), 3);
    }

    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        interact(state, level, pos);
        super.attack(state, level, pos, player);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(EnigmaticItems.ASTRAL_DUST)) {
            stack.consume(1, player);
            level.playSound(null, pos, EnigmaticSounds.ASTRAL_GLASS_CHANGE.get(),  SoundSource.BLOCKS, 0.25F, 1.8F + 0.2F * level.getRandom().nextFloat());
            level.setBlock(pos, state.setValue(COLORFUL, true), 3);
            return ItemInteractionResult.SUCCESS;
        }
        ItemInteractionResult result = stack.getItem() instanceof BlockItem && new BlockPlaceContext(player, hand, stack, hitResult).canPlace() ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.SUCCESS;
        if (result == ItemInteractionResult.SUCCESS) {
            if (level.isClientSide) spawnParticles(level, pos);
            else interact(state, level, pos);
        }
        return result;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    private static void interact(BlockState state, Level level, BlockPos pos) {
        spawnParticles(level, pos);
        level.setBlock(pos, state.setValue(STYLE, (state.getValue(STYLE) + 1) % 4), 3);
    }

    private static void spawnParticles(Level level, BlockPos pos) {
        float d0 = 0.5625F;
        RandomSource random = level.random;
        level.playSound(null, pos, EnigmaticSounds.ASTRAL_GLASS_CHANGE.get(),  SoundSource.BLOCKS, 0.25F, 1.8F + 0.2F * random.nextFloat());
        for(Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            if (!level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
                Direction.Axis axis = direction.getAxis();
                float d1 = axis == Direction.Axis.X ? 0.5F + d0 * direction.getStepX() : random.nextFloat();
                float d2 = axis == Direction.Axis.Y ? 0.5F + d0 * direction.getStepY() : random.nextFloat();
                float d3 = axis == Direction.Axis.Z ? 0.5F + d0 * direction.getStepZ() : random.nextFloat();
                level.addParticle(ParticleTypes.WHITE_ASH, pos.getX() + d1, pos.getY() + d2, pos.getZ() + d3, 0.0F, 0.0F, 0.0F);
            }
        }
    }
}
