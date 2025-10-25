package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class NightScroll extends CursedCurioItem {
    public static ModConfigSpec.IntValue effectiveRange;
    public static ModConfigSpec.IntValue damageBoost;
    public static ModConfigSpec.IntValue damageResistance;
    public static ModConfigSpec.IntValue lifeSteal;
    public NightScroll() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
    }
    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.night_scroll").push("cursedItems.nightScroll");
        effectiveRange = builder.defineInRange("effectiveRange", 16, 1, 64);
        damageBoost = builder.defineInRange("attackDamage", 20, 0, 100);
        damageResistance = builder.defineInRange("damageResistance", 16, 0, 100);
        lifeSteal = builder.defineInRange("lifeSteal", 8, 0, 100);
        builder.pop(2);
    }

    public static float getDarkModifier(LivingEntity entity) {
        if (entity == null || !EnigmaticHandler.canUse(entity, EnigmaticItems.NIGHT_SCROLL.toStack())) return 0.5F;
        if (entity.hasEffect(MobEffects.DARKNESS)) return 2.0F;
        BlockPos blockPos = entity.blockPosition().above();
        int sky = entity.level().getBrightness(LightLayer.SKY, blockPos);
        int block = entity.level().getBrightness(LightLayer.BLOCK, blockPos);
        if (sky >= block) return 2.0F - sky * 0.1F;
        return 2.0F - (sky * 0.04F + block * 0.06F);
    }
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.nightScroll1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.nightScroll2");
        } else TooltipHandler.holdShift(list);
        TooltipHandler.line(list);
        list.add(Component.translatable("curios.modifiers.scroll").withStyle(ChatFormatting.GOLD));
        float darkModifier = getDarkModifier(Minecraft.getInstance().player);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.nightScroll3", ChatFormatting.GOLD, String.format("%.01f%%", darkModifier * damageBoost.get()));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.nightScroll4", ChatFormatting.GOLD, String.format("%.01f%%", darkModifier * damageResistance.get()));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.nightScroll5", ChatFormatting.GOLD, String.format("%.01f%%", darkModifier * lifeSteal.get()));
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(getLocation(this), damageBoost.get() / 100.0F * getDarkModifier(slotContext.entity()), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return attributes;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        public static final Map<LivingEntity, AABB> BOXES = new WeakHashMap<>();

        @SubscribeEvent
        private static void onTick(EntityTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof LivingEntity entity) {
                if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.NIGHT_SCROLL))
                    BOXES.put(entity, entity.getBoundingBox().inflate(effectiveRange.get()));
                else BOXES.remove(entity);
            }
        }

        @SubscribeEvent
        private static void onEntitySpawn(@NotNull FinalizeSpawnEvent event) {
            if (event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION) {
                LivingEntity entity = event.getEntity();
                if (entity instanceof Phantom) {
                    if (BOXES.values().stream().anyMatch(entity.getBoundingBox()::intersects)) {
                        event.setSpawnCancelled(true);
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (EnigmaticHandler.hasCurio(event.getEntity(), EnigmaticItems.NIGHT_SCROLL)) {
                event.setNewDamage(event.getNewDamage() * (1 - 0.01F * damageResistance.get() * getDarkModifier(event.getEntity())));
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            DamageSource source = event.getSource();
            if (source.getDirectEntity() instanceof LivingEntity attacker) {
                if (EnigmaticHandler.hasCurio(attacker, EnigmaticItems.NIGHT_SCROLL)) {
                    attacker.heal(event.getNewDamage() * 0.01F * lifeSteal.get() * getDarkModifier(attacker));
                }
            }
        }
    }
}
