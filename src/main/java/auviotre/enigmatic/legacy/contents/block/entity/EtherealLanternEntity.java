package auviotre.enigmatic.legacy.contents.block.entity;

import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.etherium.EtheriumProperties;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EtherealLanternEntity extends BlockEntity {
    public EtherealLanternEntity(BlockPos pos, BlockState blockState) {
        super(EnigmaticBlockEntities.ETHEREAL_LANTERN_ENTITY.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EtherealLanternEntity entity) {
        if (level.getGameTime() % 100L == 0L) {
            List<Player> list = level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(8),
                    player -> player.getData(EnigmaticAttachments.ENIGMATIC_DATA).getEtherealShield() <= 0);
            if (level.isClientSide()) {
                if (list.isEmpty() || level.getRandom().nextBoolean()) return;
                Vec3 center = pos.getBottomCenter().add(0, state.getValue(BlockStateProperties.HANGING) ? 0.5475 : 0.36, 0);
                for (int i = 0; i < 16; i++) {
                    double theta = Math.PI * i / 8;
                    level.addParticle(ParticleTypes.END_ROD,
                            center.x + Math.sin(theta) * 0.16, center.y, center.z + Math.cos(theta) * 0.16,
                            Math.sin(theta) * 0.04, 0, Math.cos(theta) * 0.04
                    );
                }
            } else {
                for (Player player : list) {
                    EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
                    float threshold = (float) EtheriumProperties.getShieldThreshold(player);
                    data.setEtherealShield(Math.max(threshold * player.getMaxHealth() * 0.5F, 0));
                }
            }
        }
    }
}
