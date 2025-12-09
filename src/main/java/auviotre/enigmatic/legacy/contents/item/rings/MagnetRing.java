package auviotre.enigmatic.legacy.contents.item.rings;

import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class MagnetRing extends BaseCurioItem {
    public static ModConfigSpec.DoubleValue magneticRange;
    public static ModConfigSpec.IntValue buttonOffsetX;
    public static ModConfigSpec.IntValue buttonOffsetY;
    public static ModConfigSpec.IntValue buttonOffsetXCreative;
    public static ModConfigSpec.IntValue buttonOffsetYCreative;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        if (type == ModConfig.Type.CLIENT) {
            builder.push("magnetButton");
            buttonOffsetX = builder.defineInRange("OffsetX", 0, -1024, 1024);
            buttonOffsetY = builder.defineInRange("OffsetY", 0, -1024, 1024);
            buttonOffsetXCreative = builder.defineInRange("OffsetXCreative", 0, -1024, 1024);
            buttonOffsetYCreative = builder.defineInRange("OffsetYCreative", 0, -1024, 1024);
            builder.pop();
        } else {
            builder.translation("item.enigmaticlegacyplus.magnet_ring").push("else.magnetRing");
            magneticRange = builder.defineInRange("magneticRange", 8.0, 1.0, 256.0);
            builder.pop(2);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.magnetRing1", ChatFormatting.GOLD, String.format("%.0f", magneticRange.get()));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.magnetRing2");
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        LivingEntity entity = context.entity();

        if (entity.isShiftKeyDown() || !(entity instanceof Player player)) return;
        if (this.hasMagnetEffectsDisabled(entity)) return;

        double x = entity.getX();
        double y = entity.getY() + 0.75;
        double z = entity.getZ();
        double r = magneticRange.get();
        List<ItemEntity> items = entity.level().getEntitiesOfClass(ItemEntity.class, new AABB(x - r, y - r, z - r, x + r, y + r, z + r));
        int pulled = 0;
        for (ItemEntity item : items)
            if (this.canPullItem(item)) {
                if (pulled > 200) break;
                if (EnigmaticHandler.canNotPickStack(player, item.getItem())) continue;
                item.setNoPickUpDelay();
                Vec3 delta = item.position().subtract(x, y, z);
                if (delta.length() > 1.0) delta = delta.normalize();
                item.setDeltaMovement(delta.scale(-1.2F));
                item.hasImpulse = true;
                pulled++;
            }
    }

    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && !EnigmaticHandler.hasCurio(context.entity(), EnigmaticItems.DISLOCATION_RING);
    }

    public boolean hasMagnetEffectsDisabled(@NotNull LivingEntity entity) {
        return !entity.getData(EnigmaticAttachments.ENIGMATIC_DATA).isMagnetRingEnable();
    }

    protected boolean canPullItem(@NotNull ItemEntity item) {
        ItemStack stack = item.getItem();
        return item.isAlive() && !stack.isEmpty() && !item.getPersistentData().getBoolean("PreventRemoteMovement");
    }
}