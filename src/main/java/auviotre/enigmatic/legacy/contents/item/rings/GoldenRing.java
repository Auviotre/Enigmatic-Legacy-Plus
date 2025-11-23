package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class GoldenRing extends BaseCurioItem {
    @OnlyIn(Dist.CLIENT)
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        List<Component> list = super.getAttributesTooltip(tooltips, context, stack);
        if (list.isEmpty()) return list;
        if (EnigmaticHandler.isTheCursedOne(Minecraft.getInstance().player))
            list.add(Component.translatable("tooltip.enigmaticlegacy.goldenRing").withStyle(ChatFormatting.RED, ChatFormatting.STRIKETHROUGH));
        else list.add(Component.translatable("tooltip.enigmaticlegacy.goldenRing").withStyle(ChatFormatting.BLUE));
        return list;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.LUCK, new AttributeModifier(getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return true;
    }

    public boolean makesPiglinsNeutral(SlotContext context, ItemStack stack) {
        return true;
    }

    public boolean isPiglinCurrency(ItemStack stack) {
        return true;
    }
}
