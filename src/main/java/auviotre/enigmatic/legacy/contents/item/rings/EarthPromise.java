package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class EarthPromise extends CursedCurioItem {
    public static ModConfigSpec.IntValue cooldown;
    public static ModConfigSpec.IntValue firstCurseModifier;
    public static ModConfigSpec.IntValue healthThreshold;

    public EarthPromise() {
        super(defaultSingleProperties().rarity(Rarity.RARE), true);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.earth_promise").push("blessItems.earthPromise");
        cooldown = builder.defineInRange("cooldown", 1000, 200, 2000);
        firstCurseModifier = builder.defineInRange("firstCurseModifier", 25, 0, 100);
        healthThreshold = builder.defineInRange("healthThreshold", 80, 0, 100);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            if (EnigmaticHandler.isTheCursedOne(Minecraft.getInstance().player)) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.curseAlteration", ChatFormatting.GOLD, Component.translatable("tooltip.enigmaticlegacy.firstCurse"));
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.firstCurseAlteration", ChatFormatting.GOLD, firstCurseModifier.get() + "%");
                TooltipHandler.line(list);
            }
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.earthPromise1", ChatFormatting.GOLD, healthThreshold.get() + "%");
            int cool = EnigmaticHandler.isTheBlessedOne(Minecraft.getInstance().player) ? cooldown.get() / 25 : cooldown.get() / 20;
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.earthPromise2", ChatFormatting.GOLD, cool);

        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.ARMOR, new AttributeModifier(getLocation(this), 5, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(getLocation(this), 2, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.MINING_EFFICIENCY, new AttributeModifier(getLocation(this), 2, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack curio) {
        LivingEntity entity = slotContext.entity();
        return super.getFortuneLevel(slotContext, lootContext, curio) + (EnigmaticHandler.hasCurio(entity, EnigmaticItems.MINING_CHARM) ? 3 : 2);
    }

    @OnlyIn(Dist.CLIENT)
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        List<Component> list = super.getAttributesTooltip(tooltips, context, stack);
        if (!list.isEmpty()) list.add(Component.translatable("attribute.modifier.plus.0", EnigmaticHandler.hasCurio(Minecraft.getInstance().player, EnigmaticItems.MINING_CHARM) ? 3 : 2, Component.translatable("attribute.name.fortune_level")).withStyle(ChatFormatting.BLUE));
        return list;
    }


    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamage(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity victim = event.getEntity();
            if (victim instanceof Player player && !player.getCooldowns().isOnCooldown(EnigmaticItems.EARTH_PROMISE.get())) {
                if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.EARTH_PROMISE)) {
                    float damage = event.getAmount();
                    if (player.isAlive() && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && damage >= player.getHealth() * healthThreshold.get() * 0.01F) {
                        int tick = EnigmaticHandler.isTheBlessedOne(player) ? cooldown.get() * 4 / 5 : cooldown.get();
                        player.getCooldowns().addCooldown(EnigmaticItems.EARTH_PROMISE.get(), tick);
                        if (player.level() instanceof ServerLevel level) {
                            level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY(), player.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
                            level.sendParticles((ParticleOptions) EnigmaticParticles.ICHOR.get(), player.getX(), player.getY(0.5F), player.getZ(), 36, 0.1D, 0.1D, 0.1D, 0.2D);
                            player.level().playSound(null, player, SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 5.0F, 1.5F);
                        }
                        player.addEffect(new MobEffectInstance(EnigmaticEffects.PURE_RESISTANCE, 100, 4));
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
