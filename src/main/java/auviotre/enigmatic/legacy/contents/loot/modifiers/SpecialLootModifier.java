package auviotre.enigmatic.legacy.contents.loot.modifiers;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;
import java.util.function.Supplier;

public class SpecialLootModifier extends LootModifier {
    public static final Supplier<MapCodec<SpecialLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, SpecialLootModifier::new))
    );

    public static final List<ResourceLocation> SUSPICIOUS_TABLES = ImmutableList.of(
            BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY.location(),
            BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY.location(),
            BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY.location(),
            BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY.location(),
            BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON.location(),
            BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE.location()
    );

    public SpecialLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> list, LootContext context) {
        ServerLevel level = context.getLevel();
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (entity instanceof ServerPlayer player) {
            CompoundTag data = EnigmaticHandler.getPersistedData(player);
            if (data.contains("LootedIchorBottle")) {
                list.removeIf(stack -> stack.is(EnigmaticItems.ICHOR_BOTTLE));
            } else if (list.stream().anyMatch(stack -> stack.is(EnigmaticItems.ICHOR_BOTTLE))) {
                data.putBoolean("LootedIchorBottle", true);
            }

            if (BuiltInLootTables.END_CITY_TREASURE.location().equals(context.getQueriedLootTableId())) {
                if (level.dimension().equals(Level.END) && !data.contains("LootedFirstEndCityChest")) {
                    data.putBoolean("LootedFirstEndCityChest", true);
                    if (EnigmaticHandler.isTheCursedOne(player)) {
                        list.add(EnigmaticItems.ENCHANTED_ASTRAL_FRUIT.toStack());
                    }
                }
            }

            if (SUSPICIOUS_TABLES.stream().anyMatch(table -> table.equals(context.getQueriedLootTableId()))) {
                if (context.getRandom().nextFloat() < 0.075F) {
                    list.clear();
                    if (context.getRandom().nextFloat() < 0.25F) list.add(EnigmaticItems.EARTH_HEART.toStack());
                    else list.add(EnigmaticItems.EARTH_HEART_FRAGMENT.toStack());
                }
            }
        }
        return list;
    }

    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
