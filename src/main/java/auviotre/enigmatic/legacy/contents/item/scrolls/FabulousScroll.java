package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class FabulousScroll extends HeavenScroll {

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.fabulousScroll1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.fabulousScroll2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.fabulousScroll3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.fabulousScroll4");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.heavenScroll4");
        } else TooltipHandler.holdShift(list);
    }

    public void curioTick(@NotNull SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer player) {
            if (canFly(player, true)) {
                if (this.shouldCheckXpDrain(player) && !player.getData(EnigmaticAttachments.ENIGMATIC_DATA).isInBeaconRange() && player.getRandom().nextDouble() < 0.075)
                    player.giveExperiencePoints(-1);
                player.getData(EnigmaticAttachments.ENIGMATIC_DATA).InBeaconRangeTick();
                if (!canFly(player, true)) {
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100));
                }
                player.getAttributes().addTransientAttributeModifiers(this.getModifiers());
            } else {
                player.getAttributes().removeAttributeModifiers(this.getModifiers());
            }
        }
    }

    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return super.canEquip(slotContext, stack) && !EnigmaticHandler.hasCurio(slotContext.entity(), EnigmaticItems.HEAVEN_SCROLL);
    }
}
