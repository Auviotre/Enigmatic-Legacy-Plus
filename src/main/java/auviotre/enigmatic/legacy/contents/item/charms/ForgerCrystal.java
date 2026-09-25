package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.contents.item.rings.RedemptionRing;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class ForgerCrystal extends CursedCurioItem {
    public ForgerCrystal() {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.RARE), true);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enchantersPearl1");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgerCrystal1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgerCrystal2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgerCrystal3");
            LocalPlayer player = Minecraft.getInstance().player;
            if (RedemptionRing.Helper.canUseRelic(player) && player != null) {
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.forgerCrystal4");
            }
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && !EnigmaticHandler.hasCurio(context.entity(), EnigmaticItems.FORGER_GEM);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        if (context.entity() instanceof Player) {
            CuriosApi.addSlotModifier(attributes, "charm", IItemHelper.getLocation(this), 1.0, AttributeModifier.Operation.ADD_VALUE);
        }
        return attributes;
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAnvilRepair(@NotNull AnvilRepairEvent event) {
            Player player = event.getEntity();
            if (EnigmaticHandler.hasCurio(player, EnigmaticItems.FORGER_CRYSTAL)) {
                event.setBreakChance(0.0F);
            }
        }

        @SubscribeEvent
        private static void onAnvilUse(@NotNull AnvilUpdateEvent event) {
            Player player = event.getPlayer();
            if (EnigmaticHandler.hasCurio(player, EnigmaticItems.FORGER_CRYSTAL)) {
                ItemStack left = event.getLeft();
                ItemStack right = event.getRight();
                if (!left.isEmpty() && !right.isEmpty()) {
                    boolean check = left.isDamageableItem() && !left.has(DataComponents.UNBREAKABLE) && !right.has(DataComponents.UNBREAKABLE);
                    if (check)
                        check = left.isRepairable() && (left.getItem() instanceof TieredItem || left.getItem() instanceof ArmorItem);
                    if (player.getAbilities().instabuild && left.isDamageableItem()) check = true;
                    if (left.is(right.getItem()) && check && left.getDamageValue() == 0 && right.getDamageValue() == 0 && !left.isEnchanted() && !right.isEnchanted()) {
                        ItemStack copy = left.copy();
                        copy.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
                        copy.set(DataComponents.REPAIR_COST, copy.getOrDefault(DataComponents.REPAIR_COST, 0) + 8);
                        if (event.getName() != null && !event.getName().isEmpty())
                            copy.set(DataComponents.CUSTOM_NAME, Component.literal(event.getName()));
                        event.setOutput(copy);
                        event.setCost(50);
                    }
                }
            }
        }
    }
}
