package auviotre.enigmatic.legacy.mixin.entity;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(Witch.class)
public abstract class MixinWitch extends Raider implements RangedAttackMob {
    @Unique
    private boolean firstPotion = true;

    protected MixinWitch(EntityType<? extends Raider> type, Level level) {
        super(type, level);
    }

    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionContents;createItemStack(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/ItemStack;"), index = 1)
    public Holder<Potion> performRangedAttackMix(Holder<Potion> potion) {
        if (EnigmaticHandler.isCurseBoosted(this)) {
            if (potion.equals(Potions.HARMING)) {
                return Potions.STRONG_HARMING;
            } else if (potion.equals(Potions.HEALING)) {
                return Potions.STRONG_HEALING;
            } else if (potion.equals(Potions.REGENERATION)) {
                return Potions.STRONG_REGENERATION;
            } else if (potion.equals(Potions.SLOWNESS)) {
                return Potions.STRONG_SLOWNESS;
            } else if (potion.equals(Potions.POISON)) {
                return Potions.STRONG_POISON;
            } else if (potion.equals(Potions.WEAKNESS)) {
                return Potions.LONG_WEAKNESS;
            }
        }
        return potion;
    }

    @ModifyArg(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Witch;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"), index = 1)
    public ItemStack aiStepMix(ItemStack stack) {
        if (EnigmaticHandler.isCurseBoosted(this) && this.firstPotion && stack.is(Items.POTION)) {
            this.firstPotion = false;
            List<MobEffectInstance> effects = new ArrayList<>();
            effects.add(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2400));
            effects.add(new MobEffectInstance(MobEffects.WATER_BREATHING, 2400));
            effects.add(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400));
            effects.add(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1));
            effects.add(new MobEffectInstance(MobEffects.HEAL, 10, 4));
            stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), effects));
        }
        return stack;
    }
}
