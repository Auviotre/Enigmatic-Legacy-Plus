package auviotre.enigmatic.legacy.mixin;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements DataComponentHolder {
}
