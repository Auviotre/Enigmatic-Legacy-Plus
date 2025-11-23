package auviotre.enigmatic.legacy.contents.item.materials;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.contents.item.tools.ExecutionAxe;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EarthHeart extends BaseItem implements ITaintable {
    public EarthHeart() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON));
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && !level.isClientSide())
            this.handleTaintable(stack, player);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (ITaintable.isTainted(stack)) TooltipHandler.line(list, "tooltip.enigmaticlegacy.tainted");
    }

    public boolean canTaint(Player player) {
        return EnigmaticHandler.isTheOne(player);
    }

    public static class Fragment extends BaseItem {
        public Fragment() {
            super(defaultProperties().stacksTo(16).rarity(Rarity.UNCOMMON));
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {

        @SubscribeEvent
        private static void onDrops(@NotNull LivingDropsEvent event) {
            LivingEntity killed = event.getEntity();
            DamageSource source = event.getSource();
            if (killed instanceof Monster && event.isRecentlyHit() && source.getEntity() instanceof Player player && EnigmaticHandler.isTheOne(player)) {
                BlockPos blockPos = killed.blockPosition();
                int lootingLevel = ExecutionAxe.getLootingLevel(player, player.level());
                if (killed.level().dimension().equals(Level.OVERWORLD) && !killed.level().canSeeSky(blockPos)) {
                    if (blockPos.getY() <= 0 && player.getRandom().nextInt(1000) < 30 + lootingLevel * 15) {
                        ItemEntity itemEntity = new ItemEntity(killed.level(), killed.getX(), killed.getY(), killed.getZ(), EnigmaticItems.EARTH_HEART_FRAGMENT.toStack());
                        itemEntity.setPickUpDelay(10);
                        event.getDrops().add(itemEntity);
                    }
                }
            }
        }
    }
}
