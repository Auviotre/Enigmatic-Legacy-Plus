package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.entity.PermanentItemEntity;
import auviotre.enigmatic.legacy.contents.item.amulets.EldritchAmulet;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.contents.item.misc.SoulCrystal;
import auviotre.enigmatic.legacy.contents.item.misc.StorageCrystal;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.SoulArchive;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.packets.client.EnigmaticDataSyncPacket;
import auviotre.enigmatic.legacy.packets.client.PermanentDeathPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.List;
import java.util.Objects;

public class CursedRing extends CursedCurioItem {
    public static final Multimap<Player, ItemLike> POSSESSIONS = ArrayListMultimap.create();
    public static ModConfigSpec.IntValue painMultiplier;
    public static ModConfigSpec.DoubleValue neutralAngerRange;
    public static ModConfigSpec.IntValue armorDebuff;
    public static ModConfigSpec.IntValue monsterDamageDebuff;
    public static ModConfigSpec.EnumValue<SoulCrystal.LossMode> soulCrystalsMode;
    public static ModConfigSpec.BooleanValue enableInsomnia;
    public static ModConfigSpec.IntValue lootingBonus;
    public static ModConfigSpec.IntValue fortuneBonus;
    public static ModConfigSpec.IntValue experienceBonus;
    public static ModConfigSpec.IntValue enchantingBonus;
    public static ModConfigSpec.BooleanValue enableSpecialDrops;
    public static ModConfigSpec.BooleanValue autoEquip;
    public static ModConfigSpec.BooleanValue ultraHardcore;
    public static ModConfigSpec.IntValue maxSoulCrystalLoss;
    public static ModConfigSpec.BooleanValue forTheWorthyMode;
    public static ModConfigSpec.BooleanValue giveStarterGear;
    public static ModConfigSpec.ConfigValue<List<? extends String>> neutralWhiteList;

