package auviotre.enigmatic.legacy.contents.item.generic;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class BaseCurioItem extends BaseItem implements ICurioItem {

    public BaseCurioItem() {
        super(defaultSingleProperties());
    }

    public BaseCurioItem(Properties properties) {
        super(properties);
    }

    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return !EnigmaticHandler.hasCurio(context.entity(), this) && ICurioItem.super.canEquip(context, stack);
    }
}
