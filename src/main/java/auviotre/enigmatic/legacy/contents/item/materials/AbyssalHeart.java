package auviotre.enigmatic.legacy.contents.item.materials;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.entity.AbyssalHeartBearer;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.api.item.ISharableItem;
import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AbyssalHeart extends BaseCursedItem implements ITaintable, ISharableItem {
    public static ModConfigSpec.IntValue abyssalHeartCount;
    public static ModConfigSpec.DoubleValue abyssThreshold;
    public static ModConfigSpec.BooleanValue specialVisualEffects;

    public AbyssalHeart() {
        super(IItemHelper.singleProperties().rarity(Rarity.EPIC).component(EnigmaticComponents.ELDRITCH, true));
    }

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        if (type == ModConfig.Type.CLIENT) {
            specialVisualEffects = builder.define("specialVisualEffects", true);
        } else {
            builder.push("abyssItems");
            abyssalHeartCount = builder.defineInRange("abyssalHeartCount", 5, 1, 10);
            abyssThreshold = builder.defineInRange("abyssThreshold", 0.995, 0.0, 1.0);
            builder.pop();
        }
    }

    public static int getMaxCount(Player player) {
        return abyssalHeartCount.get();
    }

    public static int getModifier(Level level, AbyssalHeartBearer bearer) {
        Set<UUID> attackers = bearer.getAllAttackers();
        if (attackers ==  null || attackers.isEmpty()) return 0;
        int count = 0;
        for (UUID uuid : attackers) {
            Player player = level.getPlayerByUUID(uuid);
            if (player != null) {
                CompoundTag data = EnigmaticHandler.getPersistedData(player);
                int heartsGained = data.getInt("AbyssalHeartsGained");
                count += heartsGained;
            }
        }
        return count;
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
        if (entity instanceof Player player && !level.isClientSide()) {
            this.handleTaintable(stack, player);
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (ITaintable.isTainted(stack))
                stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
            else stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
        if (entity instanceof LivingEntity living && !EnigmaticHandler.isTheWorthyOne(living)) {
            if (!living.hasEffect(EnigmaticEffects.ABYSS_CORRUPTION) && !living.hasInfiniteMaterials())
                living.addEffect(new MobEffectInstance(EnigmaticEffects.ABYSS_CORRUPTION, 100, 2));
        }
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    public boolean canTaint(Player player) {
        return EnigmaticHandler.isTheWorthyOne(player);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Post event) {
            LivingEntity victim = event.getEntity();
            if (victim instanceof EnderDragon || victim instanceof AbyssalHeartBearer) {
                if (event.getSource().getEntity() instanceof Player player && EnigmaticHandler.isTheWorthyOne(player)) {
                    ((AbyssalHeartBearer) victim).addAttacker(player.getUUID());
                }
            }
        }

        @SubscribeEvent
        private static void onDrops(@NotNull LivingDropsEvent event) {
            LivingEntity killed = event.getEntity();
            if (killed instanceof EnderDragon || killed instanceof AbyssalHeartBearer) {
                if (event.isRecentlyHit() && event.getSource().getEntity() instanceof Player player && EnigmaticHandler.isTheWorthyOne(player)) {
                    CompoundTag data = EnigmaticHandler.getPersistedData(player);
                    if (data.getInt("AbyssalHeartsGained") < getMaxCount(player)) {
                        ((AbyssalHeartBearer) killed).dropAbyssalHeart(player);
                    }
                }
            }
        }

        private static float getDamageModifier(int count) {
            double res = 0.5 + 0.5 * Math.exp(-count * 0.2) + 0.75 * Math.sqrt(count * 0.2);
            return Math.max((float) res, 1.0F);
        }

        private static float getResistance(int count) {
            double res = 0.25 * (Math.exp(-count * 0.2) + Math.exp(-count * 0.1) + Math.exp(-count / 15.0) + Math.exp(-count * 0.05));
            return Math.max((float) res, 0.0F);
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof EnderDragon dragon && EnigmaticHandler.isCurseBoosted(dragon)) {
                int modifier = getModifier(dragon.level(), (AbyssalHeartBearer) dragon);
                event.setNewDamage(event.getNewDamage() * getResistance(modifier));
            }
            if (event.getSource().getEntity() instanceof EnderDragon dragon && EnigmaticHandler.isCurseBoosted(dragon)) {
                int modifier = getModifier(dragon.level(), (AbyssalHeartBearer) dragon);
                DamageContainer container = event.getContainer();
                float enchantReduction = container.getReduction(DamageContainer.Reduction.ENCHANTMENTS);
                enchantReduction += container.getReduction(DamageContainer.Reduction.MOB_EFFECTS);
                enchantReduction *= 0.8F * (1.0F - (float) Math.exp(-modifier * 0.1F));
                event.setNewDamage((event.getNewDamage() + enchantReduction) * getDamageModifier(modifier));
            }
        }

        @SubscribeEvent
        private static void onTicked(EntityTickEvent.@NotNull Post event) {
            if (event.getEntity() instanceof EnderDragon dragon && EnigmaticHandler.isCurseBoosted(dragon)) {
                if (dragon.tickCount % 200 == 0) {
                    dragon.heal(Math.max(2.5F, dragon.getMaxHealth() * 0.008F));
                }
            }
        }
    }
}
