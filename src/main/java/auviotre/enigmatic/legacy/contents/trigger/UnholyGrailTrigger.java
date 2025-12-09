package auviotre.enigmatic.legacy.contents.trigger;

import auviotre.enigmatic.legacy.registries.EnigmaticTriggers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class UnholyGrailTrigger extends SimpleCriterionTrigger<UnholyGrailTrigger.TriggerInstance> {
    public void trigger(ServerPlayer player, boolean isTheWorthyOne) {
        this.trigger(player, instance -> instance.test(isTheWorthyOne));
    }

    public Codec<TriggerInstance> codec() {
        return UnholyGrailTrigger.TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                  boolean theWorthy) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<UnholyGrailTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        Codec.BOOL.optionalFieldOf("the_worthy", false).forGetter(TriggerInstance::theWorthy)
                ).apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> use(boolean theWorthy) {
            return EnigmaticTriggers.UNHOLY_GRAIL_TRIGGER.get().createCriterion(new TriggerInstance(Optional.empty(), theWorthy));
        }

        public boolean test(boolean flag) {
            return flag == theWorthy;
        }
    }
}
