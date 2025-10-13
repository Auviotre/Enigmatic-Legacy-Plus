package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.effect.BlazingMight;
import auviotre.enigmatic.legacy.contents.effect.MoltenHeart;
import auviotre.enigmatic.legacy.contents.effect.Poison;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, EnigmaticLegacy.MODID);
    public static final DeferredHolder<MobEffect, MoltenHeart> MOLTEN_HEART = EFFECTS.register("molten_heart", MoltenHeart::new);
    public static final DeferredHolder<MobEffect, BlazingMight> BLAZING_MIGHT = EFFECTS.register("blazing_might", BlazingMight::new);
    public static final DeferredHolder<MobEffect, Poison> POISON = EFFECTS.register("poison", Poison::new);
}