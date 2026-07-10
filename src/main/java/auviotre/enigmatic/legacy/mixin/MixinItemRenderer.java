package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Shadow @Final private ItemModelShaper itemModelShaper;

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    public void getModelMix(ItemStack stack, Level level, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> info) {
        if (IItemHelper.UNKNOWN_ITEMS.stream().anyMatch(itemLike -> stack.is(itemLike.asItem()))) {
            info.setReturnValue(this.itemModelShaper.getItemModel(EnigmaticItems.UNKNOWN.get()));
        }
    }
}
