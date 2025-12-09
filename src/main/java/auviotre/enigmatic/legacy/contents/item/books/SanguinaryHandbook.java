package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.charms.BerserkEmblem;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.contents.item.scrolls.CursedScroll;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SanguinaryHandbook extends BaseCursedItem {
    public static ModConfigSpec.DoubleValue damageMultiplier;

    public SanguinaryHandbook() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.sanguinary_handbook").push("cursedItems.sanguinaryHandbook");
        damageMultiplier = builder.defineInRange("specialDamageBoost", 0.25, 0, 1);
        builder.pop(2);
    }

    public static Multimap<Holder<Attribute>, AttributeModifier> createAttributeMap(LivingEntity entity) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        float missingHealthPool = BerserkEmblem.getMissingHealthPool(entity);
        attributes.put(Attributes.ATTACK_SPEED, new AttributeModifier(EnigmaticItems.SANGUINARY_HANDBOOK.getId(), missingHealthPool * BerserkEmblem.attackSpeed.get() * 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        attributes.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(EnigmaticItems.SANGUINARY_HANDBOOK.getId(), missingHealthPool * BerserkEmblem.movementSpeed.get() * 1.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return attributes;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.inInventory", ChatFormatting.GOLD);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.sanguinaryHandbookBuff", ChatFormatting.GOLD, String.format("+%.0f%%", damageMultiplier.get() * 100));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.sanguinaryHandbook1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.sanguinaryHandbook2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.sanguinaryHandbook3");
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onFindTarget(EntityTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof LivingEntity entity) {
                if (entity instanceof OwnableEntity pet && !(entity instanceof TamableAnimal tamable && !tamable.isTame())) {
                    LivingEntity owner = pet.getOwner();
                    if (owner == null) return;
                    double range = EnigmaticHandler.hasCurio(owner, EnigmaticItems.ODE_TO_LIVING) ? LivingOde.effectiveRange.get() : HunterGuidebook.effectiveRange.get();
                    if (!entity.level().isClientSide() && owner.level() == pet.level() && owner.distanceTo(entity) <= range) {
                        if (EnigmaticHandler.hasCurio(owner, EnigmaticItems.SANGUINARY_HANDBOOK) && EnigmaticHandler.hasCurio(owner, EnigmaticItems.BERSERK_EMBLEM))
                            entity.getAttributes().addTransientAttributeModifiers(SanguinaryHandbook.createAttributeMap(owner));
                        else
                            entity.getAttributes().removeAttributeModifiers(SanguinaryHandbook.createAttributeMap(owner));
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
            Entity entity = event.getSource().getEntity();
            if (entity instanceof LivingEntity attacker) {
                if (attacker instanceof OwnableEntity pet && !(attacker instanceof TamableAnimal tamable && !tamable.isTame())) {
                    LivingEntity owner = pet.getOwner();
                    if (!EnigmaticHandler.hasCurio(owner, EnigmaticItems.SANGUINARY_HANDBOOK)) return;
                    double range = EnigmaticHandler.hasCurio(owner, EnigmaticItems.ODE_TO_LIVING) ? LivingOde.effectiveRange.get() : HunterGuidebook.effectiveRange.get();
                    if (owner.level() == pet.level() && owner.distanceTo(attacker) <= range) {
                        double damageMultiplier = SanguinaryHandbook.damageMultiplier.get();
                        if (EnigmaticHandler.hasCurio(owner, EnigmaticItems.BERSERK_EMBLEM))
                            damageMultiplier += 0.5F * (BerserkEmblem.getMissingHealthPool(owner) * BerserkEmblem.attackDamage.get());
                        if (EnigmaticHandler.hasCurio(owner, EnigmaticItems.CURSED_SCROLL)) {
                            damageMultiplier += 0.75F * (CursedScroll.getCurseAmount(owner) * CursedScroll.damageBoost.get() * 0.01F);
                        }
                        event.setAmount(event.getAmount() * (1.0F + (float) damageMultiplier));
                    }
                }
            }
        }
    }
}
