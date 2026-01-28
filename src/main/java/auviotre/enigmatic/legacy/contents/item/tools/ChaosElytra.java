package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.BaseElytraItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.packets.client.ChaosDescendingPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerFlyableFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ChaosElytra extends BaseElytraItem {
    public static ModConfigSpec.IntValue specialDamageResistance;
    public static ModConfigSpec.DoubleValue flyingSpeedModifier;
    public static ModConfigSpec.DoubleValue descendingPowerModifier;
    public static ModConfigSpec.IntValue descendingCooldown;

    public ChaosElytra() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.EPIC).durability(3248).component(EnigmaticComponents.ELDRITCH, true));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.chaos_elytra").push("abyssItems.chaosElytra");
        specialDamageResistance = builder.defineInRange("specialDamageResistance", 80, 0, 100);
        flyingSpeedModifier = builder.defineInRange("flyingSpeedModifier", 1.2, 0, 10);
        descendingPowerModifier = builder.defineInRange("descendingPowerModifier", 1.6, 1, 10);
        descendingCooldown = builder.defineInRange("descendingCooldown", 500, 200, 1200);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.majesticElytra1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.majesticElytra2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.majesticElytra3");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.chaosElytra1", ChatFormatting.GOLD, specialDamageResistance.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.chaosElytra2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.chaosElytra3");
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.worthyOnly(list, stack);
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 21;
    }

    protected boolean flyingBoost(@NotNull Player player) {
        if (player.isFallFlying()) {
            Vec3 lookAngle = player.getLookAngle().scale(flyingSpeedModifier.get());
            Vec3 movement = player.getDeltaMovement().scale(0.54F);
            player.setDeltaMovement(movement.add(lookAngle));
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    protected void addParticle(Player player) {
        int amount = 3;
        double rangeModifier = 0.1;
        for (int counter = 0; counter <= amount; counter++) {
            Vec3 pos = player.position();
            pos = pos.add(Math.random() - 0.5, -1.0 + Math.random() - 0.5, Math.random() - 0.5);
//            player.level().addParticle(ParticleTypes.DRAGON_BREATH, true, pos.x, pos.y, pos.z, ((Math.random() - 0.5D) * 2.0D) * rangeModifier, ((Math.random() - 0.5D) * 2.0D) * rangeModifier, ((Math.random() - 0.5D) * 2.0D) * rangeModifier);
            player.level().addParticle(ParticleTypes.DRAGON_BREATH, true, pos.x, pos.y, pos.z, ((Math.random() - 0.5D) * 2.0D) * rangeModifier, ((Math.random() - 0.5D) * 2.0D) * rangeModifier, ((Math.random() - 0.5D) * 2.0D) * rangeModifier);
        }
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (!EnigmaticHandler.isTheWorthyOne(entity)) return;
        if (entity instanceof Player) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (timer < 1.0F) stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
        }
        if (context.entity() instanceof Player player && player.level().isClientSide()) handleBoosting(player);
        LivingEntity livingEntity = context.entity();
        int ticks = livingEntity.getFallFlyingTicks();
        if (ticks > 0 && livingEntity.isFallFlying()) stack.elytraFlightTick(livingEntity, ticks);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            ItemStack itemBySlot = livingEntity.getItemBySlot(getEquipmentSlot());
            if ((isSelected || itemBySlot.equals(stack)) && EnigmaticHandler.isTheWorthyOne(livingEntity))
                stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
            else stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
    }

    public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) {
        return EnigmaticHandler.isTheWorthyOne(entity) && super.canEquip(stack, slot, entity);
    }


    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        public static final Map<LivingEntity, Integer> TICK_MAP = new WeakHashMap<>();
        public static final Map<LivingEntity, Vec3> MOVEMENT_MAP = new WeakHashMap<>();

        @SubscribeEvent
        private static void onTick(PlayerTickEvent.@NotNull Pre event) {
            Player player = event.getEntity();
            ItemStack stack = getElytra(player);
            if (!stack.is(EnigmaticItems.CHAOS_ELYTRA)) return;
            EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            Integer i = TICK_MAP.get(player);
            int tick = i == null ? 0 : i;
            if (!player.onGround() && player.isFallFlying()) TICK_MAP.put(player, tick + 1);
            else TICK_MAP.put(player, 0);

            if (player instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.tickCount % 3 == 0) {
                    if (serverPlayer.isFallFlying()) MOVEMENT_MAP.put(serverPlayer, serverPlayer.getDeltaMovement());
                    else MOVEMENT_MAP.put(serverPlayer, Vec3.ZERO);
                }
            }
            Vec3 movement = player.getDeltaMovement();
            if (data.isElytraBoosting() && movement.length() > 0.6F && TICK_MAP.get(player) > 20) {
                AABB box = player.getBoundingBox();
                Vec3 pos = player.position();
                EntityHitResult result = ProjectileUtil.getEntityHitResult(player, pos, pos.add(movement), box.expandTowards(movement), entity -> entity.isAlive() && entity.invulnerableTime == 0, 0.0F);
                if (result != null && result.getEntity() instanceof LivingEntity entity) {
                    if (player.canAttack(entity)) {
                        chaosDescending(player, entity);
                        Vec3 vec = entity.position().subtract(player.position()).normalize();
                        entity.knockback(movement.length(), vec.x, vec.z);
                        player.hasImpulse = true;
                        player.setDeltaMovement(player.getDeltaMovement().scale(0.5F));
                    }
                }
            }
        }

        private static boolean spaceCheck(BlockPos pos, Level level) {
            if (level.isClientSide()) return false;
            Iterable<BlockPos> iterable = BlockPos.betweenClosed(pos.offset(2, 2, 2), pos.offset(-2, -2, -2));
            int space = 0;
            for (BlockPos blockPos : iterable) {
                if (level.getBlockState(blockPos).isAir()) space += 3;
                else space -= 1;
            }
            return space > 0;
        }

        private static void chaosDescending(LivingEntity owner) {
            chaosDescending(owner, null);
        }

        private static void chaosDescending(LivingEntity owner, @Nullable LivingEntity target) {
            if (spaceCheck(owner.blockPosition(), owner.level()) && TICK_MAP.get(owner) > (target == null ? 32 : 24)) {
                if (owner instanceof Player player) {
                    boolean cooldown = player.getCooldowns().isOnCooldown(EnigmaticItems.CHAOS_ELYTRA.get());
                    if (cooldown && !player.hasInfiniteMaterials()) return;
                    player.getCooldowns().addCooldown(EnigmaticItems.CHAOS_ELYTRA.get(), target == null ? descendingCooldown.get() : 10);
                }
                if (owner.level() instanceof ServerLevel level) {
                    Vec3 pos = target == null ? owner.position() : target.position().add(0, target.getBbHeight() / 2, 0);
                    PacketDistributor.sendToPlayersNear(level, null, pos.x, pos.y, pos.z, 32, new ChaosDescendingPacket(pos, target != null));
                }
                double range = 3.5 + MOVEMENT_MAP.get(owner).length();
//                SuperpositionHandler.setPersistentBoolean(owner, "ChaoAchievementCheck", true);
                List<LivingEntity> entities = owner.level().getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(range));
                for (LivingEntity entity : entities) {
                    if (entity == owner) continue;
                    Vec3 delta = entity.position().subtract(owner.position()).normalize().scale(0.5);
                    float modifier = Math.min(1.0F, 1.2F / entity.distanceTo(owner));
                    Vec3 vec = new Vec3(delta.x, 0, delta.z).normalize().scale(modifier);
                    entity.addDeltaMovement(new Vec3(vec.x, entity.onGround() ? 1.2F * modifier : 0.0F, vec.z));
                    double powerModifier = descendingPowerModifier.getAsDouble() * (target == null ? 1.0 : 0.25);
                    double pow = Math.pow(powerModifier, Math.abs(MOVEMENT_MAP.get(owner).y));
                    AttributeInstance attribute = owner.getAttribute(Attributes.ATTACK_DAMAGE);
                    double baseValue = attribute == null ? owner.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) : attribute.getValue();
                    DamageSource source = EnigmaticDamageTypes.source(entity.level(), EnigmaticDamageTypes.ABYSS, owner);
                    entity.hurt(source, (float) (baseValue * pow));
                }
