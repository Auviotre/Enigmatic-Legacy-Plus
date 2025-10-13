package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
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
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class AvariceScroll extends CursedCurioItem {
    public static final String EFFECT_TAG = EnigmaticLegacy.MODID + ":avarice_scroll_effect";
    public static ModConfigSpec.IntValue emeraldChance;

    public AvariceScroll() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant());
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.avarice_scroll").push("cursedItems.avariceScroll");
        emeraldChance = builder.defineInRange("emeraldChance", 15, 0, 100);
        builder.pop(2);
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
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.avariceScroll5", ChatFormatting.GOLD, emeraldChance.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.avariceScroll6");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.avariceScroll7");
        } else TooltipHandler.holdShift(list);

        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
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


    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onLivingDrops(@NotNull LivingDropsEvent event) {
            LivingEntity victim = event.getEntity();
            if (event.isRecentlyHit() && event.getSource().getEntity() != null && event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (EnigmaticHandler.hasCurio(attacker, EnigmaticItems.AVARICE_SCROLL) && attacker.getRandom().nextInt(100) < emeraldChance.get()) {
                    ItemEntity itemEntity = new ItemEntity(victim.level(), victim.getX(), victim.getY(), victim.getZ(), Items.EMERALD.getDefaultInstance());
                    itemEntity.setDefaultPickUpDelay();
                    event.getDrops().add(itemEntity);
                }
            }
        }
    }
}
