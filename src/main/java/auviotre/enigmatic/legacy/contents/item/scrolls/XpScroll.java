package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class XpScroll extends BaseCurioItem {
    public static ModConfigSpec.DoubleValue magneticRange;

    public XpScroll() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON).component(EnigmaticComponents.XP_SCROLL_STORED, 0L));
    }

    public XpScroll(Properties properties) {
        super(properties.component(EnigmaticComponents.XP_SCROLL_STORED, 0L));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.xp_scroll").push("else.xpScroll");
        magneticRange = builder.defineInRange("magneticRange", 16.0, 1.0, 256.0);
        builder.pop(2);
    }

    public static void trigger(Level level, ItemStack stack, Player player, InteractionHand hand, boolean swing) {
        RandomSource random = player.getRandom();
        if (!player.isCrouching()) {
            if (stack.getOrDefault(EnigmaticComponents.XP_SCROLL_MODE, false))
                stack.set(EnigmaticComponents.XP_SCROLL_MODE, false);
            else stack.set(EnigmaticComponents.XP_SCROLL_MODE, true);
            level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 0.8F + (random.nextFloat() * 0.2F));
        } else {
            if (stack.getOrDefault(EnigmaticComponents.XP_SCROLL_ACTIVE, false)) {
                stack.set(EnigmaticComponents.XP_SCROLL_ACTIVE, false);
                level.playSound(null, player.blockPosition(), EnigmaticSounds.CHARGED_OFF.get(), SoundSource.PLAYERS, 0.8F + (random.nextFloat() * 0.2F), 0.8F + (random.nextFloat() * 0.2F));
            } else {
                stack.set(EnigmaticComponents.XP_SCROLL_ACTIVE, true);
                level.playSound(null, player.blockPosition(), EnigmaticSounds.CHARGED_ON.get(), SoundSource.PLAYERS, 0.8F + (random.nextFloat() * 0.2F), 0.8F + (random.nextFloat() * 0.2F));
            }
        }
        if (swing) player.swing(hand);
    }

    protected static int getExpLevel(long experience) {
        int experienceLevel = 0;
        int neededForNext;
        while (true) {
            if (experienceLevel >= 30) neededForNext = 112 + (experienceLevel - 30) * 9;
            else neededForNext = experienceLevel >= 15 ? 37 + (experienceLevel - 15) * 5 : 7 + experienceLevel * 2;
            if (experience < neededForNext) return experienceLevel;
            experienceLevel++;
            experience -= neededForNext;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        MutableComponent modeComponent;
        if (!stack.getOrDefault(EnigmaticComponents.XP_SCROLL_ACTIVE, false))
            modeComponent = Component.translatable("tooltip.enigmaticlegacy.xpScrollDeactivated");
        else if (stack.getOrDefault(EnigmaticComponents.XP_SCROLL_MODE, false))
            modeComponent = Component.translatable("tooltip.enigmaticlegacy.xpScrollAbsorption");
        else modeComponent = Component.translatable("tooltip.enigmaticlegacy.xpScrollExtraction");

        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll7");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll8");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll9");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll10");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll11");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScroll12", ChatFormatting.GOLD, String.format("%.0f", magneticRange.getAsDouble()));
        } else TooltipHandler.holdShift(list);

        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScrollMode", modeComponent.withStyle(ChatFormatting.GOLD));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScrollStoredXP");
        Long stored = stack.getOrDefault(EnigmaticComponents.XP_SCROLL_STORED, 0L);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.xpScrollUnits", ChatFormatting.GOLD, stored, getExpLevel(stored));

        try {
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.currentKeybind", ChatFormatting.LIGHT_PURPLE, KeyMapping.createNameSupplier("key.scrollAbility").get().getString().toUpperCase());
        } catch (NullPointerException ignored) {
        }
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand handIn) {
        ItemStack stack = player.getItemInHand(handIn);
        XpScroll.trigger(world, stack, player, handIn, true);
        return InteractionResultHolder.success(stack);
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        if (!(context.entity() instanceof Player player) || context.entity().level().isClientSide() || !stack.getOrDefault(EnigmaticComponents.XP_SCROLL_ACTIVE, false))
            return;

        Level level = player.level();
        int forNextLevel = player.getXpNeededForNextLevel();
        if (stack.getOrDefault(EnigmaticComponents.XP_SCROLL_MODE, false)) {
            int take = forNextLevel / 3;
            if (player.experienceLevel == 0) take = 1;
            if (player.totalExperience >= take) {
                player.giveExperiencePoints(-take);
                long stored = stack.getOrDefault(EnigmaticComponents.XP_SCROLL_STORED, 0L);
                stored += take;
                stack.set(EnigmaticComponents.XP_SCROLL_STORED, stored);
            }
        } else {
            long stored = stack.getOrDefault(EnigmaticComponents.XP_SCROLL_STORED, 0L);
            int needed = forNextLevel / 3;
            if (stored >= needed) {
                stack.set(EnigmaticComponents.XP_SCROLL_STORED, stored - needed);
                player.giveExperiencePoints(needed);
            } else if (stored > 0) {
                int take = (int) Math.max(1, stored / 2);
                stack.set(EnigmaticComponents.XP_SCROLL_STORED, stored - take);
                player.giveExperiencePoints(take);
            }
        }
        double range = magneticRange.get();
        List<ExperienceOrb> orbs = level.getEntitiesOfClass(ExperienceOrb.class, player.getBoundingBox().inflate(range), Entity::isAlive);
        for (ExperienceOrb orb : orbs) {
            if (orb.distanceTo(player) > range) continue;
            player.takeXpDelay = 0;
            orb.playerTouch(player);
        }
    }

    public boolean isFoil(@NotNull ItemStack stack) {
        return stack.getOrDefault(EnigmaticComponents.XP_SCROLL_ACTIVE, false);
    }

    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return false;
    }
}
