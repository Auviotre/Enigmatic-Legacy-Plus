package auviotre.enigmatic.legacy.contents.block;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;

public class EtheriumOre extends DropExperienceBlock {
    public EtheriumOre() {
        super(UniformInt.of(3, 7), Properties.ofFullCopy(Blocks.END_STONE).requiresCorrectToolForDrops().strength(6.0F, 18.0F).lightLevel(state -> 4));
    }
}