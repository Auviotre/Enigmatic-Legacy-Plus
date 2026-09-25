package auviotre.enigmatic.legacy.contents.item.op;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.client.Quote;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class QuotePlayer extends Item {
    public QuotePlayer() {
        super(IItemHelper.singleProperties().rarity(Rarity.EPIC).fireResistant());
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.quotePlayer1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.quotePlayer2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.quotePlayer3");
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.quotePlayerSelection", ChatFormatting.GOLD, Quote.getAllQuotes().get(stack.getDamageValue()).getName().toUpperCase());
    }

    private int getQuoteIndex(ItemStack stack) {
        return stack.getOrDefault(EnigmaticComponents.QUOTE_ID.get(), 0);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCrouching()) {
            player.startUsingItem(hand);
            stack.set(EnigmaticComponents.QUOTE_ID.get(), (getQuoteIndex(stack) + 1) % Quote.getAllQuotes().size());
            player.swing(hand);

            if (player instanceof ServerPlayer)
                player.displayClientMessage(Component.literal("Quote: " + Quote.getAllQuotes().get(getQuoteIndex(stack)).getName().toUpperCase()), true);
        } else if (player instanceof ServerPlayer splayer) {
            Quote.getAllQuotes().get(getQuoteIndex(stack)).play(splayer, Quote.PlayOptions.defaultPlay().delay(10));
        }
        return InteractionResultHolder.success(stack);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.PASS;
        return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }
}
