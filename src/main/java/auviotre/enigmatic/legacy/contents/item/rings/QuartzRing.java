package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import top.theillusivec4.curios.api.SlotContext;

public class QuartzRing extends BaseCurioItem {
    public static ModConfigSpec.DoubleValue resistance;

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.quartz_ring").push("else.quartzRing");
        resistance = builder.defineInRange("specialDamageResistance", 0.25, 0, 1);
        builder.pop(2);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ARMOR, new AttributeModifier(IItemHelper.getLocation(this), 2, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.LUCK, new AttributeModifier(IItemHelper.getLocation(this), 1.5, AttributeModifier.Operation.ADD_VALUE));
        builder.put(EnigmaticAttributes.MAGIC_PROTECTION, new AttributeModifier(IItemHelper.getLocation(this), resistance.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }
}
