package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class MixinVillager implements ILivingEntityExtension {
    @Inject(at = @At("RETURN"), method = "updateSpecialPrices")
    private void onSpecialPrices(Player player, CallbackInfo info) {
        if (this.self() instanceof Villager villager) {
            if (EnigmaticHandler.hasCurio(player, EnigmaticItems.AVARICE_SCROLL)) {
                for (MerchantOffer trade : villager.getOffers()) {
                    double discountValue = 0.35;
                    int discount = (int) Math.floor(discountValue * trade.getBaseCostA().getCount());
                    trade.addToSpecialPriceDiff(-Math.max(discount, 1));
                }
            }
        }
    }
}
