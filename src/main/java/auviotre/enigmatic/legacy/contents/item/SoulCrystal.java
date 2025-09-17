package auviotre.enigmatic.legacy.contents.item;

import auviotre.enigmatic.legacy.ELConfig;
import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IPermanentCrystal;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

public class SoulCrystal extends BaseItem implements IPermanentCrystal {
    public static final Map<Player, Multimap<Holder<Attribute>, AttributeModifier>> ATTRIBUTE_DISPATCHER = new WeakHashMap<>();

    public SoulCrystal() {
        super(defaultSingleProperties().rarity(Rarity.EPIC));
    }

    public static ItemStack createCrystalFrom(Player player) {
        int lostCount = getLostCrystals(player);
        setLostCrystals(player, lostCount + 1);
        return EnigmaticItems.SOUL_CRYSTAL.toStack();
    }

    public static boolean isPermanentlyDead(Player player) {
        return SoulCrystal.getLostCrystals(player) >= 10 && ELConfig.CONFIG.SEVEN_CURSES.maxSoulCrystalLoss.get() >= 10;
    }

    public static boolean retrieveSoulFromCrystal(Player player) {
        int lostCount = getLostCrystals(player);
        if (lostCount > 0) {
            setLostCrystals(player, lostCount - 1);
            if (!player.level().isClientSide) player.playSound(SoundEvents.BEACON_ACTIVATE, 1.0f, 1.0f);
            return true;
        }
        return false;
    }

    public static void setLostCrystals(Player player, int lostCount) {
        EnigmaticHandler.setCurrentWorldFractured(lostCount >= 10);
        EnigmaticHandler.getPersistedData(player).putInt("SixthCurseSoulLossCount", lostCount);
        updatePlayerSoulMap(player);
    }

    public static int getLostCrystals(Player player) {
        return EnigmaticHandler.getPersistedData(player).getInt("SixthCurseSoulLossCount");
    }

    public static Multimap<Holder<Attribute>, AttributeModifier> getOrCreateSoulMap(Player player) {
        if (ATTRIBUTE_DISPATCHER.containsKey(player))
            return ATTRIBUTE_DISPATCHER.get(player);
        else {
            Multimap<Holder<Attribute>, AttributeModifier> playerAttributes = HashMultimap.create();
            ATTRIBUTE_DISPATCHER.put(player, playerAttributes);
            return playerAttributes;
        }
    }

    public static void updatePlayerSoulMap(Player player) {
        Multimap<Holder<Attribute>, AttributeModifier> soulMap = getOrCreateSoulMap(player);
        AttributeMap attributeManager = player.getAttributes();

        // Removes former attributes
        attributeManager.removeAttributeModifiers(soulMap);

        soulMap.clear();
        int lostCount = getLostCrystals(player);
        if (lostCount > 0)
            soulMap.put(Attributes.MAX_HEALTH, new AttributeModifier(EnigmaticLegacy.location("soul_loss"), -0.1F * lostCount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        // Applies new attributes
        attributeManager.addTransientAttributeModifiers(soulMap);
        ATTRIBUTE_DISPATCHER.put(player, soulMap);
    }

    public InteractionResultHolder<ItemStack> use(Level world, @NotNull Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (retrieveSoulFromCrystal(player)) {
            updatePlayerSoulMap(player);
            // TODO: Add Particle Packet.
            player.swing(hand);
            stack.setCount(0);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    public enum LossMode {
        ALWAYS_LOSS, NEED_CURSE_RING, NEED_CURSE_RING_AND_IGNORE_KEEPINVENTORY;
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onPlayerClone(PlayerEvent.@NotNull Clone event) {
            updatePlayerSoulMap(event.getEntity());
        }

        @SubscribeEvent
        private static void entityJoinWorld(@NotNull EntityJoinLevelEvent event) {
            if (event.getEntity() instanceof ServerPlayer joinedPlayer) {
                updatePlayerSoulMap(joinedPlayer);
            }
        }
    }
}