//                SuperpositionHandler.removePersistentTag(owner, "ChaoAchievementCheck");
//                if (owner instanceof ServerPlayer serverPlayer && SuperpositionHandler.getPersistentInteger(owner, "ChaoExplosionKillCount", 0) >= 10)
//                    ChaosElytraKillTrigger.INSTANCE.trigger(serverPlayer);
//                SuperpositionHandler.removePersistentTag(owner, "ChaoExplosionKillCount");
            }
        }

        @SubscribeEvent
        private static void onFall(@NotNull PlayerFlyableFallEvent event) {
            Player player = event.getEntity();
            EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            if (!EnigmaticHandler.isTheWorthyOne(player)) return;
            if (!getElytra(player).is(EnigmaticItems.CHAOS_ELYTRA)) return;
            if (data.isElytraBoosting() && player.getViewVector(0.0F).y < -0.95) chaosDescending(player);
        }

        @SubscribeEvent
        private static void onFall(@NotNull LivingFallEvent event) {
            LivingEntity victim = event.getEntity();
            EnigmaticData data = victim.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            if (!EnigmaticHandler.isTheWorthyOne(victim)) return;
            if (!getElytra(victim).is(EnigmaticItems.CHAOS_ELYTRA)) return;
            if (data.isElytraBoosting()) {
                event.setCanceled(true);
                if (victim.getViewVector(0.0F).y < -0.95) chaosDescending(victim);
            }
        }

        @SubscribeEvent
        private static void onHurt(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity entity = event.getEntity();
            if (!EnigmaticHandler.isTheWorthyOne(entity)) return;
            if (!getElytra(entity).is(EnigmaticItems.CHAOS_ELYTRA)) return;
            DamageSource source = event.getSource();
            float modifier = 1.0F - 0.01F * specialDamageResistance.get();
            if (!(source.is(DamageTypes.FALL) || source.is(DamageTypes.FLY_INTO_WALL))) {
                Entity directEntity = event.getSource().getDirectEntity();
                if (directEntity != null && directEntity.position().subtract(entity.position()).dot(entity.getForward()) < 0)
                    event.setNewDamage(event.getNewDamage() * modifier);
            } else event.setNewDamage(event.getNewDamage() * modifier);
        }
    }
}
