package auviotre.enigmatic.legacy.contents.item.materials;

import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class EarthHeart extends BaseItem implements ITaintable {
    public EarthHeart() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON));
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && !level.isClientSide())
            this.handleTaintable(stack, player);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (ITaintable.isTainted(stack)) TooltipHandler.line(list, "tooltip.enigmaticlegacy.tainted");
    }

    public static class Fragment extends BaseItem {
        public Fragment() {
            super(defaultProperties().stacksTo(16).rarity(Rarity.UNCOMMON));
        }
    }
}
