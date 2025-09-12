package auviotre.enigmatic.legacy.handlers;

import auviotre.enigmatic.legacy.ELConfig;
import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ICursed;
import auviotre.enigmatic.legacy.api.item.IEldritch;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.SoulCrystal;
import auviotre.enigmatic.legacy.contents.item.tools.InfernalShield;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public interface EnigmaticHandler {
    List<Holder<MobEffect>> DEBUFF_LIST = new ArrayList<>();

    static boolean isTheCursedOne(LivingEntity entity) {
        return hasCurio(entity, EnigmaticItems.CURSED_RING) || entity instanceof Player player && getPersistedData(player).getBoolean("SevenCursesBearing");
    }

    static boolean isTheBlessedOne(Player player) {
        return false;
    }

    static boolean isTheWorthyOne(LivingEntity entity) {
        return isTheCursedOne(entity) && getSufferingFraction(entity) >= 0.995;
    }

    static boolean isTheOne(Player player) {
        return isTheCursedOne(player) || isTheBlessedOne(player);
    }

    static boolean hasCurio(@Nullable LivingEntity entity, ItemLike item) {
        if (entity == null) return false;
        if (item instanceof ICursed && !isTheCursedOne(entity)) return false;
        else if (item instanceof IEldritch && !isTheWorthyOne(entity)) return false;
        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(entity);
        return curios.map(curiosItemHandler -> curiosItemHandler.findFirstCurio(item.asItem()).isPresent()).orElse(false);
    }

    static ItemStack getCurio(LivingEntity entity, ItemLike item) {
        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(entity);
        if (curios.isPresent()) {
            Optional<SlotResult> firstCurio = curios.get().findFirstCurio(item.asItem());
            if (firstCurio.isPresent()) return firstCurio.get().stack();
        }
        return ItemStack.EMPTY;
    }

    static boolean hasItem(@Nullable LivingEntity entity, ItemLike item) {
        return !getItem(entity, item).isEmpty();
    }

    static ItemStack getItem(@Nullable LivingEntity entity, ItemLike item) {
        if (entity == null) return ItemStack.EMPTY;
        if (item instanceof ICursed && !isTheCursedOne(entity)) return ItemStack.EMPTY;
        else if (item instanceof IEldritch && !isTheWorthyOne(entity)) return ItemStack.EMPTY;
        if (entity instanceof Player player) {
            for (NonNullList<ItemStack> compartment : player.getInventory().compartments) {
                for (ItemStack stack : compartment) {
                    if (stack.is(item.asItem())) return stack;
                }
            }
        } else {
            for (ItemStack stack : entity.getAllSlots())
                if (stack.is(item.asItem())) return stack;
        }
        return ItemStack.EMPTY;
    }

    static boolean isAttacker(Mob entity, LivingEntity target) {
        if (entity.getTarget() == target) {
            return true;
        } else for (WrappedGoal goal : entity.targetSelector.getAvailableGoals()) {
            if (goal.getGoal() instanceof TargetGoal targetGoal && targetGoal.targetMob == target) {
                return true;
            }
        }

        Brain<?> brain = entity.getBrain();
        try {
            var memory = brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET) ? brain.getMemory(MemoryModuleType.ATTACK_TARGET) : Optional.empty();
            if (memory.isPresent() && memory.get() == target) {
                return true;
            }
        } catch (NullPointerException ignored) {
        }
        return false;
    }

    static boolean canUnequipBoundRelics(Player player) {
        return player.isCreative();
    }

    static boolean isAffectedBySoulLoss(Player player, boolean hadRing) {
        boolean keepInventory = player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        return switch (ELConfig.CONFIG.SEVEN_CURSES.soulCrystalsMode.get()) {
            case ALWAYS_LOSS -> true;
            case NEED_CURSE_RING -> hadRing;
            case NEED_CURSE_RING_AND_IGNORE_KEEPINVENTORY -> hadRing && !keepInventory;
        };
    }

    static boolean canDropSoulCrystal(Player player, boolean hadRing) {
        if (isAffectedBySoulLoss(player, hadRing)) {
            int maxCrystalLoss = 10;
            return SoulCrystal.getLostCrystals(player) < maxCrystalLoss;
        }
        return false;
    }

    static void setCurrentWorldCursed(boolean cursed) {

    }

    static void setCurrentWorldFractured(boolean fractured) {

    }

    static double getSufferingFraction(@Nullable LivingEntity livingEntity) {
        if (livingEntity == null) return 0;
        EnigmaticData data = livingEntity.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        long timeWithRing = data.getTimeWithCurses();
        long timeWithoutRing = data.getTimeWithoutCurses();

        if (timeWithRing <= 0) return 0;
        else if (timeWithoutRing <= 0) return 1;

        if (timeWithRing > 100000 || timeWithoutRing > 100000) {
            timeWithRing = timeWithRing / 100;
            timeWithoutRing = timeWithoutRing / 100;

            if (timeWithRing <= 0) return 0;
            else if (timeWithoutRing <= 0) return 1;
        }

        double total = timeWithRing + timeWithoutRing;
        double ringFraction = (timeWithRing / total);

        BigDecimal decimal = new BigDecimal(Double.toString(ringFraction));
        decimal = decimal.setScale(3, RoundingMode.HALF_UP);

        return decimal.doubleValue();
    }

    static String getSufferingTime(@Nullable Player player) {
        return String.format("%.01f%%", 100 * getSufferingFraction(player));
    }

    static String getNoSufferingTime(@Nullable Player player) {
        return String.format("%.01f%%", 100 * (1.0F - getSufferingFraction(player)));
    }

    static boolean tryForceEquip(LivingEntity entity, ItemStack curio) {
        if (!(curio.getItem() instanceof ICurioItem item))
            throw new IllegalArgumentException("I fear for now this only works with ICurioItem");

        MutableBoolean equipped = new MutableBoolean(false);

        CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
            if (!entity.level().isClientSide) {
                Map<String, ICurioStacksHandler> curios = handler.getCurios();

                loop:
                for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {
                    IDynamicStackHandler stackHandler = entry.getValue().getStacks();

                    for (int i = 0; i < stackHandler.getSlots(); i++) {
                        ItemStack present = stackHandler.getStackInSlot(i);
                        Map<String, ISlotType> slots = CuriosApi.getItemStackSlots(curio, entity.level());
                        String id = entry.getKey();

                        SlotContext context = new SlotContext(id, entity, i, false, entry.getValue().isVisible());

                        if (present.isEmpty() && (slots.containsKey(id) || slots.containsKey("curio")) && item.canEquip(context, curio)) {
                            stackHandler.setStackInSlot(i, curio);
                            item.onEquipFromUse(context, curio);
                            equipped.setTrue();
                            break loop;
                        }
                    }
                }

            }
        });

        return equipped.booleanValue();
    }

    static boolean unlockSpecialSlot(String slot, Player player) {
        return unlockSpecialSlot(slot, player, EnigmaticLegacy.location(slot + "_slot"));
    }

    static boolean unlockSpecialSlot(String slot, Player player, ResourceLocation id) {
        if (!slot.equals("scroll") && !slot.equals("spellstone") && !slot.equals("ring") && !slot.equals("charm"))
            throw new IllegalArgumentException("Slot type '" + slot + "' is not supported!");

        MutableBoolean success = new MutableBoolean(false);

        CuriosApi.getCuriosInventory(player).flatMap(handler -> handler.getStacksHandler(slot)).ifPresent(stackHandler -> {
            if (!stackHandler.getModifiers().containsKey(id)) {
                stackHandler.addPermanentModifier(new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE));
                success.setTrue();
            }
        });
        return success.getValue();
    }

    static @Nullable LivingEntity getObservedEntity(LivingEntity from, Level level, float range, int maxDist) {
        List<LivingEntity> entities = getObservedEntities(from, level, range, maxDist, true);
        return !entities.isEmpty() ? entities.getFirst() : null;
    }

    static List<LivingEntity> getObservedEntities(LivingEntity from, Level level, float range, int maxDist, boolean stopWhenFound) {
        Vec3 target = from.position().add(0.0F, from.getBbHeight() / 2.0F, 0.0F);
        List<LivingEntity> entities = new ArrayList<>();

        for (int distance = 1; distance < maxDist; ++distance) {
            target = target.add(from.getLookAngle().scale(distance)).add(0.0, 0.5, 0.0);
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(target.x - range, target.y - range, target.z - range, target.x + range, target.y + range, target.z + range));
            list.removeIf(entity -> entity == from || !from.canAttack(entity));
            entities.addAll(list);
            if (stopWhenFound && !entities.isEmpty()) {
                break;
            }
        }
        return entities;
    }

    static Holder<MobEffect> getRandomDebuff(LivingEntity entity) {
        if (DEBUFF_LIST.isEmpty()) {
            HolderLookup<MobEffect> holder = entity.level().holderLookup(Registries.MOB_EFFECT);
            List<ResourceKey<MobEffect>> keys = holder.listElementIds().toList();
            for (ResourceKey<MobEffect> key : keys) {
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(key);
                if (effect != null && !effect.isBeneficial() && !effect.isInstantenous()) {
                    try {
                        DEBUFF_LIST.add(holder.getOrThrow(key));
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return DEBUFF_LIST.get(entity.getRandom().nextInt(DEBUFF_LIST.size()));
    }

    static boolean hasNoArmor(LivingEntity entity) {
        for (ItemStack armor : entity.getArmorSlots()) {
            if (!armor.isEmpty() && !armor.is(EnigmaticTags.Items.ARMOR_CHECK_EXCLUSION)) return false;
        }
        return true;
    }

    static boolean canPickStack(Player player, ItemStack stack) {
        if (player.getInventory().getFreeSlot() >= 0)
            return true;
        else {
            List<ItemStack> allInventories = new ArrayList<>();
            allInventories.addAll(player.getInventory().items);
            allInventories.addAll(player.getInventory().offhand);
            for (ItemStack invStack : allInventories) {
                if (canMergeStacks(invStack, stack, player.getInventory().getMaxStackSize()))
                    return true;
            }
        }
        return false;
    }

    static boolean canMergeStacks(ItemStack stack1, ItemStack stack2, int invStackLimit) {
        return !stack1.isEmpty() && stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < invStackLimit;
    }

    static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.isSameItemSameComponents(stack1, stack2);
    }

    static <T> Holder.Reference<T> get(Level level, ResourceKey<Registry<T>> reg, ResourceKey<T> key) {
        return level.holderLookup(reg).getOrThrow(key);
    }

    static CompoundTag getPersistedData(Player player) {
        CompoundTag data = player.getPersistentData();
        CompoundTag persistedData;

        if (!data.contains(Player.PERSISTED_NBT_TAG))
            data.put(Player.PERSISTED_NBT_TAG, (persistedData = new CompoundTag()));
        else persistedData = data.getCompound(Player.PERSISTED_NBT_TAG);

        return persistedData;
    }

    static ItemStack mergeEnchantments(ItemStack input, ItemStack mergeFrom, boolean overMerge, boolean onlyTreasure) {
        ItemStack returnedStack = input.copy();
        ItemEnchantments inputEnchants = EnchantmentHelper.getEnchantmentsForCrafting(returnedStack);
        ItemEnchantments mergedEnchants = EnchantmentHelper.getEnchantmentsForCrafting(mergeFrom);
        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(inputEnchants);

        for(Holder<Enchantment> mergedEnchantHolder : mergedEnchants.keySet()) {
            Enchantment mergedEnchant = mergedEnchantHolder.value();
            int inputEnchantLevel = inputEnchants.getLevel(mergedEnchantHolder);
            int mergedEnchantLevel = mergedEnchants.getLevel(mergedEnchantHolder);

            if (!overMerge) {
                mergedEnchantLevel = inputEnchantLevel == mergedEnchantLevel ? Math.min(mergedEnchantLevel + 1, mergedEnchant.getMaxLevel()) : Math.max(mergedEnchantLevel, inputEnchantLevel);
            } else {
                mergedEnchantLevel = inputEnchantLevel > 0 ? Math.max(mergedEnchantLevel, inputEnchantLevel) + 1 : Math.max(mergedEnchantLevel, inputEnchantLevel);
                mergedEnchantLevel = Math.min(mergedEnchantLevel, 10);
            }

            boolean compatible = input.supportsEnchantment(mergedEnchantHolder);
            if (input.getItem() instanceof EnchantedBookItem) compatible = true;

            for(Holder<Enchantment> originalEnchant : inputEnchants.keySet()) {
                if (originalEnchant != mergedEnchantHolder && mergedEnchant.exclusiveSet().contains(originalEnchant)) {
                    compatible = false;
                }
            }
            if (compatible) {
                if (!onlyTreasure || mergedEnchantHolder.is(EnchantmentTags.TRADEABLE) || mergedEnchantHolder.is(EnchantmentTags.CURSE)) {
                    builder.set(mergedEnchantHolder, mergedEnchantLevel);
                }
            }
        }

        EnchantmentHelper.setEnchantments(returnedStack, builder.toImmutable());
        return returnedStack;
    }

    static boolean onDamageSourceBlocking(LivingEntity blocker, ItemStack useItem, DamageSource source) {
        if (blocker instanceof Player player && useItem != null) {
            if (useItem.getItem() instanceof InfernalShield) {
                if (!source.is(DamageTypeTags.BYPASSES_SHIELD) && source.getSourcePosition() != null) {
                    Vec3 sourcePos = source.getSourcePosition();
                    Vec3 viewVec = blocker.calculateViewVector(0.0F, blocker.getYHeadRot());
                    Vec3 sourceToSelf = sourcePos.vectorTo(blocker.position());
                    if (sourceToSelf.dot(viewVec) < 0.0D) {
                        int strength = -1;

                        if (player.hasEffect(EnigmaticEffects.BLAZING_MIGHT)) {
                            MobEffectInstance effectInstance = player.getEffect(EnigmaticEffects.BLAZING_MIGHT);
                            strength = effectInstance == null ? -1 : effectInstance.getAmplifier();
                            player.removeEffect(EnigmaticEffects.BLAZING_MIGHT);
                            strength = Math.min(strength, 2);
                        }
                        player.addEffect(new MobEffectInstance(EnigmaticEffects.BLAZING_MIGHT, 1200, strength + 1, true, true));

                        if (source.getDirectEntity() instanceof LivingEntity living && living.isAlive()) {
                            if (!living.fireImmune() && !(living instanceof Guardian)) {
                                StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
                                if (Arrays.stream(stacktrace).filter(element -> EnigmaticHandler.class.getName().equals(element.getClassName())).count() < 2) {
                                    living.invulnerableTime = 0;
                                    living.hurt(living.damageSources().source(DamageTypes.ON_FIRE, player), 4F);
                                    living.igniteForSeconds(4);
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
