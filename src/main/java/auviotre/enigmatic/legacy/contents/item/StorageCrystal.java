package auviotre.enigmatic.legacy.contents.item;

import auviotre.enigmatic.legacy.api.item.IPermanentCrystal;
import auviotre.enigmatic.legacy.contents.entity.PermanentItemEntity;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StorageCrystal extends BaseItem implements IPermanentCrystal {
    public StorageCrystal() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.EPIC));
    }

    public static ItemStack storeDropsOnCrystal(Collection<ItemEntity> drops, Player player, @Nullable ItemStack soulCrystal) {
        ItemStack stack = EnigmaticItems.STORAGE_CRYSTAL.toStack();
        int experience = player.totalExperience;
        player.giveExperiencePoints(-experience);
        List<ItemStack> list = new ArrayList<>();
        for (ItemEntity drop : drops) list.add(drop.getItem());
        StorageInfo storageInfo = StorageInfo.of(soulCrystal, experience, list);
        stack.set(EnigmaticComponents.STORAGE_INFO, storageInfo);
        return stack;
    }

    public static void retrieveDropsFromCrystal(ItemStack crystal, Player player, ItemStack retrieveSoul, Vec3 pos) {
        StorageInfo info = crystal.get(EnigmaticComponents.STORAGE_INFO);
        if (info == null) return;
        List<ItemStack> drops = info.drops;
        for (ItemStack drop : drops) {
            if (!player.getInventory().add(drop)) {
                PermanentItemEntity entity = new PermanentItemEntity(player.level(), pos.x, pos.y + Math.random(), pos.z, drop);
                player.level().addFreshEntity(entity);
            }
        }
        player.giveExperiencePoints(info.exp);
        if (!retrieveSoul.isEmpty()) SoulCrystal.retrieveSoulFromCrystal(player);
        else if (!player.level().isClientSide) player.playSound(SoundEvents.BEACON_ACTIVATE, 1.0f, 1.0f);
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

    public record StorageInfo(ItemStack soulCrystal, int exp, List<ItemStack> drops) {
        public static final MapCodec<StorageInfo> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                ItemStack.CODEC.fieldOf("crystal").forGetter(StorageInfo::soulCrystal),
                Codec.INT.fieldOf("exp").forGetter(StorageInfo::exp),
                Codec.list(ItemStack.CODEC).fieldOf("drops").forGetter(StorageInfo::drops)
        ).apply(instance, StorageInfo::of));

        public static final Codec<StorageInfo> CODEC = MAP_CODEC.codec();

        public static final StreamCodec<RegistryFriendlyByteBuf, StorageInfo> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, StorageInfo::soulCrystal,
                ByteBufCodecs.INT, StorageInfo::exp,
                ByteBufCodecs.fromCodec(Codec.list(ItemStack.CODEC)), StorageInfo::drops,
                StorageInfo::of);

        public static StorageInfo of(ItemStack soulCrystal, int exp, List<ItemStack> drops) {
            return new StorageInfo(soulCrystal, exp, drops);
        }
    }
}
