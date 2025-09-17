package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.entity.PermanentItemEntity;
import auviotre.enigmatic.legacy.contents.item.SoulCrystal;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.SoulArchive;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.packets.toClient.EnigmaticDataSyncPacket;
import auviotre.enigmatic.legacy.packets.toClient.PermanentDeathPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.ArrayList;
import java.util.List;

import static auviotre.enigmatic.legacy.ELConfig.CONFIG;

public class CursedRing extends CursedCurioItem {
    public static final List<Player> DEATH_WITH_CURSE_LIST = new ArrayList<>();

    public CursedRing() {
        super(defaultSingleProperties().rarity(Rarity.EPIC));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing3");
            int multiplier = CONFIG.SEVEN_CURSES.painMultiplier.getAsInt();
            if (multiplier == 200) TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing4");
            else
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing4_alt", ChatFormatting.GOLD, multiplier + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing6", ChatFormatting.GOLD, CONFIG.SEVEN_CURSES.armorDebuff.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing7", ChatFormatting.GOLD, CONFIG.SEVEN_CURSES.monsterDamageDebuff.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing8");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing9");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing10");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing11");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing12", ChatFormatting.GOLD, CONFIG.SEVEN_CURSES.lootingBonus.get());
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing13", ChatFormatting.GOLD, CONFIG.SEVEN_CURSES.fortuneBonus.get());
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing14", ChatFormatting.GOLD, CONFIG.SEVEN_CURSES.experienceBonus.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing15", ChatFormatting.GOLD, CONFIG.SEVEN_CURSES.enchantingBonus.get());
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing16");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing17");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRing18");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingLore1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingLore2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingLore3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingLore4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingLore5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingLore6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingLore7");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eternallyBound1");
            if (Minecraft.getInstance().player != null && EnigmaticHandler.canUnequipBoundRelics(Minecraft.getInstance().player)) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.eternallyBound2_creative");
            } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.eternallyBound2");

            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && CONFIG.SEVEN_CURSES.autoEquip.get()) {
            if (livingEntity instanceof Player player && (player.isCreative() || player.isSpectator())) return;
            if (!EnigmaticHandler.hasCurio(livingEntity, this)) {
                if (EnigmaticHandler.tryForceEquip(livingEntity, stack.copy())) stack.shrink(1);
            }
        }
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        context.entity().getAttributes().addTransientAttributeModifiers(this.getArmorModifiers());
        if (context.entity().level().isClientSide || !(context.entity() instanceof Player player)) return;
        if (player.isCreative() || player.isSpectator()) return;

        List<LivingEntity> genericMobs = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5));
        for (LivingEntity mob : genericMobs) {
            double visibility = player.getVisibilityPercent(mob);
            double noCheckDistance = Math.max(CONFIG.SEVEN_CURSES.neutralAngerRange.getAsDouble() / 6.0, 2);
            double angerDistance = Math.max(CONFIG.SEVEN_CURSES.neutralAngerRange.getAsDouble() * visibility, noCheckDistance);
            if (!player.hasLineOfSight(mob) && player.distanceTo(mob) > 4) continue;
            if (mob.distanceToSqr(player.getX(), player.getY(), player.getZ()) <= angerDistance * angerDistance) {
                if (mob instanceof Piglin piglin) {
                    if (piglin.getTarget() == null || !piglin.getTarget().isAlive())
                        if (player.hasLineOfSight(mob) || player.distanceTo(mob) <= noCheckDistance)
                            PiglinAi.wasHurtBy(piglin, player);
                } else if (mob instanceof NeutralMob neutral) {
                    if (!mob.getType().is(EnigmaticTags.EntityTypes.NEUTRAL_ANGER_BLACKLIST)) {
                        if (neutral == player) continue;
                        if (neutral instanceof OwnableEntity ownable && ownable.getOwner() != null && player.is(ownable.getOwner()))
                            continue;
                        if (neutral instanceof TamableAnimal tamable && tamable.isTame()) continue;
                        if (neutral instanceof IronGolem golem && golem.isPlayerCreated()) continue;
                        if (neutral instanceof Bee) continue;
                        if ((neutral.getTarget() == null || !neutral.getTarget().isAlive()) && (player.hasLineOfSight(mob) || player.distanceTo(mob) <= noCheckDistance)) {
                            neutral.setTarget(player);
                        }
                    }
                }
            }
        }
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getArmorModifiers() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        double modifier = -0.01 * CONFIG.SEVEN_CURSES.armorDebuff.getAsInt();
        builder.put(Attributes.ARMOR, new AttributeModifier(getLocation(this), modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(getLocation(this), modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return builder.build();
    }

    public ICurio.DropRule getDropRule(SlotContext context, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    public boolean canUnequip(@NotNull SlotContext context, ItemStack stack) {
        if (context.entity() instanceof Player player && EnigmaticHandler.canUnequipBoundRelics(player))
            return super.canUnequip(context, stack);
        return false;
    }

    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return false;
    }

    public void onEquip(SlotContext context, ItemStack prevStack, ItemStack stack) {
        EnigmaticHandler.setCurrentWorldCursed(true);
    }

    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        context.entity().getAttributes().removeAttributeModifiers(this.getArmorModifiers());
        EnigmaticHandler.setCurrentWorldCursed(false);
    }

    public int getLootingLevel(SlotContext context, @Nullable LootContext lootContext, ItemStack stack) {
        return super.getLootingLevel(context, lootContext, stack) + CONFIG.SEVEN_CURSES.lootingBonus.getAsInt();
    }

    public int getFortuneLevel(SlotContext context, LootContext lootContext, ItemStack stack) {
        return super.getFortuneLevel(context, lootContext, stack) + CONFIG.SEVEN_CURSES.fortuneBonus.getAsInt();
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onProbableDeath(@NotNull LivingDeathEvent event) {
            if (event.getEntity() instanceof ServerPlayer player && EnigmaticHandler.isTheCursedOne(player)) {
                DEATH_WITH_CURSE_LIST.add(player);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
        private static void onConfirmedDeath(@NotNull LivingDeathEvent event) {
            if (event.getEntity() instanceof ServerPlayer player && EnigmaticHandler.isTheCursedOne(player)) {
                if (event.isCanceled()) DEATH_WITH_CURSE_LIST.remove(player);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onLivingDropsLowest(@NotNull LivingDropsEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                SoulArchive.DimensionalPosition dimPoint = new SoulArchive.DimensionalPosition(player.getX(), player.getY(), player.getZ(), player.level());
                if (EnigmaticHandler.canDropSoulCrystal(player, DEATH_WITH_CURSE_LIST.contains(player))) {
                    ItemStack soulCrystal = SoulCrystal.createCrystalFrom(player);
                    PermanentItemEntity droppedSoulCrystal = new PermanentItemEntity(dimPoint.world, dimPoint.getPosX(), dimPoint.getPosY() + 1.5, dimPoint.getPosZ(), soulCrystal);
                    droppedSoulCrystal.setThrowerId(player.getUUID());
                    droppedSoulCrystal.setOwnerId(player.getUUID());
                    dimPoint.world.addFreshEntity(droppedSoulCrystal);
                    EnigmaticLegacy.LOGGER.info("Teared Soul Crystal from " + player.getGameProfile().getName() + " at X: " + dimPoint.getPosX() + ", Y: " + dimPoint.getPosY() + ", Z: " + dimPoint.getPosZ());
                    SoulArchive.getInstance().addItem(droppedSoulCrystal);
                }
                if (SoulCrystal.isPermanentlyDead(player)) {
                    PacketDistributor.sendToPlayer(player, new PermanentDeathPacket());
                    EnigmaticHandler.setCurrentWorldFractured(true);
                }

                DEATH_WITH_CURSE_LIST.remove(player);
            } else if (event.getEntity() instanceof Player player) {
                DEATH_WITH_CURSE_LIST.remove(player);
            }
        }

        @SubscribeEvent
        private static void onKnockback(@NotNull LivingKnockBackEvent event) {
            if (EnigmaticHandler.isTheCursedOne(event.getEntity())) {
                event.setStrength(event.getStrength() * 1.25F);
            }
        }

        @SubscribeEvent
        private static void onUseItem(@NotNull LivingEntityUseItemEvent.Start event) {
            if (EnigmaticHandler.isCursedItem(event.getItem())) {
                if (!EnigmaticHandler.isTheCursedOne(event.getEntity())) {
                    event.setCanceled(true);
                    return;
                }
                if (EnigmaticHandler.isEldritchItem(event.getItem()) && !EnigmaticHandler.isTheWorthyOne(event.getEntity())) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        private static void onTick(EntityTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof Player player) {
                CompoundTag data = EnigmaticHandler.getPersistedData(player);
                data.putBoolean("SevenCursesBearing", EnigmaticHandler.hasCurio(player, EnigmaticItems.CURSED_RING));
            }
        }

        @SubscribeEvent
        private static void onTicked(EntityTickEvent.@NotNull Post event) {
            if (event.getEntity() instanceof Player player && player.isAlive() && EnigmaticHandler.isTheCursedOne(player)) {
                if (player.isOnFire()) player.setRemainingFireTicks(player.getRemainingFireTicks() + 2);
                if (CONFIG.SEVEN_CURSES.enableInsomnia.get() && player.isSleeping()) {
                    if (player.getSleepTimer() == 8 && player instanceof ServerPlayer)
                        player.sendSystemMessage(Component.translatable("message.enigmaticlegacy.cursed_sleep").withStyle(ChatFormatting.RED));
                    else if (player.getSleepTimer() > 95) player.sleepCounter = 95;
                }
            }

            if (event.getEntity() instanceof LivingEntity entity) {
                EnigmaticData data = entity.getData(EnigmaticAttachments.ENIGMATIC_DATA);
                if (EnigmaticHandler.isTheCursedOne(entity)) data.incrementTimeWithCurses();
                else data.incrementTimeWithoutCurses();
                if (entity instanceof ServerPlayer player)
                    PacketDistributor.sendToPlayer(player, new EnigmaticDataSyncPacket(data.save()));
            }
        }

        @SubscribeEvent
        private static void onAttack(@NotNull AttackEntityEvent event) {
            Player player = event.getEntity();
            if (!(EnigmaticHandler.isTheCursedOne(player))) {
                if (EnigmaticHandler.isCursedItem(player.getMainHandItem())) {
                    event.setCanceled(true);
                    return;
                }
                if (EnigmaticHandler.isCursedItem(player.getOffhandItem())) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        private static void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
            DamageSource source = event.getSource();
            if (source.getDirectEntity() instanceof LivingEntity entity && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK))) {
                if (EnigmaticHandler.isCursedItem(entity.getMainHandItem()) && !EnigmaticHandler.isTheCursedOne(entity)) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (EnigmaticHandler.isTheCursedOne(event.getEntity())) {
                float multiplier = 0.01F * CONFIG.SEVEN_CURSES.painMultiplier.getAsInt();
                event.setNewDamage(event.getNewDamage() * multiplier);
            }
            if (event.getEntity() instanceof Monster || event.getEntity() instanceof EnderDragon) {
                if (event.getSource().getEntity() instanceof LivingEntity entity && EnigmaticHandler.isTheCursedOne(entity)) {
                    if (!entity.getMainHandItem().is(EnigmaticTags.Items.BYPASS_FOURTH_CURSE)) {
                        float modifier = 1.0F - 0.01F * CONFIG.SEVEN_CURSES.monsterDamageDebuff.getAsInt();
                        event.setNewDamage(event.getNewDamage() * modifier);
                    }
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onExperienceDrop(@NotNull LivingExperienceDropEvent event) {
            Player player = event.getAttackingPlayer();
            if (EnigmaticHandler.isTheCursedOne(player)) {
                float modifier = 0.01F * CONFIG.SEVEN_CURSES.experienceBonus.getAsInt() + 1;
                event.setDroppedExperience(event.getDroppedExperience() + Mth.floor(event.getOriginalExperience() * modifier));
            }
        }


        @SubscribeEvent(priority = EventPriority.LOW)
        private static void onPlayerJoin(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;

            if (SoulCrystal.isPermanentlyDead(player)) {
                PacketDistributor.sendToPlayer(player, new PermanentDeathPacket());
                EnigmaticHandler.setCurrentWorldFractured(true);
            } else EnigmaticHandler.setCurrentWorldFractured(false);

            try {
                if (!ModList.get().isLoaded("customstartinggear")) {
                    EnigmaticLegacy.LOGGER.info("Granting starter gear to " + player.getGameProfile().getName());
                    CompoundTag data = EnigmaticHandler.getPersistedData(player);

                    if (!data.getBoolean("UnwitnessedAmuletGift")) {
                        ItemStack stack = EnigmaticItems.UNWITNESSED_AMULET.toStack();
                        if (player.getInventory().getItem(8).isEmpty()) {
                            player.getInventory().setItem(8, stack);
                        } else {
                            if (!player.getInventory().add(stack)) {
                                ItemEntity dropRing = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                                player.level().addFreshEntity(dropRing);
                            }
                        }
                        data.putBoolean("UnwitnessedAmuletGift", true);
                    }
                    if (!data.getBoolean("CursedRingGift")) {
                        ItemStack stack = EnigmaticItems.CURSED_RING.toStack();
                        if (CONFIG.SEVEN_CURSES.ultraHardcore.get()) EnigmaticHandler.tryForceEquip(player, stack);
                        else {
                            if (player.getInventory().getItem(7).isEmpty()) {
                                player.getInventory().setItem(7, stack);
                            } else {
                                if (!player.getInventory().add(stack)) {
                                    ItemEntity dropRing = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                                    player.level().addFreshEntity(dropRing);
                                }
                            }
                        }
                        data.putBoolean("CursedRingGift", true);
                    }
                }
            } catch (Exception ex) {
                EnigmaticLegacy.LOGGER.error("Failed to check player's advancements upon joining the world!");
                ex.fillInStackTrace();
            }
        }


        @SubscribeEvent
        private static void onEnchantmentLevelSet(@NotNull EnchantmentLevelSetEvent event) {
            BlockPos pos = event.getPos();
            boolean shouldBoost = false;
            int radius = 8;
            List<Player> players = event.getLevel().getEntitiesOfClass(Player.class, AABB.encapsulatingFullBlocks(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius)));
            for (Player player : players)
                if (EnigmaticHandler.isTheCursedOne(player)) {
                    shouldBoost = true;
                    break;
                }
            if (shouldBoost)
                event.setEnchantLevel(event.getEnchantLevel() + CONFIG.SEVEN_CURSES.enchantingBonus.get());
        }

        @SubscribeEvent
        private static void onPlayerRespawn(PlayerEvent.@NotNull PlayerRespawnEvent event) {
            if (!event.getEntity().level().isClientSide()) {
                EnigmaticHandler.setCurrentWorldCursed(EnigmaticHandler.isTheCursedOne(event.getEntity()));
            }
        }
    }
}
