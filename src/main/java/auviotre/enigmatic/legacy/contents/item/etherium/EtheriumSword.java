package auviotre.enigmatic.legacy.contents.item.etherium;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EtheriumSword extends SwordItem {
    public EtheriumSword() {
        super(EtheriumProperties.TIER, new Item.Properties().fireResistant().attributes(createAttributes(EtheriumProperties.TIER, 6.0F, -2.6F)
                .withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(EnigmaticLegacy.location("etherium_sword"), 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
        ).component(EnigmaticComponents.ETHERIUM_SHIELD, 5));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        int power = 10;
        if (Minecraft.getInstance().player != null)
            power += EtheriumArmor.getShieldThreshold(Minecraft.getInstance().player) / 2;
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumSword", ChatFormatting.GOLD, power + "%");
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttacked(@NotNull LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            int threshold = EtheriumArmor.getShieldThreshold(entity);
            if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
            if (entity.getMainHandItem().is(EnigmaticItems.ETHERIUM_SWORD) && entity.getRandom().nextInt(100) < 10 + threshold / 2) {
                if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
                    Vec3 vec = entity.position().subtract(attacker.position()).normalize().scale(0.25F);
                    attacker.knockback(0.4F, vec.x, vec.z);
                    entity.level().playSound(null, entity.blockPosition(), EnigmaticSounds.ETHERIUM_SHIELD_DEFLECT.get(), SoundSource.PLAYERS, 0.6F, 0.9F + entity.getRandom().nextFloat() * 0.1F);
                }
                entity.invulnerableTime += 20;
                event.setCanceled(true);
            }
        }
    }
}
