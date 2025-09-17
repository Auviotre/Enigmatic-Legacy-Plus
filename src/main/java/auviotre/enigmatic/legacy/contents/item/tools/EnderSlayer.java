package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderSlayer extends SwordItem {
    public static final Tier TIER = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1876, 8.5F, 3.5F, 12, () -> Ingredient.of(Blocks.OBSIDIAN));

    public EnderSlayer() {
        super(TIER, new Item.Properties().fireResistant().rarity(Rarity.RARE).component(EnigmaticComponents.CURSED, true).attributes(createAttributes(TIER, 3.5F, -2.6F)));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer3", ChatFormatting.GOLD, "150%");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer7");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer8");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer9");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer10");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer11");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer12");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer13");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.enderSlayer2");
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && event.getSource().is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                if (attacker.getMainHandItem().is(EnigmaticItems.ENDER_SLAYER) && EnigmaticHandler.isTheCursedOne(attacker)) {
                    if (event.getEntity() instanceof ServerPlayer player) {
                        ItemCooldowns cooldowns = player.getCooldowns();
                        cooldowns.addCooldown(Items.ENDER_PEARL, 400);
                        cooldowns.addCooldown(EnigmaticItems.RECALL_POTION.get(), 400);
                        cooldowns.addCooldown(EnigmaticItems.TWISTED_MIRROR.get(), 400);
                        cooldowns.addCooldown(EnigmaticItems.EYE_OF_NEBULA.get(), 400);
                        //                    cooldowns.addCooldown(EnigmaticItems.THE_CUBE.get(), 400);
                    } else if (event.getEntity() instanceof EnderMan || event.getEntity() instanceof Shulker) {
                        event.getEntity().getPersistentData().putInt("EnderSlayerTeleportForbidden", 400);
                    }

                    if (event.getEntity().getType().is(EnigmaticTags.EntityTypes.END_DWELLERS)) {
                        if (attacker.level().dimension().equals(Level.END)) {
                            if (event.getEntity() instanceof EnderMan man && attacker instanceof Player player) {
                                float scale = player.getAttackStrengthScale(0.5F);
                                if (scale >= 0.99F) {
                                    event.setNewDamage((event.getNewDamage() + 100F) * 10F);
                                    man.getPersistentData().putBoolean("EnderSlayerVictim", true);
                                }
                            }
                            event.setNewDamage(event.getNewDamage() * 2.5F);
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onTick(EntityTickEvent.@NotNull Pre event) {
            Entity entity = event.getEntity();
            if (entity.getPersistentData().contains("EnderSlayerTeleportForbidden")) {
                int tick = entity.getPersistentData().getInt("EnderSlayerTeleportForbidden");
                if (tick > 1) entity.getPersistentData().putInt("EnderSlayerTeleportForbidden", tick - 1);
                else entity.getPersistentData().remove("EnderSlayerTeleportForbidden");
            }
        }

        @SubscribeEvent
        private static void onEnderTeleport(EntityTeleportEvent.@NotNull EnderEntity event) {
            if (event.getEntity().getPersistentData().contains("EnderSlayerTeleportForbidden")) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onLivingDropsLowest(@NotNull LivingDropsEvent event) {
            LivingEntity killed = event.getEntity();
            if (killed instanceof EnderMan && killed.getPersistentData().getBoolean("EnderSlayerVictim")) {
                for (ItemEntity entity : event.getDrops()) {
                    if (entity.getItem().is(Items.ENDER_PEARL)) {
                        dropXPOrb(killed.level(), killed.getX(), killed.getY(), killed.getZ(), 10);
                    } else if (entity.getItem().is(Items.ENDER_EYE)) {
                        dropXPOrb(killed.level(), killed.getX(), killed.getY(), killed.getZ(), 20);
                    } else {
                        dropXPOrb(killed.level(), killed.getX(), killed.getY(), killed.getZ(), 8);
                    }
                }
                event.getDrops().clear();
                event.setCanceled(true);
            }
        }

        private static void dropXPOrb(Level level, double x, double y, double z, int xp) {
            ExperienceOrb orb = new ExperienceOrb(level, x, y, z, xp);
            level.addFreshEntity(orb);
        }
    }
}