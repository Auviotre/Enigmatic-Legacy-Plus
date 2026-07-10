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

public class EnigmaticTrigger extends SimpleCriterionTrigger<EnigmaticTrigger.TriggerInstance> {
    /*
     * 1. Lost Engine Hit by Lightning
     * 2. Antique Bag Fully Filled
     * 3. Use Infernal Spear
     * 4. Killing with Arrogance of Chaos
     * 5. Use Escape Scroll
     * 6. Destroy Cursed Ring with stone
     */
    public void trigger(ServerPlayer player, int triggerID) {
        this.trigger(player, instance -> instance.test(triggerID));
    }

    public Codec<TriggerInstance> codec() {
        return EnigmaticTrigger.TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                  int triggerID) implements SimpleInstance {
        public static final Codec<EnigmaticTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        Codec.INT.optionalFieldOf("trigger_id", 0).forGetter(TriggerInstance::triggerID)
                ).apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> trigger(int triggerID) {
            return EnigmaticTriggers.ENIGMATIC_TRIGGER.get().createCriterion(new TriggerInstance(Optional.empty(), triggerID));
        }

        public boolean test(int id) {
            return id == triggerID;
        }
    }
}
