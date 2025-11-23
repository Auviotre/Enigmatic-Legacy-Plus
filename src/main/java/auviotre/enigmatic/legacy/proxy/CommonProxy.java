package auviotre.enigmatic.legacy.proxy;

import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.DispenserBlock;

public class CommonProxy {
    public void init() {
        DispenserBlock.registerBehavior(EnigmaticItems.ICHOR_SPEAR, new ProjectileDispenseBehavior(EnigmaticItems.ICHOR_SPEAR.asItem()) {
            protected void playSound(BlockSource blockSource) {
                blockSource.level().playSound(null, blockSource.pos(), SoundEvents.TRIDENT_THROW.value(), SoundSource.BLOCKS, 1.0F, 1.2F);
            }
        });
    }

    public void clientInit() {

    }

    public void displayPermanentDeathScreen() {
    }

    public String getClientUsername() {
        return null;
    }
}
