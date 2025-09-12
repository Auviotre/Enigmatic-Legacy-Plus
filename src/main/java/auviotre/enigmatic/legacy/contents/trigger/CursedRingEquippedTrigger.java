package auviotre.enigmatic.legacy.contents.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;

import java.util.Optional;

public class CursedRingEquippedTrigger extends SimpleCriterionTrigger<CursedRingEquippedTrigger.TriggerInstance> {
    public static final CursedRingEquippedTrigger INSTANCE = new CursedRingEquippedTrigger();


    public Codec<TriggerInstance> codec() {
        return CursedRingEquippedTrigger.TriggerInstance.CODEC;
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<CursedRingEquippedTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player))
                        .apply(instance, TriggerInstance::new)
        );
    }

}
