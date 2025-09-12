package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class DislocationRing extends MagnetRing {
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.dislocationRing1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.dislocationRing2", ChatFormatting.GOLD, String.format("%.0f", 2 * CONFIG.ELSE.magnetRingRange.get()));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.dislocationRing3");
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        if (entity.isShiftKeyDown() || !(entity instanceof Player player)) return;
        if (this.hasMagnetEffectsDisabled(entity)) return;

        double x = entity.getX();
        double y = entity.getY() + 0.75;
        double z = entity.getZ();
        double r = CONFIG.ELSE.magnetRingRange.get() * 2.0;
        List<ItemEntity> items = entity.level().getEntitiesOfClass(ItemEntity.class, new AABB(x - r, y - r, z - r, x + r, y + r, z + r));
        int pulled = 0;
        for (ItemEntity item : items)
            if (this.canPullItem(item)) {
                if (pulled > 512) break;
                if (!EnigmaticHandler.canPickStack(player, item.getItem())) continue;
                item.setNoPickUpDelay();
                item.playerTouch(player);
                pulled++;
            }
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && !EnigmaticHandler.hasCurio(context.entity(), EnigmaticItems.MAGNET_RING);
    }
}