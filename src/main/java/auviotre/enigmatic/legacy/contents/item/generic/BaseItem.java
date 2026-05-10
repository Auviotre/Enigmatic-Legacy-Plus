package auviotre.enigmatic.legacy.contents.item.generic;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.IItemDecorator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BaseItem extends Item {
    public BaseItem() {
        super(IItemHelper.properties());
    }

    public BaseItem(Properties properties) {
        super(properties);
    }

    public static BaseItem Unknown() {
        return new BaseItem(IItemHelper.singleProperties().component(DataComponents.CREATIVE_SLOT_LOCK, Unit.INSTANCE)) {
            public Component getName(ItemStack stack) {
                return Component.literal("?");
            }

            @OnlyIn(Dist.CLIENT)
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
                list.add(Component.literal("Coming soon...").withStyle(ChatFormatting.DARK_GRAY));
            }

            public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
                stack.shrink(1);
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    public static class WIPDecorator implements IItemDecorator {
        static final ResourceLocation SPRITE = EnigmaticLegacy.location("decorator/wip_warn");

        public boolean render(@NotNull GuiGraphics graphics, Font font, @NotNull ItemStack stack, int x, int y) {
            graphics.pose().pushPose();
            graphics.blitSprite(SPRITE, x, y, 16, 16);
            graphics.pose().popPose();
            return true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class UnknownDecorator implements IItemDecorator {
        static final ResourceLocation SPRITE = EnigmaticLegacy.location("decorator/unknown");

        public boolean render(@NotNull GuiGraphics graphics, Font font, @NotNull ItemStack stack, int x, int y) {
            graphics.pose().pushPose();
            graphics.blitSprite(SPRITE, x, y, 16, 16);
            graphics.pose().popPose();
            return true;
        }
    }
}
