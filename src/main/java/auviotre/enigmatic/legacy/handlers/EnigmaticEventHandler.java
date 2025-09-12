package auviotre.enigmatic.legacy.handlers;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ICursed;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.amulets.EnigmaticAmulet;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.packets.toClient.EnigmaticDataSyncPacket;
import auviotre.enigmatic.legacy.packets.toClient.ForceProjectileRotationsPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticEnchantments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

@Mod(value = EnigmaticLegacy.MODID)
@EventBusSubscriber(modid = EnigmaticLegacy.MODID)
public class EnigmaticEventHandler {
    @SubscribeEvent
    private static void onDamage(@NotNull LivingIncomingDamageEvent event) {
        ItemStack stack = ISpellstone.get(event.getEntity());
        if (!stack.isEmpty() && stack.getItem() instanceof SpellstoneItem spellstone) {
            if (spellstone.isImmuneTo(event.getSource())) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
        ItemStack stack = ISpellstone.get(event.getEntity());
        if (!stack.isEmpty() && stack.getItem() instanceof SpellstoneItem spellstone) {
            DamageSource source = event.getSource();
            if (spellstone.isResistantTo(source)) {
                if (stack.is(EnigmaticItems.GOLEM_HEART) && !EnigmaticHandler.hasNoArmor(event.getEntity()) && (source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION)))
                    return;
                event.setNewDamage(event.getOriginalDamage() * spellstone.getResistanceModifier(source).get());
            }
        }
    }

