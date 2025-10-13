package auviotre.enigmatic.legacy.handlers;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.event.LivingCurseBoostEvent;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.entity.goal.LeapAttackGoal;
import auviotre.enigmatic.legacy.contents.entity.goal.SkeletonMeleeAttackGoal;
import auviotre.enigmatic.legacy.contents.entity.goal.SpiderRangedAttackGoal;
import auviotre.enigmatic.legacy.contents.item.amulets.EldritchAmulet;
import auviotre.enigmatic.legacy.contents.item.amulets.EnigmaticAmulet;
import auviotre.enigmatic.legacy.contents.item.rings.CursedRing;
import auviotre.enigmatic.legacy.contents.item.spellstones.AngelBlessing;
import auviotre.enigmatic.legacy.packets.client.EnigmaticDataSyncPacket;
import auviotre.enigmatic.legacy.packets.client.ForceProjectileRotationsPacket;
import auviotre.enigmatic.legacy.registries.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

@Mod(value = EnigmaticLegacy.MODID)
@EventBusSubscriber(modid = EnigmaticLegacy.MODID)
public class EnigmaticEventHandler {
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
                    chance += 0.01F * AngelBlessing.deflectChance.get();
                }
                if (ISpellstone.get(player).is(EnigmaticItems.THE_CUBE)) {
                    chance += 0.35F;
                }
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
                    player.level().playSound(null, player.blockPosition(), EnigmaticSounds.DEFLECT.get(), SoundSource.PLAYERS, 1.0F, 0.95F + player.getRandom().nextFloat() * 0.1F);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (event.getSource().getEntity() instanceof Monster attacker && EnigmaticHandler.isCurseBoosted(attacker)) {
            boolean flag = attacker.fallDistance > 0.0F && !attacker.onGround() && !attacker.onClimbable() && !attacker.isInWater() && !attacker.hasEffect(MobEffects.BLINDNESS) && !attacker.isPassenger();
            if (flag) {
                if (!victim.level().isClientSide) {
                    ((ServerLevel) victim.level()).getChunkSource().broadcastAndSend(attacker, new ClientboundAnimatePacket(victim, 4));
                }
                attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, attacker.getSoundSource(), 1.0F, 1.0F);
                event.setAmount(event.getAmount() + 0.5F * event.getOriginalAmount());
            }
        }
    }

    @SubscribeEvent
    private static void onTick(EntityTickEvent.@NotNull Pre event) {
        Entity entity = event.getEntity();
        if (!entity.isAlive() || entity.level().isClientSide()) return;
        if (entity instanceof WitherSkeleton skeleton && EnigmaticHandler.isCurseBoosted(skeleton)) {
            LivingEntity target = skeleton.getTarget();
            if (target != null) {
                ItemStack backup = ItemStack.EMPTY;
                if (!skeleton.getPersistentData().getCompound("BackupItem").isEmpty())
                    backup = ItemStack.parse(skeleton.registryAccess(), skeleton.getPersistentData().getCompound("BackupItem")).orElse(ItemStack.EMPTY);
                ItemStack mainHandItem = skeleton.getMainHandItem();
                if (skeleton.distanceToSqr(target.position().add(target.getDeltaMovement().scale(1.6))) <= 25) {
                    if (mainHandItem.getItem() instanceof BowItem) {
                        ItemStack copy = skeleton.getMainHandItem().copy();
                        if (backup.isEmpty())
                            skeleton.setItemSlot(EquipmentSlot.MAINHAND, Items.STONE_SWORD.getDefaultInstance());
                        else skeleton.setItemSlot(EquipmentSlot.MAINHAND, backup.copy());
                        skeleton.getPersistentData().put("BackupItem", copy.save(skeleton.registryAccess()));
                    }
                } else {
                    if (mainHandItem.getItem() instanceof SwordItem) {
                        ItemStack copy = skeleton.getMainHandItem().copy();
                        if (backup.isEmpty())
                            skeleton.setItemSlot(EquipmentSlot.MAINHAND, Items.BOW.getDefaultInstance());
                        else skeleton.setItemSlot(EquipmentSlot.MAINHAND, backup.copy());
                        skeleton.getPersistentData().put("BackupItem", copy.save(skeleton.registryAccess()));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    private static void onClone(PlayerEvent.@NotNull Clone event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getOriginal() instanceof ServerPlayer original) {
            EnigmaticData data = original.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            data.setFireImmunityTimer(0);
            data.setFireImmunityTimer(0);
            PacketDistributor.sendToPlayer(player, new EnigmaticDataSyncPacket(data.save()));

            if (event.isWasDeath()) EldritchAmulet.reclaimInventory(original, player);
        }
    }

    @SubscribeEvent
    private static void onCraft(PlayerEvent.@NotNull ItemCraftedEvent event) {
        ItemStack crafting = event.getCrafting();
        if (crafting.is(EnigmaticTags.Items.AMULETS)) {
            Container container = event.getInventory();
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (stack.is(EnigmaticTags.Items.AMULETS) && stack.get(EnigmaticComponents.AMULET_NAME) != null) {
                    crafting.set(EnigmaticComponents.AMULET_NAME, stack.get(EnigmaticComponents.AMULET_NAME));
                    return;
                }
            }
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void onFinalTarget(@NotNull LivingChangeTargetEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.isAlive()) return;
        if (!EnigmaticHandler.isCurseBoosted(entity)) {
            if (EnigmaticHandler.isTheWorthyOne(event.getNewAboutToBeSetTarget())) {
                EnigmaticHandler.setCurseBoosted(entity, true, event.getNewAboutToBeSetTarget());
            }
        }
    }

    @SubscribeEvent
    private static void onCursed(@NotNull LivingCurseBoostEvent event) {
        if (!CursedRing.forTheWorthyMode.get()) return;
        LivingEntity entity = event.getEntity();
        LivingEntity worthy = event.getTheWorthyOne();
        if (entity.level().isClientSide()) return;
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(EnigmaticLegacy.MODID, "curse_boost");
        if (entity instanceof Zombie zombie) {
            addModifier(zombie, Attributes.ARMOR, new AttributeModifier(location, 4.0, AttributeModifier.Operation.ADD_VALUE));
        }
        if (entity.getClass() == Zombie.class && entity.getMainHandItem().isEmpty()) {
            if (entity.getRandom().nextInt(5) == 0) {
                entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.TORCH, entity.getRandom().nextInt(4) + 3));
            } else if (entity.getRandom().nextInt(8) == 0) {
                entity.setItemInHand(InteractionHand.MAIN_HAND, Items.WOODEN_AXE.getDefaultInstance());
            }
        }
    }

    private static void addModifier(@NotNull LivingEntity entity, Holder<Attribute> attribute, AttributeModifier modifier) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) instance.addPermanentModifier(modifier);
    }

    @SubscribeEvent
    private static void onEntityJoinWorld(@NotNull EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CompoundTag tag = player.getData(EnigmaticAttachments.ENIGMATIC_DATA).save();
            PacketDistributor.sendToPlayer(player, new EnigmaticDataSyncPacket(tag));
        }
        if (!CursedRing.forTheWorthyMode.get()) return;
        Entity entity = event.getEntity();
        Predicate<WrappedGoal> meleeOrBow = ((goal) -> goal.getGoal() instanceof MeleeAttackGoal || goal.getGoal() instanceof RangedCrossbowAttackGoal<?> || goal.getGoal() instanceof RangedBowAttackGoal<?>);
        int priority;
        if (entity instanceof Zombie zombie) {
            priority = getGoalPriority(zombie, meleeOrBow) - 1;
            if (priority > 0) zombie.goalSelector.addGoal(priority, new LeapAttackGoal(zombie, 0.375D));
        }
        if (entity instanceof Spider spider) {
            priority = getGoalPriority(spider, meleeOrBow);
            if (priority > 0) spider.goalSelector.addGoal(priority, new SpiderRangedAttackGoal(spider, 0.5F, 30, 8.0F));
        }
        if (entity instanceof AbstractIllager illager) {
            priority = getGoalPriority(illager, meleeOrBow) - 1;
            if (priority > 0) illager.goalSelector.addGoal(priority, new LeapAttackGoal(illager, 0.48D));
        }
        if (entity instanceof AbstractSkeleton skeleton) {
            priority = getGoalPriority(skeleton, meleeOrBow) - 1;
            if (priority > 0) skeleton.goalSelector.addGoal(priority, new SkeletonMeleeAttackGoal(skeleton));
        }
        if (entity instanceof Vex vex) {
            if (vex.getOwner() != null && EnigmaticHandler.isCurseBoosted(vex.getOwner())) {
                EnigmaticHandler.setCurseBoosted(vex, true, null);
            }
        }
    }

    private static int getGoalPriority(@NotNull Mob mob, Predicate<WrappedGoal> filter) {
        return mob.goalSelector.getAvailableGoals().stream().filter(filter).findFirst().map(WrappedGoal::getPriority).orElse(-1);
    }

    @SubscribeEvent
    private static void onMobGriefing(@NotNull EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Blaze blaze && !blaze.isAlive()) {
            event.setCanGrief(false);
        }
    }
}
