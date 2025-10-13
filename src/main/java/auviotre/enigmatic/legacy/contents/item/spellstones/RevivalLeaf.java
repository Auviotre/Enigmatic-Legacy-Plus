package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class RevivalLeaf extends SpellstoneItem {
    private static final List<TagKey<Block>> PLANT_SET = List.of(
            BlockTags.FLOWERS,
            BlockTags.SAPLINGS,
            BlockTags.REPLACEABLE_BY_TREES
    );
    public static ModConfigSpec.DoubleValue vulnerabilityModifier;
    public static ModConfigSpec.IntValue naturalRegenerationSpeed;

    public RevivalLeaf() {
        super(defaultSingleProperties().rarity(Rarity.RARE), 0xFF91D93F);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.revival_leaf").push("spellstone.revivalLeaf");
        naturalRegenerationSpeed = builder.defineInRange("naturalRegenerationSpeed", 5, 1, 40);
        vulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 2.0, 1.0, 20.0);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.revivalLeafSkill");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.revivalLeaf1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.revivalLeaf2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.revivalLeaf3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.revivalLeaf4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.revivalLeaf5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.revivalLeaf6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.revivalLeaf7");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    @OnlyIn(Dist.CLIENT)
    public void addTuneTooltip(List<Component> list) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.revivalLeaf1");
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (!entity.getActiveEffects().isEmpty()) {
            entity.getActiveEffects().removeIf(instance -> instance.is(EnigmaticEffects.POISON) || instance.is(MobEffects.HUNGER) || instance.is(MobEffects.POISON) || instance.is(MobEffects.WITHER));
            for (MobEffectInstance effect : entity.getActiveEffects()) {
                if (entity.tickCount % 4 == 0 && effect.duration > 0) effect.duration += 1;
            }
        }

        if (entity.tickCount % naturalRegenerationSpeed.get() == 0 && entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(Math.max(0.5F, entity.getMaxHealth() / 100.0F));
        }

        BlockPos pos = entity.blockPosition();
        if (entity.level() instanceof ServerLevel server) {
            RandomSource random = entity.getRandom();
            for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-3, -1, -3), pos.offset(3, 1, 3))) {
                BlockState state = server.getBlockState(blockPos);
                if (state.getBlock() instanceof CropBlock cropBlock) {
                    if (cropBlock.getMaxAge() > cropBlock.getAge(state) && random.nextInt(16) == 0) {
                        state.randomTick(server, blockPos, random);
                        Vec3 center = blockPos.getCenter();
                        if (random.nextInt(12) == 0)
                            server.sendParticles(ParticleTypes.HAPPY_VILLAGER, center.x, center.y, center.z, 1, 0.2, 0.2, 0.2, 0);
                    }
                } else if (state.getBlock() instanceof StemBlock) {
                    state.randomTick(server, blockPos, random);
                    Vec3 center = blockPos.getCenter();
                    if (random.nextInt(12) == 0)
                        server.sendParticles(ParticleTypes.HAPPY_VILLAGER, center.x, center.y, center.z, 1, 0.2, 0.2, 0.2, 0);
                }
            }
        }

        if (entity instanceof Player player && hasPlantBy(player)) {
            player.getAttributes().addTransientAttributeModifiers(this.getModifiers());
            if (player.getAbilities().flying) {
                if (player.tickCount % 12 == 0) {
                    BlockPos lazyPos = BlockPos.of(EnigmaticHandler.getPersistedData(player).getLong("RevivalFlightLazyPos"));
                    player.level().addParticle(ParticleTypes.HAPPY_VILLAGER, player.getRandomX(0.5), player.getY(), player.getRandomZ(0.5), 0, 0, 0);
                    int[] offset = {0, 1};
                    for (int x : offset)
                        for (int y : offset)
                            for (int z : offset) {
                                player.level().addParticle(ParticleTypes.HAPPY_VILLAGER, lazyPos.getX() + x, lazyPos.getY() + y, lazyPos.getZ() + z, 0, 0, 0);
                            }
                }
            }
        } else {
            entity.getAttributes().removeAttributeModifiers(this.getModifiers());
        }
    }

    public int getCooldown() {
        return 120;
    }

    public void triggerActiveAbility(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(this)) return;
        boolean paybackReceived = false;
        int expLevel = player.experienceLevel;
        if (player.totalExperience >= 40) {
            player.giveExperiencePoints(-40 + player.getRandom().nextInt(10));
            paybackReceived = true;
        }
        if (paybackReceived) {
            BlockPos pos = player.blockPosition();
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, (float) (0.8 + Math.random() * 0.2));
            List<LivingEntity> genericMobs = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(16));
            for (LivingEntity mob : genericMobs) {
                if (mob instanceof Targeting targeting && targeting.getTarget() == player) {
                    if (!mob.isInvertedHealAndHarm()) continue;
                }
                if (expLevel > 25) {
                    mob.heal(Math.min(0.2F, (expLevel - 25) * 0.01F) * mob.getMaxHealth());
                }
                mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100 + expLevel * 2, 1, false, true));
            }
            Iterable<BlockPos> iterable = BlockPos.betweenClosed(pos.offset(5, 5, 5), pos.offset(-5, -5, -5));
            for (BlockPos blockPos : iterable) {
                if (level.getBlockState(blockPos).is(Blocks.WITHER_ROSE)) {
                    level.destroyBlock(blockPos, false);
                    level.setBlock(blockPos, Blocks.POPPY.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
            super.triggerActiveAbility(level, player, stack);
        }
    }

    private boolean hasPlantBy(Player player) {
        BlockPos blockPos = player.blockPosition();
        CompoundTag data = EnigmaticHandler.getPersistedData(player);
        double reach = player.getAttributes().getValue(Attributes.BLOCK_INTERACTION_RANGE);
        int range = (int) Math.pow(reach + 1, 2) + 1;
        Iterable<BlockPos> posSet = BlockPos.betweenClosed(blockPos.offset(-range, -range, -range), blockPos.offset(range, range, range));
        if (player.getPersistentData().contains("RevivalFlightLazyPos")) {
            BlockPos lazyPos = BlockPos.of(data.getLong("RevivalFlightLazyPos"));
            BlockState blockState = player.level().getBlockState(lazyPos);
            if (PLANT_SET.stream().anyMatch(blockState::is)) {
                if (lazyPos.distToCenterSqr(player.position()) < range) return true;
            }
        }
        for (BlockPos pos : posSet) {
            BlockState blockState = player.level().getBlockState(pos);
            if (PLANT_SET.stream().anyMatch(blockState::is)) {
                if (pos.distToCenterSqr(player.position()) < range) {
                    player.getPersistentData().putLong("RevivalFlightLazyPos", pos.asLong());
                    return true;
                }
            }
        }
        return false;
    }

    protected Multimap<Holder<Attribute>, AttributeModifier> getModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        map.put(NeoForgeMod.CREATIVE_FLIGHT, new AttributeModifier(getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        return map;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            DamageSource source = event.getSource();
            if (!ISpellstone.get(entity).is(EnigmaticItems.REVIVAL_LEAF) || event.isCanceled()) return;
            if (source.is(DamageTypes.WITHER)) event.setCanceled(true);
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            if (ISpellstone.get(victim).is(EnigmaticItems.REVIVAL_LEAF)) {
                if (event.getSource().is(DamageTypeTags.IS_FIRE))
                    event.setNewDamage(event.getNewDamage() * (float) vulnerabilityModifier.getAsDouble());
                if (event.getSource().is(DamageTypes.MOB_PROJECTILE))
                    event.setNewDamage(event.getNewDamage() * (float) vulnerabilityModifier.getAsDouble() * 0.75F);
            }
        }

        @SubscribeEvent
        private static void onApplyPotion(MobEffectEvent.@NotNull Applicable event) {
            MobEffectInstance instance = event.getEffectInstance();
            if (instance == null) return;
            if (instance.getEffect().is(EnigmaticTags.Effects.ALWAYS_APPLY)) return;
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.REVIVAL_LEAF)) {
                if (instance.is(EnigmaticEffects.POISON) || instance.is(MobEffects.HUNGER) || instance.is(MobEffects.POISON) || instance.is(MobEffects.WITHER))
                    event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            LivingEntity victim = event.getEntity();
            Entity entity = event.getSource().getEntity();
            if (entity instanceof LivingEntity attacker && ISpellstone.get(attacker).is(EnigmaticItems.REVIVAL_LEAF)) {
                victim.addEffect(new MobEffectInstance(EnigmaticEffects.POISON, 200, 1), attacker);
            }
        }
    }
}
