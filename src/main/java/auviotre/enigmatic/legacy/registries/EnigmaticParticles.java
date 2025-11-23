package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class EnigmaticParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, EnigmaticLegacy.MODID);
    public static final DeferredHolder<ParticleType<?>, ParticleType<ColorParticleOption>> SPELL = register(ColorParticleOption::codec, ColorParticleOption::streamCodec);
    public static final DeferredHolder<ParticleType<?>, ParticleType<SimpleParticleType>> ICHOR = PARTICLE_TYPES.register("ichor", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, ParticleType<SimpleParticleType>> ICHOR_CURSE = PARTICLE_TYPES.register("ichor_curse", () -> new SimpleParticleType(false));

    private static <T extends ParticleOptions> @NotNull DeferredHolder<ParticleType<?>, ParticleType<T>> register(final Function<ParticleType<T>, MapCodec<T>> codecGetter, final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
        return PARTICLE_TYPES.register("spell", () -> new ParticleType<T>(false) {
            public MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        });
    }
}
