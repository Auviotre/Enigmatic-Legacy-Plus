package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.entity.PiglinWanderer;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

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
            PiglinWanderer.class, Items.PIGLIN_HEAD,
            WitherSkeleton.class, Items.WITHER_SKELETON_SKULL,
            EnderDragon.class, Items.DRAGON_HEAD
    );
    public static ModConfigSpec.IntValue beheadingBase;
    public static ModConfigSpec.IntValue beheadingBonus;

    public ExecutionAxe() {
        super(Tiers.NETHERITE, new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON).attributes(createAttributes(Tiers.NETHERITE, 5.0F, -2.4F)));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.execution_axe").push("else.executionAxe");
        beheadingBase = builder.defineInRange("beheadingBase", 10, 0, 50);
        beheadingBonus = builder.defineInRange("beheadingBonus", 5, 0, 20);
        builder.pop(2);
    }

    public static int getLootingLevel(LivingEntity entity, Level level) {
        int loot = 0;
        Holder.Reference<Enchantment> holder = EnigmaticHandler.get(level, Registries.ENCHANTMENT, Enchantments.LOOTING);
        loot += EnchantmentHelper.getEnchantmentLevel(holder, entity);
        loot += CuriosApi.getCuriosInventory(entity).map(handler -> handler.getLootingLevel(null)).orElse(0);
        return loot;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.executionAxe1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.executionAxe2", ChatFormatting.GOLD, beheadingBonus.get() + "%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.executionAxe3");
        } else {
            TooltipHandler.holdShift(list);
            TooltipHandler.line(list);
        }

        int looting = beheadingBase.get();
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            looting += beheadingBonus.get() * getLootingLevel(Minecraft.getInstance().player, Minecraft.getInstance().level);
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
                int looting = getLootingLevel(attacker, attacker.level());
                if (item.is(EnigmaticItems.EXECUTION_AXE) && attacker.getRandom().nextInt(100) < beheadingBase.get() + looting * beheadingBonus.get() && drops.stream().noneMatch(entity -> entity.getItem().is(skull))) {
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
