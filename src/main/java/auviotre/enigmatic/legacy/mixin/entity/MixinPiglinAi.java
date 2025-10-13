package auviotre.enigmatic.legacy.mixin.entity;

import auviotre.enigmatic.legacy.contents.item.scrolls.AvariceScroll;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PiglinAi.class)
public class MixinPiglinAi {
    private static void markPiglinWithCondition(Piglin piglin, Player player) {
        if (piglin != null && EnigmaticHandler.hasCurio(player, EnigmaticItems.AVARICE_SCROLL))
            if (!piglin.getTags().contains(AvariceScroll.EFFECT_TAG)) {
                piglin.addTag(AvariceScroll.EFFECT_TAG);
            }
    }

    private static List<ItemStack> distributeExcess(ItemStack stack) {
        List<ItemStack> newStacks = new ArrayList<>();
        while (stack.getCount() > stack.getMaxStackSize()) {
            newStacks.add(stack.split(stack.getMaxStackSize()));
        }
        return newStacks;
    }

    @Inject(at = @At("RETURN"), method = "pickUpItem")
    private static void onPiglinItemPickup(Piglin piglin, ItemEntity itemEntity, CallbackInfo info) {
        Entity owner = itemEntity.getOwner();
        if (!itemEntity.isAlive() && itemEntity.level() instanceof ServerLevel world && owner instanceof Player player) {
            markPiglinWithCondition(piglin, player);
        }
    }

    @Inject(at = @At("RETURN"), method = "mobInteract")
    private static void onBarterByHand(Piglin piglin, Player player, InteractionHand hand, @NotNull CallbackInfoReturnable<InteractionResult> info) {
        if (info.getReturnValue() == InteractionResult.CONSUME) {
            markPiglinWithCondition(piglin, player);
        }
    }

    @Inject(at = @At("HEAD"), method = "stopHoldingOffHandItem", cancellable = true)
    private static void onPiglinBarter(@NotNull Piglin piglin, boolean repay, CallbackInfo info) {
        ItemStack stack = piglin.getItemInHand(InteractionHand.OFF_HAND);
        if (piglin.level() instanceof ServerLevel && piglin.getTags().contains(AvariceScroll.EFFECT_TAG)) {
            piglin.removeTag(AvariceScroll.EFFECT_TAG);
            if (piglin.isAdult()) {
                if (repay && stack.isPiglinCurrency()) {
                    info.cancel();
                    piglin.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                    List<ItemStack> generatedLoot = PiglinAi.getBarterResponseItems(piglin);
                    List<ItemStack> newStacks = new ArrayList<>();
                    generatedLoot.forEach(lootStack -> {
                        if (lootStack != null && !lootStack.isEmpty()) {
                            double multiplier = piglin.getRandom().nextDouble() * 2.0;
                            int bonusAmount = (int) Math.round(lootStack.getCount() * multiplier);
                            int newCount = lootStack.getCount() + bonusAmount;
                            lootStack.setCount(newCount);
                            newStacks.addAll(distributeExcess(lootStack));
                        }
                    });
                    generatedLoot.addAll(newStacks);
                    PiglinAi.throwItems(piglin, generatedLoot);
                }
            }
        }
    }
}