    @SubscribeEvent
    private static void onDamagePost(LivingDamageEvent.@NotNull Post event) {
        DamageSource source = event.getSource();
        float damage = event.getNewDamage();
        if (source.getEntity() instanceof LivingEntity entity) {
            var nemesis = EnigmaticHandler.get(entity.level(), Registries.ENCHANTMENT, EnigmaticEnchantments.NEMESIS_CURSE);
            if (EnchantmentHelper.getEnchantmentLevel(nemesis, entity) > 0) {
                entity.hurt(EnigmaticDamageTypes.source(entity.level(), EnigmaticDamageTypes.NEMESIS_CURSE, event.getEntity()), damage * 0.35F);
            }
        }

        LivingEntity entity = event.getEntity();
        var sorrow = EnigmaticHandler.get(entity.level(), Registries.ENCHANTMENT, EnigmaticEnchantments.SORROW_CURSE);
        if (EnchantmentHelper.getEnchantmentLevel(sorrow, entity) > 0 && entity.getRandom().nextFloat() < 10.12F) {
            float severity = damage > 4 ? damage / 4 : 1;
            severity *= 0.5F + entity.getRandom().nextFloat();
            int amplifier = Math.min((int) (severity / 2), 3);
            MobEffectInstance instance = new MobEffectInstance(EnigmaticHandler.getRandomDebuff(entity), (int) (300 * severity), amplifier, false, true);
            entity.addEffect(instance);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    private static void onDeath(@NotNull LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            if (event.getSource().is(EnigmaticDamageTypes.NEMESIS_CURSE)) {
                event.setCanceled(true);
                entity.setHealth(1);
            }
        }
    }

    @SubscribeEvent
    private static void onDamageIncoming(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.getDirectEntity() instanceof LivingEntity entity && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK))) {
            if (entity.getMainHandItem().getItem() instanceof ICursed && !EnigmaticHandler.isTheCursedOne(entity)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(@NotNull ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult result) {
            if (result.getEntity() instanceof ServerPlayer player) {
                Entity projectile = event.getEntity();
                if (projectile instanceof Projectile arrow) {
                    if (arrow.getOwner() == player) {
                        for (String tag : arrow.getTags()) {
                            if (tag.startsWith("AB_DEFLECTED")) {
                                try {
                                    int time = Integer.parseInt(tag.split(":")[1]);
                                    if (arrow.tickCount - time < 10)
                                        // If we cancel the event here it gets stuck in the infinite loop
                                        return;
                                } catch (Exception ex) {
                                    ex.fillInStackTrace();
                                }
                            }
                        }
                    }
                }

                float chance = 0.0F;

                if (ISpellstone.get(player).is(EnigmaticItems.ANGEL_BLESSING)) {
                    chance += 0.01F * CONFIG.SPELLSTONES.deflectChance.get();
                }

//                    if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.THE_CUBE)) {
//                        trigger = true;
//                        chance += 0.35;
//                    }

                if (EnigmaticAmulet.hasColor(player, EnigmaticAmulet.AmuletColor.VIOLET)) {
                    chance += 0.15F;
                }

                if (chance > 0.0F && player.getRandom().nextFloat() <= chance) {
                    event.setCanceled(true);

                    projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0D));
                    projectile.yRotO = projectile.getYRot() + 180.0F;
                    projectile.setYRot(projectile.getYRot() + 180.0F);

                    if (projectile instanceof AbstractArrow arrow) {
                        if (!(arrow instanceof ThrownTrident)) arrow.setOwner(player);
                        arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }

                    projectile.getTags().removeIf(tag -> tag.startsWith("AB_DEFLECTED"));
                    projectile.addTag("AB_DEFLECTED:" + projectile.tickCount);

                    Vec3 movement = projectile.getDeltaMovement();
                    PacketDistributor.sendToPlayer(player, new ForceProjectileRotationsPacket(projectile.getId(), projectile.getYRot(), projectile.getXRot(), movement.x, movement.y, movement.z, projectile.getX(), projectile.getY(), projectile.getZ()));
                    // TODO
                    // entity.level().playSound(null, entity.blockPosition(), EnigmaticSounds.DEFLECT, SoundSource.PLAYERS, 1.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
                }
            }
        }
    }


    @SubscribeEvent
    private static void getBreakSpeed(PlayerEvent.@NotNull BreakSpeed event) {
        LivingEntity entity = event.getEntity();
        if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.HEAVEN_SCROLL) || EnigmaticHandler.hasCurio(entity, EnigmaticItems.FABULOUS_SCROLL)) {
            if (entity instanceof Player player && player.getAbilities().flying)
                event.setNewSpeed(event.getNewSpeed() * 5.0F);
        }
    }

    @SubscribeEvent
    private static void entityJoinWorld(@NotNull EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CompoundTag tag = player.getData(EnigmaticAttachments.ENIGMATIC_DATA).save();
            PacketDistributor.sendToPlayer(player, new EnigmaticDataSyncPacket(tag));
        }
    }

    @SubscribeEvent
    private static void onClone(PlayerEvent.@NotNull Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EnigmaticData data = event.getOriginal().getData(EnigmaticAttachments.ENIGMATIC_DATA);
            data.setFireImmunityTimer(0);
            data.setFireImmunityTimer(0);
            PacketDistributor.sendToPlayer(player, new EnigmaticDataSyncPacket(data.save()));
        }
    }

    @SubscribeEvent
    private static void onGrantAdvancement(@NotNull AdvancementEvent.AdvancementEarnEvent event) {
        String id = event.getAdvancement().id().toString();
        Player player = event.getEntity();
        if (id.equals(EnigmaticLegacy.MODID + ":main/discover_spellstone")) {
            if (EnigmaticHandler.unlockSpecialSlot("spellstone", player)) {
                player.displayClientMessage(Component.translatable("message.enigmaticlegacy.slot_unlocked", Component.translatable("curios.identifier.spellstone").withStyle(ChatFormatting.YELLOW)), true);
            }
        } else if (id.equals(EnigmaticLegacy.MODID + ":main/discover_scroll")) {
            if (EnigmaticHandler.unlockSpecialSlot("scroll", player)) {
                player.displayClientMessage(Component.translatable("message.enigmaticlegacy.slot_unlocked", Component.translatable("curios.identifier.scroll").withStyle(ChatFormatting.YELLOW)), true);
            }
        }
    }
}
