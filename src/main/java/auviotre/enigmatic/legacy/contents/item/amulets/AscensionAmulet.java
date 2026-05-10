package auviotre.enigmatic.legacy.contents.item.amulets;

import auviotre.enigmatic.legacy.api.item.IAmulet;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class AscensionAmulet extends EnigmaticAmulet {
    public AscensionAmulet() {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.RARE), Color.ALL);
    }

    @OnlyIn(Dist.CLIENT)
    protected void addAttributes(List<Component> list, ItemStack stack) {
        TooltipHandler.line(list, "curios.modifiers.amulet", ChatFormatting.GOLD);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantersPearl1", ChatFormatting.GOLD, 1);
        for (Color color : Color.values()) {
            if (color != Color.NULL && color != Color.ALL) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletModifier" + color, ChatFormatting.GOLD, IAmulet.getAttributeVar(color));
            }
        }
    }

    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack));
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().addTransientAttributeModifiers(getAllModifiers(entity));
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        CuriosApi.addSlotModifier(attributes, "charm", IItemHelper.getLocation(this), 1.0, AttributeModifier.Operation.ADD_VALUE);
        return attributes;
    }
}
