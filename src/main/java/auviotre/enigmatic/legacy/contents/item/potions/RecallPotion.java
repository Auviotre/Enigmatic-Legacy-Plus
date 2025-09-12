package auviotre.enigmatic.legacy.contents.item.potions;

import auviotre.enigmatic.legacy.contents.item.generic.BaseDrinkableItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class RecallPotion extends BaseDrinkableItem {
    public RecallPotion() {
        super(defaultSingleProperties().craftRemainder(Items.GLASS_BOTTLE));
    }

    public static void teleportToRespawnPoint(ServerPlayer player, ParticleOptions particle) {
        double hOffset = player.getBbWidth() / 6;
        double yOffset = player.getBbHeight() / 4;
        if (player.level() instanceof ServerLevel server) {
            server.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
            server.sendParticles(particle, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
        }

        ResourceKey<Level> respawnDimension = player.getRespawnDimension();
        ServerLevel destLevel = player.server.getLevel(respawnDimension);
        if (destLevel == null) destLevel = player.server.overworld();
        BlockPos respawnPos = player.getRespawnPosition();
        if (respawnPos == null) respawnPos = destLevel.getSharedSpawnPos();
        Vec3 vec3 = player.adjustSpawnLocation(destLevel, respawnPos).getBottomCenter();

        if (!player.level().equals(destLevel))
            player.changeDimension(new DimensionTransition(destLevel, vec3, player.getDeltaMovement(), player.getYRot(), player.getXRot(), DimensionTransition.DO_NOTHING));
        player.teleportTo(vec3.x, vec3.y, vec3.z);

        if (player.level() instanceof ServerLevel server) {
            server.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
            server.sendParticles(particle, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.recallPotion1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.recallPotion2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.recallPotion3");
    }

    public void onConsumed(Level level, Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            teleportToRespawnPoint(serverPlayer, ColorParticleOption.create(EnigmaticParticles.SPELL.get(), 0xF017A3C8));
        }
    }
}
