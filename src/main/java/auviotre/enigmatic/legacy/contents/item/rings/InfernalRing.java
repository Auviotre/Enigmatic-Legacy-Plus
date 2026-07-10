package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class InfernalRing extends BaseCurioItem {
    public static ModConfigSpec.IntValue resistance;

    public InfernalRing() {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.UNCOMMON));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.infernal_ring").push("else.infernalRing");
        resistance = builder.defineInRange("specialDamageResistance", 60, 0, 100);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.infernalRing1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.infernalRing2");
            int value = resistance.get();
            if (EnigmaticHandler.hasCurio(Minecraft.getInstance().player, EnigmaticItems.HELL_BLADE_CHARM))
                value = Math.min(99, value / 3 * 4);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.infernalRing3", ChatFormatting.GOLD, value + "%");
        } else TooltipHandler.holdShift(list);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(IItemHelper.getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR, new AttributeModifier(IItemHelper.getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (event.getNewDamage() >= Float.MAX_VALUE) return;
            LivingEntity victim = event.getEntity();
            float damage = event.getNewDamage();
            if (damage <= victim.getAbsorptionAmount() || damage <= 0) return;
            if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.INFERNAL_RING) && victim.getMaxHealth() == victim.getHealth()) {
                int value = resistance.get();
                if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.HELL_BLADE_CHARM))
                    value = Math.min(99, value / 3 * 4);
                event.setNewDamage(damage * (1 - 0.01F * value));
            }
        }
    }
}
