package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class RedemptionRing extends BaseCurioItem {
    public static ModConfigSpec.IntValue damageMultiplier;
    public static ModConfigSpec.IntValue attackMultiplier;
    public static ModConfigSpec.IntValue regenerationSpeed;
    public static ModConfigSpec.IntValue lootingBonus;
    public static ModConfigSpec.IntValue fortuneBonus;

    public RedemptionRing() {
        super(defaultSingleProperties().rarity(Rarity.EPIC)
                .component(EnigmaticComponents.BLESSED, true)
                .component(EnigmaticComponents.REDEMPTION_LEVEL, 0)
        );
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.redemption_ring").push("blessItems.redemptionRing");
        damageMultiplier = builder.defineInRange("damageMultiplier", 25, 0, 100);
        attackMultiplier = builder.defineInRange("attackMultiplier", 20, 0, 100);
        regenerationSpeed = builder.defineInRange("regenerationSpeed", 20, 5, 100);
        lootingBonus = builder.defineInRange("lootingBonusAlt", 1, 0, 8);
        fortuneBonus = builder.defineInRange("fortuneBonusAlt", 1, 0, 8);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            int level = Helper.getLevel(stack);
            RandomSource random = RandomSource.create();
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRing1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRing2");
            if (level > 0)
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRingBless1", ChatFormatting.GOLD, Helper.getDamageResistance(stack) + "%");
            else
                TooltipHandler.obscure(list, "tooltip.enigmaticlegacy.redemptionRingBless1", random, ChatFormatting.GOLD, "100%");
            if (level > 1) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRingBless2", ChatFormatting.GOLD, Helper.getDamageBoost(stack) + "%");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRingBless3");
            } else {
                TooltipHandler.obscure(list, "tooltip.enigmaticlegacy.redemptionRingBless2", random, ChatFormatting.GOLD, "0%");
                TooltipHandler.obscure(list, "tooltip.enigmaticlegacy.redemptionRingBless3", random);
            }
            if (level > 2) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRingBless4", ChatFormatting.GOLD, Helper.getBonus(stack, lootingBonus.get()));
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRingBless5", ChatFormatting.GOLD, Helper.getBonus(stack, fortuneBonus.get()));
            } else {
                TooltipHandler.obscure(list, "tooltip.enigmaticlegacy.redemptionRingBless4", random, ChatFormatting.GOLD, 0);
                TooltipHandler.obscure(list, "tooltip.enigmaticlegacy.redemptionRingBless5", random, ChatFormatting.GOLD, 0);
            }
            if (level > 3) TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRingBless6");
            else TooltipHandler.obscure(list, "tooltip.enigmaticlegacy.redemptionRingBless6", random);
            if (level > 4) TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRingBless7");
            else TooltipHandler.obscure(list, "tooltip.enigmaticlegacy.redemptionRingBless7", random);
            if (flag.isAdvanced()) {
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessLevel", ChatFormatting.DARK_GRAY, level);
            }
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRingLore1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRingLore2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.redemptionRingLore3");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eternallyBound1");
            if (EnigmaticHandler.canUnequipBoundRelics(Minecraft.getInstance().player)) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.eternallyBound2_creative");
            } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.eternallyBound2");
            TooltipHandler.line(list);
            TooltipHandler.holdShift(list);
        }
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (Helper.getLevel(stack) > 1 && entity.tickCount % regenerationSpeed.get() == 0 && entity.getHealth() < entity.getMaxHealth() * 0.9F) {
            float delta = entity.getMaxHealth() * 0.9F - entity.getHealth();
            entity.heal(Math.max(delta / 20.0F * Helper.getRegenerationModifier(stack), 0.5F));
        }
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return !EnigmaticHandler.isTheCursedOne(context.entity()) && super.canEquip(context, stack);
    }

    public boolean canUnequip(@NotNull SlotContext context, ItemStack stack) {
        if (context.entity() instanceof Player player && EnigmaticHandler.canUnequipBoundRelics(player))
            return super.canUnequip(context, stack);
        return false;
    }

    public int getLootingLevel(SlotContext context, @Nullable LootContext lootContext, ItemStack stack) {
        return super.getLootingLevel(context, lootContext, stack) + Helper.getBonus(stack, lootingBonus.get());
    }

    public int getFortuneLevel(SlotContext context, LootContext lootContext, ItemStack stack) {
        return super.getFortuneLevel(context, lootContext, stack) + Helper.getBonus(stack, fortuneBonus.get());
    }

    public interface Helper {
        float[] BLESS_PROGRESSES = {0.0F, 10.0F, 25.0F, 45.0F, 75.0F, 99.0F, 200F};
        long[] BLESS_DURATION = {5, 10, 20, 40, 60, 90, 600};

        static int getLevel(ItemStack stack) {
            return Math.clamp(stack.getOrDefault(EnigmaticComponents.REDEMPTION_LEVEL, 0), 0, 6);
        }

        static int getLevel(LivingEntity entity) {
            if (!EnigmaticHandler.isTheBlessedOne(entity)) return 0;
            ItemStack curio = EnigmaticHandler.getCurio(entity, EnigmaticItems.REDEMPTION_RING);
            return curio.isEmpty() ? 0 : getLevel(curio);
        }

        static boolean canUseRelic(LivingEntity entity) {
            return getLevel(entity) > 4;
        }

        static int getPossibleLevel(LivingEntity entity) {
            if (entity == null) return 0;
            double suffering = EnigmaticHandler.getSufferingFraction(entity) * 100;
            EnigmaticData data = entity.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            long time = data.getTimeWithCurses() / 1200L;
            int level = 0;
            while (suffering >= BLESS_PROGRESSES[level] && time >= BLESS_DURATION[level]) level++;
            return level;
        }

        static int getDamageResistance(ItemStack stack) {
            return 100 - damageMultiplier.get() * Math.clamp(getLevel(stack), 0, 5) / 5;
        }

        static int getDamageBoost(ItemStack stack) {
            return attackMultiplier.get() * (Math.clamp(getLevel(stack), 1, 5) - 1) / 4;
        }

        static float getRegenerationModifier(ItemStack stack) {
            int level = getLevel(stack);
            if (level > 1) return Math.min(0.25F * (level - 1), 1.25F);
            return 0;
        }

        static int getBonus(ItemStack stack, int base) {
            int level = getLevel(stack);
            if (level > 4) return base;
            if (level > 2) return (base + 1) / 2;
            return 0;
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onTick(EntityTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof Player player) {
                CompoundTag data = EnigmaticHandler.getPersistedData(player);
                data.putBoolean("RedemptionBearing", EnigmaticHandler.hasCurio(player, EnigmaticItems.REDEMPTION_RING));
            }
        }

        @SubscribeEvent
        private static void onKnockback(@NotNull LivingKnockBackEvent event) {
            if (EnigmaticHandler.isTheBlessedOne(event.getEntity()) && Helper.getLevel(event.getEntity()) > 0) {
                event.setStrength(event.getStrength() * 0.95F);
            }
        }


        @SubscribeEvent
        private static void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
            if (event.getAmount() >= Float.MAX_VALUE) return;
            LivingEntity victim = event.getEntity();
            if (EnigmaticHandler.isTheBlessedOne(victim) && Helper.getLevel(victim) > 0) {
                ItemStack curio = EnigmaticHandler.getCurio(victim, EnigmaticItems.REDEMPTION_RING);
                float multiplier = 0.01F * (curio.isEmpty() ? 100 : Helper.getDamageResistance(curio));
                event.setAmount(event.getAmount() * multiplier);
            }

            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (EnigmaticHandler.isTheBlessedOne(attacker) && Helper.getLevel(attacker) > 1) {
                    ItemStack curio = EnigmaticHandler.getCurio(attacker, EnigmaticItems.REDEMPTION_RING);
                    float multiplier = 1 + 0.01F * (curio.isEmpty() ? 0 : Helper.getDamageBoost(curio));
                    event.setAmount(event.getAmount() * multiplier);
                }
            }
        }
    }
}
