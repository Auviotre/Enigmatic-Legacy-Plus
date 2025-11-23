package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.entity.projectile.ThrownIchorSpear;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class IchorSpear extends BaseItem implements ProjectileItem {
    public static ModConfigSpec.IntValue baseDamage;
    public static ModConfigSpec.IntValue duration;
    public static ModConfigSpec.IntValue amplifier;

    public IchorSpear() {
        super(defaultProperties(16));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.ichor_spear").push("else.ichorSpear");
        baseDamage = builder.defineInRange("baseDamage", 4, 0, 12);
        duration = builder.defineInRange("duration", 600, 0, 1200);
        amplifier = builder.defineInRange("amplifier", 1, 0, 4);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.ichorSpear");
            if (stack.getCount() == stack.getMaxStackSize())
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.ichorSpearFull");
        } else TooltipHandler.holdShift(list);
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int tickCount) {
        int duration = this.getUseDuration(stack, entity) - tickCount;
        if (duration >= 8) {
            if (!level.isClientSide()) {
                ThrownIchorSpear spear = new ThrownIchorSpear(entity, level, stack.copyWithCount(1));
                if (stack.getCount() == stack.getMaxStackSize() || entity.hasInfiniteMaterials())
                    spear.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                float strength = Math.min(3.5F, duration * 0.1F + 0.6F);
                float pitch = Math.min(0.65F + duration * 0.02F, 1.1F);
                spear.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, strength, 1.0F);

                level.addFreshEntity(spear);
                level.playSound(null, entity.blockPosition(), SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, pitch);
            }

            if (stack.getCount() != stack.getMaxStackSize()) {
                stack.consume(1, entity);
            }
            if (entity instanceof Player player) {
                player.awardStat(Stats.ITEM_USED.get(this));
                player.swing(player.getUsedItemHand());
            }
        }
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemInHand);
    }

    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        ThrownIchorSpear spear = new ThrownIchorSpear(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1));
        spear.pickup = AbstractArrow.Pickup.ALLOWED;
        return spear;
    }
}
