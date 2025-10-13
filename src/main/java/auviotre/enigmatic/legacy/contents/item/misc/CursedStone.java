package auviotre.enigmatic.legacy.contents.item.misc;

import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class CursedStone extends BaseCursedItem {
    public CursedStone() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant());
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedStone1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedStone2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedStone3");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedStone4");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedStone5");
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.canUndone");
    }
}
