package auviotre.enigmatic.legacy.contents.item.misc;

import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.contents.item.rings.RedemptionRing;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class BlessStone extends BaseCursedItem {
    public BlessStone() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant(), true);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessStone1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessStone2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessStone3");
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.canUndone");
        if (flag.isAdvanced()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessLevel", ChatFormatting.DARK_GRAY, RedemptionRing.Helper.getPossibleLevel(Minecraft.getInstance().player));
        }
    }
}
