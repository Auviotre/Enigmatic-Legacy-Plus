package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LivingOde extends BaseItem {
    public static ModConfigSpec.DoubleValue effectiveRange;
    public static ModConfigSpec.IntValue redirectResistance;

    public LivingOde() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.ode_to_living").push("else.odeToLiving");
        effectiveRange = builder.defineInRange("effectiveRange", 24.0, 4.0, 96.0);
        redirectResistance = builder.defineInRange("specialDamageResistance", 50, 0, 100);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.inInventory", ChatFormatting.GOLD);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.animalGuidebook1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.animalGuidebook2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.hunterGuidebook1", ChatFormatting.GOLD, String.format("%.0f", effectiveRange.get()));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.hunterGuidebook2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.odeToLiving1", ChatFormatting.GOLD, redirectResistance.get() + "%");
        if (EnigmaticHandler.isTheCursedOne(Minecraft.getInstance().player)) {
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.secondCurse"));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.secondCurseAlteration2");
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onFindTarget(@NotNull LivingChangeTargetEvent event) {
            LivingEntity entity = event.getEntity();
            LivingEntity target = event.getNewAboutToBeSetTarget();
            if (EnigmaticHandler.hasItem(target, EnigmaticItems.ODE_TO_LIVING)) {
                if (entity instanceof NeutralMob && entity.getLastAttacker() != target) event.setCanceled(true);
                else if (entity instanceof Animal) event.setCanceled(true);
            }
        }

        @SubscribeEvent
        private static void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
            Entity entity = event.getSource().getEntity();
            if (entity instanceof LivingEntity attacker && EnigmaticHandler.hasItem(attacker, EnigmaticItems.ODE_TO_LIVING)) {
                if (event.getEntity() instanceof Animal animal) {
                    event.setCanceled(!EnigmaticHandler.isAttacker(animal, attacker));
                }
            }
        }
    }
}
