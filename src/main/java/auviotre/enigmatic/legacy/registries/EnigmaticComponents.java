package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.ISpellstone;
import auviotre.enigmatic.legacy.contents.item.charms.ForgerGem;
import auviotre.enigmatic.legacy.contents.item.etherium.EtheriumArmor;
import auviotre.enigmatic.legacy.contents.item.misc.StorageCrystal;
import auviotre.enigmatic.legacy.contents.item.spellstones.other.Spelltuner;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EnigmaticComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, EnigmaticLegacy.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CURSED = COMPONENTS.register("cursed",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BLESSED = COMPONENTS.register("blessed",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ELDRITCH = COMPONENTS.register("eldritch",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINER_POINT = COMPONENTS.register("miner_point",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ForgerGem.ToolInfo>> TOOL_DURABILITY_INFO = COMPONENTS.register("tool_durability_info",
            () -> DataComponentType.<ForgerGem.ToolInfo>builder().persistent(ForgerGem.ToolInfo.CODEC).networkSynchronized(ForgerGem.ToolInfo.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TAINTABLE = COMPONENTS.register("taintable",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> AMULET_NAME = COMPONENTS.register("amulet_name",
            () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> AMULET_COLOR = COMPONENTS.register("amulet_color",
            () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SPELLCORE_POWER = COMPONENTS.register("spellcore_power",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Spelltuner.Context>> SPELLTUNER_CONTEXT = COMPONENTS.register("spelltuner_context",
            () -> DataComponentType.<Spelltuner.Context>builder().persistent(Spelltuner.Context.CODEC).networkSynchronized(Spelltuner.Context.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StorageCrystal.StorageInfo>> STORAGE_INFO = COMPONENTS.register("storage_info",
            () -> DataComponentType.<StorageCrystal.StorageInfo>builder().persistent(StorageCrystal.StorageInfo.CODEC).networkSynchronized(StorageCrystal.StorageInfo.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MALICE_DURABILITY = COMPONENTS.register("malice_durability",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MALICE_MAX_DURABILITY = COMPONENTS.register("malice_max_durability",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> XP_SCROLL_ACTIVE = COMPONENTS.register("xp_active",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> XP_SCROLL_MODE = COMPONENTS.register("xp_mode",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> XP_SCROLL_STORED = COMPONENTS.register("xp_stored",
            () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> DIMENSIONAL_POS = COMPONENTS.register("dimensional_pos",
            () -> DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> WORMHOLE_UUID = COMPONENTS.register("wormhole_uuid",
            () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ETHERIUM_SHIELD = COMPONENTS.register("etherium_shield",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ELDRITCH_TIMER = COMPONENTS.register("eldritch_timer",
            () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LOOT_TABLE_ID = COMPONENTS.register("loot_table_id",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NO_DROP = COMPONENTS.register("no_drop",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REVIVE_COOLDOWN = COMPONENTS.register("revive_cooldown",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());


    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent(priority = EventPriority.HIGH)
        private static void onAddAttributeTooltips(@NotNull AddAttributeTooltipsEvent event) {
            Level level = event.getContext().level();
            if (level == null || !level.isClientSide()) return;
            ItemStack stack = event.getStack();
            int power = stack.getOrDefault(ETHERIUM_SHIELD, 0);
            if (stack.has(ETHERIUM_SHIELD) && power > 0 && !(stack.getItem() instanceof ICurioItem)) {
                Player player = event.getContext().player();
                boolean flag = ISpellstone.get(player).is(EnigmaticItems.ETHERIUM_CORE);
                if (flag) power = Mth.floor(power * 1.4F);
                if (stack.getItem() instanceof TieredItem && player != null) {
                    power += EtheriumArmor.getShieldThreshold(player);
                    if (player.getMainHandItem().has(ETHERIUM_SHIELD)) {
                        int i = player.getMainHandItem().getOrDefault(ETHERIUM_SHIELD, 0);
                        if (flag) i = Mth.floor(i * 1.4F);
                        if (i > 0) power -= i;
                    }
                    event.addTooltipLines(Component.translatable("tooltip.enigmaticlegacy.etheriumAttributeAlt", power + "%").withStyle(ChatFormatting.DARK_GREEN));
                } else event.addTooltipLines(Component.translatable("tooltip.enigmaticlegacy.etheriumAttribute", power + "%").withStyle(ChatFormatting.BLUE));
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        private static void canEquip(@NotNull CurioCanEquipEvent event) {
            if (EnigmaticHandler.isCursedItem(event.getStack()) && !EnigmaticHandler.isTheCursedOne(event.getEntity()))
                event.setEquipResult(TriState.FALSE);
            else if (EnigmaticHandler.isEldritchItem(event.getStack()) && !EnigmaticHandler.isTheWorthyOne(event.getEntity()))
                event.setEquipResult(TriState.FALSE);
        }
    }
}
