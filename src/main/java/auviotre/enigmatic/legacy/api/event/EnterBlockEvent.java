package auviotre.enigmatic.legacy.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class EnterBlockEvent extends PlayerEvent {
    private final BlockState blockState;

    public EnterBlockEvent(Player player, BlockState blockState) {
        super(player);
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }
}
