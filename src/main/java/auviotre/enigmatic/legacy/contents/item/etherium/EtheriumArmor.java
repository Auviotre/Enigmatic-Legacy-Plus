package auviotre.enigmatic.legacy.contents.item.etherium;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticAttributes;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import com.google.common.base.Suppliers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class EtheriumArmor extends ArmorItem {
    public static ModConfigSpec.BooleanValue etheriumShieldRenderLayer;
    private final Supplier<ItemAttributeModifiers> defaultModifiers;

    public EtheriumArmor(Type type) {
        super(EtheriumProperties.MATERIAL, type, BaseItem.defaultProperties().fireResistant().durability(type.getDurability(132)));
        this.defaultModifiers = Suppliers.memoize(() -> {
            ArmorMaterial material = EtheriumProperties.MATERIAL.value();
            int i = material.getDefense(type);
            float f = material.toughness();
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
            EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(type.getSlot());
            ResourceLocation location = ResourceLocation.withDefaultNamespace("armor." + type.getName());
            builder.add(Attributes.ARMOR, new AttributeModifier(location, i, AttributeModifier.Operation.ADD_VALUE), group);
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(location, f, AttributeModifier.Operation.ADD_VALUE), group);
            builder.add(EnigmaticAttributes.ETHERIUM_SHIELD, new AttributeModifier(location, 0.08F, AttributeModifier.Operation.ADD_VALUE), group);
            return builder.build();
        });
    }

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        if (type == ModConfig.Type.CLIENT) {
            EtheriumArmor.etheriumShieldRenderLayer = builder.define("etheriumShieldRenderLayer", true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumArmor");
    }

    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.defaultModifiers.get();
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGH)
        private static void onAttacked(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
            if (event.getSource().getDirectEntity() instanceof AbstractHurtingProjectile || event.getSource().getDirectEntity() instanceof AbstractArrow) {
                if (EtheriumProperties.hasShield(entity)) {
                    event.setCanceled(true);
                    entity.level().playSound(null, entity.blockPosition(), EnigmaticSounds.ETHERIUM_SHIELD_DEFLECT.get(), SoundSource.AMBIENT, 1.0F, 0.9F + entity.getRandom().nextFloat() * 0.1F);
                    return;
                }
            }
            EnigmaticData data = entity.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            float shield = data.getEtherealShield();
            if (shield > 0 && !event.getSource().is(DamageTypeTags.BYPASSES_SHIELD)) {
                if (event.getAmount() < shield) event.setCanceled(true);
                data.setEtherealShield(Math.clamp(shield - event.getAmount(), 0, entity.getMaxHealth()));
                event.setAmount(Math.max(0, event.getAmount() - shield));
                if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
                    Vec3 vec = entity.position().subtract(attacker.position()).normalize();
                    attacker.knockback(0.55F, vec.x, vec.z);
                    entity.level().playSound(null, entity.blockPosition(), EnigmaticSounds.ETHERIUM_SHIELD_DEFLECT.get(), SoundSource.PLAYERS, 1.0F, 0.9F + entity.getRandom().nextFloat() * 0.1F);
                }
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity entity = event.getEntity();
            if (event.getNewDamage() > 0 && event.getNewDamage() < Float.MAX_VALUE) {
                if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
                if (EtheriumProperties.hasShield(entity)) {
                    if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
                        Vec3 vec = entity.position().subtract(attacker.position()).normalize();
                        attacker.knockback(0.75F, vec.x, vec.z);
                        entity.level().playSound(null, entity.blockPosition(), EnigmaticSounds.ETHERIUM_SHIELD_DEFLECT.get(), SoundSource.PLAYERS, 1.0F, 0.9F + entity.getRandom().nextFloat() * 0.1F);
                    }
                    float modifier = (float) (0.9F - 0.5F * Math.sqrt(EtheriumProperties.getShieldThreshold(entity) * 0.01F));
                    event.setNewDamage(event.getNewDamage() * modifier);
                }
            }
        }
    }
}
