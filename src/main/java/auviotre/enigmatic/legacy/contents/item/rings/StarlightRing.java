package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class StarlightRing extends BaseCurioItem {
    public static ModConfigSpec.DoubleValue resistance;

    public StarlightRing() {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.RARE));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.starlight_ring").push("else.starlightRing");
        resistance = builder.defineInRange("specialDamageResistance", 0.3, 0, 1);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.starlightRing1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.starlightRing2");
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(IItemHelper.getLocation(this), 6, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.LUCK, new AttributeModifier(IItemHelper.getLocation(this), 4, AttributeModifier.Operation.ADD_VALUE));
        builder.put(EnigmaticAttributes.MAGIC_PROTECTION, new AttributeModifier(IItemHelper.getLocation(this), resistance.getAsDouble(), AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer player && player.tickCount % 3 == 0) {
            player.getData(EnigmaticAttachments.ENIGMATIC_DATA).setInBeaconRangeTick(5);
        }
    }

    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player)
            player.getData(EnigmaticAttachments.ENIGMATIC_DATA).setInBeaconRangeTick(0);
    }
}
