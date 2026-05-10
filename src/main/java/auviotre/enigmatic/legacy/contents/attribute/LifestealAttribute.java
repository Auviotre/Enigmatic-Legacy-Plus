package auviotre.enigmatic.legacy.contents.attribute;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

public class LifestealAttribute extends PercentageAttribute {
    public LifestealAttribute() {
        super("attribute.name.lifesteal", 0.0, 0.0, 64.0);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && !attacker.level().isClientSide()) {
                AttributeInstance attribute = attacker.getAttribute(EnigmaticAttributes.LIFESTEAL);
                ;
                float lifesteal = attribute == null ? 0.0F : (float) attribute.getValue();
                attacker.heal(event.getNewDamage() * lifesteal);
            }
        }
    }
}
