package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
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

import java.util.ArrayList;
import java.util.List;

public class LivingOde extends BaseItem {
    public static ModConfigSpec.DoubleValue effectiveRange;
    public static ModConfigSpec.IntValue redirectResistance;
    public static ModConfigSpec.IntValue cooldown;

    public LivingOde() {
        super(IItemHelper.singleProperties().rarity(Rarity.RARE));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.ode_to_living").push("else.odeToLiving");
        effectiveRange = builder.defineInRange("effectiveRange", 24.0, 4.0, 96.0);
        redirectResistance = builder.defineInRange("specialDamageResistance", 50, 0, 100);
        cooldown = builder.defineInRange("cooldown", 1200, 200, 2000);
        builder.pop(2);
    }

    public static boolean containInList(ItemStack stack, LivingEntity entity) {
        List<String> list = stack.get(EnigmaticComponents.BLACKLIST);
        return list != null && list.contains(entity.getType().getDescriptionId());
    }

    public static void addBlackList(ItemStack stack, LivingEntity entity) {
        List<String> list = new ArrayList<>(stack.getOrDefault(EnigmaticComponents.BLACKLIST, List.of()));
        String s = entity.getType().getDescriptionId();
        if (!list.contains(s)) list.add(s);
        stack.set(EnigmaticComponents.BLACKLIST, list);
    }

    public static void removeFromList(ItemStack stack, LivingEntity entity) {
        List<String> list = new ArrayList<>(stack.getOrDefault(EnigmaticComponents.BLACKLIST, List.of()));
        list.remove(entity.getType().getDescriptionId());
        stack.set(EnigmaticComponents.BLACKLIST, list);
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
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.odeToLiving2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.odeToLiving3");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.odeToLiving4");
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.getCooldowns().isOnCooldown(this) && player.level() instanceof ServerLevel level) {
            boolean noAction = true;
            if (containInList(stack, target)) {
                removeFromList(stack, target);
                noAction = false;
            }
            if (target instanceof NeutralMob neutral && neutral.isAngry()) {
                if (target instanceof Animal && neutral.getTarget() == player) neutral.stopBeingAngry();
                noAction = false;
            }
            if (noAction) return InteractionResult.PASS;
            level.sendParticles(ParticleTypes.HEART, target.getX(), target.getEyeY(), target.getZ(), 5, target.getBbWidth(), 0.1D, target.getBbWidth(), 0.1D);
            if (player.hasInfiniteMaterials()) player.getCooldowns().addCooldown(this, cooldown.get());
            return InteractionResult.SUCCESS;
        } else return InteractionResult.PASS;
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
            if (entity instanceof LivingEntity attacker && !attacker.level().isClientSide()) {
                if (attacker.getWeaponItem().is(EnigmaticItems.ODE_TO_LIVING)) {
                    if (event.getEntity() instanceof Animal animal && isProtectedAnimal(attacker, animal))
                        addBlackList(attacker.getWeaponItem(), event.getEntity());
                }
                if (EnigmaticHandler.hasItem(attacker, EnigmaticItems.ODE_TO_LIVING)) {
                    if (event.getEntity() instanceof Animal animal && isProtectedAnimal(attacker, animal)) {
                        event.setCanceled(EnigmaticHandler.isNotAttacker(animal, attacker));
                    }
                }
            }
        }

        private static boolean isProtectedAnimal(LivingEntity entity, Animal animal) {
            ItemStack book = EnigmaticHandler.getItem(entity, EnigmaticItems.ODE_TO_LIVING);
            return !book.isEmpty() && !containInList(book, animal);
        }
    }
}
