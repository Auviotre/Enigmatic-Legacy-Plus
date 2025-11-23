package auviotre.enigmatic.legacy.contents.block;

import auviotre.enigmatic.legacy.contents.block.entity.EtherealLanternEntity;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class EtherealLantern extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<EtherealLantern> CODEC = simpleCodec(EtherealLantern::new);
    protected static final VoxelShape AABB = Shapes.or(
            Block.box(4.5, 0.0, 4.5, 11.5, 1.0, 11.5),
            Block.box(5.0, 1.0, 5.0, 11.0, 8.0, 11.0),
            Block.box(4.5, 8.0, 4.5, 11.5, 9.0, 11.5),
            Block.box(6.0, 9.0, 6.0, 10.0, 11.0, 10.0),
            Block.box(7.0, 11.0, 7.0, 9.0, 12.0, 9.0)
    );
    protected static final VoxelShape HANGING_AABB = Shapes.or(
            Block.box(4.5, 3.0, 4.5, 11.5, 4.0, 11.5),
            Block.box(5.0, 4.0, 5.0, 11.0, 11.0, 11.0),
            Block.box(4.5, 11.0, 4.5, 11.5, 12.0, 11.5),
            Block.box(6.0, 12.0, 6.0, 10.0, 14.0, 10.0),
            Block.box(7.0, 14.0, 7.0, 9.0, 16.0, 9.0)
    );

    public EtherealLantern() {
        super(Properties.ofFullCopy(Blocks.LANTERN).strength(9.5F));
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(BlockStateProperties.HANGING, false));
    }

    public EtherealLantern(Properties properties) {
        super(properties);
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealLanternEntity(pos, state);
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState blockstate = this.defaultBlockState().setValue(BlockStateProperties.HANGING, direction == Direction.UP);
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return blockstate.setValue(BlockStateProperties.WATERLOGGED, fluidstate.getType() == Fluids.WATER);
                }
            }
        }
        return null;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EnigmaticBlockEntities.ETHEREAL_LANTERN_ENTITY.get(), EtherealLanternEntity::tick);
    }

    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(BlockStateProperties.HANGING) ? Direction.UP : Direction.DOWN;
        return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(BlockStateProperties.HANGING) ? HANGING_AABB : AABB;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED, BlockStateProperties.HANGING);
    }

    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return Direction.DOWN == direction && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    protected FluidState getFluidState(@NotNull BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public MapCodec<EtherealLantern> codec() {
        return CODEC;
    }

    public static class Item extends BlockItem {
        public Item(Block block) {
            super(block, new Properties().stacksTo(16).fireResistant());
        }

        @OnlyIn(Dist.CLIENT)
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.etherealLantern");
        }
    }
}
