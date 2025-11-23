package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class QuartzRing extends BaseCurioItem {
    @OnlyIn(Dist.CLIENT)
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        List<Component> list = super.getAttributesTooltip(tooltips, context, stack);
        if (!list.isEmpty()) list.add(Component.translatable("attribute.modifier.take.1", "25", Component.translatable("tooltip.enigmaticlegacy.quartzRingAttribute")).withStyle(ChatFormatting.BLUE));
        return list;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ARMOR, new AttributeModifier(getLocation(this), 2, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.LUCK, new AttributeModifier(getLocation(this), 1.5, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity entity = event.getEntity();
            if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.QUARTZ_RING) && event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
                event.setNewDamage(event.getNewDamage() * 0.75F);
            }
        }
    }
}
