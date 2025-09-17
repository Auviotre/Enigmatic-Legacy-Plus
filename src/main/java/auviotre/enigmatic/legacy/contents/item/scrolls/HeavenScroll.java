package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
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
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class HeavenScroll extends BaseCurioItem {
    public HeavenScroll() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.heavenScroll1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.heavenScroll2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.heavenScroll3");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.heavenScroll4");
        } else TooltipHandler.holdShift(list);
    }

    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return super.canEquip(slotContext, stack) && !EnigmaticHandler.hasCurio(slotContext.entity(), EnigmaticItems.FABULOUS_SCROLL);
    }

    public void onUnequip(@NotNull SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().removeAttributeModifiers(this.getModifiers());
        if (entity instanceof Player player)
            player.getData(EnigmaticAttachments.ENIGMATIC_DATA).setInBeaconRangeTick(0);
    }

    public void curioTick(@NotNull SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer player) {
            if (canFly(player, false)) {
                if (this.shouldCheckXpDrain(player) && player.getRandom().nextDouble() < 0.04)
                    player.giveExperiencePoints(-1);
                player.getData(EnigmaticAttachments.ENIGMATIC_DATA).InBeaconRangeTick();
                if (!canFly(player, false)) {
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100));
                }
                player.getAttributes().addTransientAttributeModifiers(this.getModifiers());
            } else {
                player.getAttributes().removeAttributeModifiers(this.getModifiers());
            }
        }
    }

    protected Multimap<Holder<Attribute>, AttributeModifier> getModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        map.put(NeoForgeMod.CREATIVE_FLIGHT, new AttributeModifier(getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        return map;
    }

    protected boolean shouldCheckXpDrain(@NotNull Player player) {
        return !player.isCreative() && player.getAbilities().flying;
    }

    protected boolean canFly(@NotNull Player player, boolean rangeChecked) {
        return player.totalExperience > 0 && (rangeChecked || player.getData(EnigmaticAttachments.ENIGMATIC_DATA).isInBeaconRange());
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void getBreakSpeed(PlayerEvent.@NotNull BreakSpeed event) {
            LivingEntity entity = event.getEntity();
            if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.HEAVEN_SCROLL) || EnigmaticHandler.hasCurio(entity, EnigmaticItems.FABULOUS_SCROLL)) {
                if (entity instanceof Player player && player.getAbilities().flying)
                    event.setNewSpeed(event.getNewSpeed() * 5.0F);
            }
        }
    }
}
