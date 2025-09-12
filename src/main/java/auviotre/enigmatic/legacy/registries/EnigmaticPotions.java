package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, EnigmaticLegacy.MODID);
    public static final DeferredHolder<Potion, Potion> HASTE = POTIONS.register("haste", () -> new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_HASTE = POTIONS.register("long_haste", () -> new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 9600)));
    public static final DeferredHolder<Potion, Potion> STRONG_HASTE = POTIONS.register("strong_haste", () -> new Potion(new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1)));
    public static final DeferredHolder<Potion, Potion> MOLTEN_HEART = POTIONS.register("molten_heart", () -> new Potion(new MobEffectInstance(EnigmaticEffects.MOLTEN_HEART, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_MOLTEN_HEART = POTIONS.register("long_molten_heart", () -> new Potion(new MobEffectInstance(EnigmaticEffects.MOLTEN_HEART, 9600)));
}
