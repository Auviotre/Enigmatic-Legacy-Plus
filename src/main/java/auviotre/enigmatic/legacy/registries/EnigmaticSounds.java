package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, EnigmaticLegacy.MODID);
    public static final DeferredHolder<SoundEvent, SoundEvent> CHARGED_ON = register("misc.charge_on");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHARGED_OFF = register("misc.charge_off");
    public static final DeferredHolder<SoundEvent, SoundEvent> ACCELERATE = register("misc.accelerate");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEFLECT = register("misc.deflect");
    public static final DeferredHolder<SoundEvent, SoundEvent> SWORD_HIT_REJECT = register("misc.sword_hit_reject");
    public static final DeferredHolder<SoundEvent, SoundEvent> EAT_REVERSE = register("misc.uneat");
    public static final DeferredHolder<SoundEvent, SoundEvent> COSMIC_CAKE_RESTORE = register("block.cosmic_cake.restore");
    public static final DeferredHolder<SoundEvent, SoundEvent> ETHERIUM_SHIELD_DEFLECT = register("misc.etherium_shield.deflect");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARMOR_EQUIP_ETHERIUM = register("item.armor.equip_etherium");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(EnigmaticLegacy.location(name)));
    }
}
