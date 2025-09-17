package auviotre.enigmatic.legacy.contents.item.potions;

import auviotre.enigmatic.legacy.contents.item.generic.BaseDrinkableItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WormholePotion extends BaseDrinkableItem {
    public WormholePotion() {
        super(defaultSingleProperties().craftRemainder(Items.GLASS_BOTTLE));
    }

    private static void performTeleport(Player player, ServerLevel level, Player target, Set<RelativeMovement> relativeList) {
        double hOffset = player.getBbWidth() / 6;
        double yOffset = player.getBbHeight() / 4;
        ColorParticleOption particle = ColorParticleOption.create(EnigmaticParticles.SPELL.get(), 0xF02CCDB1);
        if (Level.isInSpawnableBounds(target.blockPosition())) {
            level.sendParticles(particle, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
            Vec3 vec3 = player.adjustSpawnLocation(level, target.blockPosition()).getBottomCenter();
            if (player.teleportTo(level, vec3.x, vec3.y, vec3.z, relativeList, player.getYRot(), player.getXRot())) {
                level.sendParticles(particle, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
                level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
                if (player instanceof LivingEntity livingentity) {
                    if (livingentity.isFallFlying()) {
                        return;
                    }
                }
                player.setDeltaMovement(player.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                player.setOnGround(true);
            }
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (stack.is(this)) {
            if (!stack.has(EnigmaticComponents.WORMHOLE_UUID))
                stack.set(EnigmaticComponents.WORMHOLE_UUID, entity.getUUID().toString());
            else {
                try {
                    String string = stack.get(EnigmaticComponents.WORMHOLE_UUID);
                    if (string == null) return;
                    UUID uuid = UUID.fromString(string);
                } catch (Exception exception) {
                    stack.remove(EnigmaticComponents.WORMHOLE_UUID);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (stack.has(EnigmaticComponents.WORMHOLE_UUID)) {
            String string = stack.get(EnigmaticComponents.WORMHOLE_UUID);
            if (string == null) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.wormholePotion2");
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.wormholePotion3");
                return;
            }
            UUID uuid = UUID.fromString(string);
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.level().getPlayerByUUID(uuid) != null) {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.wormholePotion1", ChatFormatting.GOLD, player.level().getPlayerByUUID(uuid).getName());
            }
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.wormholePotion2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.wormholePotion3");
        }
    }

    public void onConsumed(Level level, Player player, ItemStack stack) {
        if (stack.has(EnigmaticComponents.WORMHOLE_UUID)) {
            String string = stack.get(EnigmaticComponents.WORMHOLE_UUID);
            if (string == null) return;
            UUID uuid = UUID.fromString(string);
            Player destination = player.level().getPlayerByUUID(uuid);
            if (destination != null && destination.level() instanceof ServerLevel server) {
                performTeleport(player, server, destination, EnumSet.noneOf(RelativeMovement.class));
            }
        }
    }

    public boolean isFoil(ItemStack stack) {
        return stack.has(EnigmaticComponents.WORMHOLE_UUID);
    }
}
