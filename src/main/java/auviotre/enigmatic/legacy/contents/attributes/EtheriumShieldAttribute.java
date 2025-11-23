package auviotre.enigmatic.legacy.contents.attributes;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.NeoForgeConfig;
import net.neoforged.neoforge.common.PercentageAttribute;

import java.util.Locale;

public class EtheriumShieldAttribute extends PercentageAttribute {
    public static final ResourceLocation BASE_ID = EnigmaticLegacy.location("base_etherium_threshold");

    public EtheriumShieldAttribute() {
        super("attribute.name.etherium_shield", 0.0, 0.0, 1.0);
        this.setSyncable(true);
    }

    public ResourceLocation getBaseId() {
        return BASE_ID;
    }

    public MutableComponent toBaseComponent(double value, double entityBase, boolean merged, TooltipFlag flag) {
        LocalPlayer player = Minecraft.getInstance().player;
        double currentBase = entityBase;
        double current = value;
        if (player != null) {
            AttributeInstance attribute = player.getAttribute(EnigmaticAttributes.ETHERIUM_SHIELD);
            if (attribute != null) {
                try {
                    AttributeInstance instance = new AttributeInstance(EnigmaticAttributes.ETHERIUM_SHIELD, (e) -> {
                    });
                    instance.replaceFrom(attribute);
                    instance.removeModifier(BASE_ID);
                    currentBase = instance.getValue();
                    instance.addPermanentModifier(new AttributeModifier(BASE_ID, value, AttributeModifier.Operation.ADD_VALUE));
                    current = instance.getValue();
                } catch (Exception ignore) {
                }
            }
        }

        MutableComponent component = Component.translatable("attribute.modifier.equals.0", toValueComponent(AttributeModifier.Operation.ADD_VALUE, current, flag), Component.translatable(this.getDescriptionId()));
        if (flag.isAdvanced() && !merged && NeoForgeConfig.COMMON.attributeAdvancedTooltipDebugInfo.get()) {
            double baseBonus = current - currentBase;
            String baseBonusText = String.format(Locale.ROOT, baseBonus > 0 ? " + %s" : " - %s", FORMAT.format(value * 100.0) + "%");
            Component debugInfo = Component.translatable("neoforge.attribute.debug.base", toValueComponent(AttributeModifier.Operation.ADD_VALUE, currentBase, flag), baseBonusText).withStyle(ChatFormatting.GRAY);
            component.append(CommonComponents.SPACE).append(debugInfo);
        }
        return component;
    }
}
