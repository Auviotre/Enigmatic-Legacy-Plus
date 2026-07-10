package auviotre.enigmatic.legacy.contents.item.misc;

import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.api.item.IPermanentCrystal;
import auviotre.enigmatic.legacy.contents.entity.misc.PermanentItemEntity;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StorageCrystal extends BaseItem implements IPermanentCrystal {
    public static ModConfigSpec.BooleanValue enable;

    public StorageCrystal() {
        super(IItemHelper.singleProperties().fireResistant().rarity(Rarity.EPIC));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.storage_crystal").push("else.storageCrystal");
        enable = builder.define("enable", true);
        builder.pop(2);
    }

    public static ItemStack storeDropsOnCrystal(Collection<ItemEntity> drops, Player player, ItemStack soulCrystal) {
        ItemStack stack = EnigmaticItems.STORAGE_CRYSTAL.toStack();
        int experience = player.totalExperience;
        player.giveExperiencePoints(-experience);
        player.experienceLevel = 0;
        player.experienceProgress = 0;
        List<ItemStack> list = new ArrayList<>();
        for (ItemEntity drop : drops) if (!drop.getItem().isEmpty()) list.add(drop.getItem().copy());
        if (soulCrystal.isEmpty()) soulCrystal = EnigmaticItems.STORAGE_CRYSTAL.toStack();
        Info storageInfo = Info.of(soulCrystal, experience, list);
        stack.set(EnigmaticComponents.STORAGE_INFO, storageInfo);
        return stack;
    }

    public static void retrieveDropsFromCrystal(ItemStack crystal, ServerPlayer player, ItemStack retrieveSoul, Vec3 pos, Map<String, ItemStack> map) {
        Info info = crystal.get(EnigmaticComponents.STORAGE_INFO);
        if (info == null) return;
        List<ItemStack> drops = info.drops();
        Inventory inventory = player.getInventory();
        for (String key : map.keySet()) {
            ItemStack stack = map.get(key);
            try {
                if (key.contains("curio")) {
                    int i = Integer.parseInt(key.replace("curio", ""));
                    for (ItemStack drop : new ArrayList<>(drops)) {
                        if (ItemStack.isSameItemSameComponents(drop, stack)) {
                            CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
                                IItemHandlerModifiable curios = handler.getEquippedCurios();
                                if (curios.getStackInSlot(i).isEmpty() && curios.isItemValid(i, stack) && i < curios.getSlots()) {
                                    curios.setStackInSlot(i, stack);
                                    drops.remove(drop);
                                }
                            });
                            break;
                        }
                    }
                } else {
                    int i = Integer.parseInt(key);
                    for (ItemStack drop : new ArrayList<>(drops)) {
                        if (ItemStack.isSameItemSameComponents(drop, stack)) {
                            if (inventory.getItem(i).isEmpty()) {
                                inventory.setItem(i, stack);
                                drops.remove(drop);
                            }
                            break;
                        }
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }
        for (ItemStack drop : drops) {
            if (!inventory.add(drop)) {
                PermanentItemEntity entity = new PermanentItemEntity(player.level(), pos.x, pos.y, pos.z, drop);
                entity.setThrowerId(player.getUUID());
                entity.setOwnerId(player.getUUID());
                player.level().addFreshEntity(entity);
            }
        }
        player.giveExperiencePoints(info.exp);
        if (retrieveSoul.is(EnigmaticItems.SOUL_CRYSTAL)) SoulCrystal.retrieveSoulFromCrystal(player);
        else if (!player.level().isClientSide) player.playSound(SoundEvents.BEACON_ACTIVATE, 1.0f, 1.0f);
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

    public record Info(ItemStack soulCrystal, int exp, List<ItemStack> drops) {
        public static final MapCodec<Info> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("crystal").forGetter(Info::soulCrystal),
                Codec.INT.fieldOf("exp").forGetter(Info::exp),
                Codec.list(ItemStack.CODEC).fieldOf("drops").forGetter(Info::drops)
        ).apply(instance, Info::of));

        public static final Codec<Info> CODEC = MAP_CODEC.codec();

        public static final StreamCodec<RegistryFriendlyByteBuf, Info> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, Info::soulCrystal,
                ByteBufCodecs.INT, Info::exp,
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), Info::drops,
                Info::of);

        public static Info of(ItemStack soulCrystal, int exp, List<ItemStack> drops) {
            return new Info(soulCrystal, exp, drops);
        }

        public List<ItemStack> drops() {
            return new ArrayList<>(drops);
        }
    }
}
