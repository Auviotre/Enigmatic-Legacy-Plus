package auviotre.enigmatic.legacy.contents.item.scrolls;

import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.entity.misc.ExplorerMarker;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class ExplorerScroll extends BaseCurioItem {
    public static ModConfigSpec.IntValue distance;
    public static ModConfigSpec.IntValue cooldown;

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.explorer_scroll").push("else.explorerScroll");
        distance = builder.defineInRange("effectiveRange", 4, 1, 8);
        cooldown  = builder.defineInRange("cooldown", 320, 160, 800);
        builder.pop(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.explorerScroll");
            try {
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.currentKeybind", ChatFormatting.LIGHT_PURPLE, KeyMapping.createNameSupplier("key.scrollAbility").get().getString().toUpperCase());
            } catch (NullPointerException ignored) {
            }
        } else TooltipHandler.holdShift(list);
    }

    public static void trigger(Level level, ServerPlayer player) {
        if (player.getCooldowns().isOnCooldown(EnigmaticItems.EXPLORER_SCROLL.get())) return;
        player.getCooldowns().addCooldown(EnigmaticItems.EXPLORER_SCROLL.get(), cooldown.get());
        BlockPos blockPos = player.blockPosition();
        int d = distance.get();
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(blockPos.offset(d, d, d), blockPos.offset(-d, -d, -d));
        for (BlockPos pos : iterable) {
            if (level.getBlockEntity(pos) instanceof RandomizableContainerBlockEntity blockEntity) {
                if (blockEntity.getLootTable() != null) {
                    ExplorerMarker explorerMarker = new ExplorerMarker(level, pos, player);
                    level.addFreshEntity(explorerMarker);
                }
            }
        }
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(getLocation(this), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        builder.put(Attributes.MOVEMENT_EFFICIENCY, new AttributeModifier(getLocation(this), 0.2, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.SNEAKING_SPEED, new AttributeModifier(getLocation(this), 0.16, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(getLocation(this), 1.2, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }
}
