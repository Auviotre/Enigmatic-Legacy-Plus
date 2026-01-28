package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attribute.EtheriumShieldAttribute;
import auviotre.enigmatic.legacy.contents.attribute.ProjectileDeflectAttribute;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnigmaticAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, EnigmaticLegacy.MODID);

    public static final DeferredHolder<Attribute, EtheriumShieldAttribute> ETHERIUM_SHIELD = ATTRIBUTES.register("etherium_shield", EtheriumShieldAttribute::new);
    public static final DeferredHolder<Attribute, ProjectileDeflectAttribute> PROJECTILE_DEFLECT = ATTRIBUTES.register("projectile_deflect", ProjectileDeflectAttribute::new);
}