    public CursedRing() {
        super(defaultSingleProperties().rarity(Rarity.EPIC));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.push("sevenCurses");
        painMultiplier = builder.defineInRange("painMultiplier", 200, 100, 500);
        neutralAngerRange = builder.defineInRange("neutralAngerRange", 24.0, 4.0, 100.0);
        armorDebuff = builder.defineInRange("armorDebuff", 30, 0, 100);
        monsterDamageDebuff = builder.defineInRange("monsterDamageDebuff", 40, 0, 100);
        soulCrystalsMode = builder.defineEnum("soulCrystalsMode", SoulCrystal.LossMode.NEED_CURSE_RING);
        enableInsomnia = builder.define("enableInsomnia", true);
        lootingBonus = builder.defineInRange("lootingBonus", 1, 0, 10);
        fortuneBonus = builder.defineInRange("fortuneBonus", 1, 0, 10);
        experienceBonus = builder.defineInRange("experienceBonus", 300, 0, 1000);
        enchantingBonus = builder.defineInRange("enchantingBonus", 10, 0, 30);
        enableSpecialDrops = builder.define("enableSpecialDrops", true);
        autoEquip = builder.define("autoEquip", false);
        ultraHardcore = builder.define("ultraHardcore", false);
        maxSoulCrystalLoss = builder.defineInRange("maxSoulCrystalLoss", 9, 0, 10);
        forTheWorthyMode = builder.define("forTheWorthy", true);
        giveStarterGear = builder.define("giveStarterGear", true);
        neutralWhiteList = builder.defineList("neutralWhiteList", List.of("minecraft:bee", "the_bumblezone:bee_queen"), () -> "minecraft:player", Objects::nonNull);
        builder.pop();
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingCurses");
            int multiplier = painMultiplier.getAsInt();
            if (multiplier == 200) TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingCurse1");
            else
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingCurse1_alt", ChatFormatting.GOLD, multiplier + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingCurse2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingCurse3", ChatFormatting.GOLD, armorDebuff.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingCurse4", ChatFormatting.GOLD, monsterDamageDebuff.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingCurse5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingCurse6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingCurse7");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingBlesses");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingBless1", ChatFormatting.GOLD, lootingBonus.get());
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingBless2", ChatFormatting.GOLD, fortuneBonus.get());
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingBless3", ChatFormatting.GOLD, experienceBonus.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingBless4", ChatFormatting.GOLD, enchantingBonus.get());
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingBless5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingBless6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.cursedRingBless7");
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
            if (EnigmaticHandler.canUnequipBoundRelics(Minecraft.getInstance().player)) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.eternallyBound2_creative");
            } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.eternallyBound2");

            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && autoEquip.get()) {
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

        if (EnigmaticHandler.hasItem(player, EnigmaticItems.ODE_TO_LIVING)) return;
        List<LivingEntity> genericMobs = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5));
        for (LivingEntity mob : genericMobs) {
            double visibility = player.getVisibilityPercent(mob);
            double noCheckDistance = Math.max(neutralAngerRange.getAsDouble() / 3.2, 4.8);
            double angerDistance = Math.max(neutralAngerRange.getAsDouble() * visibility, noCheckDistance);
            if (!player.hasLineOfSight(mob) && player.distanceTo(mob) > 4.8) continue;
            ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
            if (neutralWhiteList.get().contains(key.toString())) continue;
            if (mob.distanceToSqr(player.getX(), player.getY(), player.getZ()) <= angerDistance * angerDistance) {
                if (mob instanceof Piglin piglin && !EnigmaticHandler.hasCurio(player, EnigmaticItems.AVARICE_SCROLL)) {
                    if (piglin.getTarget() == null || !piglin.getTarget().isAlive())
                        if (player.hasLineOfSight(mob) || player.distanceTo(mob) <= noCheckDistance)
                            PiglinAi.wasHurtBy(piglin, player);
                } else if (mob instanceof NeutralMob neutral) {
                    if (neutral == player) continue;
                    if (neutral instanceof TraceableEntity ownable && ownable.getOwner() != null && player.is(ownable.getOwner()))
                        continue;
                    if (neutral instanceof OwnableEntity ownable && ownable.getOwner() != null && player.is(ownable.getOwner()))
                        continue;
                    if (neutral instanceof TamableAnimal tamable && tamable.isTame()) continue;
                    if (neutral instanceof IronGolem golem && golem.isPlayerCreated()) continue;
                    if ((neutral.getTarget() == null || !neutral.getTarget().isAlive()) && (player.hasLineOfSight(mob) || player.distanceTo(mob) <= noCheckDistance)) {
                        neutral.setTarget(player);
                    }
                }
            }
        }
        if (forTheWorthyMode.get()) {
            List<EnderMan> endermen = player.level().getEntitiesOfClass(EnderMan.class, player.getBoundingBox().inflate(32));
            for (EnderMan enderman : endermen) {
                if (player.getRandom().nextDouble() <= (0.0025)) {
                    if (enderman.teleportTowards(player) && player.hasLineOfSight(enderman)) {
                        EnigmaticHandler.setCurseBoosted(enderman, true, player);
                        enderman.setTarget(player);
                    }
                }
            }
        }
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getArmorModifiers() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        double modifier = -0.01 * armorDebuff.getAsInt();
        builder.put(Attributes.ARMOR, new AttributeModifier(getLocation(this), modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(getLocation(this), modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return builder.build();
    }

    public ICurio.DropRule getDropRule(SlotContext context, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return !EnigmaticHandler.isTheBlessedOne(context.entity()) && super.canEquip(context, stack);
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
        super.onUnequip(context, newStack, stack);
    }

    public int getLootingLevel(SlotContext context, @Nullable LootContext lootContext, ItemStack stack) {
        return super.getLootingLevel(context, lootContext, stack) + lootingBonus.getAsInt();
    }

    public int getFortuneLevel(SlotContext context, LootContext lootContext, ItemStack stack) {
        return super.getFortuneLevel(context, lootContext, stack) + fortuneBonus.getAsInt();
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onProbableDeath(@NotNull LivingDeathEvent event) {
            if (event.getEntity() instanceof ServerPlayer player && EnigmaticHandler.isTheCursedOne(player)) {
                POSSESSIONS.put(player, EnigmaticItems.CURSED_RING);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
        private static void onConfirmedDeath(@NotNull LivingDeathEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                if (event.isCanceled()) {
                    POSSESSIONS.removeAll(player);
                    return;
                }

                if (StorageCrystal.enable.get() && (EnigmaticHandler.hasCurio(player, EnigmaticItems.ENIGMATIC_AMULET) || EnigmaticHandler.hasCurio(player, EnigmaticItems.ASCENSION_AMULET)))
                    POSSESSIONS.put(player, EnigmaticItems.ENIGMATIC_AMULET);

                boolean flag = false;
                for (InteractionHand hand : InteractionHand.values()) {
                    ItemStack stack = player.getItemInHand(hand);
                    if (stack.is(EnigmaticItems.BLESS_STONE)) {
                        POSSESSIONS.put(player, EnigmaticItems.BLESS_STONE);
                        stack.shrink(1);
                        flag = true;
                        LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(player.level());
                        if (lightningbolt != null) {
                            lightningbolt.moveTo(Vec3.atBottomCenterOf(player.blockPosition()));
                            lightningbolt.setCause(player instanceof ServerPlayer ? player : null);
                            player.level().addFreshEntity(lightningbolt);
                        }
                        break;
                    }
                }

                if (!flag) for (InteractionHand hand : InteractionHand.values()) {
                    ItemStack stack = player.getItemInHand(hand);
                    if (stack.is(EnigmaticItems.CURSED_STONE)) {
                        POSSESSIONS.put(player, EnigmaticItems.CURSED_STONE);
                        stack.shrink(1);
                        break;
                    }
                }

                if (EldritchAmulet.keepInventory.get() && EnigmaticHandler.hasCurio(player, EnigmaticItems.ELDRITCH_AMULET)) {
                    POSSESSIONS.put(player, EnigmaticItems.ELDRITCH_AMULET);
                    EldritchAmulet.storeInventory(player);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onLivingDropsLowest(@NotNull LivingDropsEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                SoulArchive.DimensionalPosition dimPoint = new SoulArchive.DimensionalPosition(player.getX(), player.getY(), player.getZ(), player.level());
                boolean dropSoulCrystal = EnigmaticHandler.canDropSoulCrystal(player, POSSESSIONS.containsEntry(player, EnigmaticItems.CURSED_RING));
                if (POSSESSIONS.containsEntry(player, EnigmaticItems.ENIGMATIC_AMULET) && StorageCrystal.enable.get() && !event.getDrops().isEmpty()) {
                    ItemStack soulCrystal = dropSoulCrystal ? SoulCrystal.createCrystalFrom(player) : ItemStack.EMPTY;
                    ItemStack storageCrystal = StorageCrystal.storeDropsOnCrystal(event.getDrops(), player, soulCrystal);
                    PermanentItemEntity entity = new PermanentItemEntity(dimPoint.world, dimPoint.getPosX(), dimPoint.getPosY() + 1, dimPoint.getPosZ(), storageCrystal);
                    entity.setThrowerId(player.getUUID());
                    entity.setOwnerId(player.getUUID());
                    dimPoint.world.addFreshEntity(entity);
                    event.getDrops().clear();
                    EnigmaticLegacy.LOGGER.info("Teared Extradimensional Storage Crystal from " + player.getGameProfile().getName() + " at X: " + dimPoint.getPosX() + ", Y: " + dimPoint.getPosY() + ", Z: " + dimPoint.getPosZ());
                    SoulArchive.getInstance().addItem(entity);
                } else if (dropSoulCrystal) {
                    ItemStack soulCrystal = SoulCrystal.createCrystalFrom(player);
                    PermanentItemEntity entity = new PermanentItemEntity(dimPoint.world, dimPoint.getPosX(), dimPoint.getPosY() + 1, dimPoint.getPosZ(), soulCrystal);
                    entity.setThrowerId(player.getUUID());
                    entity.setOwnerId(player.getUUID());
                    dimPoint.world.addFreshEntity(entity);
                    EnigmaticLegacy.LOGGER.info("Teared Soul Crystal from " + player.getGameProfile().getName() + " at X: " + dimPoint.getPosX() + ", Y: " + dimPoint.getPosY() + ", Z: " + dimPoint.getPosZ());
                    SoulArchive.getInstance().addItem(entity);
                }
                if (SoulCrystal.isPermanentlyDead(player)) {
                    PacketDistributor.sendToPlayer(player, new PermanentDeathPacket());
                    EnigmaticHandler.setCurrentWorldFractured(true);
                }

                POSSESSIONS.removeAll(player);
            } else if (event.getEntity() instanceof Player player) {
                POSSESSIONS.removeAll(player);
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
                if (EnigmaticHandler.isBlessedItem(event.getItem()) && RedemptionRing.Helper.canUseRelic(event.getEntity()))
                    return;
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
                if (enableInsomnia.get() && player.isSleeping()) {
                    if (player.getSleepTimer() == 8 && player instanceof ServerPlayer)
                        player.sendSystemMessage(Component.translatable("message.enigmaticlegacy.cursed_sleep").withStyle(ChatFormatting.RED));
                    else if (player.getSleepTimer() > 95) player.sleepCounter = 95;
                }
            }

            if (event.getEntity() instanceof LivingEntity entity && entity.isAlive()) {
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
            boolean redemption = RedemptionRing.Helper.canUseRelic(player);
            if (!EnigmaticHandler.isTheCursedOne(player)) {
                boolean blessed = EnigmaticHandler.isBlessedItem(player.getMainHandItem());
                if (EnigmaticHandler.isCursedItem(player.getMainHandItem()) && !blessed || blessed && !redemption) {
                    event.setCanceled(true);
                    return;
                }
                blessed = EnigmaticHandler.isBlessedItem(player.getOffhandItem());
                if (EnigmaticHandler.isCursedItem(player.getOffhandItem()) && !blessed || blessed && !redemption) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        private static void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
            if (event.getAmount() >= Float.MAX_VALUE) return;
            DamageSource source = event.getSource();
            if (source.getDirectEntity() instanceof LivingEntity entity && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK))) {
                if (EnigmaticHandler.isCursedItem(entity.getMainHandItem()) && !EnigmaticHandler.isTheCursedOne(entity)) {
                    if (!(EnigmaticHandler.isBlessedItem(entity.getMainHandItem()) && RedemptionRing.Helper.canUseRelic(entity))) {
                        event.setCanceled(true);
                        return;
                    }
                }
                if (EnigmaticHandler.isEldritchItem(entity.getMainHandItem()) && !EnigmaticHandler.isTheWorthyOne(entity)) {
                    event.setCanceled(true);
                }
            }

            if (EnigmaticHandler.isTheCursedOne(event.getEntity())) {
                float multiplier = 0.01F * painMultiplier.getAsInt();
                if (EnigmaticHandler.hasCurio(event.getEntity(), EnigmaticItems.EARTH_PROMISE))
                    multiplier = (multiplier - 1) * (1 - EarthPromise.firstCurseModifier.get() * 0.01F) + 1.0F;
                event.setAmount(event.getAmount() * multiplier);
            }

            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (!EnigmaticHandler.isCurseBoosted(event.getEntity()) && EnigmaticHandler.isTheWorthyOne(attacker)) {
                    EnigmaticHandler.setCurseBoosted(event.getEntity(), true, attacker);
                }
            }
            if (event.getEntity() instanceof Monster || event.getEntity() instanceof EnderDragon) {
                if (event.getSource().getEntity() instanceof LivingEntity entity && EnigmaticHandler.isTheCursedOne(entity)) {
                    if (!entity.getMainHandItem().is(EnigmaticTags.Items.BYPASS_FOURTH_CURSE)) {
                        float modifier = 1.0F - 0.01F * monsterDamageDebuff.getAsInt();
                        event.setAmount(event.getAmount() * modifier);
                    }
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onExperienceDrop(@NotNull LivingExperienceDropEvent event) {
            Player player = event.getAttackingPlayer();
            if (EnigmaticHandler.isTheCursedOne(player)) {
                float modifier = 0.01F * experienceBonus.getAsInt() + 1;
                event.setDroppedExperience(event.getDroppedExperience() + Mth.floor(event.getOriginalExperience() * modifier));
            }
            if (event.getEntity() instanceof ServerPlayer dropper) {
                if (POSSESSIONS.containsEntry(dropper, EnigmaticItems.ENIGMATIC_AMULET) && !event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                    event.setCanceled(true);
                }
            }
        }


        @SubscribeEvent(priority = EventPriority.LOW)
        private static void onPlayerJoin(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;

            if (SoulCrystal.isPermanentlyDead(player)) {
                PacketDistributor.sendToPlayer(player, new PermanentDeathPacket());
                EnigmaticHandler.setCurrentWorldFractured(true);
            } else EnigmaticHandler.setCurrentWorldFractured(false);

            if (!giveStarterGear.get()) return;
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
                        if (ultraHardcore.get()) EnigmaticHandler.tryForceEquip(player, stack);
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
                event.setEnchantLevel(event.getEnchantLevel() + enchantingBonus.get());
        }

        @SubscribeEvent
        private static void onPlayerRespawn(PlayerEvent.@NotNull PlayerRespawnEvent event) {
            if (!event.getEntity().level().isClientSide()) {
                EnigmaticHandler.setCurrentWorldCursed(EnigmaticHandler.isTheCursedOne(event.getEntity()));
            }
        }

        @SubscribeEvent
        private static void onDrop(@NotNull DropRulesEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                if (player.level().dimension().equals(Level.NETHER) && POSSESSIONS.containsEntry(player, EnigmaticItems.CURSED_STONE)) {
                    BlockPos deathPos = player.blockPosition();
                    if (isInLava(player.level(), deathPos)) {
                        BlockPos surfacePos = deathPos;
                        while (true) {
                            BlockPos nextAbove = surfacePos.above();
                            if (isInLava(player.level(), nextAbove)) surfacePos = nextAbove;
                            else break;
                        }

                        boolean confirmLavaPool = true;

                        for (int i = -2; i <= 2 && confirmLavaPool; ++i) {
                            final int fi = i;
                            confirmLavaPool = BlockPos.betweenClosedStream(surfacePos.offset(-2, i, -2), surfacePos.offset(2, i, 2))
                                    .map(blockPos -> {
                                        if (fi <= 0) return isInLava(player.level(), blockPos);
                                        else
                                            return player.level().isEmptyBlock(blockPos) || isInLava(player.level(), blockPos);
                                    })
                                    .reduce((prevResult, nextElement) -> prevResult && nextElement).orElse(false);
                        }

                        if (confirmLavaPool) {
                            event.addOverride(stack -> stack != null && stack.is(EnigmaticItems.CURSED_RING), ICurio.DropRule.DESTROY);
                            SoulCrystal.setLostCrystals(player, SoulCrystal.getLostCrystals(player) + 1);
                            EnigmaticHandler.destroyCurio(player, EnigmaticItems.CURSED_RING);
                            player.level().playSound(null, player.blockPosition(), SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 1.0F, 0.5F);
                            return;
                        }
                    }
                }

                if (POSSESSIONS.containsEntry(player, EnigmaticItems.BLESS_STONE)) {
                    event.addOverride(stack -> stack != null && stack.is(EnigmaticItems.CURSED_RING), ICurio.DropRule.DESTROY);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 1.0F, 0.5F);
                    CuriosApi.getCuriosInventory(player).ifPresent((handler) -> {
                        IItemHandlerModifiable curios = handler.getEquippedCurios();
                        for (int i = 0; i < handler.getSlots(); ++i) {
                            ItemStack stackInSlot = curios.getStackInSlot(i);
                            if (stackInSlot.is(EnigmaticItems.CURSED_RING)) {
                                ItemStack stack = EnigmaticItems.REDEMPTION_RING.toStack();
                                stack.set(EnigmaticComponents.REDEMPTION_LEVEL, RedemptionRing.Helper.getPossibleLevel(player));
                                curios.setStackInSlot(i, stack);
                            }
                        }
                        for (int i = 0; i < handler.getSlots(); ++i) {
                            ItemStack stackInSlot = curios.getStackInSlot(i);
                            if (EnigmaticHandler.isCursedItem(stackInSlot)) {
                                PermanentItemEntity itemEntity = new PermanentItemEntity(player.level(), player.getRandomX(4), player.getRandomY(), player.getRandomZ(4), stackInSlot);
                                itemEntity.setGlowingTag(true);
                                player.level().addFreshEntity(itemEntity);
                                curios.setStackInSlot(i, ItemStack.EMPTY);
                            }
                        }
                    });
                }
            }
        }

        private static boolean isInLava(@NotNull Level world, BlockPos pos) {
            FluidState fluidState = world.getBlockState(pos).getFluidState();
            return fluidState.is(FluidTags.LAVA) && fluidState.isSource();
        }
    }
}
