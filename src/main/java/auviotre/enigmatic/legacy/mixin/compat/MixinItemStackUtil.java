package auviotre.enigmatic.legacy.mixin.compat;

import auviotre.enigmatic.legacy.contents.item.books.TheAcknowledgment;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.util.ItemStackUtil;

@Mixin(ItemStackUtil.class)
public class MixinItemStackUtil {
    @Inject(method = "getBookFromStack", at = @At("HEAD"), cancellable = true)
    private static void getBookMix(ItemStack stack, CallbackInfoReturnable<Book> info) {
        if (stack.getItem() instanceof TheAcknowledgment) {
            info.setReturnValue(ItemModBook.getBook(stack));
        }
    }
}
