package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class EnderRing extends BaseCurioItem {
    public static ModConfigSpec.IntValue buttonOffsetX;
    public static ModConfigSpec.IntValue buttonOffsetY;
    public static ModConfigSpec.IntValue buttonOffsetXCreative;
    public static ModConfigSpec.IntValue buttonOffsetYCreative;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        if (type == ModConfig.Type.CLIENT) {
            builder.push("enderButton");
            buttonOffsetX = builder.defineInRange("OffsetX", 0, -1024, 1024);
            buttonOffsetY = builder.defineInRange("OffsetY", 0, -1024, 1024);
            buttonOffsetXCreative = builder.defineInRange("OffsetXCreative", 0, -1024, 1024);
            buttonOffsetYCreative = builder.defineInRange("OffsetYCreative", 0, -1024, 1024);
            builder.pop();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderRing1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderRing2");
        try {
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.currentKeybind", ChatFormatting.LIGHT_PURPLE, KeyMapping.createNameSupplier("key.enderRing").get().getString().toUpperCase());
        } catch (NullPointerException ignored) {
        }
    }
}
