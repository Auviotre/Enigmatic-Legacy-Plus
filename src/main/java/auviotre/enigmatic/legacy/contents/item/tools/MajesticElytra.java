package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.packets.server.UpdateElytraBoostPacket;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.illusivesoulworks.caelus.api.CaelusApi;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class MajesticElytra extends BaseCurioItem implements Equipable {
    private static final ResourceLocation LOCATION = EnigmaticLegacy.location("majestic_elytra");
    private static final AttributeModifier ELYTRA_MODIFIER = new AttributeModifier(LOCATION, 1.0, AttributeModifier.Operation.ADD_VALUE);
    @OnlyIn(Dist.CLIENT)
    private static boolean isBoosting;

    public MajesticElytra() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.RARE).durability(3476).component(EnigmaticComponents.ETHERIUM_TOOL, 4));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    public static ItemStack get(LivingEntity entity) {
        if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.MAJESTIC_ELYTRA)) {
            return EnigmaticHandler.getCurio(entity, EnigmaticItems.MAJESTIC_ELYTRA);
        } else {
            ItemStack itemBySlot = entity.getItemBySlot(EquipmentSlot.CHEST);
            return itemBySlot.is(EnigmaticItems.MAJESTIC_ELYTRA) ? itemBySlot : ItemStack.EMPTY;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleBoosting(Player player) {
        if (Minecraft.getInstance().player != player) return;

        if (Minecraft.getInstance().options.keyJump.isDown() && flyingBoost(player)) {
            if (!isBoosting) PacketDistributor.sendToServer(new UpdateElytraBoostPacket(isBoosting = true));
        } else if (isBoosting) PacketDistributor.sendToServer(new UpdateElytraBoostPacket(isBoosting = false));
    }

    private static boolean flyingBoost(Player player) {
        if (player.isFallFlying()) {
            Vec3 lookAngle = player.getLookAngle();
            Vec3 movement = player.getDeltaMovement();
            player.setDeltaMovement(movement.add(lookAngle.x * 0.1D + (lookAngle.x * 1.5D - movement.x) * 0.5D, lookAngle.y * 0.1D + (lookAngle.y * 1.5D - movement.y) * 0.5D, lookAngle.z * 0.1D + (lookAngle.z * 1.5D - movement.z) * 0.5D));
            return true;
        }
        return false;
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        if (context.entity() instanceof Player player && player.level().isClientSide()) handleBoosting(player);
        LivingEntity livingEntity = context.entity();
        int ticks = livingEntity.getFallFlyingTicks();
        if (ticks > 0 && livingEntity.isFallFlying()) stack.elytraFlightTick(livingEntity, ticks);
    }

    public boolean elytraFlightTick(ItemStack stack, @NotNull LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0) {
                    stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
                }
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        } else if (entity instanceof Player player) handleBoosting(player);
        return true;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return this.swapWithEquipmentSlot(this, level, player, hand);
    }

    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(EnigmaticItems.ETHERIUM_INGOT);
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
                    stack = get(player);
                    if (stack.is(EnigmaticItems.MAJESTIC_ELYTRA) && ElytraItem.isFlyEnabled(stack)) {
                        attribute.addTransientModifier(ELYTRA_MODIFIER);
                    }
                }
            }

            if (player instanceof ServerPlayer) {
                if (data.isElytraBoosting()) {
                    flyingBoost(player);
                    if (stack.is(EnigmaticItems.MAJESTIC_ELYTRA)) {
                        int flightTicks = player.getFallFlyingTicks();
                        int nextFlightTick = flightTicks + 1;
                        if (nextFlightTick % 5 == 0) stack.hurtAndBreak(1, player, EquipmentSlot.CHEST);
                    }
                }
            }

            if (player.level().isClientSide()) {
                if (data.isElytraBoosting()) {
                    if (!player.isFallFlying()) {
                        if (player == Minecraft.getInstance().player) handleBoosting(player);
                        else data.setElytraBoosting(false);
                        return;
                    }
                    int amount = 3;
                    double rangeModifier = 0.1;
                    for (int counter = 0; counter <= amount; counter++) {
                        Vec3 pos = player.position();
                        pos = pos.add(Math.random() - 0.5, -1.0 + Math.random() - 0.5, Math.random() - 0.5);
                        player.level().addParticle(ParticleTypes.DRAGON_BREATH, true, pos.x, pos.y, pos.z, ((Math.random() - 0.5D) * 2.0D) * rangeModifier, ((Math.random() - 0.5D) * 2.0D) * rangeModifier, ((Math.random() - 0.5D) * 2.0D) * rangeModifier);
                    }
                }
            }
        }
    }
}
