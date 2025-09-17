package auviotre.enigmatic.legacy.contents.item.spellstones;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.generic.SpellstoneItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class LostEngine extends SpellstoneItem {

    public LostEngine() {
        super(defaultSingleProperties().rarity(Rarity.RARE));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkill");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneSkillAbsent");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstoneCooldown", ChatFormatting.GOLD, String.format("%.01f", 0.05F * getCooldown()));
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.spellstonePassive");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lostEngine1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lostEngine2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lostEngine3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lostEngine4", ChatFormatting.GOLD, "40%");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lostEngine5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lostEngine6");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.lostEngine7");
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
        this.addKeyText(list);
    }

    public int getCooldown() {
        return 0;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(Attributes.GRAVITY, new AttributeModifier(getLocation(this), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        attributes.put(Attributes.JUMP_STRENGTH, new AttributeModifier(getLocation(this), 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        attributes.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(getLocation(this), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        attributes.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(getLocation(this), 4, AttributeModifier.Operation.ADD_VALUE));
        attributes.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(getLocation(this), 0.2, AttributeModifier.Operation.ADD_VALUE));
        return attributes;
    }

    public List<Component> getAttributesTooltip(@NotNull List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.LOST_ENGINE)) {
                if (event.getSource().is(EnigmaticTags.DamageTypes.LOST_ENGINE_IMMUNE_TO))
                    event.setCanceled(true);
            }
        }

        @SubscribeEvent
        private static void onDamage(LivingDamageEvent.@NotNull Pre event) {
            if (ISpellstone.get(event.getEntity()).is(EnigmaticItems.LOST_ENGINE)) {
                DamageSource source = event.getSource();
                if (source.is(Tags.DamageTypes.IS_MAGIC)) {
                    event.setNewDamage(event.getNewDamage() * 2.5F);
                } else if (source.is(DamageTypeTags.IS_LIGHTNING)) {
                    event.setNewDamage(event.getNewDamage() * 2.0F);
                }
            }
        }

        @SubscribeEvent
        private static void onPlayerTick(PlayerTickEvent.@NotNull Pre event) {
            Player player = event.getEntity();
            if (EnigmaticHandler.hasCurio(player, EnigmaticItems.LOST_ENGINE)) {
                if (!player.level().isClientSide() && player.tickCount % 3 == 0) player.getCooldowns().tick();
                if (player.level().isClientSide() && Minecraft.getInstance().player == player) {
                    boolean spaceDown = Minecraft.getInstance().options.keyJump.isDown();
                    if (spaceDown && player.getDeltaMovement().y > 0.225F && !player.level().getBlockState(player.blockPosition()).canOcclude()) {
                        player.addDeltaMovement(new Vec3(0.0D, 0.0256D, 0.0D));
                        float width = player.getBbWidth();
                        for (int i = 0; i < player.getRandom().nextInt(3); i++) {
                            player.level().addParticle(ParticleTypes.CLOUD, player.getRandomX(width), player.getY() + player.getRandom().nextFloat() * 0.2F, player.getRandomZ(width), 0, 0.5F * player.getRandom().nextFloat() * player.getDeltaMovement().y, 0);
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onLivingJump(LivingEvent.@NotNull LivingJumpEvent event) {
            LivingEntity entity = event.getEntity();
            if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.LOST_ENGINE)) {
                if (entity.isCrouching()) {
                    float rot = entity.getYRot() * Mth.PI / 180.0F;
                    float sin = -Mth.sin(rot) * 0.055F;
                    float cos = Mth.cos(rot) * 0.055F;
                    entity.addDeltaMovement(new Vec3(sin * 3F, 0.45, cos * 3F));
                    for (int i = 0; i < 5; i++) {
                        float width = entity.getBbWidth();
                        entity.level().addParticle(ParticleTypes.CLOUD, entity.getRandomX(width), entity.getY(), entity.getRandomZ(0.5), sin, 0.12F * entity.getRandom().nextFloat() + 0.05F, cos);
                    }
                }
            }
        }

        @SubscribeEvent
        private static void onCriticalHit(@NotNull CriticalHitEvent event) {
            if (EnigmaticHandler.hasCurio(event.getEntity(), EnigmaticItems.LOST_ENGINE)) {
                event.setDamageMultiplier(event.getDamageMultiplier() + 0.4F);
            }
        }

        @SubscribeEvent
        private static void onLivingChangeTarget(@NotNull LivingChangeTargetEvent event) {
            LivingEntity entity = event.getEntity();
            LivingEntity target = event.getNewAboutToBeSetTarget();
            if (entity instanceof Targeting targetedEntity && EnigmaticHandler.hasCurio(target, EnigmaticItems.LOST_ENGINE)) {
                if (entity.getLastHurtByMob() != target && (targetedEntity.getTarget() == null || !targetedEntity.getTarget().isAlive())) {
                    if (entity instanceof AbstractGolem || entity.getType().is(EnigmaticTags.EntityTypes.EXTRA_GOLEM)) {
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onDamageLowest(LivingDamageEvent.@NotNull Pre event) {
            LivingEntity victim = event.getEntity();
            if (EnigmaticHandler.hasCurio(victim, EnigmaticItems.LOST_ENGINE) && event.getSource().is(DamageTypeTags.IS_LIGHTNING)) {
                if (victim instanceof ServerPlayer player && player.level() instanceof ServerLevel server) {
                    for (NonNullList<ItemStack> compartment : player.getInventory().compartments) {
                        for (ItemStack itemStack : compartment) {
                            double modifier = itemStack.getItem() instanceof ArmorItem ? 1.2 : 2;
                            itemStack.hurtAndBreak((int) (itemStack.getMaxDamage() * modifier), server, player, item -> {
                            });
                            if (itemStack.has(DataComponents.UNBREAKABLE)) {
                                itemStack.remove(DataComponents.UNBREAKABLE);
                            }
                        }
                    }
                    CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
                        int slots = handler.getEquippedCurios().getSlots();
                        for (int i = 0; i < slots; i++) {
                            ItemStack stackInSlot = handler.getEquippedCurios().getStackInSlot(i);
                            if (!stackInSlot.is(EnigmaticItems.CURSED_RING)) {
                                stackInSlot.hurtAndBreak(stackInSlot.getMaxDamage() * 2, server, player, item -> {
                                });
                            }
                        }
                    });
                }
                event.setNewDamage(event.getNewDamage() * (victim.getRandom().nextInt(4) + 4) + victim.getMaxHealth());
            }
        }
    }
}
