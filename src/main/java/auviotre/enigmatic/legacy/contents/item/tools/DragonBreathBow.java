package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.contents.entity.projectile.DragonBreathArrow;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DragonBreathBow extends ProjectileWeaponItem {
    public DragonBreathBow() {
        super(BaseItem.defaultSingleProperties().rarity(Rarity.RARE).durability(1574));
    }

    public static float getPowerForTime(int tick) {
        float f = (float) tick / 32.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
//      TooltipHandler.line(list, "tooltip.enigmaticlegacy.dragonBreathBowAmmo", ChatFormatting.LIGHT_PURPLE, Items.DRAGON_BREATH.getDescription());
        PotionContents potioncontents = stack.get(DataComponents.POTION_CONTENTS);
        if (potioncontents != null) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.dragonBreathBowEffects");
            potioncontents.addPotionTooltip(list::add, 1.0F, context.tickRate());
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.dragonBreathBowNoEffects1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.dragonBreathBowNoEffects2");
        }
        if (stack.isEnchanted()) TooltipHandler.line(list);
    }

    public InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, InteractionHand hand) {
        ItemStack bow = player.getItemInHand(hand);
        InteractionResultHolder<ItemStack> ret = EventHooks.onArrowNock(bow, level, player, hand, true);
        if (ret != null) {
            return ret;
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(bow);
        }
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 16;
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            ItemStack projectile = Items.DRAGON_BREATH.getDefaultInstance();
            int tick = this.getUseDuration(stack, entityLiving) - timeLeft;
            tick = EventHooks.onArrowLoose(stack, level, player, tick, true);
            if (tick < 0) return;
            float f = getPowerForTime(tick);
            if (f > 0.1F) {
                List<ItemStack> list = List.of(projectile);
                if (level instanceof ServerLevel server) {
                    this.shoot(server, player, player.getUsedItemHand(), stack, list, f * 3.0F, 1.0F, f == 1.0F, null);
                }
                player.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public AbstractArrow customArrow(@NotNull AbstractArrow arrow, ItemStack projectile, ItemStack bow) {
        DragonBreathArrow dragonBreathArrow = new DragonBreathArrow(arrow.getX(), arrow.getY(), arrow.getZ(), arrow.level(), bow);
        dragonBreathArrow.setCritArrow(arrow.isCritArrow());
        dragonBreathArrow.setOwner(arrow.getOwner());
        return dragonBreathArrow;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ItemStack::isEmpty;
    }

    public int getDefaultProjectileRange() {
        return 16;
    }

    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + angle, 0.0F, velocity, inaccuracy);
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.PRIMARY && slot.mayPickup(player) && slot.hasItem()) {
            PotionContents bow = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            List<MobEffectInstance> remains = bow.customEffects();
            if (other.is(Items.SPLASH_POTION)) {
                PotionContents contents = other.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                if (contents.potion().isPresent()) {
                    access.set(Items.GLASS_BOTTLE.getDefaultInstance());
                    List<MobEffectInstance> effects = contents.potion().get().value().getEffects();
                    List<MobEffectInstance> newEffects = new ArrayList<>();
                    for (MobEffectInstance effect : effects)
                        newEffects.add(new MobEffectInstance(effect.getEffect(), effect.getDuration() / 10, effect.getAmplifier()));
                    int index = 0;
                    while (newEffects.size() < 4 && remains.size() > index) newEffects.add(remains.get(index++));
                    stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), newEffects));
                    return true;
                }
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }
}
