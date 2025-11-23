package auviotre.enigmatic.legacy.contents.effect;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

public class IchorCorrosion extends MobEffect {
    public IchorCorrosion() {
        super(MobEffectCategory.HARMFUL, 0xCE753D);
        ResourceLocation location = EnigmaticLegacy.location("effect.ichor_corrosion");
        this.addAttributeModifier(Attributes.ARMOR, location, -0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, location, -0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, location, -0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent.@NotNull Pre event) {
        if (event.getNewDamage() >= Float.MAX_VALUE) return;
        if (event.getEntity().hasEffect(EnigmaticEffects.ICHOR_CORROSION)) {
            MobEffectInstance effect = event.getEntity().getEffect(EnigmaticEffects.ICHOR_CORROSION);
            if (effect != null) {
                event.setNewDamage(event.getNewDamage() * (1.1F + 0.1F * effect.getAmplifier()));
            }
        }
    }
}
