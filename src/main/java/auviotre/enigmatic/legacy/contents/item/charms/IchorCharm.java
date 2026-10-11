package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class IchorCharm extends BaseCurioItem {
    public IchorCharm() {
        super(IItemHelper.singleProperties().fireResistant());
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.ichorCharm1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.ichorCharm2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.ichorCharm3");
        } else TooltipHandler.holdShift(list);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Post event) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (EnigmaticHandler.hasCurio(attacker, EnigmaticItems.ICHOR_CHARM)) {
                    Collection<MobEffectInstance> effects = event.getEntity().getActiveEffects();
                    int amplifier = -1;
                    int duration = -1;
                    for (MobEffectInstance effect : effects) {
                        if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                            amplifier = Math.max(amplifier, effect.getAmplifier());
                            duration =  Math.max(duration, effect.getDuration() / 2);
                        }
                    }
                    if (amplifier > -1) event.getEntity().addEffect(new MobEffectInstance(EnigmaticEffects.ICHOR_CORROSION,  duration, amplifier));
                }
            }
        }

        @SubscribeEvent
        private static void onApply(MobEffectEvent.@NotNull Applicable event) {
            if (event.getEffectInstance().is(EnigmaticEffects.ICHOR_CORROSION)) {
                if (EnigmaticHandler.hasCurio(event.getEntity(), EnigmaticItems.ICHOR_CHARM)) {
                    event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                }
            }
        }
    }
}
