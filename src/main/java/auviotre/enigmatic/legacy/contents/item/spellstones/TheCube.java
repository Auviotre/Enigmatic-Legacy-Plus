package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.packets.client.TheCubeRevivePacket;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TheCube extends SpellstoneItem {
    private static final List<ResourceKey<Level>> WORLDS = ImmutableList.of(Level.OVERWORLD, Level.NETHER, Level.END);
    private static final Map<ServerPlayer, Future<Optional<GlobalPos>>> LOCATION_CACHE = new WeakHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public TheCube() {
        super(defaultSingleProperties().rarity(Rarity.EPIC), -1);
    }

    public static float getDamageLimit(LivingEntity entity) {
        return EnigmaticHandler.isTheCursedOne(entity) ? 150.0F : 100.0F;
    }

    public static void clearLocationCache() {
        LOCATION_CACHE.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCubeSkill");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube5", ChatFormatting.GOLD, String.format("%d", (int) getDamageLimit(Minecraft.getInstance().player)));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube7");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube8");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube9");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube10");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube11");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube12");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theCube13");

        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    @OnlyIn(Dist.CLIENT)
    public void addTuneTooltip(List<Component> list) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.spelltunerEffectAll");
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getCurrentModifiers(LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(getLocation(this), entity.isSprinting() ? 0.3F : 0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return attributes;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(NeoForgeMod.SWIM_SPEED, new AttributeModifier(getLocation(this), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        builder.put(Attributes.MINING_EFFICIENCY, new AttributeModifier(getLocation(this), 0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(getLocation(this), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        builder.put(Attributes.LUCK, new AttributeModifier(getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    public int getFortuneLevel(SlotContext context, LootContext loot, ItemStack stack) {
        return super.getFortuneLevel(context, loot, stack) + 1;
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (entity.isOnFire()) entity.clearFire();
        if (entity.isFreezing()) entity.setTicksFrozen(0);
        if (entity.getAirSupply() < entity.getMaxAirSupply()) entity.setAirSupply(entity.getMaxAirSupply());
        entity.getAttributes().addTransientAttributeModifiers(this.getCurrentModifiers(entity));

        if (context.entity() instanceof ServerPlayer player) {
            if (!LOCATION_CACHE.containsKey(player)) {
                this.generateCachedLocation(player);
            } else {
                Future<Optional<GlobalPos>> future = LOCATION_CACHE.get(player);

                if (future.isDone() && !future.isCancelled()) {
                    try {
                        Optional<GlobalPos> location = future.get();
                        if (location.isPresent() && location.get().dimension() == player.level().dimension()) {
                            this.generateCachedLocation(player);
                        }
                    } catch (Exception exception) {
                        throw new RuntimeException(exception);
                    }
                }
            }
        }
    }

    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        context.entity().getAttributes().removeAttributeModifiers(this.getCurrentModifiers(context.entity()));
        super.onUnequip(context, newStack, stack);
    }

    public int getCooldown() {
        return 2400;
    }

    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) return;

        GlobalPos location = null;
        if (LOCATION_CACHE.containsKey(player)) {
            try {
                var future = LOCATION_CACHE.get(player);
                Optional<GlobalPos> optional = LOCATION_CACHE.get(player).get();
                if (future.isDone() && optional.isPresent()) {
                    location = optional.get();
                } else future.cancel(true);

                LOCATION_CACHE.remove(player);
            } catch (Exception ignored) {
            }
        }

        if (location == null) {
            EnigmaticLegacy.LOGGER.info("No cached location found for {}, generating new one synchronously.", player.getGameProfile().getName());
            Optional<GlobalPos> optional = this.findRandomLocation(player);
            if (optional.isPresent()) location = optional.get();
            else return;
        }

        double hOffset = player.getBbWidth() / 6;
        double yOffset = player.getBbHeight() / 4;
        if (player.level() instanceof ServerLevel server) {
            server.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
            server.sendParticles(ParticleTypes.WITCH, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
        }

        ServerLevel destLevel = player.server.getLevel(location.dimension());
        if (destLevel == null) destLevel = player.server.overworld();
        Vec3 vec3 = location.pos().getBottomCenter();
        if (!player.level().equals(destLevel))
            player.changeDimension(new DimensionTransition(destLevel, vec3, player.getDeltaMovement(), player.getYRot(), player.getXRot(), DimensionTransition.DO_NOTHING));
        player.teleportTo(vec3.x, vec3.y, vec3.z);

        if (player.level() instanceof ServerLevel server) {
            server.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
            server.sendParticles(ParticleTypes.WITCH, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
        }
        super.triggerActiveAbility(level, player, stack);
    }

    private void generateCachedLocation(ServerPlayer player) {
        Future<Optional<GlobalPos>> future = this.executor.submit(() -> {
            try {
                Optional<GlobalPos> location = this.findRandomLocation(player);
                EnigmaticLegacy.LOGGER.debug("Found random location: " + location);
                return location;
            } catch (Exception exception) {
                EnigmaticLegacy.LOGGER.error("Could not find random location for:" + player.getGameProfile().getName());
                throw exception;
            }
        });
        LOCATION_CACHE.put(player, future);
    }

    private Optional<GlobalPos> findRandomLocation(ServerPlayer player) {
        ArrayList<ResourceKey<Level>> list = new ArrayList<>();
        for (ResourceKey<Level> world : WORLDS) {
            if (player.level().dimension().equals(world)) continue;
            list.add(world);
        }
        ResourceKey<Level> key = list.get(player.getRandom().nextInt(list.size()));
        ServerLevel level = player.server.getLevel(key);
        if (level == null) level = player.server.overworld();

        RandomSource random = player.getRandom();
        int border = (int) level.getWorldBorder().getSize() / 2;
        int attempts = 0;
        int radius = Math.min(border, 10000);

        while (true) {
            BlockPos pos = new BlockPos(radius - random.nextInt(radius * 2), key == Level.NETHER ? 100 : 200, radius - random.nextInt(radius * 2));
            level.getChunkAt(pos);

            for (int i = 0; i < 4; i++) {
                if (i > 0)
                    pos = new BlockPos((pos.getX() >> 4) * 16 + random.nextInt(16), pos.getY(), (pos.getZ() >> 4) * 16 + random.nextInt(16));

                Optional<Vec3> location = this.findValidPosition(level, pos.getX(), pos.getY(), pos.getZ());
                if (location.isPresent()) return Optional.of(new GlobalPos(key, BlockPos.containing(location.get())));
            }

            if (++attempts > 128) return Optional.empty();
        }
    }

    private Optional<Vec3> findValidPosition(Level world, int x, int y, int z) {
        int checkAxis = y - 10;
        for (int counter = 0; counter <= checkAxis; counter++) {
            BlockPos below = new BlockPos(x, y - counter - 1, z);
            BlockPos feet = new BlockPos(x, y - counter, z);
            BlockPos head = new BlockPos(x, y - counter + 1, z);

            if (world.getMinBuildHeight() >= below.getY())
                return Optional.empty();

            if (!world.isEmptyBlock(below) && world.getBlockState(below).canOcclude() && world.isEmptyBlock(feet) && world.isEmptyBlock(head))
                return Optional.of(feet.getBottomCenter());
        }

        return Optional.empty();
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        public static final Map<LivingEntity, Float> LAST_HEALTH = new WeakHashMap<>();

        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity victim = event.getEntity();
            if (ISpellstone.get(victim).is(EnigmaticItems.THE_CUBE)) {
                if (event.getSource().is(EnigmaticTags.DamageTypes.THE_CUBE_IMMUNE_TO)) event.setCanceled(true);
            }
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
                if (ISpellstone.get(victim).is(EnigmaticItems.THE_CUBE)) {
                    RandomSource random = victim.getRandom();
                    if (event.getAmount() <= getDamageLimit(victim) && random.nextFloat() <= 0.35F) {
                        event.setCanceled(true);
                        attacker.hurt(event.getSource(), event.getAmount());
                        victim.level().playSound(null, victim.blockPosition(), EnigmaticSounds.SWORD_HIT_REJECT.get(), SoundSource.PLAYERS, 1F, 1F);
                    } else {
                        Holder<MobEffect> debuff = EnigmaticHandler.getRandomDebuff(attacker);
                        int time = 200 + random.nextInt(1000);
                        int amplifier = random.nextDouble() <= 0.15 ? 2 : random.nextDouble() <= 0.4 ? 1 : 0;
                        attacker.addEffect(new MobEffectInstance(debuff, time, amplifier, false, true));
                    }
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onFinalDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            if (!ISpellstone.get(victim).is(EnigmaticItems.THE_CUBE)) return;
            if (event.getSource().getEntity() != null) {
                if (event.getNewDamage() > getDamageLimit(victim)) {
                    event.setNewDamage(1.0F);
                    victim.level().playSound(null, victim.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1F, 1F);
                    if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
                        Vec3 vec = victim.position().subtract(attacker.position()).normalize();
                        attacker.knockback(0.75F, vec.x, vec.z);
                        victim.level().playSound(null, victim.blockPosition(), EnigmaticSounds.ETHERIUM_SHIELD_DEFLECT.get(), SoundSource.PLAYERS, 1.0F, 0.9F + victim.getRandom().nextFloat() * 0.1F);
                    }
                }
            }
            LAST_HEALTH.put(victim, victim.getHealth());
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onLivingDeath(@NotNull LivingDeathEvent event) {
            LivingEntity entity = event.getEntity();
            if (ISpellstone.get(entity).is(EnigmaticItems.THE_CUBE) && LAST_HEALTH.containsKey(entity) && LAST_HEALTH.get(entity) >= 1.5F) {
                if (event.getSource().is(Tags.DamageTypes.IS_TECHNICAL)) return;
                event.setCanceled(true);
                entity.setHealth(1);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        private static void onFinalDeath(@NotNull LivingDeathEvent event) {
            if (event.getEntity() instanceof ServerPlayer player && player.level() instanceof ServerLevel level) {
                if (event.getSource().is(DamageTypes.GENERIC_KILL)) return;
                if (ISpellstone.get(player).is(EnigmaticItems.THE_CUBE)) {
                    if (!player.getCooldowns().isOnCooldown(EnigmaticItems.THE_CUBE.get())) {
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth() * 0.3F);
                        ItemStack cube = EnigmaticHandler.getCurio(player, EnigmaticItems.THE_CUBE);
                        EnigmaticItems.THE_CUBE.get().triggerActiveAbility(level, player, cube);

                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 2));
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1));
                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1));

                        PacketDistributor.sendToPlayer(player, new TheCubeRevivePacket(player.position(), cube.copy()));
                    }
                }
            }
        }
    }
}
