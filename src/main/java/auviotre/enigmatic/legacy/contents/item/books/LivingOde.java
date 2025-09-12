package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LivingOde extends BaseItem {
    public LivingOde() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
        NeoForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.inInventory", ChatFormatting.GOLD);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.animalGuidebook1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.animalGuidebook2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.hunterGuidebook1", ChatFormatting.GOLD, 24);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.hunterGuidebook2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.odeToLiving1", ChatFormatting.GOLD, "50%");
        if (EnigmaticHandler.isTheCursedOne(Minecraft.getInstance().player)) {
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.secondCurse"));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.secondCurseAlteration2");
        }
    }

    @SubscribeEvent
    public void onFindTarget(@NotNull LivingChangeTargetEvent event) {
        LivingEntity entity = event.getEntity();
        LivingEntity target = event.getNewAboutToBeSetTarget();
        if (EnigmaticHandler.hasItem(target, this)) {
            if (entity instanceof NeutralMob && entity.getLastAttacker() != target) event.setCanceled(true);
            else if (entity instanceof Animal) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
        Entity entity = event.getSource().getEntity();
        if (entity instanceof LivingEntity attacker && EnigmaticHandler.hasItem(attacker, this)) {
            if (event.getEntity() instanceof Animal animal) {
                event.setCanceled(!EnigmaticHandler.isAttacker(animal, attacker));
            }
        }
    }
}
