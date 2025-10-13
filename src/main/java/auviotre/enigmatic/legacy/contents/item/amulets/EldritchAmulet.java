package auviotre.enigmatic.legacy.contents.item.amulets;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EldritchAmulet extends BaseCurioItem {
    public static ModConfigSpec.IntValue attackDamage;
    public static ModConfigSpec.IntValue lifeSteal;

    public EldritchAmulet() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.EPIC).component(EnigmaticComponents.ELDRITCH, true));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.eldritch_amulet").push("abyssItems.eldritchAmulet");
        attackDamage = builder.defineInRange("attackDamage", 20, 0, 100);
        lifeSteal = builder.defineInRange("lifeSteal", 15, 0, 100);
        builder.pop(2);
    }


    private static Map<String, NonNullList<ItemStack>> inventoryMap(Player player) {
        Map<String, NonNullList<ItemStack>> inventories = new HashMap<>();
        inventories.put("Armor", player.getInventory().armor);
        inventories.put("Main", player.getInventory().items);
        inventories.put("Offhand", player.getInventory().offhand);
        return inventories;
    }

    public static void storeInventory(ServerPlayer player) {
        Map<String, NonNullList<ItemStack>> inventories = inventoryMap(player);
        CompoundTag tag = new CompoundTag();
        Holder<Enchantment> holder = player.registryAccess().holderOrThrow(Enchantments.VANISHING_CURSE);
        inventories.forEach((key, value) -> {
            ListTag list = new ListTag();
            for (int i = 0; i < value.size(); i++) {
                ItemStack stack = value.get(i);
                if (EnchantmentHelper.getTagEnchantmentLevel(holder, stack) > 0) stack = ItemStack.EMPTY;
                list.add(stack.saveOptional(player.registryAccess()));
                value.set(i, ItemStack.EMPTY);
            }
            tag.put("Inventory" + key, list);
        });
        EnigmaticHandler.getPersistedData(player).put("ELPersistentInventory", tag);
    }

    public static boolean reclaimInventory(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        Map<String, NonNullList<ItemStack>> inventories = inventoryMap(newPlayer);
        Tag maybeTag = EnigmaticHandler.getPersistedData(oldPlayer).get("ELPersistentInventory");
        boolean hadTag = false;
        if (maybeTag instanceof CompoundTag tag) {
            EnigmaticHandler.getPersistedData(oldPlayer).remove("ELPersistentInventory");
            hadTag = true;
            inventories.forEach((key, value) -> {
                Tag maybeList = tag.get("Inventory" + key);
                if (maybeList instanceof ListTag list) {
                    for (int i = 0; i < value.size(); i++) {
                        CompoundTag stackTag = list.getCompound(i);
                        ItemStack stack = ItemStack.parseOptional(newPlayer.registryAccess(), stackTag);
                        value.set(i, stack);
                    }
                }
            });
        }
        return hadTag;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmulet1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmulet2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmulet3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmulet4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmulet5");
        } else {
            TooltipHandler.holdShift(list);
            String name = stack.get(EnigmaticComponents.AMULET_NAME);
            if (name != null) {
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.enigmaticAmuletInscription", ChatFormatting.RED, name);
            }
        }
        TooltipHandler.line(list);
        TooltipHandler.worthyOnly(list, stack);
        TooltipHandler.line(list);
        list.add(Component.translatable("curios.modifiers.charm").withStyle(ChatFormatting.GOLD));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmuletStat1", ChatFormatting.GOLD, attackDamage.getAsInt() + "%");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.eldritchAmuletStat2", ChatFormatting.GOLD, lifeSteal.getAsInt() + "%");
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();
        if (!EnigmaticHandler.isTheWorthyOne(entity)) return;
        if (entity instanceof Player) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (timer < 1.0F) stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
        }
        if (entity.tickCount % 5 == 0 && !entity.level().isClientSide()) {
            List<LivingEntity> entities = EnigmaticHandler.getObservedEntities(entity, entity.level(), 3, 128, false);
            for (LivingEntity target : entities) {
                if (EnigmaticHandler.hasCurio(target, this)) continue;
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1));
                target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 1));
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 1));
            }
        }
        entity.getAttributes().addTransientAttributeModifiers(getModifiers());
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity && !level.isClientSide()) {
            float timer = stack.getOrDefault(EnigmaticComponents.ELDRITCH_TIMER, 0.0F);
            if (isSelected && EnigmaticHandler.isTheWorthyOne(livingEntity))
                stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.min(1.0F, timer + 0.3F));
            else stack.set(EnigmaticComponents.ELDRITCH_TIMER, Math.max(0.0F, timer - 0.3F));
        }
    }

    public void onUnequip(@NotNull SlotContext context, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = context.entity();
        entity.getAttributes().removeAttributeModifiers(getModifiers());
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers() {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(getLocation(this), attackDamage.getAsInt() * 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return map;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamagePost(LivingDamageEvent.@NotNull Post event) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && !attacker.level().isClientSide()) {
                if (EnigmaticHandler.hasCurio(attacker, EnigmaticItems.ELDRITCH_AMULET))
                    attacker.heal(event.getNewDamage() * 0.01F * lifeSteal.get());
            }
        }
    }
}
