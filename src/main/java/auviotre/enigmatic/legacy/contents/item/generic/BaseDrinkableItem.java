package auviotre.enigmatic.legacy.contents.item.generic;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class BaseDrinkableItem extends BaseItem {

    public BaseDrinkableItem(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.canDrink(level, player, stack))
            return ItemUtils.startUsingInstantly(level, player, hand);
        return InteractionResultHolder.pass(stack);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            ItemStack remainingItem = stack.getCraftingRemainingItem();
            this.onConsumed(level, player, stack);

            if (player instanceof ServerPlayer serverPlayer) CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);

            player.awardStat(Stats.ITEM_USED.get(this));
            stack.consume(1, player);

            if (!player.hasInfiniteMaterials()) {
                if (stack.isEmpty()) return remainingItem;
                player.getInventory().add(remainingItem);
            }
        }
        entity.gameEvent(GameEvent.DRINK);
        return stack;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    public boolean canDrink(Level level, Player player, ItemStack stack) {
        return true;
    }

    public void onConsumed(Level level, Player player, ItemStack stack) {
    }
}
