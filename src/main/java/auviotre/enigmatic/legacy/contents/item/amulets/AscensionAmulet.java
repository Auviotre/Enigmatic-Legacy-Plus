package auviotre.enigmatic.legacy.contents.item.amulets;

import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class AscensionAmulet extends EnigmaticAmulet {
    public AscensionAmulet() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.RARE));
    }

    @OnlyIn(Dist.CLIENT)
    protected void addAttributes(List<Component> list, ItemStack stack) {
        TooltipHandler.line(list, "curios.modifiers.charm", ChatFormatting.GOLD);
        for (AmuletColor color : AmuletColor.values()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletModifier" + color.toString());
        }
    }


    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().addTransientAttributeModifiers(getAllModifiers(entity));
    }
}
