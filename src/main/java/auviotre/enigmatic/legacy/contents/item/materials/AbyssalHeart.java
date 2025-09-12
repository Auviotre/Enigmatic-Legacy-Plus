package auviotre.enigmatic.legacy.contents.item.materials;

import auviotre.enigmatic.legacy.api.entity.IAbyssalHeartBearer;
import auviotre.enigmatic.legacy.api.item.IEldritch;
import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AbyssalHeart extends BaseCursedItem implements IEldritch, ITaintable {
    public AbyssalHeart() {
        super(BaseItem.defaultSingleProperties().rarity(Rarity.EPIC));
        NeoForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (!Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.abyssalHeart1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.abyssalHeart2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.abyssalHeart3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.abyssalHeart4");
            TooltipHandler.line(list);
        }
        TooltipHandler.worthyOnly(list, stack);
    }


    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && EnigmaticHandler.isTheWorthyOne(player) && !level.isClientSide()) {
            this.handleTaintable(stack, player);
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (ITaintable.isTainted(stack)) stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
            else  stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
    }

    @SubscribeEvent
    public void onDrops(@NotNull LivingDropsEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed instanceof EnderDragon || killed instanceof IAbyssalHeartBearer) {
            if (event.isRecentlyHit() && event.getSource().getEntity() instanceof Player player && EnigmaticHandler.isTheWorthyOne(player)) {
                CompoundTag data = EnigmaticHandler.getPersistedData(player);
                if (data.getInt("AbyssalHeartsGained") < 5) {
                    ((IAbyssalHeartBearer) killed).dropAbyssalHeart(player);
                }
            }
        }
    }
}
