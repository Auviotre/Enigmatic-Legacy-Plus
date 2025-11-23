package auviotre.enigmatic.legacy.handlers;

import auviotre.enigmatic.legacy.contents.item.materials.AbyssalHeart;
import auviotre.enigmatic.legacy.contents.item.rings.RedemptionRing;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TooltipHandler {
    char[] UPPERCASE_LETTERS = "ABCDEFGHJKLMNOPQRSTUVWXYZ".toCharArray();
    char[] LOWERCASE_LETTERS = "abcdeghjmnopqrsuvwxyz".toCharArray();
    char[] NUMBERS = "0123456789".toCharArray();
    char[] NORMAL_LETTERS = "ABCDEFGHJKLMNOPQRSTUVWXYZabcdeghjmnopqrsuvwxyz+/\\-=$%^_".toCharArray();

    static void line(List<Component> list) {
        list.add(Component.empty());
    }

    static void line(List<Component> list, String location) {
        list.add(Component.translatable(location));
    }

    static void line(List<Component> list, String location, Object... value) {
        list.add(Component.translatable(location, value));
    }

    static void line(List<Component> list, String location, ChatFormatting format, Object... value) {
        list.add(Component.translatable(location, value).withStyle(format));
    }

    static void holdShift(List<Component> list) {
        list.add(Component.translatable("tooltip.enigmaticlegacy.holdShift"));
    }

    static void cursedOnly(List<Component> list, ItemStack stack) {
        if (EnigmaticHandler.isBlessedItem(stack) && RedemptionRing.Helper.canUseRelic(Minecraft.getInstance().player)) {
            list.add(Component.translatable("tooltip.enigmaticlegacy.blessOneAvailable1").withStyle(ChatFormatting.GOLD));
            list.add(Component.translatable("tooltip.enigmaticlegacy.blessOneAvailable2").withStyle(ChatFormatting.GOLD));
        } else {
            ChatFormatting color = EnigmaticHandler.isTheCursedOne(Minecraft.getInstance().player) ? ChatFormatting.GOLD : ChatFormatting.DARK_RED;
            list.add(Component.translatable("tooltip.enigmaticlegacy.cursedOnesOnly1").withStyle(color));
            list.add(Component.translatable("tooltip.enigmaticlegacy.cursedOnesOnly2").withStyle(color));
        }
        if (stack.isEnchanted()) list.add(Component.empty());
    }

    static void worthyOnly(List<Component> list, ItemStack stack) {
        if (Screen.hasShiftDown()) {
            Player player = Minecraft.getInstance().player;
            ChatFormatting color = player != null && EnigmaticHandler.isTheWorthyOne(player) ? ChatFormatting.GOLD : ChatFormatting.DARK_RED;
            Component percent = Component.literal(String.format("%.01f%%", 100 * AbyssalHeart.abyssThreshold.get())).withStyle(ChatFormatting.GOLD);
            list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly1"));
            list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly2", percent));
            list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly3", percent));
            list.add(Component.empty());
            list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly4").withStyle(color).append(Component.literal(EnigmaticHandler.getSufferingTime(player)).withStyle(ChatFormatting.LIGHT_PURPLE)));
            if (stack.isEnchanted()) list.add(Component.empty());
        } else cursedOnly(list, stack);
    }


    @Contract("_, _ -> new")
    static @NotNull String obscureString(String string, RandomSource random) {
        char[] oldArray = string.toCharArray();
        char[] newArray = new char[oldArray.length];
        boolean code = false;

        for (int i = 0; i < oldArray.length; i++) {
            char ch = oldArray[i];
            newArray[i] = ch;

            if (ch == '§') {
                code = true;
                continue;
            } else if (code) {
                code = false;
                continue;
            } else if (ch == ' ')
                continue;
            else if (ch == '-')
                continue;

            char[] replacements;

            if (contains(UPPERCASE_LETTERS, ch)) replacements = UPPERCASE_LETTERS;
            else if (contains(LOWERCASE_LETTERS, ch)) replacements = LOWERCASE_LETTERS;
            else if (contains(NUMBERS, ch)) replacements = NUMBERS;
            else replacements = NORMAL_LETTERS;

            ch = replacements[random.nextInt(replacements.length)];
            newArray[i] = ch;
        }

        return new String(newArray);
    }

    static void obscure(List<Component> list, String location, RandomSource random) {
        MutableComponent component = Component.translatable(location);
        list.add(Component.literal(obscureString(component.getString(), random)).withStyle(component.getStyle()));
    }

    static void obscure(List<Component> list, String location, RandomSource random, ChatFormatting format, Object... value) {
        MutableComponent component = Component.translatable(location, value).withStyle(format);
        list.add(Component.literal(obscureString(component.getString(), random)).withStyle(component.getStyle()));
    }

    private static boolean contains(char[] array, char ch) {
        for (char ach : array) if (ach == ch) return true;
        return false;
    }
}
