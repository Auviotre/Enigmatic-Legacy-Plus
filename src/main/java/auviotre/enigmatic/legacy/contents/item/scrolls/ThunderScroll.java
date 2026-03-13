package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.CursedCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.packets.server.EmptyLeftClickPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class ThunderScroll extends CursedCurioItem {
    public static final String TAG_ID = "ElectricPoint";

    public ThunderScroll() {
        super(defaultSingleProperties().rarity(Rarity.RARE), true);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.thunderScroll1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.thunderScroll2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.thunderScroll3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.thunderScroll4");
            if (EnigmaticHandler.isTheBlessedOne(Minecraft.getInstance().player)) {
                TooltipHandler.line(list);
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.thunderScroll5");
            }
        } else TooltipHandler.holdShift(list);

        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.SWEEPING_DAMAGE_RATIO, new AttributeModifier(getLocation(this), 1, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onAttack(@NotNull LivingIncomingDamageEvent event) {
            DamageSource source = event.getSource();
            if (source.getEntity() instanceof LivingEntity entity && EnigmaticHandler.hasCurio(entity, EnigmaticItems.THUNDER_SCROLL)) {
                if (EnigmaticHandler.isTheBlessedOne(entity)) {
                    event.setAmount(modify(event.getEntity(), event.getAmount()));
                }
            }
        }

        private static float modify(LivingEntity target, float damage) {
            if (target.getAttributes().hasAttribute(Attributes.ARMOR)) {
                double value = target.getArmorValue();
                if (value > 0) {
                    double factor = 1.0 - Math.min(0.0375 * value, 0.75);
                    damage = (float) (damage / Math.sqrt(factor));
                }
            }
            return damage;
        }

        @SubscribeEvent
        private static void onTick(EntityTickEvent.@NotNull Pre event) {
            if (event.getEntity() instanceof LivingEntity entity) {
                int electric = entity.getPersistentData().getInt(TAG_ID);
                if (electric > 0) {
                    if (electric > 1200) {
                        LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(entity.level());
                        if (lightningbolt != null) {
                            lightningbolt.moveTo(Vec3.atBottomCenterOf(entity.blockPosition()));
                            lightningbolt.setSilent(entity.getRandom().nextBoolean());
                            lightningbolt.addTag("HarmlessThunder");
                            lightningbolt.setDamage(lightningbolt.getDamage() * electric / 600.0F);
                            entity.level().addFreshEntity(lightningbolt);
                        }
                        entity.getPersistentData().putInt(TAG_ID, (electric - 1200) / 2 + 100);
                    } else entity.getPersistentData().putInt(TAG_ID, electric - 1);
                } else entity.getPersistentData().remove(TAG_ID);
            }
        }

        @SubscribeEvent
        private static void onDamaged(LivingDamageEvent.@NotNull Post event) {
            DamageSource source = event.getSource();
            LivingEntity victim = event.getEntity();
            if (source.getEntity() instanceof LivingEntity entity && EnigmaticHandler.hasCurio(entity, EnigmaticItems.THUNDER_SCROLL)) {
                if (entity.getWeaponItem().canPerformAction(ItemAbilities.SWORD_SWEEP) && source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                    int electric = victim.getPersistentData().getInt(TAG_ID);
                    victim.getPersistentData().putInt(TAG_ID, electric + 60 + entity.getRandom().nextInt(80) + (int) (event.getNewDamage() * 10));
                }
            }
        }

        @SubscribeEvent
        private static void onLeftClick(PlayerInteractEvent.@NotNull LeftClickEmpty event) {
            Player player = event.getEntity();
            boolean flag = player.onGround() && EnigmaticHandler.hasCurio(player, EnigmaticItems.THUNDER_SCROLL);
            if (flag && player.getWeaponItem().canPerformAction(ItemAbilities.SWORD_SWEEP) && !player.getCooldowns().isOnCooldown(EnigmaticItems.THUNDER_SCROLL.get())) {
                PacketDistributor.sendToServer(new EmptyLeftClickPacket(true));
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        private static void onLightningStrike(@NotNull EntityStruckByLightningEvent event) {
            if (event.getLightning().getTags().contains("HarmlessThunder")) {
                if (event.getEntity() instanceof ItemEntity) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
