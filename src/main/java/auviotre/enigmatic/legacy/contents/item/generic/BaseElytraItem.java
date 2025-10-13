package auviotre.enigmatic.legacy.contents.item.generic;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.packets.server.UpdateElytraBoostPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import com.illusivesoulworks.caelus.api.CaelusApi;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public abstract class BaseElytraItem extends BaseCurioItem implements Equipable {
    protected static final ResourceLocation LOCATION = EnigmaticLegacy.location("elytra");
    protected static final AttributeModifier ELYTRA_MODIFIER = new AttributeModifier(LOCATION, 1.0, AttributeModifier.Operation.ADD_VALUE);
    @OnlyIn(Dist.CLIENT)
    private boolean isBoosting;

    public BaseElytraItem(Properties properties) {
        super(properties);
    }

    public static ItemStack getElytra(LivingEntity entity) {
        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(entity);
        if (curios.isPresent()) {
            Optional<SlotResult> firstCurio = curios.get().findFirstCurio(stack -> stack.getItem() instanceof BaseElytraItem);
            if (firstCurio.isPresent()) return firstCurio.get().stack();
        }
        ItemStack itemBySlot = entity.getItemBySlot(EquipmentSlot.CHEST);
        return itemBySlot.getItem() instanceof BaseElytraItem ? itemBySlot : ItemStack.EMPTY;
    }

    protected abstract boolean flyingBoost(Player player);

    @OnlyIn(Dist.CLIENT)
    protected abstract void addParticle(Player player);

    @OnlyIn(Dist.CLIENT)
    public void handleBoosting(Player player) {
        if (Minecraft.getInstance().player != player) return;
        if (Minecraft.getInstance().options.keyJump.isDown() && flyingBoost(player)) {
            if (!isBoosting) PacketDistributor.sendToServer(new UpdateElytraBoostPacket(isBoosting = true));
        } else if (isBoosting) PacketDistributor.sendToServer(new UpdateElytraBoostPacket(isBoosting = false));
    }

    public boolean elytraFlightTick(ItemStack stack, @NotNull LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0)
                    stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        } else if (entity instanceof Player player) handleBoosting(player);
        return true;
    }

    public Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_ELYTRA;
    }

    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_ELYTRA.value(), 1F, 1F);
    }

    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return true;
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && getElytra(context.entity()).isEmpty();
    }

    public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) {
        return super.canEquip(stack, slot, entity) && getElytra(entity).isEmpty();
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onTick(PlayerTickEvent.@NotNull Pre event) {
            Player player = event.getEntity();
            EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            ItemStack stack = ItemStack.EMPTY;
            AttributeInstance attribute = player.getAttribute(CaelusApi.getInstance().getFallFlyingAttribute());
            if (attribute != null) {
                attribute.removeModifier(ELYTRA_MODIFIER);
                if (!attribute.hasModifier(LOCATION)) {
                    stack = getElytra(player);
                    if (!stack.isEmpty() && ElytraItem.isFlyEnabled(stack)) {
                        attribute.addTransientModifier(ELYTRA_MODIFIER);
                    }
                }
            }
            if (stack.isEmpty()) return;
            BaseElytraItem elytra = (BaseElytraItem) stack.getItem();
            if (player instanceof ServerPlayer && data.isElytraBoosting()) {
                elytra.flyingBoost(player);
                int flightTicks = player.getFallFlyingTicks();
                int nextFlightTick = flightTicks + 1;
                if (nextFlightTick % 5 == 0) stack.hurtAndBreak(1, player, EquipmentSlot.CHEST);
            }
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        private static void onClientTick(PlayerTickEvent.@NotNull Pre event) {
            Player player = event.getEntity();
            EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
            ItemStack stack = getElytra(player);
            if (player.level().isClientSide() && data.isElytraBoosting()) {
                if (!player.isFallFlying()) {
                    if (player == Minecraft.getInstance().player)
                        if (stack.getItem() instanceof BaseElytraItem elytra)
                            elytra.handleBoosting(player);
                        else data.setElytraBoosting(false);
                    return;
                }
                if (stack.getItem() instanceof BaseElytraItem elytra) elytra.addParticle(player);
            }
        }
    }
}
