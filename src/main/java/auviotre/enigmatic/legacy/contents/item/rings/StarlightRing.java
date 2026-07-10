package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class StarlightRing extends BaseCurioItem {
    public static ModConfigSpec.IntValue resistance;

    public StarlightRing() {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.RARE));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.starlight_ring").push("else.starlightRing");
        resistance = builder.defineInRange("specialDamageResistance", 30, 0, 100);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.starlightRing1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.starlightRing2");
    }

    @OnlyIn(Dist.CLIENT)
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        List<Component> list = super.getAttributesTooltip(tooltips, context, stack);
        if (!list.isEmpty())
            list.add(Component.translatable("attribute.modifier.take.1", resistance.get(), Component.translatable("tooltip.enigmaticlegacy.quartzRingAttribute")).withStyle(ChatFormatting.BLUE));
        return list;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(IItemHelper.getLocation(this), 6, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.LUCK, new AttributeModifier(IItemHelper.getLocation(this), 4, AttributeModifier.Operation.ADD_VALUE));
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

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (event.getNewDamage() >= Float.MAX_VALUE) return;
            LivingEntity entity = event.getEntity();
            if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.QUARTZ_RING) && event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
                event.setNewDamage(event.getNewDamage() * (1.0F - 0.01F * resistance.get()));
            }
        }
    }
}
