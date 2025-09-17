package auviotre.enigmatic.legacy.contents.item.etherium;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EtheriumArmor extends ArmorItem {
    public static final Holder<ArmorMaterial> MATERIAL = Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL, EnigmaticLegacy.location("etherium"),
            new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
                map.put(Type.HELMET, 4);
                map.put(Type.CHESTPLATE, 9);
                map.put(Type.LEGGINGS, 7);
                map.put(Type.BOOTS, 4);
                map.put(Type.BODY, 12);
            }), 24, EnigmaticSounds.ARMOR_EQUIP_ETHERIUM, () -> Ingredient.of(EnigmaticItems.ETHERIUM_INGOT),
                    List.of(new ArmorMaterial.Layer(EnigmaticLegacy.location("etherium"))), 4.0F, 0.0F)
    );

    public EtheriumArmor(Type type) {
        super(MATERIAL, type, BaseItem.defaultProperties().fireResistant().durability(type.getDurability(132)).component(EnigmaticComponents.ETHERIUM_TOOL, 10));
    }

    public static int getShieldThreshold(@NotNull LivingEntity entity) {
        AtomicInteger etherPoint = new AtomicInteger();
        for (ItemStack slot : entity.getArmorSlots()) {
            int i = slot.getOrDefault(EnigmaticComponents.ETHERIUM_TOOL, 0);
            if (i > 0) etherPoint.addAndGet(i);
        }
        for (ItemStack slot : entity.getHandSlots()) {
            int i = slot.getOrDefault(EnigmaticComponents.ETHERIUM_TOOL, 0);
            if (i > 0) etherPoint.addAndGet(i);
        }
        CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
            IItemHandlerModifiable curios = handler.getEquippedCurios();
            for (int i = 0; i < curios.getSlots(); i++) {
                int v = curios.getStackInSlot(i).getOrDefault(EnigmaticComponents.ETHERIUM_TOOL, 0);
                if (v > 0) etherPoint.addAndGet(v);
            }
        });
        return Math.min(etherPoint.get(), 99);
    }

    public static boolean hasShield(@NotNull LivingEntity entity) {
        return entity.getMaxHealth() * getShieldThreshold(entity) >= entity.getHealth() * 100;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumArmor");
        TooltipHandler.line(list);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttacked(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
            if (event.getSource().getDirectEntity() instanceof AbstractHurtingProjectile || event.getSource().getDirectEntity() instanceof AbstractArrow) {
                if (EtheriumArmor.hasShield(entity)) {
                    event.setCanceled(true);
                    entity.level().playSound(null, entity.blockPosition(), EnigmaticSounds.ETHERIUM_SHIELD_DEFLECT.get(), SoundSource.AMBIENT, 1.0F, 0.9F + entity.getRandom().nextFloat() * 0.1F);
                }
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity entity = event.getEntity();
            if (event.getNewDamage() > 0) {
                if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
                if (EtheriumArmor.hasShield(entity)) {
                    if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
                        Vec3 vec = entity.position().subtract(attacker.position()).normalize();
                        attacker.knockback(0.75F, vec.x, vec.z);
                        entity.level().playSound(null, entity.blockPosition(), EnigmaticSounds.ETHERIUM_SHIELD_DEFLECT.get(), SoundSource.PLAYERS, 1.0F, 0.9F + entity.getRandom().nextFloat() * 0.1F);
                    }
                    event.setNewDamage(event.getNewDamage() * 0.5F);
                }
            }
        }
    }
}
