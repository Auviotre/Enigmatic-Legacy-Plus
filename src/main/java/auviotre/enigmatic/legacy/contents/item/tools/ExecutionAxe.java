package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ExecutionAxe extends SwordItem {
    public static final Map<Class<? extends LivingEntity>, ItemLike> SKULL_MAP = Map.of(
            Zombie.class, Items.ZOMBIE_HEAD,
            Skeleton.class, Items.SKELETON_SKULL,
            Creeper.class, Items.CREEPER_HEAD,
            Piglin.class, Items.PIGLIN_HEAD,
            PiglinBrute.class, Items.PIGLIN_HEAD,
            WitherSkeleton.class, Items.WITHER_SKELETON_SKULL,
            EnderDragon.class, Items.DRAGON_HEAD
    );

    public ExecutionAxe() {
        super(Tiers.NETHERITE, new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON).attributes(createAttributes(Tiers.NETHERITE, 5.0F, -2.4F)));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.executionAxe1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.executionAxe2", ChatFormatting.GOLD, "5%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.executionAxe3");
        } else {
            TooltipHandler.holdShift(list);
            TooltipHandler.line(list);
        }

        int looting = 10;
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            Holder.Reference<Enchantment> holder = EnigmaticHandler.get(Minecraft.getInstance().level, Registries.ENCHANTMENT, Enchantments.LOOTING);
            looting += 5 * EnchantmentHelper.getEnchantmentLevel(holder, Minecraft.getInstance().player);
        }
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.executionAxeBeheadingChance", ChatFormatting.GOLD, looting + "%");
        TooltipHandler.line(list);
    }

    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return false;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDrop(@NotNull LivingDropsEvent event) {
            LivingEntity victim = event.getEntity();
            if (SKULL_MAP.containsKey(victim.getClass()) && event.isRecentlyHit() && event.getSource().getEntity() != null && event.getSource().getEntity() instanceof LivingEntity attacker) {
                ItemStack item = attacker.getMainHandItem();
                Item skull = SKULL_MAP.get(victim.getClass()).asItem();
                Collection<ItemEntity> drops = event.getDrops();
                int looting = EnchantmentHelper.getEnchantmentLevel(EnigmaticHandler.get(attacker.level(), Registries.ENCHANTMENT, Enchantments.LOOTING), attacker);
                if (item.is(EnigmaticItems.EXECUTION_AXE) && attacker.getRandom().nextInt(100) < 10 + looting * 5 && drops.stream().noneMatch(entity -> entity.getItem().is(skull))) {
                    ItemEntity itemEntity = new ItemEntity(victim.level(), victim.getX(), victim.getY(), victim.getZ(), skull.getDefaultInstance());
                    itemEntity.setDefaultPickUpDelay();
                    drops.add(itemEntity);

                    if (event.getSource().getEntity() instanceof ServerPlayer player) {
                        //                    BeheadingTrigger.INSTANCE.trigger((ServerPlayer) event.getSource().getEntity());
                    }
                }
            }
        }
    }
}
