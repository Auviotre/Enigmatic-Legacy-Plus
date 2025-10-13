package auviotre.enigmatic.legacy.contents.loot.conditions;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticLootConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public record SpellstoneLootCondition(float baseChance, float bonusChance) implements LootItemCondition {
    public static final MapCodec<SpellstoneLootCondition> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("baseChance").forGetter(SpellstoneLootCondition::baseChance),
            Codec.floatRange(0.0F, 1.0F).fieldOf("bonusChance").forGetter(SpellstoneLootCondition::bonusChance)
    ).apply(instance, SpellstoneLootCondition::new));

    public static LootItemCondition.Builder chance(float base, float bonus) {
        return () -> new SpellstoneLootCondition(base, bonus);
    }

    public @NotNull LootItemConditionType getType() {
        return EnigmaticLootConditions.SPELLSTONE.get();
    }

    public boolean test(@NotNull LootContext context) {
        float chance = this.baseChance;
        if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof LivingEntity entity) {
            if (EnigmaticHandler.hasCurio(entity, EnigmaticItems.SPELLTUNER)) {
                chance += this.bonusChance;
            }
        }
        return context.getRandom().nextFloat() < chance;
    }
}
