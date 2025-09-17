package auviotre.enigmatic.legacy.contents.item.generic;

import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CursedCurioItem extends BaseCursedItem implements ICurioItem {
    public CursedCurioItem(Properties properties) {
        super(properties);
    }

    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
