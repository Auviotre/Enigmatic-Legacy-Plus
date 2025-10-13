package auviotre.enigmatic.legacy.contents.block.entity;

import auviotre.enigmatic.legacy.registries.EnigmaticBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SpellstoneTableEntity extends BlockEntity {
    public int time;
    public float rot;
    public float oRot;
    public SpellstoneTableEntity(BlockPos pos, BlockState blockState) {
        super(EnigmaticBlockEntities.SPELLSTONE_TABLE_ENTITY.get(), pos, blockState);
    }

    public static void animationTick(Level level, BlockPos pos, BlockState state, SpellstoneTableEntity table) {
        table.oRot = table.rot;
        Vec3 vec3 = pos.getBottomCenter();
        Player player = level.getNearestPlayer(vec3.x, vec3.y, vec3.z, 2.5, false);
        table.rot = player == null ? table.rot + 0.03F : table.rot + 0.06F;
        ++table.time;
        float delta = (float) (Math.PI * 2);
        while(table.rot >= Math.PI) {
            table.oRot -= delta;
            table.rot -= delta;
        }
        while(table.rot < -Math.PI) {
            table.oRot += delta;
            table.rot += delta;
        }
    }
}
