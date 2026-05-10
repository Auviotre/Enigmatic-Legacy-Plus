package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticEnchantments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticPotions;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements DataComponentHolder {
    @Shadow
    public abstract boolean is(Item item);

    @Shadow
    public abstract boolean is(TagKey<Item> tag);

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    public void breakMix(int amount, ServerLevel level, @Nullable LivingEntity user, Consumer<Item> consumer, CallbackInfo info) {
        ItemStack curio = EnigmaticHandler.getCurio(user, EnigmaticItems.ETHEREAL_FORGING_CHARM);
        if (!curio.isEmpty() && user != null) {
            double prob = 0.05;
            var holder = EnigmaticHandler.get(user.level(), Registries.ENCHANTMENT, EnigmaticEnchantments.ETHERIC_RESONANCE);
            if (curio.getEnchantmentLevel(holder) > 0) prob = 0.03;
            if (user.getRandom().nextFloat() > prob) info.cancel();
        }
    }

    @Inject(method = "hasFoil", at = @At("RETURN"), cancellable = true)
    public void hasFoilMix(CallbackInfoReturnable<Boolean> info) {
        if (this.is(Tags.Items.POTION_BOTTLE) || this.is(Items.TIPPED_ARROW)) {
            PotionContents contents = this.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (contents.potion().isPresent() && EnigmaticPotions.ULTIMATE_POTIONS.contains(contents.potion().get())) {
                info.setReturnValue(true);
            }
        }
    }
}
