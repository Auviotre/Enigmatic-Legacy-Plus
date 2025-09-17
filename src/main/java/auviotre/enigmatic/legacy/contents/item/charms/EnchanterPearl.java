package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class EnchanterPearl extends CursedCurioItem {
    public EnchanterPearl() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant());
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantersPearl1");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantersPearl2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantersPearl3");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantersPearl4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantersPearl5");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantersPearl6");
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        if (EnigmaticHandler.isTheCursedOne(context.entity()))
            CuriosApi.addSlotModifier(attributes, "charm", getLocation(this), 1.0, AttributeModifier.Operation.ADD_VALUE);
        return attributes;
    }

    public List<Component> getAttributesTooltip(@NotNull List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }
}
