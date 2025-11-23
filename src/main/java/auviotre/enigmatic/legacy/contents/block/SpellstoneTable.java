package auviotre.enigmatic.legacy.contents.block;

import auviotre.enigmatic.legacy.contents.block.entity.SpellstoneTableEntity;
import auviotre.enigmatic.legacy.contents.gui.SpellstoneTableMenu;
import auviotre.enigmatic.legacy.registries.EnigmaticBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpellstoneTable extends BaseEntityBlock {
    public static final MapCodec<SpellstoneTable> CODEC = simpleCodec(SpellstoneTable::new);
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public SpellstoneTable() {
        super(Properties.ofFullCopy(Blocks.ENCHANTING_TABLE));
    }

    public SpellstoneTable(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SpellstoneTableEntity(blockPos, blockState);
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? createTickerHelper(type, EnigmaticBlockEntities.SPELLSTONE_TABLE_ENTITY.get(), SpellstoneTableEntity::animationTick) : null;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(16) == 0) {
            level.addParticle(ParticleTypes.WITCH, pos.getX() + random.nextFloat(), pos.getY() + 0.8 + random.nextFloat() * 0.1, pos.getZ() + random.nextFloat(), 0, 0, 0);
        }
    }

    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (!(blockentity instanceof SpellstoneTableEntity)) return null;
        return new SimpleMenuProvider((id, inventory, player) -> new SpellstoneTableMenu(id, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable("gui.enigmaticlegacy.spellstone_crafting"));
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
