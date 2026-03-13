package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.entity.projectile.SoulFlameBall;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticDamageTypes;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.function.Predicate;

public class IllusionLantern extends SpellstoneItem {
    public static ModConfigSpec.IntValue bypassDamageResistance;
    public static ModConfigSpec.DoubleValue ILVulnerabilityModifier;
    public static ModConfigSpec.IntValue cooldown;

    public IllusionLantern() {
        super(defaultSingleProperties().rarity(Rarity.RARE).component(EnigmaticComponents.ILLUSION_COUNT, 0), 0xE86DD5DE);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.illusion_lantern").push("spellstone.illusionLantern");
        ILVulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 1.25, 1.0, 20.0);
        bypassDamageResistance = builder.defineInRange("resistanceModifier", 50, 0, 80);
        cooldown = builder.defineInRange("cooldown", 60, 20, 200);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkillAbsent");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.illusionLantern1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.illusionLantern2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.illusionLantern3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.illusionLantern4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.illusionLantern5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.illusionLantern6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.illusionLantern7");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    @OnlyIn(Dist.CLIENT)
    public void addTuneTooltip(List<Component> list) {
//        TooltipHandler.line(list, "tooltip.enigmaticlegacy.golemHeart6", ChatFormatting.GOLD, String.format("%.0f%%", resistance));
    }

    public int getCooldown() {
        return 0;
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof ServerPlayer entity) {
            int count = stack.getOrDefault(EnigmaticComponents.ILLUSION_COUNT, 0);
            if (count > 0 && checkAttack(entity, entity.level())) count--;
            if (!entity.getCooldowns().isOnCooldown(this)) {
                if (count < 5) {
                    count++;
                    entity.getCooldowns().addCooldown(this, cooldown.get());
                }
            }
            stack.set(EnigmaticComponents.ILLUSION_COUNT, Math.clamp(count, 0, 5));
            if (entity.tickCount % 32 == 0 && !entity.hasInfiniteMaterials()) {
                Level level = entity.level();
                BlockPos blockPos = entity.blockPosition();
                boolean seeSky = level.canSeeSkyFromBelowWater(blockPos);
                int rawBrightness = level.getLightEngine().getRawBrightness(blockPos, 15);
                if (seeSky && rawBrightness > 12) {
                    entity.hurt(EnigmaticDamageTypes.source(level, EnigmaticDamageTypes.EVIL_CURSE, entity), entity.getMaxHealth() / 8.0F);
                }
                if (entity.getRandom().nextBoolean()) return;
                Iterable<BlockPos> posSet = BlockPos.betweenClosed(blockPos.offset(-5, -5, -5), blockPos.offset(5, 5, 5));
                for (BlockPos pos : posSet) {
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.is(EnigmaticTags.Blocks.PLANTS) && entity.getRandom().nextInt(3) == 0 && pos.getBottomCenter().distanceTo(entity.position()) < 5.0F) {
                        level.destroyBlock(pos, false, entity);
                    }
                }
            }
        }
    }

    private boolean checkAttack(LivingEntity owner, Level level) {
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(16.0), entity -> entity.isAlive() && entity instanceof Targeting);
        for (LivingEntity entity : entities) {
            LivingEntity target = ((Targeting) entity).getTarget();
            if (target != null && target.is(owner)) {
                SoulFlameBall flameBall = new SoulFlameBall(level, owner, entity);
                double angle = owner.getRandom().nextFloat() * Math.PI * 2;
                flameBall.setDeltaMovement(new Vec3(Math.sin(angle), 0.1 + owner.getRandom().nextFloat() * 0.3, Math.cos(angle)).scale(1.2));
                level.addFreshEntity(flameBall);
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Decorator implements IItemDecorator {
        public boolean render(GuiGraphics graphics, Font font, @NotNull ItemStack stack, int x, int y) {
            int count = stack.getOrDefault(EnigmaticComponents.ILLUSION_COUNT, 0);
            if (stack.getCount() == 1 && count > 0) {
                graphics.pose().pushPose();
                String s = String.valueOf(count);
                graphics.pose().translate(0.0F, 0.0F, 200.0F);
                graphics.drawString(font, s, x + 19 - 2 - font.width(s), y + 6 + 3, Mth.hsvToRgb(0.51F, 1.0F, 0.8F + count * 0.02F), true);
                graphics.pose().popPose();
                return true;
            }
            return false;
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onLivingChangeTarget(@NotNull LivingChangeTargetEvent event) {
            LivingEntity entity = event.getEntity();
            LivingEntity target = event.getNewAboutToBeSetTarget();
            if (entity instanceof Targeting targetedEntity && ISpellstone.get(target).is(EnigmaticItems.ILLUSION_LANTERN)) {
                if (entity.getLastHurtByMob() != target && (targetedEntity.getTarget() == null || !targetedEntity.getTarget().isAlive())) {
                    if (entity.getType().is(EntityTypeTags.WITHER_FRIENDS)) {
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            DamageSource source = event.getSource();
            if (source.is(Tags.DamageTypes.IS_TECHNICAL)) return;
            if (ISpellstone.get(entity).is(EnigmaticItems.ILLUSION_LANTERN)) {
                if (!source.is(Tags.DamageTypes.IS_MAGIC))
                    event.setAmount((float) (event.getAmount() * ILVulnerabilityModifier.get()));
                if (source.is(EnigmaticTags.DamageTypes.ILLUSION_LANTERN_RESISTANT_TO)) {
                    event.setAmount(event.getAmount() * (1 - bypassDamageResistance.get() * 0.01F));
                }
            }
            if (source.getDirectEntity() instanceof SoulFlameBall) {
                event.setInvulnerabilityTicks(0);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity entity = event.getEntity();
            if (event.getSource().is(EnigmaticTags.DamageTypes.ILLUSION_LANTERN_RESISTANT_TO)) return;
            if (ISpellstone.get(entity).is(EnigmaticItems.ILLUSION_LANTERN)) {
                Predicate<LivingEntity> noSharing = living -> {
                    if (living instanceof OwnableEntity own && own.getOwner() == entity) return false;
                    else if (living.equals(event.getSource().getEntity())) return false;
                    else if (living.equals(entity) || entity.isAlliedTo(living)) return false;
                    return living.isAlive() && !ISpellstone.get(living).is(EnigmaticItems.ILLUSION_LANTERN);
                };
                List<LivingEntity> entities = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(8), noSharing);
                if (entities.isEmpty()) return;
                int size = entities.size();
                float baseDamage = event.getNewDamage() / (size + 1) / (size + 1);
                event.setNewDamage(baseDamage * (2 * size + 1));
                for (LivingEntity livingEntity : entities) {
                    livingEntity.hurt(EnigmaticDamageTypes.source(entity.level(), DamageTypes.MAGIC, entity), baseDamage * size);
                }
            }
        }
    }
}
