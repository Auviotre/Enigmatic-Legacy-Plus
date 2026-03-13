package auviotre.enigmatic.legacy.contents.item.potions;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseDrinkableItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TwistedPotion extends BaseDrinkableItem {
    public TwistedPotion() {
        super(defaultSingleProperties().craftRemainder(Items.GLASS_BOTTLE).rarity(Rarity.UNCOMMON)
                .component(EnigmaticComponents.CURSED, true));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.twistedPotion1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.twistedPotion2");
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

    public void onConsumed(Level level, Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            teleportToDeathPoint(serverPlayer, ColorParticleOption.create(EnigmaticParticles.SPELL.get(), 0xF06D00C3));
        }
    }

    private static void teleportToDeathPoint(ServerPlayer player, ParticleOptions particle) {
        double hOffset = player.getBbWidth() / 6;
        double yOffset = player.getBbHeight() / 4;
        if (player.level() instanceof ServerLevel server) {
            server.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
            server.sendParticles(particle, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
        }
        CompoundTag deathLoc = (CompoundTag) EnigmaticHandler.getPersistedData(player).get("LastDeathLoc");
        if (deathLoc == null) return;

        ResourceKey<Level> deathDimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(deathLoc.getString("dimension")));
        ServerLevel destLevel = player.server.getLevel(deathDimension);
        if (destLevel == null) return;
        BlockPos respawnPos = BlockPos.containing(deathLoc.getDouble("x"), deathLoc.getDouble("y"), deathLoc.getDouble("z"));
        Vec3 vec3 = player.adjustSpawnLocation(destLevel, respawnPos).getBottomCenter();

        if (!player.level().equals(destLevel))
            player.changeDimension(new DimensionTransition(destLevel, vec3, player.getDeltaMovement(), player.getYRot(), player.getXRot(), DimensionTransition.DO_NOTHING));
        player.teleportTo(vec3.x, vec3.y, vec3.z);
        player.setHealth(Math.max(1.0F, player.getHealth() * 0.8F));

        if (player.level() instanceof ServerLevel server) {
            server.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
            server.sendParticles(particle, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void onLivingDrops(@NotNull LivingDropsEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                CompoundTag deathLocation = new CompoundTag();
                deathLocation.putDouble("x", player.getX());
                deathLocation.putDouble("y", player.getY());
                deathLocation.putDouble("z", player.getZ());
                deathLocation.putString("dimension", player.level().dimension().location().toString());
                CompoundTag persistedData = EnigmaticHandler.getPersistedData(player);
                persistedData.put("LastDeathLoc", deathLocation);
            }
        }
    }
}
