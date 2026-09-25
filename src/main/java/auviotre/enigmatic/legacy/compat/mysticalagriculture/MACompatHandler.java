package auviotre.enigmatic.legacy.compat.mysticalagriculture;

import auviotre.enigmatic.legacy.contents.item.misc.Infinimeal;
import com.blakebr0.mysticalagriculture.block.MysticalCropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MACompatHandler {
    public static boolean tryGrow(Level level, BlockState state, BlockPos pos) {
        if (state.getBlock() instanceof MysticalCropBlock crop) {
            if (level.isClientSide()) Infinimeal.spawnBoneMealParticles(level, pos, 15);
            if (level instanceof ServerLevel server) crop.performBonemeal(server, level.getRandom(), pos, state);
            return true;
        }
        return false;
    }
}
