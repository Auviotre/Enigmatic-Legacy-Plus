package auviotre.enigmatic.legacy.contents.item.legacy;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.gui.LoreInscriberMenu;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class LoreInscriber extends BaseItem {
    public LoreInscriber() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber2", ChatFormatting.GOLD, Component.translatable("item.enigmaticlegacyplus.lore_fragment"));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber5");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber7");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber8");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber9");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber10");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreInscriber11");
        } else TooltipHandler.holdShift(list);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.swing(hand);
        if (!level.isClientSide)
            player.openMenu(new LoreInscriberMenu.Provider(Component.translatable("gui.enigmaticlegacy.lore_inscriber").withStyle(ChatFormatting.GRAY)));
        return InteractionResultHolder.success(stack);
    }

    public interface Helper {

        static ItemStack setDisplayName(ItemStack stack, String name) {
            stack.set(DataComponents.ITEM_NAME, Component.literal(name));
            return stack;
        }

        static ItemStack addLoreString(ItemStack stack, String string) {
            if (!stack.has(DataComponents.LORE)) return stack;
            ItemLore lore = stack.get(DataComponents.LORE);
            Objects.requireNonNull(lore);
            stack.set(DataComponents.LORE, lore.withLineAdded(Component.literal(string)));
            return stack;
        }

        static ItemStack setLoreString(ItemStack stack, String string, int index) {
            if (!stack.has(DataComponents.LORE)) return stack;
            List<Component> list = Objects.requireNonNull(stack.get(DataComponents.LORE)).lines();
            if (list.size() - 1 >= index) list.set(index, Component.literal(string));
            else list.add(Component.literal(string));
            stack.set(DataComponents.LORE, new ItemLore(list));
            return stack;
        }

        static ItemStack removeLoreString(ItemStack stack, int index) {
            if (!stack.has(DataComponents.LORE)) return stack;
            List<Component> list = Objects.requireNonNull(stack.get(DataComponents.LORE)).lines();
            if (!list.isEmpty()) {
                if (index == -1) list.removeLast();
                else if (list.size() - 1 >= index) list.remove(index);
            }
            stack.set(DataComponents.LORE, new ItemLore(list));
            return stack;
        }

        static ItemStack mergeDisplayData(ItemStack from, ItemStack to) {
            Component name = from.get(DataComponents.ITEM_NAME);
            if (name != null) to.set(DataComponents.ITEM_NAME, name);
            ItemLore lore = from.get(DataComponents.LORE);
            if (lore != null) to.set(DataComponents.LORE, lore);
            return to;
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAnvilUpdate(@NotNull AnvilUpdateEvent event) {
            if (event.getLeft().getCount() == 1) {
                ItemStack lore = event.getRight();
                if (lore.is(EnigmaticItems.LORE_FRAGMENT) && lore.has(DataComponents.ITEM_NAME)) {
                    event.setCost(4);
                    event.setMaterialCost(1);
                    event.setOutput(Helper.mergeDisplayData(lore, event.getLeft().copy()));
                }
            }
        }
    }

    public static class Fragment extends BaseItem {
        public Fragment() {
            super(defaultProperties(16));
        }

        @OnlyIn(Dist.CLIENT)
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreFragment1", ChatFormatting.GOLD, Component.translatable("item.enigmaticlegacyplus.lore_inscriber"));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreFragment2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.loreFragment3");
        }
    }

    public static class Parser {
        private final boolean isLore;
        private final boolean removeString;
        private int loreIndex;
        private String handledString;

        private Parser(String string) {
            this.loreIndex = -1;
            this.handledString = string;
            this.isLore = this.handledString.startsWith("!");
            this.removeString = this.handledString.startsWith("-!");

            if (this.isLore) {
                this.handledString = this.handledString.replaceFirst("!", "");
                String index = Parser.parseIndex(this.handledString);
                this.loreIndex = Integer.parseInt(index);

                if (this.loreIndex != -1) {
                    this.handledString = this.handledString.replaceFirst(index, "");
                }
            } else if (this.removeString) {
                this.handledString = this.handledString.replaceFirst("-!", "");
                String index = Parser.parseIndex(this.handledString);
                this.loreIndex = Integer.parseInt(index);

                if (this.loreIndex != -1) {
                    this.handledString = this.handledString.replaceFirst(index, "");
                }
            }

            this.handledString = Parser.parseFormatting(this.handledString);
        }

        public static Parser parseField(String field) {
            return new Parser(field);
        }

        private static String parseFormatting(String field) {
            String formatter = Component.translatable("tooltip.enigmaticlegacy.paragraph").getString();
            String subformat = Component.translatable("tooltip.enigmaticlegacy.subformat").getString();

            return field.replace(subformat, formatter);
        }

        private static String parseIndex(String field) {
            String number = "";
            int index = -1;

            for (char symbol : field.toCharArray()) {
                if (Character.isDigit(symbol)) number = number + symbol;
                else break;
                if (number.length() >= 2) break;
            }

            if (!number.isEmpty()) return number;

            return "" + index;
        }

        public boolean isLoreString() {
            return this.isLore;
        }

        public boolean shouldRemoveString() {
            return this.removeString;
        }

        public int getLoreIndex() {
            return this.loreIndex;
        }

        public String getFormattedString() {
            return this.handledString;
        }
    }
}
