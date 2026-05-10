package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseElytraItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import auviotre.enigmatic.legacy.registries.EnigmaticEnchantments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class MajesticElytra extends BaseElytraItem {
    public static ModConfigSpec.IntValue resistanceModifier;

    public MajesticElytra() {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.RARE).durability(3476));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.majestic_elytra").push("else.majesticElytra");
        resistanceModifier = builder.defineInRange("resistanceModifier", 75, 0, 95);
        builder.pop(2);
    }


    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.majesticElytra1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.majesticElytra2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.majesticElytra3");
        if (Minecraft.getInstance().level != null) {
            var holder = EnigmaticHandler.get(Minecraft.getInstance().level, Registries.ENCHANTMENT, EnigmaticEnchantments.ETHERIC_RESONANCE);
            if (stack.getEnchantmentLevel(holder) > 0)
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.majesticElytraBuff", ChatFormatting.GOLD, String.format("%d%%", resistanceModifier.get()));
        }
        if (stack.isEnchanted()) TooltipHandler.line(list);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(EnigmaticAttributes.ETHERIUM_SHIELD, new AttributeModifier(IItemHelper.getLocation(this), 0.04, AttributeModifier.Operation.ADD_VALUE));
        return attributes;
    }

    public boolean flyingBoost(@NotNull Player player) {
        if (player.isFallFlying()) {
            Vec3 lookAngle = player.getLookAngle().scale(0.85F);
            Vec3 movement = player.getDeltaMovement().scale(0.5F);
            player.setDeltaMovement(movement.add(lookAngle));
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void addParticle(Player player) {
        int amount = 3;
        double rangeModifier = 0.1;
        for (int counter = 0; counter <= amount; counter++) {
            Vec3 pos = player.position();
            pos = pos.add(Math.random() - 0.5, -1.0 + Math.random() - 0.5, Math.random() - 0.5);
            player.level().addParticle(ParticleTypes.DRAGON_BREATH, true, pos.x, pos.y, pos.z, ((Math.random() - 0.5D) * 2.0D) * rangeModifier, ((Math.random() - 0.5D) * 2.0D) * rangeModifier, ((Math.random() - 0.5D) * 2.0D) * rangeModifier);
        }
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 24;
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        if (context.entity() instanceof Player player && player.level().isClientSide()) handleBoosting(player);
        LivingEntity livingEntity = context.entity();
        int ticks = livingEntity.getFallFlyingTicks();
        if (ticks > 0 && livingEntity.isFallFlying()) stack.elytraFlightTick(livingEntity, ticks);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            if (livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(this)) {
                if (livingEntity instanceof Player player && player.level().isClientSide()) handleBoosting(player);
                int ticks = livingEntity.getFallFlyingTicks();
                if (ticks > 0 && livingEntity.isFallFlying()) stack.elytraFlightTick(livingEntity, ticks);
            }
        }
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return this.swapWithEquipmentSlot(this, level, player, hand);
    }

    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(EnigmaticItems.ETHERIUM_INGOT);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onHurt(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity entity = event.getEntity();
            if (!getElytra(entity).is(EnigmaticItems.MAJESTIC_ELYTRA)) return;
            DamageSource source = event.getSource();
            float modifier = 1.0F - 0.01F * resistanceModifier.get();
            if (!source.is(Tags.DamageTypes.IS_TECHNICAL) && entity.isFallFlying())
                event.setNewDamage(event.getNewDamage() * modifier);
        }
    }
}
