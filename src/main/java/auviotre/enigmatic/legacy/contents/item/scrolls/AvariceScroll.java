package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class AvariceScroll extends CursedCurioItem {
    public static final String EFFECT_TAG = EnigmaticLegacy.MODID + ":avarice_scroll_effect";

    public AvariceScroll() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant());
        NeoForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.avariceScroll1", ChatFormatting.GOLD, 1);
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.avariceScroll2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.avariceScroll3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.avariceScroll4");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.avariceScroll5", ChatFormatting.GOLD, "15%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.avariceScroll6");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.avariceScroll7");
        } else TooltipHandler.holdShift(list);

        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }


    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && EnigmaticHandler.isTheCursedOne(context.entity());
    }

    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack curio) {
        return super.getFortuneLevel(slotContext, lootContext, curio) + 1;
    }


    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return true;
    }


    public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
        return true;
    }


    @SubscribeEvent
    public void onLivingDrops(@NotNull LivingDropsEvent event) {
        LivingEntity victim = event.getEntity();
        if (event.isRecentlyHit() && event.getSource().getEntity() != null && event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (EnigmaticHandler.hasCurio(attacker, this) && attacker.getRandom().nextFloat() < 0.15F) {
                ItemEntity itemEntity = new ItemEntity(victim.level(), victim.getX(), victim.getY(), victim.getZ(), Items.EMERALD.getDefaultInstance());
                itemEntity.setDefaultPickUpDelay();
                event.getDrops().add(itemEntity);
            }
        }
    }
}
