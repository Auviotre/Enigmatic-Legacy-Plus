package auviotre.enigmatic.legacy.contents.item.materials;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import net.minecraft.world.item.Item;

public class StarlightParticle extends Item {
    public StarlightParticle() {
        super(IItemHelper.properties().fireResistant());
    }
}
