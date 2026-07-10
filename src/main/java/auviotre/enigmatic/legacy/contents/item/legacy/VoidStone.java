package auviotre.enigmatic.legacy.contents.item.legacy;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VoidStone extends BaseItem {
    private static final List<String> DEFAULT_LIST = List.of(
            "enigmaticlegacyplus:void_stone",
            "sophisticatedbackpacks:backpack",
            "sophisticatedbackpacks:copper_backpack",
            "sophisticatedbackpacks:iron_backpack",
            "sophisticatedbackpacks:gold_backpack",
            "sophisticatedbackpacks:diamond_backpack",
            "sophisticatedbackpacks:netherite_backpack"
    );
    public static ModConfigSpec.ConfigValue<List<? extends String>> blacklist;

    public VoidStone() {
        super(IItemHelper.singleProperties().rarity(Rarity.UNCOMMON).fireResistant().component(EnigmaticComponents.BOOLEAN, false).component(EnigmaticComponents.BLACKLIST, List.of()));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.void_stone").push("else.void_stone");
        blacklist = builder.defineList("devourBlacklist", DEFAULT_LIST, () -> "minecraft:air", Objects::nonNull);
        builder.pop(2);
    }

    private static void addBlackList(ItemStack stone, ItemStack destroy) {
        List<String> list = new ArrayList<>(stone.getOrDefault(EnigmaticComponents.BLACKLIST, List.of()));
        list.add(IItemHelper.getLocation(destroy.getItem()).toString());
        stone.set(EnigmaticComponents.BLACKLIST, list);
    }

    private static void removeBlackList(ItemStack stone, ItemStack destroy) {
        List<String> list = new ArrayList<>(stone.getOrDefault(EnigmaticComponents.BLACKLIST, List.of()));
        list.remove(IItemHelper.getLocation(destroy.getItem()).toString());
        stone.set(EnigmaticComponents.BLACKLIST, list);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidStone1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidStone2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidStone3");
        TempStorage storage = stack.get(EnigmaticComponents.TEMPORARY_STORAGE);
        if (storage != null) {
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidStoneStorage", ChatFormatting.GOLD, storage.stack().getDisplayName());
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidStoneAlt");
        }
        TooltipHandler.line(list);
        String s = stack.getOrDefault(EnigmaticComponents.BOOLEAN, false) ? "tooltip.enigmaticlegacy.enabled" : "tooltip.enigmaticlegacy.disabled";
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidStoneAutoDestory", ChatFormatting.GOLD, Component.translatable(s));
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidStoneShift");
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        TempStorage storage = stack.get(EnigmaticComponents.TEMPORARY_STORAGE);
        Boolean flag = stack.getOrDefault(EnigmaticComponents.BOOLEAN, false);
        if (player.isShiftKeyDown()) {
            stack.set(EnigmaticComponents.BOOLEAN, !flag);
            return InteractionResultHolder.success(stack);
        } else if (storage != null) {
            if (flag) return InteractionResultHolder.pass(stack);
            ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), storage.stack());
            entity.setThrower(player);
            entity.setNoPickUpDelay();
            player.level().addFreshEntity(entity);
            removeBlackList(stack, storage.stack());
            stack.remove(EnigmaticComponents.TEMPORARY_STORAGE);
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, hand);
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        boolean notAllowed = blacklist.get().contains(IItemHelper.getLocation(other.getItem()).toString());
        if (notAllowed || action == ClickAction.PRIMARY || !slot.mayPlace(stack) || !slot.mayPickup(player) || other.isEmpty())
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        addBlackList(stack, other);
        stack.set(EnigmaticComponents.TEMPORARY_STORAGE, TempStorage.of(other));
        other.setCount(0);
        if (player.level().isClientSide) {
            player.playSound(EnigmaticSounds.VOID_STONE_DEVOUR.get(), 0.25F, 0.7F + player.getRandom().nextFloat() * 0.25F);
        }
        return true;
    }

    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        boolean notAllowed = blacklist.get().contains(IItemHelper.getLocation(slot.getItem().getItem()).toString());
        if (notAllowed || action == ClickAction.PRIMARY || !slot.mayPlace(stack) || !slot.mayPickup(player) || !slot.hasItem())
            return super.overrideStackedOnOther(stack, slot, action, player);
        addBlackList(stack, slot.getItem());
        stack.set(EnigmaticComponents.TEMPORARY_STORAGE, TempStorage.of(slot.getItem()));
        slot.set(ItemStack.EMPTY);
        if (player.level().isClientSide) {
            player.playSound(EnigmaticSounds.VOID_STONE_DEVOUR.get(), 0.25F, 0.7F + player.getRandom().nextFloat() * 0.25F);
        }
        return true;
    }


    public record TempStorage(ItemStack stack) {
        public static final MapCodec<TempStorage> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("count").forGetter(TempStorage::stack)
        ).apply(instance, TempStorage::of));

        public static final Codec<TempStorage> CODEC = MAP_CODEC.codec();

        public static final StreamCodec<RegistryFriendlyByteBuf, TempStorage> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, TempStorage::stack,
                TempStorage::of);

        public static @NotNull TempStorage of(ItemStack stack) {
            return new TempStorage(stack.copy());
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onLivingDrops(ItemEntityPickupEvent.@NotNull Pre event) {
            ItemEntity itemEntity = event.getItemEntity();
            Player player = event.getPlayer();
            ItemStack stone = EnigmaticHandler.getItem(player, EnigmaticItems.VOID_STONE);
            if (!stone.isEmpty()) {
                if (!stone.getOrDefault(EnigmaticComponents.BOOLEAN, false)) return;
                List<String> list = stone.getOrDefault(EnigmaticComponents.BLACKLIST, List.of());
                if (list.contains(IItemHelper.getLocation(itemEntity.getItem().getItem()).toString())) {
                    stone.set(EnigmaticComponents.TEMPORARY_STORAGE, TempStorage.of(itemEntity.getItem()));
                    itemEntity.discard();
                    event.setCanPickup(TriState.FALSE);
                }
            }
        }
    }
}