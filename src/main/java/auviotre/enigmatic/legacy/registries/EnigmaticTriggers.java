package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.trigger.UnholyGrailTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create(Registries.TRIGGER_TYPE, EnigmaticLegacy.MODID);
    public static final DeferredHolder<CriterionTrigger<?>, UnholyGrailTrigger> UNHOLY_GRAIL_TRIGGER = TRIGGER_TYPES.register("unholy_grail", UnholyGrailTrigger::new);
}
