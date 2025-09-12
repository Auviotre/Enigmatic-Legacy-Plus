package auviotre.enigmatic.legacy.contents.block.entity;

import auviotre.enigmatic.legacy.registries.EnigmaticBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DimensionalAnchorEntity extends BlockEntity {
    public DimensionalAnchorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public DimensionalAnchorEntity(BlockPos pos, BlockState state) {
        this(EnigmaticBlockEntities.DIMENSIONAL_ANCHOR_ENTITY.get(), pos, state);
    }

    public boolean shouldRenderFace(@NotNull Direction face) {
        return face.getAxis() == Direction.Axis.Y;
    }
}
