package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.contents.item.generic.BaseElytraItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class MajesticElytra extends BaseElytraItem {

    public MajesticElytra() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.RARE).durability(3476).component(EnigmaticComponents.ETHERIUM_SHIELD, 4));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.majesticElytra1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.majesticElytra2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.majesticElytra3");
    }

    public boolean flyingBoost(@NotNull Player player) {
        if (player.isFallFlying()) {
            Vec3 lookAngle = player.getLookAngle().scale(0.85F);
            Vec3 movement = player.getDeltaMovement().scale(0.5F);
            player.setDeltaMovement(movement.add(lookAngle));
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void addParticle(Player player) {
        int amount = 3;
        double rangeModifier = 0.1;
        for (int counter = 0; counter <= amount; counter++) {
            Vec3 pos = player.position();
            pos = pos.add(Math.random() - 0.5, -1.0 + Math.random() - 0.5, Math.random() - 0.5);
            player.level().addParticle(ParticleTypes.DRAGON_BREATH, true, pos.x, pos.y, pos.z, ((Math.random() - 0.5D) * 2.0D) * rangeModifier, ((Math.random() - 0.5D) * 2.0D) * rangeModifier, ((Math.random() - 0.5D) * 2.0D) * rangeModifier);
        }
    }

    public void curioTick(@NotNull SlotContext context, ItemStack stack) {
        if (context.entity() instanceof Player player && player.level().isClientSide()) handleBoosting(player);
        LivingEntity livingEntity = context.entity();
        int ticks = livingEntity.getFallFlyingTicks();
        if (ticks > 0 && livingEntity.isFallFlying()) stack.elytraFlightTick(livingEntity, ticks);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return this.swapWithEquipmentSlot(this, level, player, hand);
    }

    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(EnigmaticItems.ETHERIUM_INGOT);
    }
}
