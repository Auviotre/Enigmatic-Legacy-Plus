package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class DesolationRing extends BaseCurioItem {
    public DesolationRing() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.EPIC).component(EnigmaticComponents.ELDRITCH, true));
    }
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.desolationRing1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.desolationRing2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.desolationRing3");
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.worthyOnly(list, stack);
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (!EnigmaticHandler.isTheWorthyOne(entity)) return;
        if (entity instanceof Player) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (timer < 1.0F) stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (isSelected && EnigmaticHandler.isTheWorthyOne(livingEntity)) stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
            else stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
    }


    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        public static final Map<LivingEntity, AABB> BOXES = new WeakHashMap<>();

        @SubscribeEvent
        private static void onPlayerTick(EntityTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof LivingEntity entity) {
                if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.DESOLATION_RING))
                    BOXES.put(entity, entity.getBoundingBox().inflate(64));
                else BOXES.remove(entity);
            }
            if (event.getEntity() instanceof ItemEntity itemEntity) {
                if (BOXES.values().stream().anyMatch(itemEntity.getBoundingBox()::intersects)) {
                    itemEntity.tick();
                    itemEntity.tick();
                }
            }
        }

        @SubscribeEvent
        private static void onEntitySpawn(@NotNull FinalizeSpawnEvent event) {
            if (event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION) {
                LivingEntity entity = event.getEntity();
                if (entity instanceof NeutralMob) {
                    if (BOXES.values().stream().anyMatch(entity.getBoundingBox()::intersects)) {
                        event.setSpawnCancelled(true);
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onLivingChangeTarget(@NotNull LivingChangeTargetEvent event) {
            LivingEntity entity = event.getEntity();
            LivingEntity target = event.getNewAboutToBeSetTarget();
            if (entity instanceof Targeting targetedEntity && EnigmaticHandler.hasCurio(target, EnigmaticItems.DESOLATION_RING)) {
                if (entity.getLastHurtByMob() != target && (targetedEntity.getTarget() == null || !targetedEntity.getTarget().isAlive())) {
                    if (entity instanceof Enemy) event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onLivingDrops(@NotNull LivingDropsEvent event) {
            if (event.isRecentlyHit() && event.getSource().getEntity() != null && event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (EnigmaticHandler.hasCurio(attacker, EnigmaticItems.DESOLATION_RING)) {
                    event.getDrops().clear();
                }
            }
        }
    }
}
