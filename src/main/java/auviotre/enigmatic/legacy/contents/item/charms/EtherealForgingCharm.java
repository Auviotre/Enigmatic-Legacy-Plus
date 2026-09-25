package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticEnchantments;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
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
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class EtherealForgingCharm extends BaseCurioItem {
    public EtherealForgingCharm() {
        super(IItemHelper.singleProperties().rarity(Rarity.UNCOMMON).component(EnigmaticComponents.BOOLEAN, false));
    }

    public Component getName(@NotNull ItemStack stack) {
        return stack.getOrDefault(EnigmaticComponents.BOOLEAN, false) ? Component.translatable("item.enigmaticlegacyplus.ethereal_forging_charm_active") : Component.translatable("item.enigmaticlegacyplus.ethereal_forging_charm");
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.etherealForgingCharm1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.etherealForgingCharm2");
        if (stack.getOrDefault(EnigmaticComponents.BOOLEAN, false)) {
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.etherealForgingCharm3");
        }
        if (Minecraft.getInstance().level != null) {
            var holder = EnigmaticHandler.get(Minecraft.getInstance().level, Registries.ENCHANTMENT, EnigmaticEnchantments.ETHERIC_RESONANCE);
            if (stack.getEnchantmentLevel(holder) > 0)
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.etherealForgingCharmBuff");
        }
        if (stack.isEnchanted()) TooltipHandler.line(list);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(EnigmaticAttributes.ETHERIUM_SHIELD, new AttributeModifier(IItemHelper.getLocation(this), stack.getOrDefault(EnigmaticComponents.BOOLEAN, false) ? 0.05 : 0.03, AttributeModifier.Operation.ADD_VALUE));
        return attributes;
    }
}