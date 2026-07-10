package auviotre.enigmatic.legacy.contents.item.spellstones.other;

import auviotre.enigmatic.legacy.ELEnumExtensions;
import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.entity.projectile.AngelBeam;
import auviotre.enigmatic.legacy.contents.entity.projectile.EngineHook;
import auviotre.enigmatic.legacy.contents.entity.projectile.SoulFlameBall;
import auviotre.enigmatic.legacy.contents.item.charms.HellBladeCharm;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.packets.client.SpellstoneSwordPacket;
import auviotre.enigmatic.legacy.registries.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.AttributeUtil;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static auviotre.enigmatic.legacy.registries.EnigmaticComponents.INT;
import static auviotre.enigmatic.legacy.registries.EnigmaticComponents.SPELL_LEVEL;

public class SpellstoneSword extends Item {
    private static final float BASE_DAMAGE = 6.0F;

    public SpellstoneSword() {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.RARE).durability(999).component(SPELL_LEVEL, 0).component(INT, 0));
    }

    public static void parry(Level level, LivingEntity blocker, DamageSource source, float damage) {
        if (level instanceof ServerLevel server)
            PacketDistributor.sendToPlayersNear(server, null, blocker.getX(), blocker.getY(), blocker.getZ(), 48, new SpellstoneSwordPacket(blocker.position(), 100));
        ItemStack useItem = blocker.getUseItem();
        Entity directEntity = source.getDirectEntity();
        useItem.hurtAndBreak(Mth.floor(damage / 2), blocker, LivingEntity.getSlotForHand(blocker.getUsedItemHand()));
        if (blocker instanceof Player player) player.getCooldowns().removeCooldown(useItem.getItem());
        blocker.stopUsingItem();
        blocker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2, 0, true, false));
        blocker.swing(blocker.getUsedItemHand());
        if (directEntity instanceof LivingEntity living && blocker != living) {
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, blocker.getBoundingBox().inflate(3.2));
            for (LivingEntity entity : entities) {
                if (entity == blocker) continue;
                Vec3 delta = entity.position().subtract(blocker.position()).normalize().scale(0.5);
                float modifier = Math.min(1.0F, 0.8F / entity.distanceTo(blocker));
                Vec3 vec = new Vec3(delta.x, 0, delta.z).normalize().scale(modifier);
                entity.addDeltaMovement(new Vec3(vec.x, entity.onGround() ? 1.2F * modifier : 0.0F, vec.z));
                entity.getPersistentData().putInt("DisasterCurse", 30);
                entity.hurt(entity.damageSources().mobAttack(blocker), 4.0F);
                entity.invulnerableTime = 5;
            }
            if (blocker instanceof Player player) player.getCooldowns().addCooldown(useItem.getItem(), 21);
            int sLevel = useItem.getOrDefault(SPELL_LEVEL, 0);
            useItem.set(INT, sLevel > 4 ? 4 : 2);
        }
    }

    public static void shoot(Level level, @NotNull LivingEntity shooter, @NotNull ItemStack stack) {
        int i = stack.getOrDefault(INT, 0);
        RandomSource random = shooter.getRandom();
        if (shooter.isCrouching()) {
            if (!shooter.hasInfiniteMaterials()) {
                stack.hurtAndBreak(3, shooter, LivingEntity.getSlotForHand(shooter.getUsedItemHand()));
                stack.set(INT, Math.max(0, i - 3));
            }
            if (shooter instanceof Player player) player.getCooldowns().addCooldown(stack.getItem(), 10);
            shooter.playSound(SoundEvents.BREEZE_SHOOT, 0.8F, 1.5F + random.nextFloat() * 0.2F);
            shooter.playSound(SoundEvents.TRIDENT_THROW.value(), 0.6F, 0.9F + random.nextFloat() * 0.3F);
            shooter.playSound(SoundEvents.WIND_CHARGE_BURST.value(), 0.6F, 0.5F);
            double range = shooter.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 0.5F;
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(shooter.getX() - range, shooter.getY() - range, shooter.getZ() - range, shooter.getX() + range, shooter.getY() + range, shooter.getZ() + range));
            list.removeIf(entity -> entity == shooter || entity.position().subtract(shooter.position()).normalize().dot(shooter.getForward()) < 0.25);
            AngelBeam angelBeam = EnigmaticEntities.ANGEL_BEAM.get().create(level);
            if (angelBeam != null) {
                angelBeam.setPos(shooter.position());
                angelBeam.setOwner(shooter);
            }
            if (level.isClientSide()) {
                Vec3 pos = new Vec3(shooter.getX(),  shooter.getY(0.5), shooter.getZ());
                for (double j = 0; j < 48; j++) {
                    Vec3 forward = shooter.getLookAngle().scale(0.8).add(random.nextFloat() * 0.8F - 0.4F, random.nextFloat() * 0.4F - 0.2F, random.nextFloat() * 0.8F - 0.4F).scale(0.6);
                    level.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, forward.x, forward.y, forward.z);
                }
                for (double j = 0; j < 48; j++) {
                    Vec3 forward = shooter.getLookAngle().scale(0.8).add(random.nextFloat() * 0.8F - 0.4F, random.nextFloat() * 0.4F - 0.2F, random.nextFloat() * 0.8F - 0.4F);
                    level.addParticle(ParticleTypes.WHITE_SMOKE, pos.x, pos.y, pos.z, forward.x, forward.y, forward.z);
                }
            }
            level.addFreshEntity(shooter);
            for (LivingEntity entity : list) {
                entity.hurt(shooter.damageSources().indirectMagic(angelBeam == null ? shooter : angelBeam, shooter), (float) shooter.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.8F);
                Vec3 vec = shooter.position().subtract(entity.position()).normalize();
                entity.knockback(2.0F, vec.x, vec.z);
                if (stack.getOrDefault(SPELL_LEVEL, 0) > 4) {
                    int bless = entity.getPersistentData().getInt("ResonanceAngelBless");
                    entity.getPersistentData().putInt("ResonanceAngelBless", bless | 4);
                }
            }
            if (!level.isClientSide() && angelBeam != null) angelBeam.discard();
        } else {
            if (!shooter.hasInfiniteMaterials()) {
                stack.hurtAndBreak(1, shooter, LivingEntity.getSlotForHand(shooter.getUsedItemHand()));
                stack.set(INT, i - 1);
            }
            if (shooter instanceof Player player) player.getCooldowns().addCooldown(stack.getItem(), 6);
            shooter.playSound(SoundEvents.BREEZE_SHOOT, 0.6F, 1.8F + random.nextFloat() * 0.2F);
            shooter.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.6F + random.nextFloat() * 0.2F);
            Vec3 beginning = shooter.getEyePosition();
            if (!shooter.level().isClientSide()) {
                AngelBeam angelBeam = new AngelBeam(level, shooter, beginning, ISpellstone.get(shooter).is(EnigmaticItems.ANGEL_BLESSING) ? 4.8F : 4.0F);
                level.addFreshEntity(angelBeam);
            }
        }
    }

    public static InteractionResultHolder<ItemStack> handleHook(Level level, @NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
        UUID uuid = player.getData(EnigmaticAttachments.ENIGMATIC_DATA).getEngineHook();
        if (uuid == null) {
            if (level instanceof ServerLevel serverlevel) {
                EngineHook engineHook = new EngineHook(player, level, stack);
                serverlevel.addFreshEntity(engineHook);
            }
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
            player.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 1.0F);
            return InteractionResultHolder.success(stack);
        } else {
            player.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 0.8F);
            if (level instanceof ServerLevel server && server.getEntity(uuid) instanceof EngineHook hook) {
                if (hook.getCurrentState() == EngineHook.State.FLYING) {
                    hook.discard();
                    return InteractionResultHolder.success(stack);
                }
                if (hook.getCurrentState() == EngineHook.State.HOOKED_IN_BLOCK) hook.pullStart();
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                return ItemUtils.startUsingInstantly(level, player, hand);
            } else player.getData(EnigmaticAttachments.ENIGMATIC_DATA).setEngineHook(null);
        }
        return InteractionResultHolder.pass(stack);
    }

    public static void soulFire(Level level, @NotNull LivingEntity shooter, @NotNull ItemStack stack, InteractionHand hand) {
        int i = stack.getOrDefault(INT, 0);
        shooter.swing(hand);
        if (shooter instanceof Player player) player.getCooldowns().addCooldown(stack.getItem(), 5);
        shooter.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F);
        if (!shooter.hasInfiniteMaterials()) stack.set(INT, i - 1);
        SoulFlameBall flameBall = new SoulFlameBall(level, shooter, null);
        flameBall.setDeltaMovement(shooter.getLookAngle().scale(1.6));
        level.addFreshEntity(flameBall);
    }

    public static ItemStack getResonance(@NotNull ItemStack stack) {
        Resonance resonance = stack.get(EnigmaticComponents.SPELL_RESONANCE);
        return resonance == null ? ItemStack.EMPTY : resonance.spellstone();
    }

    public static boolean isResonatingWith(@NotNull ItemStack stack, ItemLike spellstone) {
        Resonance resonance = stack.get(EnigmaticComponents.SPELL_RESONANCE);
        return resonance != null && resonance.spellstone().is(spellstone.asItem());
    }

    public static int getMaxEnergy(@NotNull ItemStack stack) {
        int level = stack.getOrDefault(SPELL_LEVEL, 0);
        if (isResonatingWith(stack, EnigmaticItems.BLAZING_CORE)) return 100;
        if (isResonatingWith(stack, EnigmaticItems.OCEAN_STONE)) return 64;
        if (isResonatingWith(stack, EnigmaticItems.FORGOTTEN_ICE)) return 100;
        if (isResonatingWith(stack, EnigmaticItems.REVIVAL_LEAF)) return 32;
        if (isResonatingWith(stack, EnigmaticItems.ANGEL_BLESSING)) return 7 + level;
        if (isResonatingWith(stack, EnigmaticItems.LOST_ENGINE)) return 125;
        if (isResonatingWith(stack, EnigmaticItems.ILLUSION_LANTERN)) return 15 + level * 3;
        if (isResonatingWith(stack, EnigmaticItems.VOID_PEARL)) return 3 + level;
        return 0;
    }

    public static int getColor(ItemStack stack) {
        if (getResonance(stack).getItem() instanceof SpellstoneItem spellstone) return spellstone.getColor();
        return 0;
    }

    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        Pair<Double, Double> bonus = getAttributeBonus(stack, stack.getOrDefault(SPELL_LEVEL, 0));
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, BASE_DAMAGE + bonus.getFirst(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -4.0F + bonus.getSecond(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        if (Form.getValue(stack) == Form.EYE_OF_NEBULA.value)
            builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(AttributeUtil.BASE_ENTITY_REACH_ID, 0.6, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        if (Form.getValue(stack) == Form.VOID_PEARL.value && stack.getOrDefault(INT, 0) > 0)
            builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(AttributeUtil.BASE_ENTITY_REACH_ID, 0.5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }

    public Pair<Double, Double> getAttributeBonus(ItemStack stack, int level) {
        int form = (int) (Form.getValue(stack) * 10);
        if (form == 5 && level > 4) return Pair.of(9.0, 2.4);
        boolean flag = level > 2;
        return switch (form) {
            case 1 -> Pair.of(flag ? 5.0 : 3.5, flag ? 1.45 : 1.35);
            case 2 -> Pair.of(flag ? 6.0 : 4.0, flag ? 1.4 : 1.25);
            case 3 -> Pair.of(flag ? 4.0 : 2.5, flag ? 1.5 : 1.35);
            case 4 -> Pair.of(flag ? 3.0 : 2.0, flag ? 1.75 : 1.6);
            case 5 -> Pair.of(flag ? 2.5 : 1.5, flag ? 2.0 : 1.8);
            case 6 -> Pair.of(flag ? 2.0 : 1.0, 2.0);
            case 7 -> Pair.of(flag ? 3.5 : 2.5, flag ? 1.7 : 1.65);
            case 8 -> Pair.of(flag ? 2.0 : 1.0, 1.6);
            case 9 -> Pair.of(flag ? 4.0 : 3.0, flag ? 1.8 : 1.75);
            case 10 -> Pair.of(flag ? 5.5 : 3.0, flag ? 1.5 : 1.4);
            default -> Pair.of(flag ? 2.0 : 0.0, flag ? 1.6 : 1.5);
        };
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        ItemStack resonance = getResonance(stack);
        int level = stack.getOrDefault(SPELL_LEVEL, 0);
        if (Screen.hasShiftDown()) {
            if (resonance.isEmpty()) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSword1");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSword2");
            } else {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneResonateEffect");
                int form = (int) (Form.getValue(stack) * 10);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneResonateEffect" + form + "-1");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneResonateEffect" + form + "-2");
                if (level > 4)
                    TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneResonateEffect" + form + "-3");
            }
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        if (resonance.isEmpty()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneResonateAbsent");
        } else {
            MutableComponent component = Component.translatable(resonance.getDescriptionId());
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneResonate", ChatFormatting.GOLD, component);
        }
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSwordLevel", ChatFormatting.GOLD, level);
    }

    public String getDescriptionId(ItemStack stack) {
        ItemStack resonance = getResonance(stack);
        if (resonance.isEmpty()) return super.getDescriptionId();
        return super.getDescriptionId() + "." + IItemHelper.getLocation(resonance.getItem()).getPath();
    }

    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        int level = stack.getOrDefault(SPELL_LEVEL, 0);
        if (isResonatingWith(stack, EnigmaticItems.REVIVAL_LEAF) && stack.getOrDefault(INT, 0) >= getMaxEnergy(stack)) {
            stack.set(INT, 0);
            attacker.heal(attacker.getMaxHealth() * 0.8F);
            if (attacker.level() instanceof ServerLevel server)
                PacketDistributor.sendToPlayersNear(server, null, attacker.getX(), attacker.getY(), attacker.getZ(), 24, new SpellstoneSwordPacket(attacker.position(), 50));
        } else if (isResonatingWith(stack, EnigmaticItems.BLAZING_CORE)) {
            target.igniteForSeconds(5 + level);
            if (level > 4) {
                attacker.igniteForSeconds(attacker.getRemainingFireTicks() * 0.05F + 5 + level);
            }
        }
    }

    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (state.getDestroySpeed(level, pos) != 0.0F) stack.hurtAndBreak(2, entity, EquipmentSlot.MAINHAND);
        return true;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected && entity.getInBlockState().is(Blocks.BUBBLE_COLUMN) && isResonatingWith(stack, EnigmaticItems.OCEAN_STONE) && entity.tickCount % 4 == 0 && stack.getOrDefault(SPELL_LEVEL, 0) > 4) {
            stack.set(INT, Math.min(getMaxEnergy(stack), stack.getOrDefault(INT, 0) + 2));
        }
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND && stack.is(this)) {
            ItemStack itemInHand = player.getItemInHand(InteractionHand.OFF_HAND);
            int form = (int) (Form.getValue(stack) * 10);
            if (form != 0 && itemInHand.is(EnigmaticItems.SPELLCORE))
                return ItemUtils.startUsingInstantly(level, player, hand);
            int i;
            switch (form) {
                case 1, 4, 5:
                    break;
                case 2:
                    if (stack.getOrDefault(INT, 0) > 0) return ItemUtils.startUsingInstantly(level, player, hand);
                    break;
                case 3:
                    if (player.isInWaterRainOrBubble() || stack.getOrDefault(INT, 0) > 7) return ItemUtils.startUsingInstantly(level, player, hand);
                    break;
                case 6, 8:
                    i = stack.getOrDefault(INT, 0);
                    if (i > 0)  {
                        if (form == 6) shoot(level, player, stack);
                        if (form == 8) soulFire(level, player, stack, hand);
                    }
                    else player.startUsingItem(hand);
                    return InteractionResultHolder.consume(stack);
                case 7:
                    return handleHook(level, player, stack, hand);
                case 9:
                    GlobalPos globalPos = stack.get(EnigmaticComponents.DIMENSIONAL_POS);
                    if (globalPos != null) {
                        if (level.dimension() == globalPos.dimension()) {
                            if (player.isCrouching()) {
                                Vec3 pos = globalPos.pos().getBottomCenter();
                                player.teleportTo(pos.x, pos.y, pos.z);
                                level.playSound(player, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.25F);
                                if (level instanceof ServerLevel server)
                                    server.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY(), player.getZ(), 32, 0.2, 0.4, 0.2, 0.03);
                                stack.remove(EnigmaticComponents.DIMENSIONAL_POS);
                                return InteractionResultHolder.success(stack);
                            }
                        } else stack.remove(EnigmaticComponents.DIMENSIONAL_POS);
                    }
                    return ItemUtils.startUsingInstantly(level, player, hand);
                case 10:
                    player.startUsingItem(hand);
                    player.getCooldowns().addCooldown(this, 64);
                    return InteractionResultHolder.consume(stack);
                default:
                    if (itemInHand.getItem() instanceof SpellstoneItem) {
                        if (itemInHand.is(EnigmaticItems.ETHERIUM_CORE)) return InteractionResultHolder.fail(stack);
                        if (itemInHand.is(EnigmaticItems.THE_CUBE)) return InteractionResultHolder.fail(stack);
                        return ItemUtils.startUsingInstantly(level, player, hand);
                    }
            }
        }
        return InteractionResultHolder.fail(stack);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        InteractionHand hand = user.getUsedItemHand();
        if (hand == InteractionHand.MAIN_HAND && stack.is(this)) {
            ItemStack itemInHand = user.getItemInHand(InteractionHand.OFF_HAND);
            int form = (int) (Form.getValue(stack) * 10);
            if (form != 0 && itemInHand.is(EnigmaticItems.SPELLCORE)) {
                user.swing(hand);
                ItemStack resonance = getResonance(stack);
                if (!resonance.isEmpty()) {
                    user.playSound(EnigmaticSounds.CHARGED_OFF.get(), 1.0F, 1.2F + user.getRandom().nextFloat() * 0.2F);
                    user.setItemInHand(InteractionHand.OFF_HAND, resonance.copy());
                    stack.set(DataComponents.RARITY, Rarity.RARE);
                    stack.remove(EnigmaticComponents.SPELL_RESONANCE);
                    return stack;
                }
            }
            switch (form) {
                case 1, 2, 3, 4, 5, 7, 9, 10:
                    break;
                case 6, 8:
                    if (stack.getOrDefault(INT, 0) <= 0) {
                        if (user instanceof Player player) player.getCooldowns().addCooldown(this, 12);
                        user.swing(hand);
                        stack.set(INT, getMaxEnergy(stack));
                        level.playSound(null, user.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6F, 0.8F);
                    }
                default:
                    if (itemInHand.getItem() instanceof SpellstoneItem spellstone) {
                        user.playSound(EnigmaticSounds.CHARGED_ON.get(), 1.0F, 1.2F + user.getRandom().nextFloat() * 0.2F);
                        user.swing(hand);
                        user.setItemInHand(InteractionHand.OFF_HAND, EnigmaticItems.SPELLCORE.toStack());
                        if (spellstone.equals(EnigmaticItems.CREATION_HEART.get()))
                            return EnigmaticItems.THE_JUDGEMENT.toStack();
                        stack.set(DataComponents.RARITY, Rarity.EPIC);
                        stack.set(EnigmaticComponents.SPELL_RESONANCE, Resonance.of(itemInHand.copy()));
                    }
            }
        }
        return stack;
    }

    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingDuration) {
        InteractionHand hand = user.getUsedItemHand();
        int tick = stack.getUseDuration(user) - remainingDuration;
        if (hand == InteractionHand.MAIN_HAND && stack.is(this)) {
            int energy = stack.getOrDefault(INT, 0);
            int sLevel = stack.getOrDefault(SPELL_LEVEL, 0);
            if (isResonatingWith(stack, EnigmaticItems.BLAZING_CORE)) {
                if (level instanceof ServerLevel serverLevel)
                    PacketDistributor.sendToPlayersNear(serverLevel, null, user.getX(), user.getY(), user.getZ(), 32, new SpellstoneSwordPacket(user.getEyePosition(), user.getLookAngle(), 22));
                if (user.tickCount % 5 == 0) {
                    if (!user.hasInfiniteMaterials()) stack.set(INT, Math.max(0, energy - 3));
                    double range = 3;
                    List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(user.getX() - range, user.getY() - range, user.getZ() - range, user.getX() + range, user.getY() + range, user.getZ() + range));
                    list.removeIf(entity -> entity == user || entity.position().subtract(user.position()).normalize().dot(user.getForward()) < 0.32);
                    for (LivingEntity entity : list) {
                        entity.hurt(EnigmaticDamageTypes.source(level, DamageTypes.LAVA, user), (float) user.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.6F);
                        entity.invulnerableTime = 4;
                        Vec3 vec = user.position().subtract(entity.position()).normalize();
                        entity.knockback(0.2F, vec.x, vec.z);
                    }
                }
            } else if (isResonatingWith(stack, EnigmaticItems.OCEAN_STONE) && tick == 20 - sLevel) {
                user.playSound(EnigmaticSounds.CHARGED_ON.get(), 1.0F, 0.9F + 0.1F * user.getRandom().nextFloat());
            } else if (isResonatingWith(stack, EnigmaticItems.ANGEL_BLESSING)) {
                if (tick == 12) level.playSound(null, user.blockPosition(), EnigmaticSounds.CHARGED_ON.get(), SoundSource.PLAYERS, 0.6F, 0.4F);
                else if (tick == 24) level.playSound(null, user.blockPosition(), EnigmaticSounds.CHARGED_ON.get(), SoundSource.PLAYERS, 0.6F, 0.8F);
            } else if (isResonatingWith(stack, EnigmaticItems.LOST_ENGINE)) {
                UUID uuid = user.getData(EnigmaticAttachments.ENIGMATIC_DATA).getEngineHook();
                if (uuid == null) {
                    user.getData(EnigmaticAttachments.ENIGMATIC_DATA).setEngineHook(null);
                    return;
                }
                if (user.tickCount % 3 == 0) user.playSound(SoundEvents.CHAIN_PLACE, 0.8F, 0.9F + 0.3F * user.getRandom().nextFloat());
                if (level instanceof ServerLevel server && server.getEntity(uuid) instanceof EngineHook hook) {
                    if (hook.getCurrentState() == EngineHook.State.HOOKED_IN_BLOCK) hook.pull();
                    else hook.drag();
                    if (stack.getOrDefault(SPELL_LEVEL, 0) > 4) stack.set(INT, Math.min(getMaxEnergy(stack), energy + 1));
                }
            } else if (isResonatingWith(stack, EnigmaticItems.EYE_OF_NEBULA) && tick == 32) {
                user.playSound(EnigmaticSounds.CHARGED_ON.get(), 1.0F, 0.9F + 0.1F * user.getRandom().nextFloat());
            }
        }
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingDuration) {
        InteractionHand hand = user.getUsedItemHand();
        int tick = stack.getUseDuration(user) - remainingDuration;
        if (hand == InteractionHand.MAIN_HAND && stack.is(this)) {
            int sLevel = stack.getOrDefault(SPELL_LEVEL, 0);
            if (isResonatingWith(stack, EnigmaticItems.BLAZING_CORE) && stack.getOrDefault(INT, 0) > 0) {
                if (user instanceof Player player) {
                    player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                    player.sweepAttack();
                    player.getCooldowns().addCooldown(this, 20);
                }
                user.swing(InteractionHand.MAIN_HAND);
            } else if (isResonatingWith(stack, EnigmaticItems.OCEAN_STONE) && tick >= (20 - sLevel) && user instanceof Player player) {
                stack.hurtAndBreak(1, user, LivingEntity.getSlotForHand(hand));
                float f = 2.4F;
                float yRot = player.getYRot();
                float xRot = player.getXRot();
                float x = (float) (-Math.sin(Math.toRadians(yRot)) * Math.cos(Math.toRadians(xRot)));
                float y = (float) -Math.sin(Math.toRadians(xRot));
                float z = (float) (Math.cos(Math.toRadians(yRot)) * Math.cos(Math.toRadians(xRot)));
                float len = Mth.sqrt(x * x + y * y + z * z);
                x *= f / len;
                y *= f / len;
                z *= f / len;
                player.push(x, y, z);
                if (!player.isInWaterRainOrBubble()) stack.set(INT, Math.max(0, stack.getOrDefault(INT, 0) - 4));
                float modifier = sLevel * 0.2F + 0.8F;
                player.startAutoSpinAttack(20, (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * modifier, stack);
                if (player.onGround()) player.move(MoverType.SELF, new Vec3(0.0F, 1.2F, 0.0F));
                level.playSound(null, player, SoundEvents.TRIDENT_RIPTIDE_3.value(), SoundSource.PLAYERS, 1.0F, 1.0F + 0.2F * player.getRandom().nextFloat());
            } else if (isResonatingWith(stack, EnigmaticItems.LOST_ENGINE)) {
                UUID uuid = user.getData(EnigmaticAttachments.ENIGMATIC_DATA).getEngineHook();
                if (uuid != null) {
                    if (level instanceof ServerLevel server && server.getEntity(uuid) instanceof EngineHook hook) hook.discard();
                    user.swing(InteractionHand.MAIN_HAND);
                } else user.getData(EnigmaticAttachments.ENIGMATIC_DATA).setEngineHook(null);
                user.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 0.2F);
            } else if (isResonatingWith(stack, EnigmaticItems.EYE_OF_NEBULA) && tick >= 32) {
                List<LivingEntity> observedEntities = EnigmaticHandler.getObservedEntities(user, level, 2, 16 + tick / 4, false);
                observedEntities.removeIf(entity -> entity.getBoundingBox().getSize() > user.getBoundingBox().getSize() * 5);
                if (!observedEntities.isEmpty()) {
                    LivingEntity entity = observedEntities.getLast();
                    Vec3 position = entity.position();
                    level.playSound(user, BlockPos.containing(position), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.25F);
                    Vec3 vec3 = user.position();
                    double value = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    if (sLevel > 4)
                        entity.hurt(EnigmaticDamageTypes.source(user.level(), DamageTypes.MAGIC, user), (float) (value * (1.2F + position.distanceTo(vec3) * 0.02F)));
                    else entity.hurt(EnigmaticDamageTypes.source(user.level(), DamageTypes.MAGIC, user), (float) value * 1.2F);
                    stack.hurtAndBreak(1, user, LivingEntity.getSlotForHand(hand));
                    entity.teleportTo(vec3.x, vec3.y, vec3.z);
                    user.teleportTo(position.x, position.y, position.z);
                    user.lookAt(EntityAnchorArgument.Anchor.EYES, vec3);
                    if (user instanceof Player player) player.sweepAttack();
                    user.swing(hand);
                    double len = position.distanceTo(vec3) / 1.25;
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(3), living -> living.isAlive() && living != user && user.canAttack(living));
                    for (LivingEntity livingEntity : entities) {
                        Vec3 vec = position.subtract(livingEntity.position()).normalize();
                        livingEntity.knockback(0.8F, vec.x, vec.z);
                    }
                    for (int i = 0; i < len; i++) {
                        double x = (vec3.x * i + position.x * (len - i)) / len;
                        double y = (vec3.y * i + position.y * (len - i)) / len;
                        double z = (vec3.z * i + position.z * (len - i)) / len;
                        if (sLevel > 4) {
                            AABB aabb = new AABB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
                            entities = level.getEntitiesOfClass(LivingEntity.class, aabb, living -> living.isAlive() && living != user && user.canAttack(living));
                            for (LivingEntity livingEntity : entities) {
                                livingEntity.hurt(EnigmaticDamageTypes.source(user.level(), DamageTypes.MAGIC, user), (float) value * 0.4F);
                            }
                        }
                        if (level instanceof ServerLevel server)
                            server.sendParticles(ParticleTypes.PORTAL, x, y, z, 48, 0.2, 0.4, 0.2, 0.03);
                    }
                } else level.playSound(user, user.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 0.6F, 0.25F);
            }
        }
    }

    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        if (player != null && context.getHand() == InteractionHand.MAIN_HAND && stack.is(this)) {
            if (isResonatingWith(stack, EnigmaticItems.EYE_OF_NEBULA) && player.isCrouching()) {
                if (stack.get(EnigmaticComponents.DIMENSIONAL_POS) == null) {
                    Level level = context.getLevel();
                    level.playSound(player, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6F, 1.2F);
                    stack.set(EnigmaticComponents.DIMENSIONAL_POS, GlobalPos.of(level.dimension(), pos.above()));
                    return InteractionResult.SUCCESS;
                }
            } else if (isResonatingWith(stack, EnigmaticItems.BLAZING_CORE)) {
                int energy = stack.getOrDefault(INT, 0);
                if (energy == 0) {
                    int heat = 0;
                    Iterable<BlockPos> posSet = BlockPos.betweenClosed(pos.offset(3, 3, 3), pos.offset(-3, -3, -3));
                    for (BlockPos blockPos : posSet) {
                        BlockState state = context.getLevel().getBlockState(blockPos);
                        if (state.is(Blocks.FIRE)) {
                            heat++;
                            context.getLevel().removeBlock(blockPos, false);
                        }
                        if (state.is(Blocks.LAVA)) heat += 9;
                        if (state.is(Blocks.MAGMA_BLOCK)) heat += 2;
                        if (heat > 8) {
                            player.getCooldowns().addCooldown(this, 10);
                            if (player instanceof ServerPlayer serverPlayer)
                                PacketDistributor.sendToPlayer(serverPlayer, new SpellstoneSwordPacket(context.getClickLocation(), 20));
                            stack.set(INT, getMaxEnergy(stack));
                            return InteractionResult.SUCCESS;
                        }
                    }
                } else if (!player.isCrouching()) {
                    int sLevel = stack.getOrDefault(SPELL_LEVEL, 0);
                    player.getCooldowns().addCooldown(this, 40 - sLevel * 2);
                    if (player instanceof ServerPlayer serverPlayer)
                        PacketDistributor.sendToPlayer(serverPlayer, new SpellstoneSwordPacket(context.getClickLocation(), 21));
                    if (!player.hasInfiniteMaterials()) stack.set(INT, Math.max(0, energy - 10));
                    List<Mob> entities = player.level().getEntitiesOfClass(Mob.class, new AABB(pos).inflate(2), LivingEntity::isAlive);
                    for (Mob mob : entities) {
                        mob.hurt(EnigmaticDamageTypes.source(player.level(), DamageTypes.LAVA, player), (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.25F);
                        Vec3 vec = context.getClickLocation().subtract(0.0, 0.5, 0.0).subtract(mob.position()).normalize();
                        mob.knockback(1.2F, vec.x, vec.z);
                        mob.igniteForSeconds(8);
                    }
                    Vec3 vec3 = player.getDeltaMovement();
                    if (!player.onGround()) player.setDeltaMovement(vec3.x, 0.4F, vec3.z);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useOn(context);
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        if (isResonatingWith(stack, EnigmaticItems.ANGEL_BLESSING)) return 36;
        if (isResonatingWith(stack, EnigmaticItems.ILLUSION_LANTERN)) return 80;
        if (isResonatingWith(stack, EnigmaticItems.VOID_PEARL)) return 24;
        return getResonance(stack).isEmpty() || entity.getOffhandItem().is(EnigmaticItems.SPELLCORE) ? 60 : 32000;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        if (isResonatingWith(stack, EnigmaticItems.BLAZING_CORE)) return UseAnim.BLOCK;
        if (isResonatingWith(stack, EnigmaticItems.OCEAN_STONE)) return UseAnim.BOW;
        if (isResonatingWith(stack, EnigmaticItems.LOST_ENGINE)) return UseAnim.BLOCK;
        if (isResonatingWith(stack, EnigmaticItems.VOID_PEARL)) return UseAnim.BLOCK;
        if (isResonatingWith(stack, EnigmaticItems.ILLUSION_LANTERN)) return UseAnim.BOW;
        return UseAnim.CUSTOM;
    }

    public AABB getSweepHitBox(ItemStack stack, Player player, Entity target) {
        if (isResonatingWith(stack, EnigmaticItems.VOID_PEARL) && stack.getOrDefault(INT, 0) > 0) {
            return target.getBoundingBox().inflate(8, 0.3, 8);
        }
        return super.getSweepHitBox(stack, player, target);
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        if (ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(ability)) return true;
        if (isResonatingWith(stack, EnigmaticItems.VOID_PEARL)) return ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(ability);
        return false;
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 64;
    }

    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(EnigmaticItems.SPELLSTONE_DEBRIS);
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.PRIMARY && slot.mayPickup(player) && slot.hasItem() && other.is(EnigmaticItems.SPELLCORE)) {
            int level = stack.getOrDefault(SPELL_LEVEL, 0);
            if (level < 5) {
                stack.set(SPELL_LEVEL, level + 1);
                other.shrink(1);
                player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.4F + player.getRandom().nextFloat() * 0.2F);
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }
    
    public record Resonance(ItemStack spellstone) {
        public static final MapCodec<Resonance> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.SINGLE_ITEM_CODEC.fieldOf("spellstone").forGetter(Resonance::spellstone)
        ).apply(instance, Resonance::of));

        public static final Codec<Resonance> CODEC = MAP_CODEC.codec();

        public static final StreamCodec<RegistryFriendlyByteBuf, Resonance> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, Resonance::spellstone,
                Resonance::of);

        public static Resonance of(ItemStack spellstone) {
            return new Resonance(spellstone);
        }
    }
    
    public enum Form {
        GOLEM_HEART(0.1F, "golem_heart"),
        BLAZING_CORE(0.2F, "blazing_core"),
        OCEAN_STONE(0.3F, "ocean_stone"),
        FORGOTTEN_ICE(0.4F, "forgotten_ice"),
        REVIVAL_LEAF(0.5F, "revival_leaf"),
        ANGEL_BLESSING(0.6F, "angel_blessing"),
        LOST_ENGINE(0.7F, "lost_engine"),
        ILLUSION_LANTERN(0.8F, "illusion_lantern"),
        EYE_OF_NEBULA(0.9F, "eye_of_nebula"),
        VOID_PEARL(1.0F, "void_pearl");

        public final float value;
        public final String spellstone;

        Form(float value, String spellstone) {
            this.value = value;
            this.spellstone = spellstone;
        }

        public static float getValue(ItemStack stack) {
            ItemStack resonance = getResonance(stack);
            if (resonance.isEmpty()) return 0.0F;
            for (Form form : Form.values()) {
                if (form.spellstone.equals(IItemHelper.getLocation(resonance.getItem()).getPath())) return form.value;
            }
            return 0.0F;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientExtension implements IClientItemExtensions {
        public static void animate(ModelPart rightArm, ModelPart leftArm, @NotNull LivingEntity entity, boolean rightHanded) {
            ModelPart right = rightHanded ? rightArm : leftArm;
            ModelPart left = rightHanded ? leftArm : rightArm;
            float duration = (float) entity.getUseItem().getUseDuration(entity);
            float tick = Mth.clamp((float) entity.getTicksUsingItem(), 0.0F, duration);
            float partial = tick / duration;
            right.yRot = rightHanded ? -0.8F : 0.8F;
            right.xRot = Mth.lerp(partial, -0.98F, -1.28F);
            left.xRot = Mth.lerp(partial, -0.98F, -1.28F);
            left.yRot = 0.4F * (rightHanded ? 1 : -1);
        }

        public HumanoidModel.@Nullable ArmPose getArmPose(@NotNull LivingEntity entity, InteractionHand hand, ItemStack stack) {
            if (entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0) {
                if (Form.getValue(stack) == 0.0F || entity.getOffhandItem().is(EnigmaticItems.SPELLCORE)) return ELEnumExtensions.SPELLSTONE_SWORD.getValue();
                if (isResonatingWith(stack, EnigmaticItems.ANGEL_BLESSING)) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            }
            if (!entity.swinging && isResonatingWith(stack, EnigmaticItems.ANGEL_BLESSING) && stack.getOrDefault(INT, 0) > 0) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
            return null;
        }

        public boolean applyForgeHandTransform(PoseStack poseStack, @NotNull LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
            int i = arm == HumanoidArm.RIGHT ? 1 : -1;
            float useTick = stack.getUseDuration(player) - (player.getUseItemRemainingTicks() - partialTick + 1.0F);
            if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                if (Form.getValue(stack) == 0.0F || player.getOffhandItem().is(EnigmaticItems.SPELLCORE)) {
                    poseStack.translate(i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
                    poseStack.translate(i * -0.2785682F, 0.18344387F, 0.15731531F);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-13.935F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(i * 35.3F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(i * -9.785F));
                    float f12 = useTick / 20.0F;
                    f12 = Math.min(1.0F, (f12 * f12 + f12 * 2.0F) / 3.0F);
                    if (f12 > 0.1F) {
                        float f15 = Mth.sin((useTick - 0.1F) * 1.3F);
                        float f18 = f12 - 0.1F;
                        float f20 = f15 * f18;
                        poseStack.translate(0.0F, f20 * 0.004F, 0.0F);
                    }
                    poseStack.translate(0.0F, 0.0F, f12 * 0.04F);
                    poseStack.scale(1.0F, 1.0F, 1.0F + f12 * 0.2F);
                    poseStack.mulPose(Axis.YN.rotationDegrees(i * 45.0F));
                    return true;
                } else if (isResonatingWith(stack, EnigmaticItems.EYE_OF_NEBULA)) {
                    poseStack.translate(i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
                    poseStack.translate(i * -0.2785682F, 0.18344387F, 0.15731531F);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-13.935F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(i * 35.3F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(i * -9.785F));
                    float f12 = useTick / 20.0F;
                    f12 = Math.min(1.0F, (f12 * f12 + f12 * 2.0F) / 3.0F);
                    if (f12 > 0.1F) {
                        float f15 = Mth.sin((useTick - 0.1F) * 1.3F);
                        float f18 = f12 - 0.1F;
                        float f20 = f15 * f18;
                        poseStack.translate(0.0F, f20 * 0.004F, 0.0F);
                    }
                    poseStack.translate(0.0F, 0.1F, f12 * 0.04F);
                    poseStack.scale(1.0F, 1.0F, 1.0F + f12 * 0.2F);
                    poseStack.mulPose(Axis.YN.rotationDegrees(i * 35.0F));
                    poseStack.mulPose(Axis.XN.rotationDegrees(i * 85.0F));
                    return true;
                } else if (isResonatingWith(stack, EnigmaticItems.ANGEL_BLESSING)) {
                    poseStack.translate(i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
                    poseStack.translate(i * -0.4785682F, -0.094387F, 0.05731531F);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-11.935F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(i * 65.3F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(i * -9.785F));
                    float f13 = Math.min(1.0F, useTick / stack.getUseDuration(player));
                    if (f13 > 0.1F) {
                        float f16 = Mth.sin((useTick - 0.1F) * 1.3F);
                        float f3 = f13 - 0.1F;
                        float f4 = f16 * f3;
                        poseStack.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                    }
                    poseStack.translate(f13 * 0.0F, f13 * 0.0F, f13 * 0.04F);
                    poseStack.scale(1.0F, 1.0F, 1.0F + f13 * 0.2F);
                    poseStack.mulPose(Axis.YN.rotationDegrees(i * 45.0F));
                    return true;
                }
            } else if (isResonatingWith(stack, EnigmaticItems.ANGEL_BLESSING)) {
                swingTransform(poseStack, i, swingProcess);
                poseStack.translate(i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
                attackTransform(poseStack, i, swingProcess);
                if (stack.getOrDefault(INT, 0) > 0 && swingProcess < 0.001F && player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                    poseStack.translate(i == 1 ? -0.641864F : 0.125F, 0.0F, 0.0F);
                    poseStack.mulPose(Axis.YP.rotationDegrees(i * 5.0F));
                }
                return true;
            } else if (isResonatingWith(stack, EnigmaticItems.ILLUSION_LANTERN) && stack.getOrDefault(INT, 0) > 0 && player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                swingTransform(poseStack, i, swingProcess);
                poseStack.translate(i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
                attackTransform(poseStack, i, swingProcess);
                poseStack.mulPose(Axis.ZN.rotationDegrees(i * 17.5F));
                poseStack.mulPose(Axis.XN.rotationDegrees(i * 75.0F));
                return true;
            }
            return false;
        }

        private void swingTransform(PoseStack poseStack, int i, float swingProcess) {
            float f = -0.4F * Mth.sin(Mth.sqrt(swingProcess) * (float) Math.PI);
            float f1 = 0.2F * Mth.sin(Mth.sqrt(swingProcess) * (float) (Math.PI * 2));
            float f2 = -0.2F * Mth.sin(swingProcess * (float) Math.PI);
            poseStack.translate(i * f, f1, f2);
        }

        private void attackTransform(PoseStack poseStack, int i, float swingProcess) {
            float f = Mth.sin(swingProcess * swingProcess * (float) Math.PI);
            poseStack.mulPose(Axis.YP.rotationDegrees(i * (45.0F + f * -20.0F)));
            float f1 = Mth.sin(Mth.sqrt(swingProcess) * (float) Math.PI);
            poseStack.mulPose(Axis.ZP.rotationDegrees(i * f1 * -20.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(f1 * -80.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(i * -45.0F));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Decorator implements IItemDecorator {
        public boolean render(GuiGraphics graphics, Font font, @NotNull ItemStack stack, int x, int y) {
            int energy = stack.getOrDefault(INT, 0);
            if (energy > 0 && getMaxEnergy(stack) > 0) {
                graphics.pose().pushPose();
                int j = x + 2;
                int k = y + 12;
                int width = Mth.floor(13.0F * energy / getMaxEnergy(stack));
                int color = getColor(stack);
                graphics.fill(RenderType.guiOverlay(), j, k, j + 13, k + (stack.isBarVisible() ? 1 : 2), 0xFF000000);
                graphics.fill(RenderType.guiOverlay(), j, k, j + width, k + 1, color | 0xFF000000);
                graphics.pose().popPose();
                return true;
            }
            return false;
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (event.getNewDamage() >= Float.MAX_VALUE) return;
            LivingEntity entity = event.getEntity();
            ItemStack stack = entity.getWeaponItem();
            if (stack.is(EnigmaticItems.SPELLSTONE_SWORD)) {
                if (isResonatingWith(stack, EnigmaticItems.GOLEM_HEART)) {
                    if (event.getSource().is(EnigmaticTags.DamageTypes.GOLEM_HEART_IS_MELEE)) {
                        event.setNewDamage(event.getNewDamage() * 0.8F);
                    }
                } else if (isResonatingWith(stack, EnigmaticItems.OCEAN_STONE)) {
                    if (entity.isInWaterRainOrBubble()) event.setNewDamage(event.getNewDamage() * 0.6F);
                } else if (isResonatingWith(stack, EnigmaticItems.FORGOTTEN_ICE)) {
                    int shield = stack.getOrDefault(INT, 0);
                    if (event.getNewDamage() <= shield) {
                        shield -= (int) event.getNewDamage();
                        event.setNewDamage(0);
                    } else {
                        shield = 0;
                        event.setNewDamage(event.getNewDamage() - shield);
                    }
                    stack.set(INT, shield);
                }
            }
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                stack = attacker.getWeaponItem();
                int level = stack.getOrDefault(SPELL_LEVEL, 0);
                if (isResonatingWith(stack, EnigmaticItems.BLAZING_CORE) && level > 4) {
                    int time = (int) Math.pow((double) attacker.getRemainingFireTicks() / 100, 0.9);
                    event.setNewDamage(event.getNewDamage() * (1.0F + time * 0.05F));
                } else if (isResonatingWith(stack, EnigmaticItems.OCEAN_STONE)) {
                    if (attacker.isInWaterRainOrBubble()) event.setNewDamage(event.getNewDamage() * 1.25F);
                } else if (isResonatingWith(stack, EnigmaticItems.ANGEL_BLESSING)) {
                    if (!attacker.onGround() && attacker.level().getBlockState(attacker.blockPosition()).isAir()) {
                        event.setNewDamage(event.getNewDamage() * 1.6F);
                    }
                    if (level > 4) {
                        int bless = entity.getPersistentData().getInt("ResonanceAngelBless");
                        int buff = (bless & 1) + (bless & 2 >> 1) + (bless & 4 >> 2);
                        float modifier = switch (buff) {
                            case 3 -> 1.0F;
                            case 2 -> 0.5F;
                            case 1 -> 0.25F;
                            default -> 0;
                        };
                        event.setNewDamage(event.getNewDamage() * (1.0F + modifier));
                    }
                } else if (isResonatingWith(stack, EnigmaticItems.LOST_ENGINE)) {
                     int len = (int) Math.pow(attacker.fallDistance * 2 + attacker.getDeltaMovement().length() * 10, 0.9);
                    event.setNewDamage(event.getNewDamage() * (1.0F + 0.025F * (float) len));
                } else if (isResonatingWith(stack, EnigmaticItems.ILLUSION_LANTERN)) {
                    if (event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                        int soulLevel = entity.getPersistentData().getInt("IllusionSoulLevel");
                        if (entity.getHealth() < soulLevel * 2 + event.getNewDamage()) {
                            event.setNewDamage(event.getNewDamage() + 5 * soulLevel);
                            if (entity.level() instanceof ServerLevel server)
                                PacketDistributor.sendToPlayersNear(server, null, entity.getX(), entity.getY(), entity.getZ(), 24, new SpellstoneSwordPacket(entity.position(), 80));
                        }
                    }
                    if (event.getSource().getDirectEntity() instanceof SoulFlameBall) {
                        int soulLevel = entity.getPersistentData().getInt("IllusionSoulLevel");
                        entity.getPersistentData().putInt("IllusionSoulLevel", soulLevel + 1);
                    }
                    if (level > 4) {
                        float width = entity.getBbWidth() * 4;
                        List<Mob> entities = entity.level().getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate(width, entity.getBbHeight() / 2, width),
                                living -> living.isAlive() && living.getType().is(EntityTypeTags.UNDEAD));
                        for (Mob living : entities) {
                            if (living == entity) continue;
                            if ((living.getTarget() == null || living.getTarget() == attacker) && living.getLastAttacker() != attacker)
                                living.setTarget(entity);
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            LivingEntity entity = event.getEntity();
            if (event.getNewDamage() >= Float.MAX_VALUE) return;
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
                ItemStack stack = attacker.getWeaponItem();
                if (stack.is(EnigmaticItems.SPELLSTONE_SWORD) && event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                    int energy = getMaxEnergy(stack);
                    int level = stack.getOrDefault(SPELL_LEVEL, 0);
                    if (isResonatingWith(stack, EnigmaticItems.FORGOTTEN_ICE)) {
                        if (event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                            int ticksFrozen = entity.getTicksFrozen();
                            entity.setTicksFrozen(ticksFrozen + 40 + 10 * level);
                            int add = Math.min(energy, stack.getOrDefault(INT, 0) + ticksFrozen / 100);
                            stack.set(INT, add);
                        }
                    } else if (isResonatingWith(stack, EnigmaticItems.REVIVAL_LEAF)) {
                        if (event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                            int value = stack.getOrDefault(INT, 0);
                            if (attacker.getRandom().nextInt(100) < 30 + level * 4) {
                                MobEffectInstance poison = entity.getEffect(MobEffects.POISON);
                                if (poison != null) {
                                    entity.addEffect(new MobEffectInstance(EnigmaticEffects.POISON, poison.getDuration(), poison.getAmplifier(), true, true));
                                    entity.removeEffect(MobEffects.POISON);
                                } else {
                                    MobEffectInstance effect = entity.getEffect(EnigmaticEffects.POISON);
                                    int amplifier = effect == null ? 0 : Math.min(4, effect.getAmplifier() + 1);
                                    int duration = effect == null ? 400 : Math.max(240, effect.getDuration() + 60);
                                    entity.addEffect(new MobEffectInstance(EnigmaticEffects.POISON, duration, amplifier, true, true));
                                }
                            }
                            MobEffectInstance effect = entity.getEffect(EnigmaticEffects.POISON);
                            if (effect != null) {
                                float modifier = 0.0F;
                                if (attacker instanceof Player player) modifier = player.getAttackStrengthScale(0.0F);
                                stack.set(INT, Math.min(energy, value + (int) (modifier * (effect.getAmplifier() + 1))));
                            }
                            if (level > 4) {
                                if (entity.isInvertedHealAndHarm())
                                    entity.hurt(EnigmaticDamageTypes.source(attacker.level(), DamageTypes.MAGIC, attacker), event.getNewDamage() * 0.4F);
                                else entity.heal(event.getNewDamage() * 0.4F);
                            }
                        }
                    } else if (isResonatingWith(stack, EnigmaticItems.ANGEL_BLESSING)) {
                        if (event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK) && level > 4) {
                            int bless = entity.getPersistentData().getInt("ResonanceAngelBless");
                            entity.getPersistentData().putInt("ResonanceAngelBless", bless | 1);
                        }
                    } else if (isResonatingWith(stack, EnigmaticItems.ILLUSION_LANTERN)) {
                        int soulLevel = entity.getPersistentData().getInt("IllusionSoulLevel");
                        entity.getPersistentData().putInt("IllusionSoulLevel", soulLevel + (level + 1) / 2);
                    } else if (isResonatingWith(stack, EnigmaticItems.VOID_PEARL)) {
                        entity.invulnerableTime = 0;
                        entity.hurt(EnigmaticDamageTypes.source(entity.level(), EnigmaticDamageTypes.DARKNESS, attacker), event.getNewDamage() * (0.2F + level * 0.05F));
                        if (level > 4) {
                            List<MobEffectInstance> instances = new ArrayList<>(entity.getActiveEffects());
                            int count = 0;
                            for (MobEffectInstance instance : instances) {
                                if (entity.removeEffect(instance.getEffect()))
                                    count += (instance.getAmplifier() + 1);
                            }
                            if (count > 0) {
                                entity.invulnerableTime = 0;
                                entity.hurt(EnigmaticDamageTypes.source(entity.level(), EnigmaticDamageTypes.DARKNESS, attacker), event.getNewDamage() * (count * 0.05F));
                            }
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onCrit(@NotNull CriticalHitEvent event) {
            Player player = event.getEntity();
            Entity target = event.getTarget();
            ItemStack stack = player.getWeaponItem();
            int level = stack.getOrDefault(SPELL_LEVEL, 0);
            if (event.isVanillaCritical() && stack.is(EnigmaticItems.SPELLSTONE_SWORD)) {
                if (isResonatingWith(stack, EnigmaticItems.FORGOTTEN_ICE)) {
                    int ticksFrozen = target.getTicksFrozen();
                    if (level > 4 && ticksFrozen > 400) {
                        List<Mob> mobs = player.level().getEntitiesOfClass(Mob.class, target.getBoundingBox().inflate(4), mob -> mob.isAlive() && mob != target);
                        int total = ticksFrozen / 27;
                        for (Mob mob : mobs) total += mob.getTicksFrozen() / 27;
                        double modifier = Math.sqrt(total);
                        event.setDamageMultiplier(event.getDamageMultiplier() + (float) modifier);
                        double value = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        target.setTicksFrozen(0);
                        if (player.level() instanceof ServerLevel server) {
                            PacketDistributor.sendToPlayersNear(server, null, target.getX(), target.getY(), target.getZ(), 48, new SpellstoneSwordPacket(target.position().add(0, target.getBbHeight() * 0.5, 0), 40));
                        }
                        for (Mob mob : mobs)
                            mob.hurt(EnigmaticDamageTypes.source(player.level(), DamageTypes.FREEZE, player), (float) (value * modifier * 0.32));
                    } else {
                        float frozen = ticksFrozen / 540.0F;
                        event.setDamageMultiplier(event.getDamageMultiplier() + frozen);
                    }
                } else if (isResonatingWith(stack, EnigmaticItems.VOID_PEARL)) {
                    int energy = stack.getOrDefault(INT, 0);
                    if (energy > 0 && target instanceof LivingEntity entity) {
                        stack.set(INT, Math.max(0, energy - 1));
                        entity.knockback(0.4000000059604645, Mth.sin(player.getYRot() * 0.017453292F), -Mth.cos(player.getYRot() * 0.017453292F));
                        event.setDamageMultiplier(event.getDamageMultiplier() + 1.5F);
                        if (player.level() instanceof ServerLevel server)
                            PacketDistributor.sendToPlayersNear(server, null, entity.getX(), entity.getY(), entity.getZ(), 48, new SpellstoneSwordPacket(entity.position(), 101));
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            if (event.getAmount() >= Float.MAX_VALUE) return;
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
                ItemStack stack = attacker.getWeaponItem();
                if (stack.is(EnigmaticItems.SPELLSTONE_SWORD)) {
                    if (isResonatingWith(stack, EnigmaticItems.GOLEM_HEART)) {
                        float armor = (float) entity.getAttributeValue(Attributes.ARMOR);
                        event.setAmount(event.getAmount() + armor * 0.4F);
                        if (stack.getOrDefault(SPELL_LEVEL, 0) > 4) {
                            float base = (float) Math.pow(HellBladeCharm.getAttributeBase(attacker, Attributes.KNOCKBACK_RESISTANCE), 0.9);
                            event.setAmount(event.getAmount() * (1.0F + base * 0.8F));
                        }
                    } else if (isResonatingWith(stack, EnigmaticItems.LOST_ENGINE) && stack.getOrDefault(SPELL_LEVEL, 0) > 4) {
                        int energy = stack.getOrDefault(INT, 0);
                        if (energy >= getMaxEnergy(stack)) {
                            if (!attacker.hasInfiniteMaterials()) stack.set(INT, 0);
                            event.setAmount(event.getAmount() * 2.5F);
                        }
                    }
                }
            }
            if (entity.getUseItem().is(EnigmaticItems.SPELLSTONE_SWORD)) {
                if (isResonatingWith(entity.getUseItem(), EnigmaticItems.VOID_PEARL)) {
                    Entity directEntity = event.getSource().getDirectEntity();
                    if (directEntity != null && directEntity.position().subtract(entity.position()).dot(entity.getForward()) > 0) {
                        if (!event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
                            parry(entity.level(), entity, event.getSource(), event.getAmount());
                            event.setCanceled(true);
                        }
                    }
                }
            }
            if (entity.getWeaponItem().is(EnigmaticItems.SPELLSTONE_SWORD)) {
                int level = entity.getWeaponItem().getOrDefault(SPELL_LEVEL, 0);
                if (isResonatingWith(entity.getWeaponItem(), EnigmaticItems.BLAZING_CORE) && level > 4) {
                    if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onDeath(@NotNull LivingDeathEvent event) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
                ItemStack stack = attacker.getWeaponItem();
                LivingEntity entity = event.getEntity();
                if (event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK) && stack.is(EnigmaticItems.SPELLSTONE_SWORD)) {
                    int energy = stack.getOrDefault(INT, 0);
                    if (isResonatingWith(stack, EnigmaticItems.BLAZING_CORE) && energy > 0) {
                        stack.set(INT, Math.max(0, energy - 4));
                        if (attacker instanceof ServerPlayer serverPlayer)
                            PacketDistributor.sendToPlayer(serverPlayer, new SpellstoneSwordPacket(entity.position(), 21));
                        List<Mob> entities = attacker.level().getEntitiesOfClass(Mob.class, new AABB(entity.blockPosition()).inflate(2), LivingEntity::isAlive);
                        for (Mob mob : entities) {
                            if (mob == attacker) continue;
                            mob.hurt(EnigmaticDamageTypes.source(attacker.level(), DamageTypes.LAVA, entity), (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
                            Vec3 vec = entity.position().subtract(0.0, 0.5, 0.0).subtract(mob.position()).normalize();
                            mob.knockback(1.08F, vec.x, vec.z);
                            mob.igniteForSeconds(8);
                        }
                    } else if (isResonatingWith(stack, EnigmaticItems.ILLUSION_LANTERN)) {
                        stack.set(INT, getMaxEnergy(stack));
                    }
                }
            }
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        private static void onHand(RenderHandEvent event) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            ItemStack useItem = player.getUseItem();
            if (useItem.is(EnigmaticItems.SPELLSTONE_SWORD) && isResonatingWith(useItem, EnigmaticItems.ANGEL_BLESSING)) {
                int i = useItem.getOrDefault(INT, 0);
                if (i == 0 && event.getHand() != player.getUsedItemHand()) event.setCanceled(true);
            }
            ItemStack mainHandItem = player.getMainHandItem();
            if (event.getHand() == InteractionHand.OFF_HAND && mainHandItem.is(EnigmaticItems.SPELLSTONE_SWORD)) {
                if (isResonatingWith(mainHandItem, EnigmaticItems.ANGEL_BLESSING) && mainHandItem.getOrDefault(INT, 0) > 0)
                    event.setCanceled(true);
            }
        }
    }
}
