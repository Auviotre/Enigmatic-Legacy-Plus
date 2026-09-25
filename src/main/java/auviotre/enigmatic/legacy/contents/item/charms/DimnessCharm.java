package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.contents.item.materials.AbyssalHeart;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class DimnessCharm extends CursedCurioItem {
    public DimnessCharm() {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.EPIC).component(EnigmaticComponents.ELDRITCH, true));
    }

    public static boolean canEquip(LivingEntity entity) {
        return EnigmaticHandler.isTheCursedOne(entity) && EnigmaticHandler.getSufferingFraction(entity) >= Math.clamp(2 * AbyssalHeart.abyssThreshold.get() - 1, 0.05, 0.999);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.thirdCurse"));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.thirdCurseAlteration", ChatFormatting.GOLD, "40%");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.fourthCurse"));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.fourthCurseAlteration2", ChatFormatting.GOLD, "25%");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.dimnessCharm1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.dimnessCharm2");
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            Player player = Minecraft.getInstance().player;
            ChatFormatting color = player != null && canEquip(player) ? ChatFormatting.GOLD : ChatFormatting.DARK_RED;
            double rate = 200 * AbyssalHeart.abyssThreshold.get() - 100;
            Component percent = Component.literal(String.format("%.01f%%", Math.clamp(rate, 0.05, 99.9))).withStyle(ChatFormatting.GOLD);
            list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly1"));
            list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly2", percent));
            list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly3", percent));
            list.add(Component.empty());
            list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly4").withStyle(color).append(Component.literal(EnigmaticHandler.getSufferingTime(player)).withStyle(ChatFormatting.LIGHT_PURPLE)));
            if (stack.isEnchanted()) list.add(Component.empty());
        } else TooltipHandler.cursedOnly(list, stack);
    }
}
