package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.entity.projectile.AbyssProjectile;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.*;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.util.AttributeUtil;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnnihilatingSword extends SwordItem {
    private static final Tier TIER = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 3268, 10.8F, 8.0F, 64, () -> Ingredient.EMPTY);

    public AnnihilatingSword() {
        super(TIER, IItemHelper.singleProperties().fireResistant().rarity(Rarity.EPIC).component(EnigmaticComponents.CURSED, true)
                .component(EnigmaticComponents.ELDRITCH, true).component(EnigmaticComponents.ANNI_ENERGY, 0.0F));
    }

    private static void parry(Level level, ItemStack stack, Player blocker, DamageSource source, float damage) {
        if (level.isClientSide()) return;
        int tick = stack.getUseDuration(blocker) - blocker.getUseItemRemainingTicks();
        stack.hurtAndBreak(Mth.floor(damage / 2), blocker, LivingEntity.getSlotForHand(blocker.getUsedItemHand()));
        blocker.getCooldowns().addCooldown(stack.getItem(), Mth.clamp(tick, 30, 50) - (tick <= 8 ? 5 : 0));
        blocker.swing(blocker.getUsedItemHand());
        blocker.stopUsingItem();
        addEnergy(stack, blocker);
        blocker.getData(EnigmaticAttachments.ENIGMATIC_DATA).setAnnihilatingTick(16);
        if (source.getEntity() != null) {
            RandomSource random = blocker.getRandom();
            level.playSound(null, blocker.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 0.5F, 1.1F + 0.2F * random.nextFloat());
            if (tick <= 8)
                level.playSound(null, blocker.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.5F, 1.2F + 0.3F * random.nextFloat());
        }

        float dmg = (float) (blocker.getAttributes().getValue(Attributes.ATTACK_DAMAGE) + damage);
        if (source.getDirectEntity() instanceof LivingEntity attacker) {
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, attacker.getBoundingBox().inflate(1.2));
            for (LivingEntity entity : entities) {
                if (entity == blocker) continue;
                double xRatio = blocker.getX() - entity.getX();
                double zRatio = blocker.getZ() - entity.getZ();
                entity.hasImpulse = true;
                Vec3 vec = entity.getDeltaMovement();
                Vec3 add = new Vec3(xRatio, 0.0, zRatio).normalize().scale(tick < 25 ? 1.2F : 0.55F).scale(1.0 - 0.5 * entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                if (tick <= 8) entity.hurt(entity.damageSources().mobAttack(blocker), dmg * 0.5F);
                entity.setDeltaMovement(vec.x / 2.0 - add.x, entity.onGround() ? Math.min(0.4, vec.y / 2.0 + 0.5) : vec.y, vec.z / 2.0 - add.z);
            }
        } else if (source.getDirectEntity() instanceof Projectile projectile && tick <= 8) {
            if (!(projectile instanceof AbstractArrow arrow) || arrow.pickup != AbstractArrow.Pickup.ALLOWED) {
                AbyssProjectile abyssProjectile = new AbyssProjectile(blocker.level(), blocker);
                abyssProjectile.setDamage(dmg * 4);
                if (source.getEntity() != null) abyssProjectile.setTarget(source.getEntity());
                abyssProjectile.setPos(projectile.position());
                abyssProjectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.6));
                blocker.level().addFreshEntity(abyssProjectile);
                projectile.discard();
            }
        }
    }

    private static void sweepAttack(Player player, int level, float damage) {
        damage *= (1.2F + 0.08F * level);
        Vec3 position = player.position().add(0, player.getBbHeight() / 2, 0);
        Vec3 offset = new Vec3(level + 3, 0.5, level + 3);
        AABB box = new AABB(position.subtract(offset), position.add(offset));
        float ratio = (float) (Math.PI / 180.0F) * player.getYRot();
        box = box.move(-level * Mth.sin(ratio), 0, level * Mth.cos(ratio));
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, box, Entity::isAlive);
        for (LivingEntity entity : entities) {
            if (entity == player) continue;
            if (player.isAlliedTo(entity)) continue;
            if (entity instanceof ArmorStand armorStand && armorStand.isMarker()) continue;
            if (entity.distanceTo(player) > player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + level)
                continue;
            if (entity.distanceTo(player) < level || entity.position().subtract(player.position()).dot(player.getForward()) > 0) {
                entity.knockback(level * 0.5 + 0.4, Mth.sin(ratio), -Mth.cos(ratio));
                entity.hurt(player.damageSources().playerAttack(player), damage);
//                if (entity.level() instanceof ServerLevel server) {
//                    server.sendParticles(EnigmaticAddonParticles.ABYSS_CHAOS, entity.getX(), entity.getEyeY(), entity.getZ(), 16, entity.getBbWidth(), entity.getBbHeight() / 2, entity.getBbWidth(), 0);
//                }
            }
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.2F, 0.2F);
        player.swing(InteractionHand.MAIN_HAND);
        player.resetAttackStrengthTicker();
        player.sweepAttack();
        player.getData(EnigmaticAttachments.ENIGMATIC_DATA).setAnnihilationSweepData((int) damage, 5, level);
    }

    private static void rangeAttack(Player player, int level, float damage) {
        for (int i = 0; i < 4 * level; i++) {
            AbyssProjectile abyssProjectile = new AbyssProjectile(player.level(), player);
            abyssProjectile.setDamage(damage);
            abyssProjectile.setPos(new Vec3(player.getRandomX(1), player.getRandomY(), player.getRandomZ(1)));
            abyssProjectile.setDeltaMovement(abyssProjectile.position().subtract(player.position()).add(0.0, 0.32, 0.0).scale(1.25));
            player.level().addFreshEntity(abyssProjectile);
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.25F, 1.5F);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.2F, 0.2F);
        player.swing(InteractionHand.MAIN_HAND);
        player.resetAttackStrengthTicker();
        player.sweepAttack();
    }

    public static int getMaxEnergy(LivingEntity entity) {
        return EnigmaticHandler.isAbyssBoosted(entity) ? 5 : 3;
    }

    private static float getDamageBoost(ItemStack stack) {
        ItemEnchantments enchantments = stack.getTagEnchantments();
        int maxlevel = 0;
        for (Holder<Enchantment> key : enchantments.keySet()) {
            if (key.is(EnchantmentTags.DAMAGE_EXCLUSIVE)) maxlevel = Math.max(maxlevel, enchantments.getLevel(key));
        }
        return 0.8F + 0.15F * maxlevel;
    }

    public static void addEnergy(ItemStack stack, LivingEntity entity) {
        boolean boosted = EnigmaticHandler.isAbyssBoosted(entity);
        float energy = stack.getOrDefault(EnigmaticComponents.ANNI_ENERGY, 0.0F);
        energy = energy + entity.getRandom().nextFloat() * (boosted ? 1.2F : 0.6F);
        stack.set(EnigmaticComponents.ANNI_ENERGY, Math.clamp(energy, 0.0F, getMaxEnergy(entity)));
    }

    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        int energy = stack.getOrDefault(EnigmaticComponents.ANNI_ENERGY, 0.0F).intValue();
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 19.0F + energy * 3.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.8F + energy * 0.1F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        if (energy > 0)
            builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(AttributeUtil.BASE_ENTITY_REACH_ID, energy * 0.3F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            LocalPlayer player = Minecraft.getInstance().player;
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSword1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSword2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSword3");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSword4");
            if (player != null && ItemStack.isSameItemSameComponents(player.getOffhandItem(), stack)) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSword5Off");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSword6Off");
            } else {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSword5Main", ChatFormatting.GOLD, String.format("+%d%%", (int) (getDamageBoost(stack) * 100)));
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSword6Main");
            }
            int energy = stack.getOrDefault(EnigmaticComponents.ANNI_ENERGY, 0.0F).intValue();
            if (energy > 0) {
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSword7", ChatFormatting.GOLD, energy);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSword8", ChatFormatting.GOLD, String.format("×%.02f", 1 + energy * 0.2));
            }
            if (EnigmaticHandler.isAbyssBoosted(Minecraft.getInstance().player)) {
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.abyssBoost");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.annihilatingSwordBoost");
            }
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.worthyOnly(list, stack);
        if (stack.isEmpty()) TooltipHandler.line(list);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if ((isSelected || livingEntity.getOffhandItem().is(this)) && EnigmaticHandler.isTheWorthyOne(livingEntity))
                stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.4F));
            else stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
        if (entity instanceof LivingEntity living && !EnigmaticHandler.isTheWorthyOne(living)) {
            if (!living.hasEffect(EnigmaticEffects.ABYSS_CORRUPTION) && !living.hasInfiniteMaterials())
                living.addEffect(new MobEffectInstance(EnigmaticEffects.ABYSS_CORRUPTION, 100, 2));
        }
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!EnigmaticHandler.canUse(player, stack)) return InteractionResultHolder.pass(stack);
        if (hand.equals(InteractionHand.OFF_HAND) && player.getMainHandItem().isEmpty()) {
            return ItemUtils.startUsingInstantly(level, player, hand);
        } else if (hand.equals(InteractionHand.MAIN_HAND) && player.getOffhandItem().isEmpty()) {
            if (stack.getOrDefault(EnigmaticComponents.ANNI_ENERGY, 0.0F) >= 1.0F) {
                return ItemUtils.startUsingInstantly(level, player, hand);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int tick) {
        if (entity instanceof Player player && player.getOffhandItem().is(this)) {
            player.getCooldowns().addCooldown(this, 8);
        }
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && player.getMainHandItem().is(this)) {
            ItemStack handItem = player.getMainHandItem();
            int energy = handItem.getOrDefault(EnigmaticComponents.ANNI_ENERGY, 0.0F).intValue();
            if (energy > 0) {
                float damage = (float) (player.getAttributes().getValue(Attributes.ATTACK_DAMAGE));
//                if (energy >= 5)
//                    player.sendSystemMessage(Component.literal("AbyssFinalAttack! (But not)"));
                if (EnigmaticHandler.isAbyssBoosted(player)) {
                    sweepAttack(player, energy, damage);
                    rangeAttack(player, energy, damage);
                } else if (player.onGround() || player.level().getBlockState(player.blockPosition().below(2)).canOcclude()) {
                    sweepAttack(player, energy, damage);
                } else rangeAttack(player, energy, damage);
                player.getCooldowns().addCooldown(this, 100 - energy * 8);
                stack.hurtAndBreak(energy * 3, player, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
                if (!player.hasInfiniteMaterials()) {
                    var holder = EnigmaticHandler.get(level, Registries.ENCHANTMENT, Enchantments.UNBREAKING);
                    int unbreak = stack.getEnchantmentLevel(holder);
                    if (unbreak > 0 && player.getRandom().nextInt(unbreak + 3) < unbreak) {
                        handItem.set(EnigmaticComponents.ANNI_ENERGY, energy / 2.0F);
                    } else handItem.set(EnigmaticComponents.ANNI_ENERGY, 0.0F);
                }
            }
        }
        return stack;
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    public @NotNull AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target) {
        return target.getBoundingBox().inflate(1.75, 0.3, 1.75);
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32000;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Decorator implements IItemDecorator {
        public boolean render(GuiGraphics graphics, Font font, @NotNull ItemStack stack, int x, int y) {
            int energy = stack.getOrDefault(EnigmaticComponents.ANNI_ENERGY, 0.0F).intValue();
            if (stack.getCount() == 1 && energy > 0) {
                graphics.pose().pushPose();
                String s = String.valueOf(energy);
                graphics.pose().translate(0.0F, 0.0F, 200.0F);
                graphics.drawString(font, s, x + 19 - 2 - font.width(s), y + 6 + 3, 16733695, true);
                graphics.pose().popPose();
                return true;
            }
            return false;
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGH)
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (event.getAmount() >= Float.MAX_VALUE) return;
            if (event.getEntity() instanceof LivingEntity entity && entity.getData(EnigmaticAttachments.ENIGMATIC_DATA).getAnnihilatingTick() > 0) {
                event.setCanceled(true);
                return;
            }
            if (event.getEntity() instanceof Player entity && entity.getUseItem().is(EnigmaticItems.ANNIHILATING_SWORD)) {
                if (entity.getUsedItemHand().equals(InteractionHand.OFF_HAND)) {
                    Entity directEntity = event.getSource().getDirectEntity();
                    if (directEntity != null && directEntity.position().subtract(entity.position()).dot(entity.getForward()) > 0) {
                        if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                            parry(entity.level(), entity.getUseItem(), entity, event.getSource(), event.getAmount());
                            event.setCanceled(true);
                        }
                    }
                } else event.setAmount(event.getAmount() * 0.2F);
            }
            if (event.getSource().getEntity() instanceof Player entity && (event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK) || event.getSource().is(EnigmaticDamageTypes.ABYSS))) {
                ItemStack stack = entity.getMainHandItem();
                if (stack.is(EnigmaticItems.ANNIHILATING_SWORD)) {
                    if (entity.getOffhandItem().isEmpty())
                        event.setAmount(event.getAmount() * (1 + getDamageBoost(stack)));
                    int energy = stack.getOrDefault(EnigmaticComponents.ANNI_ENERGY, 0.0F).intValue();
                    if (energy > 0) event.setAmount(event.getAmount() * (1 + energy * 0.2F));
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            if (event.getSource().getEntity() instanceof LivingEntity entity && entity.getMainHandItem().is(EnigmaticItems.ANNIHILATING_SWORD)) {
                if (!entity.getOffhandItem().isEmpty()) return;
                CompoundTag data = victim.getPersistentData();
                float annihilation = data.getFloat("AnnihilationPoint") + 0.08F * event.getNewDamage() / victim.getHealth();
                if (entity.getRandom().nextFloat() < annihilation) {
                    data.putBoolean("AnnihilationKill", true);
                    if (victim.isAlive()) event.setNewDamage(event.getNewDamage() * 10F);
                    if (entity.level() instanceof ServerLevel server) {
//                    server.sendParticles(EnigmaticParticles.ABYSS, victim.getX(), victim.getEyeY(), victim.getZ(), 16, victim.getBbWidth(), victim.getBbHeight() / 2, entity.getBbWidth(), 0);
                    }
                }
                data.putFloat("AnnihilationPoint", annihilation);
            }
        }

        @SubscribeEvent
        private static void onTick(EntityTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof LivingEntity entity && !entity.level().isClientSide()) {
                EnigmaticData data = entity.getData(EnigmaticAttachments.ENIGMATIC_DATA);
                if (data.getAnnihilatingTick() > 0) {
                    entity.getAttributes().addTransientAttributeModifiers(getAttackSpeedBonus());
                    data.setAnnihilatingTick(data.getAnnihilatingTick() - 1);
                } else entity.getAttributes().removeAttributeModifiers(getAttackSpeedBonus());
            }
            if (event.getEntity() instanceof Player player && EnigmaticHandler.isTheWorthyOne(player)) {
                int[] data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA).getAnnihilationSweepData(); // dmg tick level
                if (data.length == 3 && data[1] > 0 && player.level() instanceof ServerLevel server) {
                    int energy = data[2];
                    int tick = data[1];
                    float base = (float) (Math.PI / 180.0F) * player.getYRot();
                    double x = -(3 + 0.3 * energy) * Mth.sin((float) (base + (tick - 3) * 25F * (Math.PI / 180.0F))) + player.getX() - Mth.sin(base) * 0.6F;
                    double y = player.getY(0.5);
                    double z = (3 + 0.3 * energy) * Mth.cos((float) (base + (tick - 3) * 25F * (Math.PI / 180.0F))) + player.getZ() + Mth.cos(base) * 0.6F;
                    server.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.25F, 1.5F);
//                    server.sendParticles(EnigmaticAddonParticles.ABYSS_CHAOS, x, y, z, 32, 1, 1, 1, 0.02);
                    server.sendParticles(ParticleTypes.WITCH, x, y, z, 32, 1, 1, 1, 0.02);
                    server.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 0, 0.0, 0.0, 0.0, 0.0);
                    player.getData(EnigmaticAttachments.ENIGMATIC_DATA).setAnnihilationSweepData(data[0], tick - 1, energy);
                    double o = 0.8 + 0.45 * energy;
                    AABB aabb = new AABB(x - o, y - o, z - o, x + o, y + o, z + o);
                    List<LivingEntity> entities = server.getEntitiesOfClass(LivingEntity.class, aabb, Entity::isAlive);
                    for (LivingEntity target : entities) {
                        if (target == player) continue;
                        if (player.isAlliedTo(target)) continue;
                        if (target instanceof ArmorStand && ((ArmorStand) target).isMarker()) continue;
                        target.hurt(EnigmaticDamageTypes.source(target.level(), EnigmaticDamageTypes.ABYSS, player), data[0] * 0.4F);
                        target.invulnerableTime = 0;
                    }
                }
            }
        }

        private static Multimap<Holder<Attribute>, AttributeModifier> getAttackSpeedBonus() {
            ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(EnigmaticLegacy.location("annihilating_boost"), 2, AttributeModifier.Operation.ADD_VALUE));
            return builder.build();
        }

        @SubscribeEvent
        private static void onUse(LivingEntityUseItemEvent.@NotNull Start event) {
            if (event.getItem().is(EnigmaticItems.ANNIHILATING_SWORD)) {
                event.setDuration(event.getHand().equals(InteractionHand.MAIN_HAND) ? 24 : 32000);
            }
        }

        @SubscribeEvent
        private static void onCraft(PlayerEvent.@NotNull ItemCraftedEvent event) {
        }
    }
}
