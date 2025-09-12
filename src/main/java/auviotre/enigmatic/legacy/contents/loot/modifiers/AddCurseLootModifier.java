package auviotre.enigmatic.legacy.contents.loot.modifiers;

import auviotre.enigmatic.legacy.ELConfig;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class AddCurseLootModifier extends AddTableLootModifier {
    public static final Supplier<MapCodec<AddCurseLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
                    .and(ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(modifier -> modifier.lootTable))
                    .apply(inst, AddCurseLootModifier::new)));
    private final ResourceKey<LootTable> lootTable;

    public AddCurseLootModifier(LootItemCondition[] conditionsIn, ResourceKey<LootTable> table) {
        super(conditionsIn, table);
        this.lootTable = table;
    }

    protected @Nonnull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> list, LootContext context) {
        if (!ELConfig.CONFIG.SEVEN_CURSES.enableSpecialDrops.get()) return list;
        if (!context.hasParam(LootContextParams.ATTACKING_ENTITY)) return list;
        Entity entity = context.getParam(LootContextParams.ATTACKING_ENTITY);
        if (entity instanceof LivingEntity attacker && EnigmaticHandler.isTheCursedOne(attacker)) {
            context.getResolver().get(Registries.LOOT_TABLE, this.lootTable).ifPresent(tableRefer -> {
                LootTable table = tableRefer.value();
                Objects.requireNonNull(list);
                table.getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), list::add));
            });
        }
        return list;
    }

    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
