package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class StarlightBucket extends Item {

    public StarlightBucket() {
        super(IItemHelper.singleProperties());
    }

    private Item getContent(ItemStack stack) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(stack.getOrDefault(EnigmaticComponents.CONTENT, "minecraft:air")));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.starlightBucket1");
        Component component = Component.translatable("tooltip.enigmaticlegacy.starlightBucketEmpty");
        Item content = getContent(stack);
        if (content instanceof MilkBucketItem) {
            component = Component.translatable("fluid_type.minecraft.milk");
        } else if (content instanceof BucketItem bucket) {
            if (bucket.content != Fluids.EMPTY) component = new FluidStack(bucket.content, Integer.MAX_VALUE).getHoverName();
        } else  if (content instanceof SolidBucketItem bucket) {
            if (!bucket.getBlock().defaultBlockState().isAir()) component = bucket.getBlock().getName();
        }
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.starlightBucket2", ChatFormatting.LIGHT_PURPLE, component);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item content = getContent(stack);
        if (content instanceof MilkBucketItem) return ItemUtils.startUsingInstantly(level, player, hand);
        if (content instanceof BucketItem bucket) {
            BlockHitResult result = getPlayerPOVHitResult(level, player, bucket.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
            if (result.getType() == HitResult.Type.MISS) return InteractionResultHolder.pass(stack);
            if (result.getType() != HitResult.Type.BLOCK) return InteractionResultHolder.pass(stack);
            BlockPos blockPos = result.getBlockPos();
            Direction direction = result.getDirection();
            BlockPos relative = blockPos.relative(direction);
            BlockState blockState = level.getBlockState(blockPos);
            if (level.mayInteract(player, blockPos) && player.mayUseItemAt(relative, direction, stack)) {
                if (bucket.content == Fluids.EMPTY) {
                    if (blockState.getBlock() instanceof BucketPickup pickup) {
                        ItemStack pickStack = pickup.pickupBlock(player, level, blockPos, blockState);
                        if (!pickStack.isEmpty()) {
                            player.awardStat(Stats.ITEM_USED.get(this));
                            pickup.getPickupSound(blockState).ifPresent((event) -> player.playSound(event, 1.0F, 1.0F));
                            level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
                            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                        }
                    }
                } else {
                    BlockPos place = blockState.getBlock() instanceof LiquidBlockContainer container && container.canPlaceLiquid(player, level, blockPos, blockState, bucket.content) ? blockPos : relative;
                    if (bucket.emptyContents(player, level, place, result, stack)) {
                        bucket.checkExtraContent(player, level, stack, place);
                        if (player instanceof ServerPlayer serverPlayer)
                            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, place, stack);
                        player.awardStat(Stats.ITEM_USED.get(this));
                        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                    }
                }
            }
        }
        return InteractionResultHolder.fail(stack);
    }

    public InteractionResult useOn(UseOnContext context) {
        Item content = getContent(context.getItemInHand());
        if (content instanceof SolidBucketItem bucket) {
            return bucket.place(new BlockPlaceContext(context.getLevel(), context.getPlayer(), context.getHand(), new ItemStack(content), context.getHitResult()));
        }
        return super.useOn(context);
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.PRIMARY || !slot.mayPlace(stack) || !slot.mayPickup(player) || other.isEmpty())
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        if (other.is(Tags.Items.BUCKETS) && !other.is(EnigmaticItems.STARLIGHT_BUCKET)) {
            stack.set(EnigmaticComponents.CONTENT, IItemHelper.getLocation(other.getItem()).toString());
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 0.8F + 0.4F * player.getRandom().nextFloat() * 0.2F);
            return true;
        }
        return false;
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (getContent(stack) instanceof MilkBucketItem) {
            if (entity instanceof ServerPlayer player) {
                CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
                player.awardStat(Stats.ITEM_USED.get(this));
            }
            if (!level.isClientSide) entity.removeEffectsCuredBy(EffectCures.MILK);
        }
        return stack;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return getContent(stack).getUseDuration(stack ,entity);
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return getContent(stack).getUseAnimation(stack);
    }
}
