package auviotre.enigmatic.legacy.contents.block.entity;

import auviotre.enigmatic.legacy.registries.EnigmaticBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SpellstoneTableEntity extends BlockEntity {
    public boolean render;
    public int time;
    public float rot;
    public float oRot;

    public SpellstoneTableEntity(BlockPos pos, BlockState blockState) {
        super(EnigmaticBlockEntities.SPELLSTONE_TABLE_ENTITY.get(), pos, blockState);
    }

    public static void animationTick(Level level, BlockPos pos, BlockState state, SpellstoneTableEntity table) {
        table.render = level.getBlockState(pos.above()).isAir();
        table.oRot = table.rot;
        Vec3 vec3 = pos.getBottomCenter();
        Player player = level.getNearestPlayer(vec3.x, vec3.y, vec3.z, 2.5, false);
        table.rot = player == null ? table.rot + 0.025F : table.rot + 0.02F + (float) (Math.min(0.08F / Math.sqrt(player.distanceToSqr(vec3)), 0.1F));
        ++table.time;
        float delta = (float) (2.0F * Math.PI);
        while (table.rot >= Math.PI) {
            table.oRot -= delta;
            table.rot -= delta;
        }
        while (table.rot < -Math.PI) {
            table.oRot += delta;
            table.rot += delta;
        }
    }
}
