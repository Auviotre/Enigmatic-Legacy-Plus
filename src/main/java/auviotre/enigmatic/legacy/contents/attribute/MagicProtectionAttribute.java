package auviotre.enigmatic.legacy.contents.attribute;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

public class MagicProtectionAttribute extends PercentageAttribute {
    public MagicProtectionAttribute() {
        super("attribute.name.magic_protection", 0.0, -1024, 1);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            float newDamage = event.getNewDamage();
            if (Float.isInfinite(newDamage)) return;

            boolean isMagicDamage = event.getSource().is(Tags.DamageTypes.IS_MAGIC);
            if (!isMagicDamage) return;

            AttributeInstance attributeInstance = event.getEntity().getAttribute(EnigmaticAttributes.MAGIC_PROTECTION);
            if (attributeInstance == null) return;
            float protection = (float) attributeInstance.getValue();

            float protectionPercent = Math.max(1 - protection, 0);

            float finalDamage = protectionPercent * newDamage;
            event.setNewDamage(finalDamage);
        }
    }
}
