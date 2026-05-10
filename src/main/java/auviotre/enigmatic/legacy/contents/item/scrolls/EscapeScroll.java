package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.SoulArchive;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticParticles;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.List;

public class EscapeScroll extends BaseCurioItem {

    public EscapeScroll() {
        super(IItemHelper.singleProperties().rarity(Rarity.UNCOMMON));
    }

    public static void teleportItems(ServerPlayer player, Collection<ItemEntity> drops) {
        ColorParticleOption particle = ColorParticleOption.create(EnigmaticParticles.SPELL.get(), 0xF017A3C8);
        RandomSource random = player.getRandom();
        double hOffset = player.getBbWidth() / 6;
        double yOffset = player.getBbHeight() / 4;
        if (player.level() instanceof ServerLevel server) {
            server.playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + random.nextFloat() * 0.2F);
            server.sendParticles(particle, player.getX(), player.getY(0.5), player.getZ(), 48, hOffset, yOffset, hOffset, 0.03);
        }

        SoulArchive.DimensionalPosition dimPoint = SoulArchive.getRespawnPos(player);
        boolean isEndAnchor = dimPoint.getWorld().getBlockState(dimPoint.getBlockPos()).is(EnigmaticBlocks.DIMENSIONAL_ANCHOR);

        for (ItemEntity itemEntity : drops) {
            ItemEntity alternativeDrop = new ItemEntity(dimPoint.world, dimPoint.posX, dimPoint.posY, dimPoint.posZ, itemEntity.getItem());
            alternativeDrop.setTarget(itemEntity.getTarget());
            if (itemEntity.getOwner() != null) alternativeDrop.setThrower(itemEntity.getOwner());
            alternativeDrop.teleportTo(dimPoint.posX, dimPoint.posY, dimPoint.posZ);

            if (!isEndAnchor)
                alternativeDrop.setDeltaMovement(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5);
            else alternativeDrop.setDeltaMovement(0, 0, 0);

            dimPoint.world.addFreshEntity(alternativeDrop);
            itemEntity.setItem(ItemStack.EMPTY);
        }

        if (player.level() instanceof ServerLevel server) {
            server.playSound(null, BlockPos.containing(dimPoint.posX, dimPoint.posY, dimPoint.posZ), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F + random.nextFloat() * 0.2F);
            server.sendParticles(particle, dimPoint.posX, dimPoint.posY, dimPoint.posZ, 48, hOffset, yOffset, hOffset, 0.03);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.escapeScroll1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.escapeScroll2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.escapeScroll3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.escapeScroll4");
        } else TooltipHandler.holdShift(list);
    }
}
