package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootContext;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class MiningCharm extends BaseCurioItem {
    public MiningCharm() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON));
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        List<Component> list = super.getAttributesTooltip(tooltips, context, stack);
        list.add(Component.translatable("attribute.modifier.plus.0", 1, Component.translatable("attribute.name.fortune_level")).withStyle(ChatFormatting.BLUE));
        return list;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.MINING_EFFICIENCY, new AttributeModifier(getLocation(this), 3, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(getLocation(this), 2, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    public int getFortuneLevel(SlotContext context, LootContext lootContext, ItemStack stack) {
        return super.getFortuneLevel(context, lootContext, stack) + 1;
    }
}
