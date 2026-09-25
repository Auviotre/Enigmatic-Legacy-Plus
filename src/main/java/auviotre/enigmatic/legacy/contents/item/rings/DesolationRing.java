package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.packets.client.ChaosDescendingPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class DesolationRing extends BaseCurioItem {
    public static ModConfigSpec.IntValue effectiveRange;
    public static ModConfigSpec.IntValue cooldown;

    public DesolationRing() {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.EPIC).component(EnigmaticComponents.ELDRITCH, true));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.desolation_ring").push("abyssItems.desolationRing");
        effectiveRange = builder.defineInRange("effectiveRange", 64, 1, 128);
        cooldown = builder.defineInRange("cooldown", 240, 20, 1200);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.desolationRing1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.desolationRing2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.desolationRing3");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.desolationRing4");
            if (EnigmaticHandler.isAbyssBoosted(Minecraft.getInstance().player)) {
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.abyssBoost");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.desolationRingBoost");
            }
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.worthyOnly(list, stack);
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (!EnigmaticHandler.isTheWorthyOne(entity)) {
            if (!entity.hasEffect(EnigmaticEffects.ABYSS_CORRUPTION) && !entity.hasInfiniteMaterials())
                entity.addEffect(new MobEffectInstance(EnigmaticEffects.ABYSS_CORRUPTION, 100, 2));
            return;
        }
        if (entity instanceof Player) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (timer < 1.0F) stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (isSelected && EnigmaticHandler.isTheWorthyOne(livingEntity))
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

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        public static final Map<LivingEntity, AABB> BOXES = new WeakHashMap<>();

        @SubscribeEvent
        private static void onTick(EntityTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof LivingEntity entity) {
                if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.DESOLATION_RING))
                    BOXES.put(entity, entity.getBoundingBox().inflate(effectiveRange.get()));
                else BOXES.remove(entity);
            }
            if (event.getEntity() instanceof ItemEntity itemEntity) {
                if (BOXES.values().stream().anyMatch(itemEntity.getBoundingBox()::intersects)) {
                    itemEntity.age += 2;
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

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onLivingDrops(@NotNull LivingDeathEvent event) {
            LivingEntity entity = event.getEntity();
            if (entity instanceof Player player && player.getCooldowns().isOnCooldown(EnigmaticItems.DESOLATION_RING.get()))
                return;
            if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.DESOLATION_RING) && !event.getSource().is(Tags.DamageTypes.IS_TECHNICAL)) {
                List<Mob> list = entity.level().getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate((double) effectiveRange.get() / 16), victim -> {
                    if (victim.getLastAttacker() == victim) return false;
                    if (victim.getTarget() == entity) return false;
                    if (victim instanceof OwnableEntity ownable && ownable.getOwner() == entity) return false;
                    if (!entity.canAttack(victim)) return false;
                    return victim.isAlive() && victim != entity;
                });
                if (list.isEmpty()) return;
                boolean flag = false;
                float health = 0;
                for (Mob mob : list) {
                    health = mob.getHealth();
                    if (mob.hurt(event.getSource(), Float.MAX_VALUE)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    if (entity instanceof Player player)
                        player.getCooldowns().addCooldown(EnigmaticItems.DESOLATION_RING.get(), cooldown.get());
                    entity.setHealth(Math.max(health * 2, entity.getHealth() + 1));
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker && event.getEntity() instanceof Mob entity) {
                if (EnigmaticHandler.isAbyssBoosted(attacker) && EnigmaticHandler.hasCurio(attacker, EnigmaticItems.DESOLATION_RING)) {
                    if (entity.getTarget() == null && entity.getLastHurtByMob() == null && !entity.getPersistentData().getBoolean("DesolationExecution")) {
                        entity.getPersistentData().putBoolean("DesolationExecution", true);
                        entity.setHealth(Math.max(1.0F, entity.getHealth() - entity.getMaxHealth() * 0.2F));
                        entity.addEffect(new MobEffectInstance(EnigmaticEffects.ABYSS_CORRUPTION, 400, 1));
                        if (attacker.level() instanceof ServerLevel level)
                            PacketDistributor.sendToPlayersNear(level, null, entity.getX(), entity.getY(), entity.getZ(), 24, new ChaosDescendingPacket(entity.position(), true));
                        event.setAmount(event.getAmount() + entity.getMaxHealth() * 0.3F);
                    }
                }
            }
        }
    }
}
