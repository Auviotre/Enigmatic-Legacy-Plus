package auviotre.enigmatic.legacy.mixin;

import auviotre.enigmatic.legacy.contents.item.spellstones.other.SpellstoneSword;
import auviotre.enigmatic.legacy.contents.item.tools.InfernalShield;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        throw new IllegalStateException("Can't touch this");
    }

    @Inject(method = "hasInfiniteMaterials", at = @At("HEAD"), cancellable = true)
    public void hasInfiniteMaterialsMix(CallbackInfoReturnable<Boolean> info) {
        if (this.getUseItem().is(EnigmaticItems.STARLIGHT_BUCKET)) info.setReturnValue(true);
    }

    @Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
    public void disableShieldMix(CallbackInfo info) {
        if (this.getUseItem().getItem() instanceof InfernalShield) info.cancel();
    }

    @Inject(method = "destroyVanishingCursedItems", at = @At("TAIL"))
    public void onDestroyVanishingCursedItems(CallbackInfo info) {
        CuriosApi.getCuriosInventory(this).ifPresent(handler -> {
            IItemHandlerModifiable curios = handler.getEquippedCurios();
            for (int i = 0; i < curios.getSlots(); i++) {
                ItemStack stack = curios.getStackInSlot(i);
                if (!stack.isEmpty() && EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                    curios.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        });
    }

    @Inject(method = "sweepAttack", at = @At("TAIL"))
    public void sweepMix(CallbackInfo info) {
        ItemStack stack = getWeaponItem();
        if (stack.is(EnigmaticItems.SPELLSTONE_SWORD) && SpellstoneSword.isResonatingWith(stack, EnigmaticItems.VOID_PEARL)) {
            if (stack.getOrDefault(EnigmaticComponents.INT, 0) > 0) {
                List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5));
                for (LivingEntity entity : entities) {
                    if (entity.is(this)) continue;
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3));
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 3));
                    entity.addDeltaMovement(new Vec3(0, -2, 0));
                }
            }
        }
    }
}
