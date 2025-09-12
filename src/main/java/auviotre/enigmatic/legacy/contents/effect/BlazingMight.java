package auviotre.enigmatic.legacy.contents.effect;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

public class BlazingMight extends MobEffect {
    public BlazingMight() {
        super(MobEffectCategory.BENEFICIAL, 0xFF5000);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, EnigmaticLegacy.location("blazing_might"), 3.0D, AttributeModifier.Operation.ADD_VALUE);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityHurt(@NotNull LivingDamageEvent.Post event) {
        if (event.getEntity().hasEffect(EnigmaticEffects.BLAZING_MIGHT)) {
            event.getEntity().removeEffect(EnigmaticEffects.BLAZING_MIGHT);
        }
    }
}
