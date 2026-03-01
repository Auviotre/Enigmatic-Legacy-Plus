package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class ScorchedCharm extends CursedCurioItem {
    public static ModConfigSpec.IntValue lavaHealAmount;
    public static ModConfigSpec.IntValue lifestealModifier;
    public static ModConfigSpec.IntValue resistanceProbability;

    public ScorchedCharm() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.UNCOMMON), true);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.scorched_charm").push("blessItems.scorchedCharm");
        lavaHealAmount = builder.defineInRange("lavaHealAmount", 2, 0, 10);
        lifestealModifier = builder.defineInRange("lifestealModifier", 20, 0, 100);
        resistanceProbability = builder.defineInRange("resistanceProbability", 10, 0, 50);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.scorchedCharm1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.scorchedCharm2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.scorchedCharm3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.scorchedCharm4");
            LocalPlayer player = Minecraft.getInstance().player;
            if (EnigmaticHandler.isTheBlessedOne(player) && player != null) {
                int value = resistanceProbability.get() * (player.isInLava() ? 2 : 1);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.scorchedCharm5", ChatFormatting.GOLD, String.format("%d%%", value));
            }
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.isOnFire()) entity.clearFire();
        if (entity.isInLava()) {
            if (entity.tickCount % 20 == 0) entity.heal((float) lavaHealAmount.get());
            if (entity instanceof Player player && !player.isAffectedByFluids()) return;
            CollisionContext collisionContext = CollisionContext.of(entity);
            if (collisionContext.isAbove(LiquidBlock.STABLE_SHAPE, entity.blockPosition(), true) && !entity.level().getFluidState(entity.blockPosition().above()).is(FluidTags.LAVA)) {
                entity.setOnGround(true);
            } else {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, entity.isCrouching() ? -0.01 : 0.07, 0.0));
            }
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.SCORCHED_CHARM)) {
                if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
                    event.setCanceled(true);
                    return;
                }
                int percentage = resistanceProbability.get() * (entity.isInLava() ? 2 : 1);
                if (EnigmaticHandler.isTheBlessedOne(entity) && !event.getSource().is(Tags.DamageTypes.IS_TECHNICAL) && entity.getRandom().nextInt(100) < percentage) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        private static void onAttack(LivingDamageEvent.@NotNull Post event) {
            DamageSource source = event.getSource();
            if (source.getEntity() instanceof LivingEntity entity && EnigmaticHandler.hasCurio(entity, EnigmaticItems.SCORCHED_CHARM)) {
                if (event.getEntity().isOnFire()) {
                    entity.heal(event.getNewDamage() * lifestealModifier.get() / 100.0F);
                }
            }
        }

        @SubscribeEvent
        private static void onFall(@NotNull LivingFallEvent event) {
            LivingEntity entity = event.getEntity();
            if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.SCORCHED_CHARM) && entity.level().getFluidState(entity.getOnPos()).is(FluidTags.LAVA)) {
                event.setCanceled(true);
                if (entity.level() instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.LAVA, entity.getX(), entity.getY(), entity.getZ(), entity.getRandom().nextInt(1, 4), 0.1, 0.1, 0.1, 0);
                }
            }
        }
    }
}
