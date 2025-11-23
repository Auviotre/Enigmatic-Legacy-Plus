package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class ForgerGem extends BaseCurioItem {
    public static ModConfigSpec.IntValue breakChanceModifier;
    public static ModConfigSpec.IntValue extraDurabilityModifier;
    public static ModConfigSpec.IntValue extraMaxDurability;

    public ForgerGem() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON).fireResistant());
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.forger_gem").push("else.forgerGem");
        breakChanceModifier = builder.defineInRange("breakChanceModifier", 20, 0, 100);
        extraDurabilityModifier = builder.defineInRange("extraDurabilityModifier", 4, 0, 20);
        extraMaxDurability = builder.defineInRange("extraMaxDurability", 20, 0, 100);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantersPearl1");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgerGem1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgerGem2");
            if (EnigmaticHandler.isTheOne(Minecraft.getInstance().player)) {
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgerGem3");
            }
        } else TooltipHandler.holdShift(list);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        if (context.entity() instanceof Player) {
            CuriosApi.addSlotModifier(attributes, "charm", getLocation(this), 1.0, AttributeModifier.Operation.ADD_VALUE);
        }
        return attributes;
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    public record ToolInfo(int originDurability, int extraDurability) {
        public static final MapCodec<ToolInfo> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.INT.fieldOf("origin").forGetter(ToolInfo::originDurability),
                Codec.INT.fieldOf("extra").forGetter(ToolInfo::extraDurability)
        ).apply(instance, ToolInfo::of));

        public static final Codec<ToolInfo> CODEC = MAP_CODEC.codec();

        public static final StreamCodec<RegistryFriendlyByteBuf, ToolInfo> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, ToolInfo::originDurability,
                ByteBufCodecs.INT, ToolInfo::extraDurability,
                ToolInfo::of);

        public static ToolInfo of(int originDurability, int extraDurability) {
            return new ToolInfo(originDurability, extraDurability);
        }
    }


    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAnvilRepair(@NotNull AnvilRepairEvent event) {
            Player player = event.getEntity();
            if (EnigmaticHandler.hasCurio(player, EnigmaticItems.FORGER_GEM)) {
                event.setBreakChance(event.getBreakChance() * breakChanceModifier.get() * 0.01F);
            }
        }
    }
}
