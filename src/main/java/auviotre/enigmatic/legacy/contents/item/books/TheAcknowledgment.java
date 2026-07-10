package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IBagContent;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.item.PatchouliDataComponents;

import javax.annotation.Nonnull;
import java.util.List;

public class TheAcknowledgment extends Item implements IBagContent {
    private static final ResourceLocation BOOK_ID = EnigmaticLegacy.location("the_acknowledgment");

    protected TheAcknowledgment(@NotNull Properties properties, float attackDamage, float attackSpeed) {
        super(properties.attributes(IItemHelper.createAttributes(attackDamage, attackSpeed)).component(PatchouliDataComponents.BOOK, BOOK_ID));
    }

    public TheAcknowledgment() {
        this(IItemHelper.singleProperties().rarity(Rarity.EPIC), 3.5F, -2.1F);
    }

    public static boolean isOpen() {
        return BOOK_ID.equals(PatchouliAPI.get().getOpenBookGui());
    }

    public static @NotNull Component getSubtitle() {
        return PatchouliAPI.get().getSubtitle(BOOK_ID);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.fourthCurse"));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.fourthCurseAlteration2", ChatFormatting.GOLD, "20%");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theAcknowledgment1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theAcknowledgment2");
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
        TooltipHandler.line(list);
    }

    public List<Component> getTooltipInBag(List<Component> list) {
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.antiqueBagSlot");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.theAcknowledgmentBag", ChatFormatting.GOLD, "8%");
        return list;
    }

    public @Nonnull InteractionResultHolder<ItemStack> use(Level world, @NotNull Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer server) {
            PatchouliAPI.get().openBookGUI(server, BOOK_ID);
        }
        return InteractionResultHolder.success(stack);
    }

    public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity target, LivingEntity attacker) {
        target.igniteForSeconds(4);
        return super.hurtEnemy(stack, target, attacker);
    }

    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 24;
    }
}
