package auviotre.enigmatic.legacy.contents.block;

import auviotre.enigmatic.legacy.contents.block.entity.DimensionalAnchorEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.RespawnAnchorBlock.charge;
import static net.minecraft.world.level.block.RespawnAnchorBlock.getScaledChargeLevel;

public class DimensionalAnchor extends BaseEntityBlock {
    public static final IntegerProperty CHARGE = BlockStateProperties.RESPAWN_ANCHOR_CHARGES;
    public static final MapCodec<DimensionalAnchor> CODEC = simpleCodec(DimensionalAnchor::new);
    private static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Block.box(2.0, 14.0, 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);

    public DimensionalAnchor() {
        this(Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(50F, 1200F).lightLevel(state -> getScaledChargeLevel(state, 15)));
    }

    public DimensionalAnchor(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CHARGE, 0));

        DispenserBlock.registerBehavior(Items.ENDER_PEARL, new OptionalDispenseItemBehavior() {
            public ItemStack execute(BlockSource source, ItemStack stack) {
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                BlockPos facedPos = source.pos().relative(direction);
                Level level = source.level();
                BlockState facedState = level.getBlockState(facedPos);
                if (facedState.getBlock() == DimensionalAnchor.this) {
                    if (canBeCharged(facedState)) {
                        charge(null, level, facedPos, facedState);
                        stack.shrink(1);
                        this.setSuccess(true);
                    } else this.setSuccess(false);
                    return stack;
                }
                return super.execute(source, stack);
            }
        });
    }

    private static boolean isWaterThatWouldFlow(BlockPos pos, Level level) {
        FluidState fluidState = level.getFluidState(pos);
        if (!fluidState.is(FluidTags.WATER)) return false;
        else if (fluidState.isSource()) return true;
        else {
            float f = (float) fluidState.getAmount();
            if (f < 2.0F) return false;
            else {
                FluidState belowState = level.getFluidState(pos.below());
                return !belowState.is(FluidTags.WATER);
            }
        }
    }

    private static boolean isRespawnFuel(ItemStack stack) {
        return stack.is(Items.ENDER_EYE);
    }

    private static boolean canBeCharged(BlockState state) {
        return state.getValue(CHARGE) < 4;
    }

    public static boolean canSetSpawn(Level level) {
        return true;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (isRespawnFuel(stack) && canBeCharged(state)) {
            charge(player, level, pos, state);
            stack.consume(1, player);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return hand == InteractionHand.MAIN_HAND && isRespawnFuel(player.getItemInHand(InteractionHand.OFF_HAND)) && canBeCharged(state) ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(CHARGE) == 0) {
            return InteractionResult.PASS;
        } else if (!canSetSpawn(level)) {
            if (!level.isClientSide) {
                this.explode(state, level, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            if (!level.isClientSide) {
                ServerPlayer serverplayer = (ServerPlayer) player;
                if (serverplayer.getRespawnDimension() != level.dimension() || !pos.equals(serverplayer.getRespawnPosition())) {
                    serverplayer.setRespawnPosition(level.dimension(), pos, 0.0F, false, true);
                    level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.CONSUME;
        }
    }

    private void explode(BlockState state, Level level, final BlockPos pos) {
        level.removeBlock(pos, false);
        Stream<Direction> directions = Direction.Plane.HORIZONTAL.stream();
        Objects.requireNonNull(pos);
        boolean flag = directions.map(pos::relative).anyMatch((blockPos) -> isWaterThatWouldFlow(blockPos, level));
        final boolean flag1 = flag || level.getFluidState(pos.above()).is(FluidTags.WATER);
        ExplosionDamageCalculator calculator = new ExplosionDamageCalculator() {
            public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter getter, BlockPos blockPos, BlockState state, FluidState fluidState) {
                return blockPos.equals(pos) && flag1 ? Optional.of(Blocks.WATER.getExplosionResistance()) : super.getBlockExplosionResistance(explosion, getter, blockPos, state, fluidState);
            }
        };
        Vec3 vec3 = pos.getCenter();
        level.explode(null, level.damageSources().badRespawnPointExplosion(vec3), calculator, vec3, 5.0F, true, Level.ExplosionInteraction.BLOCK);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGE);
    }

    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return getScaledChargeLevel(blockState, 15);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(CHARGE) > 0) {
            if (random.nextInt(100) == 0) {
                level.playLocalSound(pos, SoundEvents.RESPAWN_ANCHOR_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
            double d0 = pos.getX() + random.nextDouble();
            double d1 = pos.getY() + 1.0;
            double d2 = pos.getZ() + random.nextDouble();
            double d3 = random.nextFloat() * 0.04;
            level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, d3, 0.0);
        }
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DimensionalAnchorEntity(pos, state);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
