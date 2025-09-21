package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnigmaticDamageTypes {
    public static final ResourceKey<DamageType> EVIL_CURSE = key("evil_curse");
    public static final ResourceKey<DamageType> NEMESIS_CURSE = key("nemesis_curse");
    public static final ResourceKey<DamageType> DARKNESS = key("darkness");

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(EVIL_CURSE, new DamageType("enigmaticlegacy.evil_curse", 0.0F));
        context.register(NEMESIS_CURSE, new DamageType("enigmaticlegacy.nemesis_curse", 0.0F));
        context.register(DARKNESS, new DamageType("enigmaticlegacy.darkness", 0.0F));
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> type) {
        return source(level, type, null, null);
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker) {
        return source(level, type, attacker, attacker);
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        var damageType = EnigmaticHandler.get(level, Registries.DAMAGE_TYPE, type);
        return new DamageSource(damageType, directEntity, causingEntity);
    }

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, EnigmaticLegacy.location(name));
    }
}
